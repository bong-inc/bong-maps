package bfst.OSMReader;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.InputStream;
import java.util.HashMap;

import static javax.xml.stream.XMLStreamConstants.*;

public class OSMReader {

    private SortedArrayList<Node> tempNodes = new SortedArrayList<>();
    private SortedArrayList<Way> tempWays = new SortedArrayList<>();
    private SortedArrayList<Relation> tempRelations = new SortedArrayList<>();
    private HashMap<Node, Way> tempCoastlines = new HashMap<>();
    private Bound bound;

    private Node nodeHolder;
    private Way wayHolder;
    private Relation relationHolder;

    private long currentID;

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
                        String qName = reader.getLocalName();
                        switch (qName) {
                            case "bounds":
                                float tempMaxLat = Float.parseFloat(reader.getAttributeValue(null, "maxlat"));
                                float tempMinLat = Float.parseFloat(reader.getAttributeValue(null, "minlat"));
                                float tempMinLon = Float.parseFloat(reader.getAttributeValue(null, "minlon"));
                                float tempMaxLon = Float.parseFloat(reader.getAttributeValue(null, "maxlon"));
                                bound = new Bound(
                                        -tempMaxLat,
                                        -tempMinLat,
                                        (float) Math.cos(tempMinLat * Math.PI / 180) * tempMinLon,
                                        (float) Math.cos(tempMaxLat * Math.PI / 180) * tempMaxLon
                                );
                                break;
                            case "node":
                                currentID = Long.parseLong(reader.getAttributeValue(null, "id"));
                                float tempLon = Float.parseFloat(reader.getAttributeValue(null, "lon"));
                                float tempLat = Float.parseFloat(reader.getAttributeValue(null, "lat"));
                                nodeHolder = new Node(currentID, (float) Math.cos(tempLat * Math.PI / 180) * tempLon, -tempLat);
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
                                break;
                            case "nd":
                                Long ref = Long.parseLong(reader.getAttributeValue(null, "ref"));
                                if(wayHolder != null){
                                    if(tempNodes.get(ref) != null) wayHolder.addNode(tempNodes.get(ref));
                                }
                                break;
                            case "member":
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
                                break;
                        }
                        break;
                    case END_ELEMENT:
                        qName = reader.getLocalName();
                        switch (qName) {
                            case "way":
                                break;
                            case "relation":
                                break;
                            case "osm":
                                break;
                        }
                        break;
                }
            }
        } catch (XMLStreamException e) {
            e.printStackTrace();
        }
    }
}

//TODO load addresses