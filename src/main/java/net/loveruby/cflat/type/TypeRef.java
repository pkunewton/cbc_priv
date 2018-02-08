package net.loveruby.cflat.type;

import net.loveruby.cflat.ast.Location;

/**
 * @author 刘科 on 2018/2/7.
 */
abstract public class TypeRef {

    protected Location location;

    public TypeRef(Location location){
        this.location = location;
    }

    public Location location() {
        return location;
    }

    @Override
    public int hashCode() {
        return toString().hashCode();
    }
}
