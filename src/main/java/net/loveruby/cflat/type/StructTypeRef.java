package net.loveruby.cflat.type;

import net.loveruby.cflat.ast.Location;

/**
 * @author 刘科 2018/5/29
 */
public class StructTypeRef extends TypeRef {

    protected String name;

    public StructTypeRef(String name){
        this(null, name);
    }

    public StructTypeRef(Location location, String name) {
        super(location);
        this.name = name;
    }

    public boolean isStruct(){
        return true;
    }

    public String name() {
        return name;
    }

    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof StructTypeRef)){
            return false;
        }
        return name.equals(((StructTypeRef) obj).name);
    }

    @Override
    public String toString() {
        return "struct " + name;
    }
}
