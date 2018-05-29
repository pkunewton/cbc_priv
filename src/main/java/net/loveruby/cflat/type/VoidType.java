package net.loveruby.cflat.type;

/**
 * @author 刘科 2018/5/29
 */
public class VoidType extends Type {

    public VoidType(){}

    @Override
    public boolean isVoid() {
        return true;
    }

    public long size() {
        return 1;
    }

    @Override
    public boolean equals(Object obj) {
        return (obj instanceof VoidType);
    }

    public boolean isSameType(Type other) {
        return other.isVoid();
    }

    public boolean isCompatible(Type other) {
        return other.isVoid();
    }

    public boolean isCastableTo(Type target) {
        return target.isVoid();
    }

    @Override
    public String toString() {
        return "void";
    }
}
