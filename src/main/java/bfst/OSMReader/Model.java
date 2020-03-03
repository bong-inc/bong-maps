package bfst.OSMReader;

import bfst.canvas.Drawable;
import bfst.canvas.Type;

import java.util.ArrayList;
import java.util.Map;

public class Model {

    private Map<Type, ArrayList<Drawable>> drawablesByType;
    private Bound bound;

    public Model(OSMReader reader){
        this.drawablesByType = reader.getDrawableByType();
        this.bound = reader.getBound();
    }

    public ArrayList<Drawable> getDrawablesOfType(Type type){
        return drawablesByType.get(type);
    }

    public Bound getBound(){
        return bound;
    }
}
