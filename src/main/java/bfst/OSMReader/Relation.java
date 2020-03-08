package bfst.OSMReader;

import java.util.ArrayList;
import java.util.function.LongSupplier;

public class Relation implements LongSupplier {
    private long id;
    private ArrayList<Node> nodes;
    private ArrayList<Way> ways;
    private ArrayList<Long> ids;
    private ArrayList<Relation> relations;

    public Relation(){
        nodes = new ArrayList<>();
        ways = new ArrayList<>();
        relations = new ArrayList<>();
        ids = new ArrayList<>();
    }

    public Relation(long id){
        this.id = id;
        nodes = new ArrayList<>();
        ways = new ArrayList<>();
        relations = new ArrayList<>();
        ids = new ArrayList<>();
    }

    public void addNode(Node node){ nodes.add(node); }
    public void addWay(Way way){ ways.add(way);}
    public void addRefId(Long id){ ids.add(id); }
    public void addRelation(Relation relation){ relations.add(relation); }

    public ArrayList<Way> getWays(){ return ways; }
    public ArrayList<Long> getIds(){return ids;}

    public void nullifyIds(){
        ids = null;
    }

    @Override
    public long getAsLong() {
        return id;
    }
}