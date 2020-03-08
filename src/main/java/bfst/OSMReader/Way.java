package bfst.OSMReader;

import java.util.ArrayList;
import java.util.Collections;
import java.util.function.LongSupplier;

public class Way implements LongSupplier {
    private long id;
    private ArrayList<Node> nodes;

    public Way(){
        nodes = new ArrayList<>();
    }

    public Way(long id){
        this.id = id;
        nodes = new ArrayList<>();
    }


    public void addNode(Node node){
        nodes.add(node);
    }

    public ArrayList<Node> getNodes(){
        return nodes;
    }

    public Node first(){
        return nodes.get(0);
    }
    public Node last(){
        return nodes.get(nodes.size()-1);
    }

    public static Way merge(Way before, Way after){
        if (before == null) return after;
        if (after == null) return before;
        Way result = new Way();
        if (before.first() == after.first()) {
            result.nodes.addAll(before.nodes);
            Collections.reverse(result.getNodes());
            result.nodes.remove(result.nodes.size() - 1);
            result.nodes.addAll(after.nodes);
        } else if (before.first() == after.last()) {
            result.nodes.addAll(after.nodes);
            result.nodes.remove(result.nodes.size() - 1);
            result.nodes.addAll(before.nodes);
        } else if (before.last() == after.first()) {
            result.nodes.addAll(before.nodes);
            result.nodes.remove(result.nodes.size() - 1);
            result.nodes.addAll(after.nodes);
        } else if (before.last() == after.last()) {
            ArrayList<Node> tmp = new ArrayList<>(after.nodes);
            Collections.reverse(tmp);
            result.nodes.addAll(before.nodes);
            result.nodes.remove(result.nodes.size() - 1);
            result.nodes.addAll(tmp);
        } else {
            throw new IllegalArgumentException("Cannot merge unconnected OSMWays");
        }
        return result;
    }

    @Override
    public long getAsLong() {
        return id;
    }
}
