package net.loveruby.cflat.type;

import net.loveruby.cflat.ast.Location;

/**
 * @author 刘科 2018/5/29
 */
public class UnionTypeRef extends TypeRef {

    protected String name;

    public UnionTypeRef(String name){
        this(null, name);
    }

    public UnionTypeRef(Location location, String name) {
        super(location);
        this.name = name;
    }

    public boolean isUnion(){
        return true;
    }

    public String name() {
        return name;
    }

    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof UnionTypeRef)){
            return false;
        }
        return name.equals(((UnionTypeRef) obj).name);
    }

    @Override
    public String toString() {
        return "union " + name;
    }
}
