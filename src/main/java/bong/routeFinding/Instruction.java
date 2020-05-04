package bong.routeFinding;

import bong.OSMReader.Node;
import bong.canvas.RouteInstructionIndicator;

public class Instruction {
    private String instruction;
    private Node node;
    private RouteInstructionIndicator indicator;

    public Instruction(String instruction, Node node) {
        this.instruction = instruction;
        this.node = node;
        this.indicator = new RouteInstructionIndicator(node.getLon(), node.getLat(), 1);
    }

    public RouteInstructionIndicator getIndicator() {
        return indicator;
    }

    public String getInstruction() {
        return instruction;
    }

    public Node getNode() {
        return node;
    }
}
