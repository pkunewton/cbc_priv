package net.loveruby.cflat.type;

import net.loveruby.cflat.exception.SemanticError;

/**
 * @author 刘科 2018/02/09
 * */
public abstract class Type {

    static final public long sizeUnknown = -1;

    abstract public long size();
    // 分配的存储空间大小
    public long allocSize() { return size(); }
    /**
     * @see net.loveruby.cflat.utils.AsmUtils 根据平台 alignment 校准分配空间
     * 校准对齐后的存储空间大小
     */
    public long alignment() { return allocSize(); }

    abstract public boolean isSameType(Type other);

    // 类型属性
    public boolean isVoid() { return false; }
    public boolean isInt() { return false; }
    public boolean isInteger() { return false; }
    public boolean isSigned() { throw new Error("isSigned for non-integer type"); }
    public boolean isPointer() { return false; }
    public boolean isArray() { return false; }
    public boolean isCompositeType() { return false; }
    public boolean isStuct() { return false; }
    public boolean isUnion() { return false; }
    public boolean isUserType() { return false; }
    public boolean isFunction(){ return false; }

    // Ability methods (unary)
    public boolean isAllocateArry(){ return false; }
    public boolean isIncompleteArray(){ return false; }
    public boolean isScalar(){ return false; }              // 是否是标量，整形和指针
    public boolean isCallable(){ return false; }

    // Ability methods (binary)
    abstract public boolean isCompatible(Type other);       // 是否兼容，相容（用于赋值运算符）
    abstract public boolean isCastableTo(Type target);      // 是否可做类型转换

    public Type baseType(){
        throw new SemanticError("#baseType called for undereferalbe type");
    }

    // cast method
    public IntegerType getIntegerType() { return (IntegerType)this; }
    public PointerType getPointerType() { return (PointerType)this; }
    public FunctionType getFunctionType() { return (FunctionType)this; }
    public StructType getStructType() { return (StructType)this; }
    public UnionType getUnionType() { return (UnionType)this; }
    public CompositeType getCompositeType() { return (CompositeType)this; }
    public ArrayType getArrayType() { return (ArrayType)this; }
}
