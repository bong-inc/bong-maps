package bfst.OSMReader;

import bfst.addressparser.Address;
import bfst.canvas.*;
import bfst.routeFinding.Edge;
import bfst.routeFinding.Graph;
import bfst.routeFinding.Street;

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
    private NodeContainer tempNodes = new NodeContainer();
    private SortedArrayList<Way> tempWays = new SortedArrayList<>();
    private SortedArrayList<Relation> tempRelations = new SortedArrayList<>();
    private HashMap<Long, Way> tempCoastlines = new HashMap<>();
    private Bound bound;
    private bfst.canvas.Type type;
    private Node nodeHolder;
    private Way wayHolder;
    private Relation relationHolder;
    private ArrayList<String> tagList = new ArrayList<>();
    private Street currentStreet;

    int counter = 0; //TODO bruges kun til videreudvikling, skal fjernes fra endelige produkt

    private ArrayList<Address> addresses = new ArrayList<>();
    private Address.Builder builder;
    private ArrayList<City> cities = new ArrayList<>();

    private Graph graph = new Graph();
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

    public Graph getGraph() {
        return graph;
    }

    public Bound getBound(){return bound;}

    public OSMReader(InputStream inputStream){
        try {
            XMLStreamReader reader = XMLInputFactory
                    .newInstance()
                    .createXMLStreamReader(inputStream);

            while (reader.hasNext()) {
                counter++;
                if (counter % 1000000 == 0) {
                    System.out.println(counter);
                }

                reader.next();
                switch (reader.getEventType()) {
                    case START_ELEMENT:
                        String element = reader.getLocalName().intern();
                        parseElement(reader, element);
                        break;
                    case END_ELEMENT:
                        element = reader.getLocalName().intern();
                        switch (element) {
                            case "node":
                                if(!builder.isEmpty()) {
                                    addresses.add(builder.build());
                                } else {
                                    tempNodes.add(nodeHolder.getAsLong(), nodeHolder.getLon(), nodeHolder.getLat());
                                    //tempNodes.add(nodeHolder);
                                }
                                break;
                            case "way":
                                for (int i = 0; i < tagList.size(); i += 2) {

                                    if (tagList.get(i).equals("access")) {
                                        if (tagList.get(i + 1).equals("no")) {
                                            break;
                                        }
                                    }
                                    if (tagList.get(i).equals("highway")) {

                                        int defaultSpeed;
                                        switch(tagList.get(i + 1)) {
                                            case "motorway":
                                                defaultSpeed = 130;
                                                break;
                                            case "primary":
                                            case "secondary":
                                            case "tertiary":
                                                defaultSpeed = 80;
                                                break;
                                            default:
                                                defaultSpeed = 50;
                                                break;
                                        }

                                        ArrayList<Node> nodes = wayHolder.getNodes();
                                        currentStreet = new Street(tagList, defaultSpeed);

                                        for (int j = 1; j < nodes.size(); j++){
                                            Edge edge = new Edge(nodes.get(j - 1), nodes.get(j), currentStreet);
                                            graph.addEdge(edge);
                                        }
                                        break; 
                                    }
                                }

                                if(type != Type.COASTLINE) {
                                    if (wayHolder.getNodes().size() > 0) {
                                        if (!drawableByType.containsKey(type))
                                            drawableByType.put(type, new ArrayList<>());
                                        drawableByType.get(type).add(new LinePath(wayHolder, type));
                                    }
                                } else {
                                    Way before = tempCoastlines.remove(wayHolder.first().getAsLong());
                                    if (before != null) {
                                        tempCoastlines.remove(before.first().getAsLong());
                                        tempCoastlines.remove(before.last().getAsLong());
                                    }
                                    Way after = tempCoastlines.remove(wayHolder.last().getAsLong());
                                    if (after != null) {
                                        tempCoastlines.remove(after.first().getAsLong());
                                        tempCoastlines.remove(after.last().getAsLong());
                                    }
                                    wayHolder = Way.merge(Way.merge(before, wayHolder), after);
                                    tempCoastlines.put(wayHolder.first().getAsLong(), wayHolder);
                                    tempCoastlines.put(wayHolder.last().getAsLong(), wayHolder);
                                }
                                type = Type.UNKNOWN;
                                break;
                            case "relation":
                                relationHolder.collectRelation();
                                if(!drawableByType.containsKey(type)) drawableByType.put(type, new ArrayList<>());
                                if(relationHolder.getWays() != null) drawableByType.get(type).add(new PolyLinePath(relationHolder, type));
                                type = Type.UNKNOWN;
                                break;
                            case "osm":
                                ArrayList<Drawable> coastlines = new ArrayList<>();
                                for(Map.Entry<Long,Way> entry : tempCoastlines.entrySet()){
                                    if(entry.getValue().first() == entry.getValue().last()){
                                        coastlines.add(new LinePath(entry.getValue(), Type.COASTLINE));
                                    } else {
                                        fixCoastline(entry.getValue());
                                        coastlines.add(new LinePath(entry.getValue(), Type.COASTLINE));
                                    }
                                }
                                if(coastlines.size() == 0){
                                    Way land = new Way();
                                    land.addNode(new Node(0, bound.getMinLon(), bound.getMinLat()));
                                    land.addNode(new Node(0, bound.getMinLon(), bound.getMaxLat()));
                                    land.addNode(new Node(0, bound.getMaxLon(), bound.getMaxLat()));
                                    land.addNode(new Node(0, bound.getMaxLon(), bound.getMinLat()));
                                    coastlines.add(new LinePath(land, Type.COASTLINE));
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
                Node max = MercatorProjector.project(tempMaxLon, tempMaxLat);
                Node min = MercatorProjector.project(tempMinLon, tempMinLat);
                bound = new Bound(
                        -max.getLat(),
                        -min.getLat(),
                        min.getLon(),
                        max.getLon()
                );
                break;
            case "node":
                builder = new Address.Builder();
                cityBuilder = new City.Builder();
                currentID = Long.parseLong(reader.getAttributeValue(null, "id"));
                float tempLon = Float.parseFloat(reader.getAttributeValue(null, "lon"));
                float tempLat = Float.parseFloat(reader.getAttributeValue(null, "lat"));
                nodeHolder = MercatorProjector.project(currentID, tempLon, -tempLat);
                builder.node(nodeHolder);
                cityBuilder.node(nodeHolder);
                break;
            case "way":
                tagList.clear();
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
                String k = reader.getAttributeValue(null, "k").intern();
                String v = reader.getAttributeValue(null, "v").intern();

                tagList.add(k);
                tagList.add(v);

                parseTag(k, v);

                break;
            case "nd":
                long ref = Long.parseLong(reader.getAttributeValue(null, "ref"));
                if(wayHolder != null){
                    Node node = tempNodes.get(ref);
                    if(tempNodes.get(ref) != null) wayHolder.addNode(node);
                    cityBuilder.node(node);
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

        if (k.equals("place") && (v.equals("city") || v.equals("town") ||  v.equals("suburb") || v.equals("village") || v.equals("hamlet"))) {
            boolean cityNotPresent = true;
            for (City city : cities) {
                if (city.getName().equals(previousName)) {
                    cityNotPresent = false;
                    break;
                }
            }
            if (cityNotPresent) {
                cityBuilder.name(previousName);
                cityBuilder.cityType(v);
                cities.add(cityBuilder.build());
            }
        }
    }

    private void parseMember(XMLStreamReader reader) {
        switch(reader.getAttributeValue(null, "type")){
            case "node":
                relationHolder.addNode(tempNodes.get(Long.parseLong(reader.getAttributeValue(null, "ref"))));
                break;
            case "way":
                long memberRef = Long.parseLong(reader.getAttributeValue(null, "ref"));
                Way tempWay = tempWays.get(memberRef);
                if (tempWay != null) {
                    switch (reader.getAttributeValue(null, "role")) {
                        case "outer":
                            relationHolder.addToOuter(tempWays.get(memberRef));
                            break;
                        case "inner":
                            relationHolder.addToInner(tempWays.get(memberRef));
                            break;
                        default:
                            relationHolder.addWay(tempWays.get(memberRef));
                            break;
                    }
                    cityBuilder.node(tempWay.last());
                }
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