package bfst.routeFinding;

import bfst.OSMReader.Node;

public class Instruction {
    private String instruction;
    private Node node;

    public Instruction(String instruction, Node node) {
        this.instruction = instruction;
        this.node = node;
    }

    public String getInstruction() {
        return instruction;
    }

    public Node getNode() {
        return node;
    }
}
