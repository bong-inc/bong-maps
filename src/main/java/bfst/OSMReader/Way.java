package bfst.OSMReader;

import java.io.Serializable;
import java.util.function.LongSupplier;

public class Way implements LongSupplier, Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private long id;
    private long[] nodes;

    private int fill;

    public Way(){
        nodes = new long[10];
        fill = 0;
    }

    public Way(long id){
        this.id = id;
        nodes = new long[10];

        fill = 0;
    }


    public void addNode(long node){
        if(fill == nodes.length){
            extend(false);
        }
        nodes[fill] = node;
        fill++;
    }
    public void addNodeToFront(long node){
        if(fill == nodes.length){
            extend(true);
        }
        nodes[0] = node;
        fill++;
    }
    public void addAll(long[] nodes){
        for(long node : nodes){
            addNode(node);
        }
    }

    public void removeFront(){
        fill--;
    }

    public void remove(long id){
        int index = -1;
        for(int i = 0; i < fill; i++){
            if(nodes[i] == id){
                index = i;
                break;
            }
        }
        if(index == -1) return;
        long[] cpyNodes = nodes;
        nodes = new long[fill-1];
        for(int i = 0; i < index; i++){
            nodes[i] = cpyNodes[i];
        }
        for(int i = index+1; i < fill; i++){
            nodes[i-1] = cpyNodes[i];
        }
        fill--;
    }

    public long[] getNodes(){
        return nodes;
    }

    public int getSize(){
        return fill;
    }

    public void reverse(){
        long[] cpyNodes = nodes;
        nodes = new long[fill];
        for(int i = 0; i < fill; i++){
            nodes[(fill-1)-i] = cpyNodes[i];
        }
    }

    public void trim(){
        long[] cpyNodes = nodes;
        nodes = new long[fill];
        for(int i = 0; i < fill; i++){
            nodes[i] = cpyNodes[i];
        }
    }

    public long first(){
        return nodes[0];
    }
    public long last(){
        return nodes[fill-1];
    }

    public static Way merge(Way before, Way after){
        if (before == null){
            return after;
        }
        if (after == null){
            return before;
        }
        Way result = new Way();
        if (before.first() == after.first()) {
            result.addAll(before.getNodes());
            result.reverse();
            result.removeFront();
            result.addAll(after.getNodes());
        } else if (before.first() == after.last()) {
            result.addAll(after.getNodes());
            result.removeFront();
            result.addAll(before.getNodes());
        } else if (before.last() == after.first()) {
            result.addAll(before.getNodes());
            result.removeFront();
            result.addAll(after.getNodes());
        } else if (before.last() == after.last()) {
            Way tmp = after;
            tmp.reverse();
            result.addAll(before.getNodes());
            result.removeFront();
            result.addAll(tmp.getNodes());
        } else {
            throw new IllegalArgumentException("Cannot merge unconnected Ways");
        }
        result.trim();
        return result;
    }

    private void extend(boolean frontFree){
        long[] cpyNodes = nodes;
        nodes = new long[cpyNodes.length * 2];
        if(frontFree){
            for(int i = 0; i < cpyNodes.length; i++){
                nodes[i+1] = cpyNodes[i];
            }
        } else {
            for(int i = 0; i < cpyNodes.length; i++){
                nodes[i] = cpyNodes[i];
            }
        }
    }

    @Override
    public long getAsLong() {
        return id;
    }
}
