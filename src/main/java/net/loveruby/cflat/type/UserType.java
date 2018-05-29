package net.loveruby.cflat.type;

import net.loveruby.cflat.ast.Location;
import net.loveruby.cflat.ast.TypeNode;

/**
 * @author 刘科 2018/5/29
 */
public class UserType extends NamedType {

    protected TypeNode real;

    public UserType(String name, TypeNode real, Location location){
        super(name, location);
        this.real = real;
    }

    public Type realType(){
        return real.type();
    }

    @Override
    public String toString() {
        return name;
    }

    // 重写 Type 中的方法，重定向到 真正的 类型 中去

    @Override
    public long size() {
        return realType().size();
    }

    @Override
    public long allocSize() {
        return realType().allocSize();
    }

    @Override
    public long alignment() {
        return realType().alignment();
    }

    @Override
    public boolean isVoid() {
        return realType().isVoid();
    }

    @Override
    public boolean isInt() {
        return realType().isInt();
    }

    @Override
    public boolean isInteger() {
        return realType().isInteger();
    }

    @Override
    public boolean isSigned() {
        return realType().isSigned();
    }

    @Override
    public boolean isPointer() {
        return realType().isPointer();
    }

    @Override
    public boolean isArray() {
        return realType().isArray();
    }

    @Override
    public boolean isCompositeType() {
        return realType().isCompositeType();
    }

    @Override
    public boolean isStruct() {
        return realType().isStruct();
    }

    @Override
    public boolean isUnion() {
        return realType().isUnion();
    }

    @Override
    public boolean isUserType() {
        return realType().isUserType();
    }

    @Override
    public boolean isFunction() {
        return realType().isFunction();
    }

    @Override
    public boolean isAllocateArray() {
        return realType().isAllocateArray();
    }

    @Override
    public boolean isIncompleteArray() {
        return realType().isIncompleteArray();
    }

    @Override
    public boolean isScalar() {
        return realType().isScalar();
    }

    @Override
    public boolean isCallable() {
        return realType().isCallable();
    }

    @Override
    public Type baseType() {
        return realType().baseType();
    }

    @Override
    public IntegerType getIntegerType() {
        return realType().getIntegerType();
    }

    @Override
    public PointerType getPointerType() {
        return realType().getPointerType();
    }

    @Override
    public FunctionType getFunctionType() {
        return realType().getFunctionType();
    }

    @Override
    public StructType getStructType() {
        return realType().getStructType();
    }

    @Override
    public UnionType getUnionType() {
        return realType().getUnionType();
    }

    @Override
    public CompositeType getCompositeType() {
        return realType().getCompositeType();
    }

    @Override
    public ArrayType getArrayType() {
        return realType().getArrayType();
    }

    public boolean isSameType(Type other) {
        return realType().isSameType(other);
    }

    public boolean isCompatible(Type other) {
        return realType().isCompatible(other);
    }

    public boolean isCastableTo(Type target) {
        return realType().isCastableTo(target);
    }
}
