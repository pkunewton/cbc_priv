package net.loveruby.cflat.type;

import net.loveruby.cflat.ast.Location;


/**
 * @author 刘科 2018/5/29
 */
public class VoidTypeRef extends TypeRef {

    public VoidTypeRef() {
        super(null);
    }

    public VoidTypeRef(Location location) {
        super(location);
    }

    public boolean isVoid(){
        return true;
    }

    public boolean equals(Object other){
        return (other instanceof VoidTypeRef);
    }

    @Override
    public String toString() {
        return "void";
    }
}
