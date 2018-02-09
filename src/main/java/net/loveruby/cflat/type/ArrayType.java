package net.loveruby.cflat.type;

/**
 * Created by Administrator on 2018/2/9.
 */
public class ArrayType extends Type {
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
