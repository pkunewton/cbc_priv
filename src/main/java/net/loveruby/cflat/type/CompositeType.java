package net.loveruby.cflat.type;

import net.loveruby.cflat.ast.Location;
import net.loveruby.cflat.ast.Slot;

import java.util.List;

/**
 * @author 刘科 2018/5/28
 */
public class CompositeType extends Type {

    protected List<Slot> members;

    public CompositeType(String name, List<Slot> members, Location location){
        super();
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

    public Type memberType(String name){
        return null;
    }

    public long memberOffset(String name){
        return 100l;
    }
}
