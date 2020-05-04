package bong.OSMReader;

//https://wiki.openstreetmap.org/wiki/Mercator#Java

public class MercatorProjector {
    final private static double R_MAJOR = 6378137.0;

    public static Node project(double x, double y) {
        return new Node(
                0,
                (float) projectX(x),
                (float) projectY(y)
        );
    }

    public static Node project(long id, double x, double y){
        return new Node(
                id,
                (float) projectX(x),
                (float) projectY(y)
        );
    }

    private static double projectX(double lon) {
        return Math.toRadians(lon) * R_MAJOR;
    }

    private static double projectY(double lat) {
        return Math.log(Math.tan(Math.PI / 4 + Math.toRadians(lat) / 2)) * R_MAJOR;
    }

    public static Node unproject(double x, double y) {
        return new Node(
                0,
                (float) unprojectX(x),
                (float) unprojectY(y)
        );
    }

    public static Node unproject(long id, double x, double y){
        return new Node(
                id,
                (float) unprojectX(x),
                (float) unprojectY(y)
        );
    }

    private static double unprojectX(double lon) {
        return Math.toDegrees(lon / R_MAJOR);
    }

    private static double unprojectY(double lat) {
        return Math.toDegrees(Math.atan(Math.exp(lat / R_MAJOR)) * 2 - Math.PI/2);
    }
    
}
