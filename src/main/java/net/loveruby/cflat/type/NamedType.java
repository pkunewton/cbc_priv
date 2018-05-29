package net.loveruby.cflat.type;

import net.loveruby.cflat.ast.Location;

/**
 * @author 刘科 2018/5/29
 */
abstract public class NamedType extends Type{

    protected String name;
    protected Location location;

    public NamedType(String name, Location location) {
        this.name = name;
        this.location = location;
    }

    public String name() {
        return name;
    }

    public Location location() {
        return location;
    }
}
