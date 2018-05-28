package net.loveruby.cflat.type;

import net.loveruby.cflat.ast.Location;
import net.loveruby.cflat.ast.TypeNode;

public class UserType extends NamedType {

    protected TypeNode real;

    public UserType(String name, TypeNode real, Location location){

    }

    public long size() {
        return 0;
    }

    public boolean isSameType(Type other) {
        return false;
    }

    public boolean isCompatible(Type other) {
        return false;
    }

    public boolean isCastableTo(Type target) {
        return false;
    }
}
