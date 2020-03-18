package bfst.OSMReader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.function.LongSupplier;

public class Relation implements LongSupplier {
    private long id;
    private ArrayList<Node> nodes;
    private ArrayList<Way> ways;
    private ArrayList<Long> ids;
    private ArrayList<Relation> relations;
    private ArrayList<Way> outer;
    private ArrayList<Way> inner;

    public Relation(){
        nodes = new ArrayList<>();
        ways = new ArrayList<>();
        relations = new ArrayList<>();
        ids = new ArrayList<>();
        inner = new ArrayList<>();
        outer = new ArrayList<>();
    }

    public Relation(long id){
        this.id = id;
        nodes = new ArrayList<>();
        ways = new ArrayList<>();
        relations = new ArrayList<>();
        ids = new ArrayList<>();
        inner = new ArrayList<>();
        outer = new ArrayList<>();
    }

    public void addToOuter(Way way) {
        outer.add(way);
    }

    public void addToInner(Way way){
        inner.add(way);
    }

    public void addNode(Node node){ nodes.add(node); }
    public void addWay(Way way){ ways.add(way);}
    public void addRefId(Long id){ ids.add(id); }
    public void addRelation(Relation relation){ relations.add(relation); }

    public ArrayList<Way> getWays(){ return ways; }
    public ArrayList<Long> getIds(){ return ids; }

    public void nullifyIds(){
        ids = null;
    }

    public void collectRelation(){
        int counter = 0;
        int amtOuter =  outer.size();
        ArrayList<Way> tempOuters = new ArrayList<>();
        ArrayList<Way> resOuters = new ArrayList<>();
        while(counter < amtOuter){
            Way resOuter = null;
            for(int i = counter; i < amtOuter; i++){
                try{
                    resOuter = Way.merge(resOuter, outer.get(i));
                    counter++;
                } catch (IllegalArgumentException e) {
                    continue;
                }
            }
            if(resOuter != null) {
                if(resOuter.last() == resOuter.first()){
                    resOuters.add(resOuter);
                }
                else{
                    tempOuters.add(resOuter);
                }
            }
        }

        HashMap<Node, Way> collector = new HashMap<>();
        for(Way way : tempOuters){
            Way before = collector.remove(way.first());
            if (before != null) {
                collector.remove(before.first());
                collector.remove(before.last());
            }
            Way after = collector.remove(way.last());
            if (after != null) {
                collector.remove(after.first());
                collector.remove(after.last());
            }
            way = Way.merge(Way.merge(before, way), after);
            collector.put(way.first(), way);
            collector.put(way.last(), way);
        }

        for(Map.Entry<Node, Way> entry : collector.entrySet()){
            if(entry.getValue().first() == entry.getValue().last()){
                resOuters.add(entry.getValue());
            }
            else{
                tempOuters.add(entry.getValue());
            }
        }

        ways.addAll(resOuters);
        ways.addAll(tempOuters);
        ways.addAll(inner);
    }

    @Override
    public long getAsLong() {
        return id;
    }
}