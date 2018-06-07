package net.loveruby.cflat.type;

/**
 * @author 刘科 2018/5/29
 */
public class IntegerType extends Type {

    protected long size;
    // 是否无符号数
    protected boolean isSigned;
    protected String name;

    public IntegerType(long size, boolean isSigned, String name) {
        this.size = size;
        this.isSigned = isSigned;
        this.name = name;
    }

    @Override
    public boolean isInteger() {
        return true;
    }

    public boolean isScalar(){
        return true;
    }

    @Override
    public boolean isSigned() {
        return isSigned;
    }

    public long minValue(){
        return isSigned ? ((long)-Math.pow(2, size * 8 - 1)) : 0;
    }

    public long maxValue(){
        return isSigned ? ((long)Math.pow(2, size * 8 - 1) - 1) : ((long)Math.pow(2, size * 8) - 1);
    }

    // 检查 参数i 是否在 当前类型可表示的整数的范围内
    public boolean isInDomain(long i){
        return (minValue() <= i) && (maxValue() >= i);
    }

    public long size() {
        return size;
    }

    @Override
    public String toString() {
        return name;
    }

    // 不重写equals方法
    // 因为同一种 IntegerType 只会生成一个
    public boolean isSameType(Type other) {
        if(!other.isInteger()){
            return false;
        }
        return equals(other.getIntegerType());
    }

    public boolean isCompatible(Type other) {
        return (other.isInteger()) && (size < other.size());
    }

    public boolean isCastableTo(Type target) {
        return (target.isInteger()) || (target.isPointer());
    }
}
