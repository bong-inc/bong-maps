package bfst.OSMReader;

import bfst.addressparser.Address;
import bfst.canvas.Drawable;
import bfst.canvas.LinePath;
import bfst.canvas.PolyLinePath;
import bfst.canvas.Type;
import bfst.citiesAndStreets.City;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static javax.xml.stream.XMLStreamConstants.END_ELEMENT;
import static javax.xml.stream.XMLStreamConstants.START_ELEMENT;

public class OSMReader {

    private SortedArrayList<Node> tempNodes = new SortedArrayList<>();
    private SortedArrayList<Way> tempWays = new SortedArrayList<>();
    private SortedArrayList<Relation> tempRelations = new SortedArrayList<>();
    private HashMap<Node, Way> tempCoastlines = new HashMap<>();
    private Bound bound;
    private bfst.canvas.Type type;
    private Node nodeHolder;
    private Way wayHolder;
    private Relation relationHolder;

    private ArrayList<Address> addresses = new ArrayList<>();

    private ArrayList<City> cities = new ArrayList<>();
    private Address.Builder builder;
    private City.Builder cityBuilder;

    private String previousName;

    private long currentID;
    private HashMap<Type, ArrayList<Drawable>> drawableByType = new HashMap<>();

    public HashMap<Type, ArrayList<Drawable>> getDrawableByType(){
        return drawableByType;
    }

    public ArrayList<Address> getAddresses(){
        return addresses;
    }

    public ArrayList<City> getCities() {
        return cities;
    }

    public Bound getBound(){return bound;}

    public OSMReader(InputStream inputStream){
        try {
            XMLStreamReader reader = XMLInputFactory
                    .newInstance()
                    .createXMLStreamReader(inputStream);

            while (reader.hasNext()) {
                reader.next();
                switch (reader.getEventType()) {
                    case START_ELEMENT:
                        String element = reader.getLocalName();
                        parseElement(reader, element);
                        break;
                    case END_ELEMENT:
                        element = reader.getLocalName();
                        switch (element) {
                            case "node":
                                if(!builder.isEmpty()) {
                                    addresses.add(builder.build());
                                }
                                break;
                            case "way":
                                if(type != Type.COASTLINE) {
                                    if(!drawableByType.containsKey(type)) drawableByType.put(type, new ArrayList<>());
                                    drawableByType.get(type).add(new LinePath(wayHolder, type));
                                } else {
                                    Way before = tempCoastlines.remove(wayHolder.first());
                                    if (before != null) {
                                        tempCoastlines.remove(before.first());
                                        tempCoastlines.remove(before.last());
                                    }
                                    Way after = tempCoastlines.remove(wayHolder.last());
                                    if (after != null) {
                                        tempCoastlines.remove(after.first());
                                        tempCoastlines.remove(after.last());
                                    }
                                    wayHolder = Way.merge(Way.merge(before, wayHolder), after);
                                    tempCoastlines.put(wayHolder.first(), wayHolder);
                                    tempCoastlines.put(wayHolder.last(), wayHolder);
                                }
                                type = Type.UNKNOWN;
                                break;
                            case "relation":
                                if(!drawableByType.containsKey(type)) drawableByType.put(type, new ArrayList<>());
                                drawableByType.get(type).add(new PolyLinePath(relationHolder, type));
                                type = Type.UNKNOWN;
                                break;
                            case "osm":
                                ArrayList<Drawable> coastlines = new ArrayList<>();
                                for(Map.Entry<Node,Way> entry : tempCoastlines.entrySet()){
                                    if(entry.getValue().first() == entry.getValue().last()){
                                        coastlines.add(new LinePath(entry.getValue(),Type.COASTLINE));
                                    } else {
                                        fixCoastline(entry.getValue());
                                        coastlines.add(new LinePath(entry.getValue(), Type.COASTLINE));
                                    }
                                }
                                drawableByType.put(Type.COASTLINE,coastlines);
                                break;
                        }
                        break;
                }
            }
        } catch (XMLStreamException e) {
            e.printStackTrace();
        }
    }

    private void parseElement(XMLStreamReader reader, String element) {
        switch (element) {
            case "bounds":
                float tempMaxLat = Float.parseFloat(reader.getAttributeValue(null, "maxlat"));
                float tempMinLat = Float.parseFloat(reader.getAttributeValue(null, "minlat"));
                float tempMinLon = Float.parseFloat(reader.getAttributeValue(null, "minlon"));
                float tempMaxLon = Float.parseFloat(reader.getAttributeValue(null, "maxlon"));
                bound = new Bound(
                        -tempMaxLat,
                        -tempMinLat,
                        (float) 0.55673548 * tempMinLon,
                        (float) 0.55673548 * tempMaxLon
                );
                break;
            case "node":
                builder = new Address.Builder();
                cityBuilder = new City.Builder();
                currentID = Long.parseLong(reader.getAttributeValue(null, "id"));
                float tempLon = Float.parseFloat(reader.getAttributeValue(null, "lon"));
                float tempLat = Float.parseFloat(reader.getAttributeValue(null, "lat"));
                nodeHolder = new Node(currentID, (float) 0.55673548 * tempLon, -tempLat);
                builder.node(nodeHolder);
                cityBuilder.node(nodeHolder);
                tempNodes.add(nodeHolder);
                break;
            case "way":
                currentID = Long.parseLong(reader.getAttributeValue(null, "id"));
                wayHolder = new Way(currentID);
                tempWays.add(wayHolder);
                break;
            case "relation":
                currentID = Long.parseLong(reader.getAttributeValue(null, "id"));
                relationHolder = new Relation();
                tempRelations.add(relationHolder);
                break;
            case "tag":
                String k = reader.getAttributeValue(null, "k");
                String v = reader.getAttributeValue(null, "v");

                parseTag(k, v);

                break;
            case "nd":
                Long ref = Long.parseLong(reader.getAttributeValue(null, "ref"));
                if(wayHolder != null){
                    if(tempNodes.get(ref) != null) wayHolder.addNode(tempNodes.get(ref));
                }
                break;
            case "member":
                parseMember(reader);
                break;
        }
    }

    private void parseTag(String k, String v) {
        if (k.equals("name")) {
            previousName = v;
        }

        Type[] typeArray = Type.getTypes();
        for (Type currentType : typeArray){
            if (k.equals(currentType.getKey())){
                for (String key : currentType.getValue()) {
                    if (v.equals(key) || key.equals("")) {
                        type = currentType;
                        break;
                    }
                }
            }
        }
        if(k.contains("addr:")){
            switch (k) {
                case "addr:city":
                    builder = builder.city(v);
                    break;
                case "addr:postal_code":
                case "addr:postcode":
                    builder = builder.postcode(v);
                    break;
                case "addr:street":
                    builder = builder.street(v);
                    break;
                case "addr:housenumber":
                    builder = builder.house(v);
                    break;
                case "addr:municipality":
                    builder = builder.municipality(v);
                    break;
            }
        }
        if (k.equals("place") && (v.equals("city") || v.equals("town") || v.equals("hamlet") || v.equals("suburb") || v.equals("village"))) {
            cityBuilder.name(previousName);
            cityBuilder.cityType(v);
            cities.add(cityBuilder.build());
        }
    }

    private void parseMember(XMLStreamReader reader) {
        switch(reader.getAttributeValue(null, "type")){
            case "node":
                relationHolder.addNode(tempNodes.get(Long.parseLong(reader.getAttributeValue(null, "ref"))));
                break;
            case "way":
                long memberRef = Long.parseLong(reader.getAttributeValue(null, "ref"));
                if (tempWays.get(memberRef) != null)
                    relationHolder.addWay(tempWays.get(memberRef));
                break;
            case "relation":
                relationHolder.addRefId(Long.parseLong(reader.getAttributeValue(null, "ref")));
                break;
        }
    }

    private void fixCoastline(Way coastline){

        ArrayList<Node> coastlineNodes = coastline.getNodes();
        Node savedNd = coastline.first();
        Node currentNd;
        for(int i = 1;;){
            if(coastlineNodes.size() <= 1) return;
            currentNd = coastlineNodes.get(i);
            float lon = currentNd.getLon();
            float lat = currentNd.getLat();
            if(lon <= bound.getMaxLon() && lon >= bound.getMinLon() && lat <= bound.getMaxLat() && lat >= bound.getMinLat()){ //Is inside bound
                break;
            }
            else{
                coastlineNodes.remove(savedNd);
                savedNd = currentNd;
            }
        }
        savedNd = coastline.last();
        int size = coastlineNodes.size();
        for(int i = size-1; i >= 0; i--){
            currentNd = coastlineNodes.get(i);
            float lon = currentNd.getLon();
            float lat = currentNd.getLat();
            if(lon <= bound.getMaxLon() && lon >= bound.getMinLon() && lat <= bound.getMaxLat() && lat >= bound.getMinLat()){ //Is inside bound
                break;
            }
            else{
                coastlineNodes.remove(savedNd);
                savedNd = currentNd;
            }
        }

        Node first = coastline.first();
        Node last = coastline.last();
        float midLon = (bound.getMaxLon() + bound.getMinLon())/2;
        float midLat = (bound.getMaxLat() + bound.getMinLat())/2;

        boolean fixed = false;

        if((first.getLon() < bound.getMaxLon() && first.getLon() > bound.getMinLon()) && (last.getLon() < bound.getMaxLon() && last.getLon() > bound.getMinLon())) {
            if( (first.getLat() < midLat && last.getLat() < midLat && first.getLon() < last.getLon()) ||
                    (first.getLat() > midLat && last.getLat() > midLat && first.getLon() > last.getLon())) {
                coastline.addNode(last);
                fixed = true;
            }
        }
        else if((first.getLat() < bound.getMaxLat() && first.getLat() > bound.getMinLat()) && (last.getLat() < bound.getMaxLat() && last.getLat() > bound.getMinLat())){
            if( (first.getLon() < midLon && last.getLon() < midLon && first.getLat() > last.getLat()) ||
                    (first.getLon() > midLon && last.getLon() > midLon && first.getLat() < last.getLat())){
                coastline.addNode(last);
                fixed = true;
            }
        }
        if(!fixed){
            Node[] boundNodes = new Node[4];
            boundNodes[0] = new Node(0, bound.getMinLon(), bound.getMinLat()); //TOPLEFT
            boundNodes[1] = new Node(0, bound.getMinLon(), bound.getMaxLat()); //BOTTOMLEFT
            boundNodes[2] = new Node(0, bound.getMaxLon(), bound.getMaxLat()); //BOTTOMRIGHT
            boundNodes[3] = new Node(0, bound.getMaxLon(), bound.getMinLat()); //TOPRIGHT

            if(first.getLat() <= bound.getMinLat()){ //TOP
                coastline.addNodeToFront(boundNodes[3]);
            }
            else if(first.getLat() >= bound.getMaxLat()){ //BOTTOM
                coastline.addNodeToFront(boundNodes[1]);
            }
            else if(first.getLon() <= bound.getMinLon()){ //LEFT
                coastline.addNodeToFront(boundNodes[0]);
            }
            else if(first.getLon() >= bound.getMaxLon()){ //RIGHT
                coastline.addNodeToFront(boundNodes[2]);
            }

            int lastNode = 10;
            if(last.getLat() <= bound.getMinLat()){ //TOP
                coastline.addNode(boundNodes[0]);
                lastNode = 0;
            }
            else if(last.getLat() >= bound.getMaxLat()){ //BOTTOM
                coastline.addNode(boundNodes[2]);
                lastNode = 2;
            }
            else if(last.getLon() <= bound.getMinLon()){ //LEFT
                coastline.addNode(boundNodes[1]);
                lastNode = 1;
            }
            else if(last.getLon() >= bound.getMaxLon()){ //RIGHT
                coastline.addNode(boundNodes[3]);
                lastNode = 3;
            }

            if(lastNode != 10){
                for(int i = lastNode; coastline.first() != coastline.last(); i++) {
                    coastline.addNode(boundNodes[i]);
                    if (i == 3) i = -1;
                }
            }
        }
    }
}