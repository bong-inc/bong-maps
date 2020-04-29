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
import java.util.List;
import java.util.Map;

import static javax.xml.stream.XMLStreamConstants.END_ELEMENT;
import static javax.xml.stream.XMLStreamConstants.START_ELEMENT;

public class OSMReader {
    private NodeContainer tempNodes = new NodeContainer();
    private SortedArrayList<Way> tempWays = new SortedArrayList<>();
    private HashMap<Long, Way> tempCoastlines = new HashMap<>();
    private Bound bound;
    private bfst.canvas.Type type;
    private Node nodeHolder;
    private Way wayHolder;
    private Relation relationHolder;
    private ArrayList<String> tagList = new ArrayList<>();
    private Street currentStreet;
    private ArrayList<Edge> roadEdges = new ArrayList<>();

    int counter = 0; //TODO bruges kun til videreudvikling, skal fjernes fra endelige produkt

    private ArrayList<Address> addresses = new ArrayList<>();
    private Address.Builder builder;
    private ArrayList<City> cities = new ArrayList<>();

    private Graph graph = new Graph();
    private City.Builder cityBuilder;

    private String previousName;

    private long currentID;
    private HashMap<Type, ArrayList<CanvasElement>> drawableByType = new HashMap<>();

    public HashMap<Type, ArrayList<CanvasElement>> getDrawableByType(){
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

    public List<Edge> getRoadEdges() {
        return roadEdges;
    }

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
                                }
                                break;
                            case "way":
                                wayHolder.trim();
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
                                            case "trunk":
                                                defaultSpeed = 80;
                                                break;
                                            case "living_street":
                                                defaultSpeed = 30;
                                                break;
                                            default:
                                                defaultSpeed = 50;
                                                break;
                                        }

                                        long[] nodes = wayHolder.getNodes();
                                        currentStreet = new Street(tagList, defaultSpeed);

                                        for (int j = 1; j < nodes.length; j++){
                                            Node currentNode = tempNodes.get(nodes[j]);
                                            Edge edge = new Edge(tempNodes.get(nodes[j-1]), currentNode, currentStreet);
                                            graph.addEdge(edge);
                                            roadEdges.add(edge);
                                        }

                                        break; 
                                    }
                                }

                                if(type != Type.COASTLINE) {
                                    if (wayHolder.getSize() > 0) {
                                        if (!drawableByType.containsKey(type))
                                            drawableByType.put(type, new ArrayList<>());
                                        drawableByType.get(type).add(new LinePath(wayHolder, type, tempNodes));
                                    }
                                    
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
                                    wayHolder = Way.merge(before, wayHolder);
                                    wayHolder = Way.merge(wayHolder, after);

                                    tempCoastlines.put(wayHolder.first(), wayHolder);
                                    tempCoastlines.put(wayHolder.last(), wayHolder);
                                }
                                type = Type.UNKNOWN;
                                break;
                            case "relation":
                                relationHolder.collectRelation(tempNodes);
                                if(!drawableByType.containsKey(type)) drawableByType.put(type, new ArrayList<>());
                                if(relationHolder.getWays().size() > 0) drawableByType.get(type).add(new PolyLinePath(relationHolder, type, tempNodes));
                                type = Type.UNKNOWN;
                                break;
                            case "osm":
                                ArrayList<CanvasElement> coastlines = new ArrayList<>();
                                for(Map.Entry<Long,Way> entry : tempCoastlines.entrySet()){
                                    if(entry.getValue().first() == entry.getValue().last()){
                                        coastlines.add(new LinePath(entry.getValue(),Type.COASTLINE,tempNodes));
                                    } else {
                                        fixCoastline(entry.getValue());
                                        coastlines.add(new LinePath(entry.getValue(), Type.COASTLINE,tempNodes));
                                    }
                                }
                                if(coastlines.size() == 0){
                                    Way land = new Way();
                                    land.addNode(-1);
                                    land.addNode(-2);
                                    land.addNode(-3);
                                    land.addNode(-4);
                                    coastlines.add(new LinePath(land, Type.COASTLINE, tempNodes));
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

        for (var entry : graph.getAdj().entrySet()) {
            entry.getValue().trimToSize();
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
                tempNodes.add(-1, bound.getMinLon(), bound.getMinLat()); //TOPLEFT
                tempNodes.add(-2, bound.getMinLon(), bound.getMaxLat()); //BOTTOMLEFT
                tempNodes.add(-3, bound.getMaxLon(), bound.getMaxLat()); //BOTTOMRIGHT
                tempNodes.add(-4, bound.getMaxLon(), bound.getMinLat()); //TOPRIGHT
                break;
            case "node":
                builder = new Address.Builder();
                cityBuilder = new City.Builder();
                currentID = Long.parseLong(reader.getAttributeValue(null, "id"));
                float tempLon = Float.parseFloat(reader.getAttributeValue(null, "lon"));
                float tempLat = Float.parseFloat(reader.getAttributeValue(null, "lat"));
                nodeHolder = MercatorProjector.project(currentID, tempLon, -tempLat);
                builder.lat(nodeHolder.getLat());
                builder.lon(nodeHolder.getLon());
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
                relationHolder = new Relation(currentID);
                //tempRelations.add(relationHolder);
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
                    if(tempNodes.get(ref) != null) wayHolder.addNode(ref);
                    Node node = tempNodes.get(ref);
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
                    cityBuilder.node(tempNodes.get(tempWay.last()));
                }
                break;
            case "relation":
                relationHolder.addRefId(Long.parseLong(reader.getAttributeValue(null, "ref")));
                break;
        }
    }

    private void fixCoastline(Way coastline){
        long[] coastlineNodes;
        Node savedNd;
        Node currentNd;
        for(int i = 1;;){
            coastlineNodes = coastline.getNodes();
            if(coastlineNodes.length <= 1) return;
            currentNd = tempNodes.get(coastlineNodes[i]);
            float lon = currentNd.getLon();
            float lat = currentNd.getLat();
            if(lon <= bound.getMaxLon() && lon >= bound.getMinLon() && lat <= bound.getMaxLat() && lat >= bound.getMinLat()){ //Is inside bound
                break;
            }
            else{
                coastline.remove(tempNodes.get(coastlineNodes[0]).getAsLong());
                savedNd = currentNd;
            }
        }
        savedNd = tempNodes.get(coastline.last());
        int size = coastlineNodes.length;
        for(int i = size-1; i >= 0; i--){
            coastlineNodes = coastline.getNodes();
            currentNd = tempNodes.get(coastlineNodes[i]);
            if(currentNd == null) continue;
            float lon = currentNd.getLon();
            float lat = currentNd.getLat();
            if(lon <= bound.getMaxLon() && lon >= bound.getMinLon() && lat <= bound.getMaxLat() && lat >= bound.getMinLat()){ //Is inside bound
                break;
            }
            else{
                coastline.remove(savedNd.getAsLong());
                savedNd = currentNd;
            }
        }

        Node first = tempNodes.get(coastline.first());
        Node last = tempNodes.get(coastline.last());
        float midLon = (bound.getMaxLon() + bound.getMinLon())/2;
        float midLat = (bound.getMaxLat() + bound.getMinLat())/2;

        boolean fixed = false;

        if((first.getLon() < bound.getMaxLon() && first.getLon() > bound.getMinLon()) && (last.getLon() < bound.getMaxLon() && last.getLon() > bound.getMinLon())) {
            if( (first.getLat() < midLat && last.getLat() < midLat && first.getLon() < last.getLon()) ||
                    (first.getLat() > midLat && last.getLat() > midLat && first.getLon() > last.getLon())) {
                coastline.addNode(last.getAsLong());
                fixed = true;
            }
        }
        else if((first.getLat() < bound.getMaxLat() && first.getLat() > bound.getMinLat()) && (last.getLat() < bound.getMaxLat() && last.getLat() > bound.getMinLat())){
            if( (first.getLon() < midLon && last.getLon() < midLon && first.getLat() > last.getLat()) ||
                    (first.getLon() > midLon && last.getLon() > midLon && first.getLat() < last.getLat())){
                coastline.addNode(last.getAsLong());
                fixed = true;
            }
        }
        if(!fixed){
            Node[] boundNodes = new Node[4];
            boundNodes[0] = new Node(-1, bound.getMinLon(), bound.getMinLat()); //TOPLEFT == -1
            boundNodes[1] = new Node(-2, bound.getMinLon(), bound.getMaxLat()); //BOTTOMLEFT == -2
            boundNodes[2] = new Node(-3, bound.getMaxLon(), bound.getMaxLat()); //BOTTOMRIGHT == -3
            boundNodes[3] = new Node(-4, bound.getMaxLon(), bound.getMinLat()); //TOPRIGHT == -4

            if(first.getLat() <= bound.getMinLat()){ //TOP
                coastline.addNodeToFront(-4L);
            }
            else if(first.getLat() >= bound.getMaxLat()){ //BOTTOM
                coastline.addNodeToFront(-2L);
            }
            else if(first.getLon() <= bound.getMinLon()){ //LEFT
                coastline.addNodeToFront(-1L);
            }
            else if(first.getLon() >= bound.getMaxLon()){ //RIGHT
                coastline.addNodeToFront(-3L);
            }

            long lastNode = 10;
            if(last.getLat() <= bound.getMinLat()){ //TOP
                coastline.addNode(-1L);
                lastNode = -1L;
            }
            else if(last.getLat() >= bound.getMaxLat()){ //BOTTOM
                coastline.addNode(-3L);
                lastNode = -3L;
            }
            else if(last.getLon() <= bound.getMinLon()){ //LEFT
                coastline.addNode(-2L);
                lastNode = -2L;
            }
            else if(last.getLon() >= bound.getMaxLon()){ //RIGHT
                coastline.addNode(-4L);
                lastNode = -4L;
            }

            if(lastNode != 10){
                for(long i = lastNode; coastline.first() != coastline.last(); i--) {
                    coastline.addNode(i);
                    if (i == -4L) i = 0;
                }
            }
            coastline.trim();
        }
    }

    public void destroy(){
        tempNodes = null;
        tempWays = null;
        //tempRelations = null;
        tempCoastlines = null;
        drawableByType = null;
    }
}