package net.loveruby.cflat.type;

/**
 * Created by Administrator on 2018/2/9.
 */
public class PointerType extends Type {

    protected long size;
    protected Type baseType;

    public long size() {
        return size;
    }

    public Type baseType(){
        return baseType;
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
