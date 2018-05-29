package net.loveruby.cflat.type;

import net.loveruby.cflat.ast.Location;
import net.loveruby.cflat.ast.Slot;
import net.loveruby.cflat.utils.AsmUtils;

import java.util.List;

/**
 * @author 刘科 2018/5/29
 */
public class UnionType extends CompositeType {
    public UnionType(String name, List<Slot> members, Location location) {
        super(name, members, location);
    }

    @Override
    public boolean isUnion() {
        return true;
    }

    public boolean isSameType(Type other){
        if(!other.isUnion()){
            return false;
        }
        return equals(other.getUnionType());
    }

    protected void computeOffset() {
        long maxSize = 0;
        long maxAlign = 1;
        for (Slot slot : members()){
            slot.setOffset(0);
            maxSize = Math.max(maxSize, slot.allocSize());
            maxAlign = Math.max(maxAlign, slot.alignment());
        }
        cacheSize = AsmUtils.align(maxSize, maxAlign);
        cacheAlign = maxAlign;
    }

    @Override
    public String toString() {
        return "union " + name;
    }
}
