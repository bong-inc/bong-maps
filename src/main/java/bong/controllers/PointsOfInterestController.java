package bong.controllers;

import bong.canvas.PointOfInterest;
import javafx.geometry.Point2D;
import javafx.scene.control.Alert;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.Alert.AlertType;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Optional;

public class PointsOfInterestController {

    private static ArrayList<PointOfInterest> pointsOfInterest;

    public PointsOfInterestController() {
        pointsOfInterest = new ArrayList<>();
    }

    public void addToPOI(PointOfInterest poi) {
        pointsOfInterest.add(poi);
    }

    public void setPOI(ArrayList<PointOfInterest> poi) {
        pointsOfInterest = poi;
    }

    public static ArrayList<PointOfInterest> getPointsOfInterest() {
        return pointsOfInterest;
    }

    public void removePOI(float x, float y) {
        for (PointOfInterest poi : pointsOfInterest) {
            if (poi.getLon() ==  x && poi.getLat() == y ) {
                pointsOfInterest.remove(poi);
                break;
            }
        }
        savePointsOfInterest();
    }

    public boolean POIContains(float x, float y) {
        for (PointOfInterest poi : pointsOfInterest) {
            if (poi.getLon() ==  x && poi.getLat() == y ) {
                return true;
            }
        }
        return false;
    }

    public void savePointsOfInterest() {
        String destFolder = System.getProperty("user.home") + File.separator + "Documents" + File.separator + "POI.bin";
        File file = new File(destFolder);
        try {
            FileController.saveBinary(file, PointsOfInterestController.getPointsOfInterest());
        } catch (IOException e) {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setHeaderText("Unable to save point of interest");
            alert.setContentText("Please try again");
            alert.showAndWait();
        }
    }

    public void showAddPointDialog(Point2D point) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Point of interest");
        dialog.setContentText("Save point of interest");
        dialog.setHeaderText("Enter the name of the point");
        dialog.setContentText("Name:");
        Optional<String> givenName = dialog.showAndWait();
        addPointOfInterest(point, givenName);
    }

    public void addPointOfInterest(Point2D point, Optional<String> givenName) {
        if (givenName.isPresent()) {
            PointOfInterest poi = new PointOfInterest((float) point.getX(), (float) point.getY(), givenName.get());
            addToPOI(poi);
        }
        savePointsOfInterest();
    }

    public void loadPointsOfInterest() {
        ArrayList<PointOfInterest> list = new ArrayList<>();
        try {
            InputStream is = new FileInputStream(System.getProperty("user.home") + File.separator + "Documents" + File.separator + "POI.bin");
            list = (ArrayList<PointOfInterest>) FileController.loadBinary(is);
        } catch (Exception ignored){

        }
        setPOI(list);
    }
}
