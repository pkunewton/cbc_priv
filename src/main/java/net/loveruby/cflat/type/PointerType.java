package net.loveruby.cflat.type;

/**
 * @author 刘科 2018/5/29
 */
public class PointerType extends Type {

    protected long size;
    protected Type baseType;

    public PointerType(long size, Type baseType) {
        this.size = size;
        this.baseType = baseType;
    }

    @Override
    public boolean isPointer() {
        return true;
    }

    public boolean isScalar(){
        return true;
    }

    @Override
    public boolean isSigned() {
        return false;
    }

    public boolean isCallable(){
        return baseType.isFunction();
    }

    public long size() {
        return size;
    }

    public Type baseType(){
        return baseType;
    }

    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof PointerType)){
            return false;
        }
        return baseType.equals(((PointerType) obj).getPointerType().baseType);
    }

    public boolean isSameType(Type other) {
        if(!other.isPointer()){
            return false;
        }
        return baseType.isSameType(other.baseType());
    }

    public boolean isCompatible(Type other) {
        if(!other.isPointer()){
            return false;
        }
        if(baseType.isVoid()){
            return true;
        }
        if(other.baseType().isVoid()){
            return true;
        }
        return baseType.isCompatible(other.baseType());
    }

    public boolean isCastableTo(Type target) {
        return target.isPointer() || target.isInteger();
    }

    @Override
    public String toString() {
        return baseType.toString() + "*";
    }
}
