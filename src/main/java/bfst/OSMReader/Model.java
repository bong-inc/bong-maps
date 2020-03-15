package bfst.OSMReader;

import bfst.addressparser.Address;
import bfst.canvas.Drawable;
import bfst.canvas.Type;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;

public class Model implements Serializable {
    private static final long serialVerionUID = 101010101010100l;

    private Map<Type, ArrayList<Drawable>> drawablesByType;
    private ArrayList<Address> addresses;
    private Bound bound;

    public Model(OSMReader reader){
        this.drawablesByType = reader.getDrawableByType();
        this.bound = reader.getBound();
        this.addresses = reader.getAddresses();
        Collections.sort(addresses);
    }

    public ArrayList<Drawable> getDrawablesOfType(Type type){
        return drawablesByType.get(type);
    }

    public ArrayList<Address> getAddresses(){
        return addresses;
    }

    public Bound getBound(){
        return bound;
    }
}
