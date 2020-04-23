package bfst.routeFinding;

import bfst.OSMReader.Node;
import bfst.canvas.RouteInstructionIndicator;

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
