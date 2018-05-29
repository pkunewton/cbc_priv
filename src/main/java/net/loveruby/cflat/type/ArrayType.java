package net.loveruby.cflat.type;

/**
 * @author 刘科 2018/5/29
 */
public class ArrayType extends Type {

    protected Type baseType;
    protected long length;
    protected long pointerSize;
    static final protected long undefined = -1;

    public ArrayType(Type baseType, long pointerSize) {
        this(baseType, undefined, pointerSize);
    }

    public ArrayType(Type baseType, long length, long pointerSize) {
        this.baseType = baseType;
        this.length = length;
        this.pointerSize = pointerSize;
    }


    @Override
    public boolean isArray() {
        return true;
    }

    // 完全分配好内存的数组
    // 如果下层是数组，下层数组也要分配好内存
    @Override
    public boolean isAllocateArray() {
        return (length != undefined) &&
                (!baseType.isArray() || baseType.isAllocateArray());
    }

    // 没有完全分配好内存的数组
    // 这种数组下层元素也是也是数组，上层也分配好内存，但是下层数组未分配好内存
    @Override
    public boolean isIncompleteArray() {
        return (baseType.isArray()) && (!baseType.isAllocateArray());
    }

    @Override
    public Type baseType() {
        return baseType;
    }

    public long length(){
        return length;
    }

    // Value size as pointer
    public long size() {
        return pointerSize;
    }

    // Value size as allocated array
    @Override
    public long allocSize() {
        if(length == undefined){
            return size();
        }else {
            return baseType.allocSize() * length;
        }
    }

    @Override
    public long alignment() {
        return baseType.alignment();
    }

    @Override
    public boolean equals(Object obj) {
        if( !(obj instanceof ArrayType)){
            return false;
        }
        return (length == ((ArrayType) obj).length) &&
                (baseType.equals(((ArrayType) obj).baseType));
    }

    public boolean isSameType(Type other) {
        if(!other.isPointer() && !other.isArray()){
            return false;
        }
        return baseType.isSameType(other.baseType());
    }

    public boolean isCompatible(Type other) {
        if(!other.isPointer() && !other.isArray()){
            return false;
        }
        if(other.baseType().isVoid()){
            return true;
        }
        return (baseType.isCompatible(other.baseType())) &&
                (baseType.size() == other.baseType().size());
    }

    public boolean isCastableTo(Type target) {
        return target.isArray() || target.isPointer();
    }

    @Override
    public String toString() {
        return baseType.toString() + "[" + ((length != undefined) ? length : "") + "]";
    }
}
