package bfst.controllers;

import bfst.OSMReader.MercatorProjector;
import bfst.OSMReader.Node;
import bfst.canvas.LinePath;
import bfst.canvas.MapCanvas;
import bfst.routeFinding.Dijkstra;
import bfst.routeFinding.Edge;
import bfst.routeFinding.Instruction;
import bfst.routeFinding.Street;
import javafx.geometry.Point2D;
import java.math.BigDecimal;
import java.math.MathContext;
import java.util.ArrayList;
import java.util.Collections;

public class RouteController {

    private MapCanvas canvas;

    public RouteController(MapCanvas canvas) {
        this.canvas = canvas;

    }

    private ArrayList<Edge> route;
    private Dijkstra dijkstra;
    private LinePath drawableRoute;
    private double routeTime;
    private double routeDistance;
    private int roundaboutCounter = 0;
    private Node lastInstructionNode;
    private String lastActionInstruction;
    private ArrayList<Instruction> instructions = new ArrayList<>();

    public Iterable<Edge> getRoute() {
        return route;
    }

    public void setLastInstructionNode(Node node) {
        lastInstructionNode = node;
    }

    public Dijkstra getDijkstra() {
        return dijkstra;
    }

    public LinePath getDrawableRoute() {
        return drawableRoute;
    }

    public void setRouteTime(double newTime) {
        routeTime = newTime;
    }

    public void setRouteDistance(double newDistance) {
        routeDistance = newDistance;
    }

    public ArrayList<Instruction> getInstructions() {
        return instructions;
    }

    public String getLastActionInstruction() {
        return lastActionInstruction;
    }

    public void clearRoute() {
        route = null;
        instructions = null;
        dijkstra = null;
        drawableRoute = null;
        canvas.repaint(2);
    }

    public double calculateTurn(Edge prevEdge, Edge currEdge) {
        Point2D prevVector = new Point2D(prevEdge.getHeadNode().getLon() - prevEdge.getTailNode().getLon(), prevEdge.getHeadNode().getLat() - prevEdge.getTailNode().getLat());
        Point2D currVector = new Point2D(currEdge.getHeadNode().getLon() - currEdge.getTailNode().getLon(), currEdge.getHeadNode().getLat() - currEdge.getTailNode().getLat());

        double prevDirection = Math.atan2(prevVector.getY(), prevVector.getX());
        double currDirection = Math.atan2(currVector.getY(), currVector.getX());
        double turn = currDirection - prevDirection;
        if (turn > Math.PI) {
            turn = - (turn - Math.PI);
        } else if (turn < - Math.PI) {
            turn = - (turn + Math.PI);
        }

        turn *= 180 / Math.PI;
        return turn;
    }

    public void resetRoundaboutCounter() {
        roundaboutCounter = 0;
    }

    public void setActionInstruction(Edge prevEdge, Edge currEdge, int roundaboutCounter) {
        double turn = calculateTurn(prevEdge, currEdge);
        if (currEdge.getStreet().getRole() == Street.Role.MOTORWAY && prevEdge.getStreet().getRole() == Street.Role.MOTORWAY_LINK) {
            lastActionInstruction = "Take the ramp onto the motorway";
        } else if (currEdge.getStreet().getRole() != Street.Role.MOTORWAY_LINK && currEdge.getStreet().getRole() != Street.Role.MOTORWAY && prevEdge.getStreet().getRole() == Street.Role.MOTORWAY_LINK) {
            lastActionInstruction = "Take the off-ramp";
        } else if (roundaboutCounter > 0) {
            lastActionInstruction = "Take exit number " + roundaboutCounter + " in the roundabout";
            resetRoundaboutCounter();
        } else if (turn > 20 && turn < 160 && currEdge.getStreet().getRole() != Street.Role.ROUNDABOUT) {
            lastActionInstruction = "Turn right";
        } else if (turn < -20 && turn > -160 && currEdge.getStreet().getRole() != Street.Role.ROUNDABOUT) {
            lastActionInstruction = "Turn left";
        }
    }

    public void addInstruction(String prevEdgeName, double tempLength, Edge currEdge) {
        String instruction = "Follow ";
        if (lastActionInstruction != null) {
            instruction = lastActionInstruction + " and follow ";
        }

        BigDecimal bd = new BigDecimal(tempLength);
        bd = bd.round(new MathContext(2));
        int roundedLength = bd.intValue();
        if (roundedLength >= 10000) {
            instruction += prevEdgeName + " for " + roundedLength / 1000 + " km";
        } else if (roundedLength > 1000) {
            instruction += prevEdgeName + " for " + (double) roundedLength / 1000 + " km";
        } else {
            instruction += prevEdgeName + " for " + roundedLength + " m";
        }
        instructions.add(new Instruction(instruction, lastInstructionNode));
        lastActionInstruction = null;
        lastInstructionNode = currEdge.getTailNode();
    }

    public void addTimeToTotal(String vehicle, Edge currEdge, double distance) {
        switch (vehicle) {
            case "Car":
                routeTime += distance / (currEdge.getStreet().getMaxspeed() / 3.6);
                break;
            case "Walk":
                routeTime += distance / 1.1; //estimate for walking speed, 1.1 m/s.
                break;
            case "Bicycle":
                routeTime += distance / 6; //6 m/s biking speed estimate.
                break;
        }
    }

    public String timeString() {
        String timeString;
        int hourCount = 0;
        double timeInMinutes = routeTime / 60;

        while (timeInMinutes >= 60) {
            hourCount++;
            timeInMinutes -= 60;
        }

        if (hourCount > 0) {
            timeString = hourCount + " h " + (int) timeInMinutes + " min";
        } else {
            timeString = (int) timeInMinutes + " min";
        }
        return timeString;
    }

    public String distanceString() {
        BigDecimal bd = new BigDecimal(routeDistance);
        bd = bd.round(new MathContext(3));
        int roundedDistance = bd.intValue();
        String distanceString;

        if (routeDistance >= 100000) {
            distanceString = roundedDistance / 1000 + " km";
        } else if (routeDistance >= 1000) {
            distanceString = (double) roundedDistance / 1000 + " km";
        } else {
            distanceString = roundedDistance + " m";
        }
        return distanceString;
    }

    public void generateRouteInfo(ArrayList<Edge> list, String vehicle) {

        instructions = new ArrayList<>();
        routeDistance = 0;
        routeTime = 0;

        Edge first = list.get(0);
        String prevEdgeName = first.getStreet().getName();
        double tempLength = 0;
        Edge prevEdge = first;
        lastInstructionNode = route.get(0).getTailNode();
        for (int i = 0; i < list.size(); i++) {
            Edge currEdge = list.get(i);
            double meterMultiplier = - (MercatorProjector.unproject(currEdge.getTailNode().getLon(), currEdge.getTailNode().getLat()).getLat()) / 100;

            if (currEdge.getStreet().getRole() == Street.Role.ROUNDABOUT && canvas.getModel().getGraph().getOutDegree(currEdge.getHeadNode().getAsLong(), vehicle) > 1) {
                roundaboutCounter++;
            }

            if (prevEdgeName == null) {
                prevEdgeName = "road";
            }
            String currEdgeName = currEdge.getStreet().getName();

            if (currEdgeName == null) {
                currEdgeName = "road";
            }

            if ((!prevEdgeName.equals(currEdgeName) && currEdge.getStreet().getRole() != Street.Role.ROUNDABOUT) || (currEdge.getStreet().getRole() != Street.Role.ROUNDABOUT && prevEdge.getStreet().getRole() == Street.Role.ROUNDABOUT)) {
                addInstruction(prevEdgeName, tempLength, currEdge);
                setActionInstruction(prevEdge, currEdge, roundaboutCounter);

                if (i == list.size() - 1) {
                    addInstruction(currEdgeName, currEdge.getWeight(), currEdge);
                }

                tempLength = currEdge.getWeight() * meterMultiplier;
            } else {
                tempLength += currEdge.getWeight() * meterMultiplier;
                if (i == list.size() - 1) {
                    addInstruction(prevEdgeName, tempLength, currEdge);
                }
            }

            prevEdgeName = currEdgeName;
            prevEdge = list.get(i);

            double distance = currEdge.getWeight() * 0.56;
            routeDistance += distance;
            addTimeToTotal(vehicle, currEdge, distance);
        }
        instructions.add(new Instruction("You have arrived at your destination", route.get(route.size() - 1).getHeadNode()));
    }

    public ArrayList<Edge> singleDirectRoute(ArrayList<Edge> route) {
        ArrayList<Edge> singleDirectedRoute = new ArrayList<>();

        Edge firstEdge = route.get(0);
        Node prevNode;

        if (firstEdge.getTailNode().getAsLong() == route.get(1).getHeadNode().getAsLong() || firstEdge.getTailNode().getAsLong() == route.get(1).getTailNode().getAsLong()) {
            prevNode = firstEdge.getHeadNode();
        } else {
            prevNode = firstEdge.getTailNode();
        }

        for (Edge edge : route) {
            Node otherNode = edge.otherNode(prevNode.getAsLong());
            Edge newEdge = new Edge(prevNode, otherNode, edge.getStreet());
            singleDirectedRoute.add(newEdge);
            prevNode = otherNode;
        }
        return  singleDirectedRoute;
    }

    public void setRoute() {
        route = dijkstra.pathTo(dijkstra.getLastNode(), 1);
        lastInstructionNode = route.get(0).getTailNode();
        ArrayList<Edge> secondPart = dijkstra.pathTo(dijkstra.getLastNode(), 2);
        Collections.reverse(secondPart);
        route.addAll(secondPart);
        route = singleDirectRoute(route);

        float[] floats = new float[route.size() * 2 + 2];

        Edge firstEdge = route.get(0);

        floats[0] = firstEdge.getTailNode().getLon();
        floats[1] = firstEdge.getTailNode().getLat();

        for (int i = 2; i < route.size() * 2 + 2; i += 2) {
            Node currentNode = route.get((i - 2) / 2).getHeadNode();
            floats[i] = currentNode.getLon();
            floats[i + 1] = currentNode.getLat();
        }

        drawableRoute = new LinePath(floats);

        canvas.repaint(16);
    }

    public void setDijkstra(long startPoint, long endPoint, String vehicle, boolean shortestRoute) throws Exception{
        long time = -System.nanoTime();
        dijkstra = new Dijkstra(canvas.getModel().getGraph(), startPoint, endPoint, vehicle, shortestRoute);
        time += System.nanoTime();
        System.out.println("Set dijkstra: " + time / 1000000f + "ms");

        setRoute();
        generateRouteInfo(route, vehicle);
        canvas.repaint(40);
    }

    public double getRouteTime() {
        return routeTime;
    }
}
