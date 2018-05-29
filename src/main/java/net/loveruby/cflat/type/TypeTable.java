package net.loveruby.cflat.type;

import net.loveruby.cflat.ast.Slot;
import net.loveruby.cflat.utils.ErrorHandler;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * @author 刘科 2018/5/29
 */
public class TypeTable {

    // 四种体系结构的数据大小
    static public TypeTable ilp32() {return newTable(1, 2, 4, 4, 4);}
    static public TypeTable ilp64() {return newTable(1, 2, 8, 8, 8);}
    static public TypeTable lp64() {return newTable(1, 2, 4, 8, 8);}
    static public TypeTable llp64() {return newTable(1, 2, 4, 4, 8);}

    static private TypeTable newTable(int charSize, int shortSize, int intSize,
                                      int longSize, int pointerSize){
        TypeTable table = new TypeTable(intSize, longSize, pointerSize);
        table.put(new VoidTypeRef(), new VoidType());
        table.put(IntegerTypeRef.charRef(), new IntegerType(charSize, true, "char"));
        table.put(IntegerTypeRef.shortRef(), new IntegerType(shortSize, true, "short"));
        table.put(IntegerTypeRef.intRef(), new IntegerType(intSize, true, "int"));
        table.put(IntegerTypeRef.longRef(), new IntegerType(longSize, true, "long"));
        table.put(IntegerTypeRef.ucharRef(), new IntegerType(charSize, false, "unsigned char"));
        table.put(IntegerTypeRef.ushortRef(), new IntegerType(shortSize, false, "unsigned short"));
        table.put(IntegerTypeRef.uintRef(), new IntegerType(intSize, false, "unsigned int"));
        table.put(IntegerTypeRef.ulongRef(), new IntegerType(longSize, false, "unsigned long"));
        return table;
    }

    private int intSize;
    private int longSize;
    private int pointerSize;
    private Map<TypeRef, Type> table;

    public TypeTable(int intSize, int longSize, int pointerSize) {
        this.intSize = intSize;
        this.longSize = longSize;
        this.pointerSize = pointerSize;
        this.table = new HashMap<TypeRef, Type>();
    }

    public boolean isDefined(TypeRef ref){
        return table.containsKey(ref);
    }

    public void put(TypeRef ref, Type type){
        if(table.containsKey(ref)){
            throw new Error("duplicated type definition: " + type);
        }
        table.put(ref, type);
    }

    public Type get(TypeRef ref){
        Type type = table.get(ref);
        if(type == null){
            if(ref instanceof UserTypeRef){
                // If unregistered UserType is used in program, it causes
                // parse error instead of semantic error.  So we do not
                // need to handle this error.
                UserTypeRef userTypeRef = (UserTypeRef)ref;
                throw new Error("undefined type: " + userTypeRef.name());
            }else if(ref instanceof PointerTypeRef){
                PointerType pointerType = new PointerType(pointerSize, get(((PointerTypeRef) ref).baseType()));
                table.put(ref, pointerType);
                return pointerType;
            }else if(ref instanceof ArrayTypeRef){
                ArrayTypeRef aref = (ArrayTypeRef)ref;
                ArrayType arrayType = new ArrayType(get(aref.baseType()), aref.length(), pointerSize);
                table.put(ref, arrayType);
                return arrayType;
            }else if(ref instanceof FunctionTypeRef){
                FunctionTypeRef fref = (FunctionTypeRef)ref;
                FunctionType functionType = new FunctionType(get(fref.returnType()),
                        fref.params().internTypes(this));
                table.put(ref, functionType);
                return functionType;
            }
            throw new Error("unregistered type: " + ref.toString());
        }
        return type;
    }

    // array is really a pointer on parameters.
    // 数组在作为参数时本着上是一个指针
    public Type getParamType(TypeRef typeRef){
        Type type = get(typeRef);
        return type.isArray() ? pointerTo(type.baseType()) : type;
    }

    // getter
    public int intSize(){
        return this.intSize;
    }

    public int longSize(){
        return this.longSize;
    }

    public int pointerSize() {
        return this.pointerSize;
    }

    public int maxIntSize(){
        return this.pointerSize;
    }

    // 获取指针大小与那种整数类型相同
    public Type ptrDiffType(){
        return get(ptrDiffTypeRef());
    }
    // returns a IntegerTypeRef whose size is equals to pointer.
    public TypeRef ptrDiffTypeRef(){
        return new IntegerTypeRef(ptrDiffTypeName());
    }
    protected String ptrDiffTypeName(){
        if(signedLong().size() == pointerSize) return "long";
        if(signedInt().size() == pointerSize) return "int";
        if(signedShort().size() == pointerSize) return "short";
        throw new Error("must not happen: integer.size != pointer.size");
    }

    public Type signedStackType(){
        return signedLong();
    }

    public Type unsignedStackType(){
        return unsignedLong();
    }

    public Collection<Type> types(){
        return table.values();
    }

    // 获取各个基础类型
    public VoidType voidType() {
        return (VoidType)table.get(new VoidTypeRef());
    }

    public IntegerType signedChar() {
        return (IntegerType)table.get(IntegerTypeRef.charRef());
    }

    public IntegerType signedShort() {
        return (IntegerType)table.get(IntegerTypeRef.shortRef());
    }

    public IntegerType signedInt() {
        return (IntegerType)table.get(IntegerTypeRef.intRef());
    }

    public IntegerType signedLong() {
        return (IntegerType)table.get(IntegerTypeRef.longRef());
    }

    public IntegerType unsignedChar() {
        return (IntegerType)table.get(IntegerTypeRef.ucharRef());
    }

    public IntegerType unsignedShort() {
        return (IntegerType)table.get(IntegerTypeRef.ushortRef());
    }

    public IntegerType unsignedInt() {
        return (IntegerType)table.get(IntegerTypeRef.uintRef());
    }

    public IntegerType unsignedLong() {
        return (IntegerType)table.get(IntegerTypeRef.ulongRef());
    }


    public PointerType pointerTo(Type type){
        return new PointerType(pointerSize, type);
    }

    // 类型定义检查
    public void semanticCheck(ErrorHandler errorHandler){
        for(Type type: types()){
            // We can safely use "instanceof" instead of isXXXX() here,
            // because the type refered from UserType must be also
            // kept in this table.
            if(type instanceof CompositeType){
                checkVoidMembers(type.getCompositeType(), errorHandler);
                checkDuplicatedMembers(type.getCompositeType(), errorHandler);
            }else if(type instanceof ArrayType){
                checkVoidMembers(type.getArrayType(), errorHandler);
            }
            checkRecursiveDefinition(type, errorHandler);
        }
    }

    // 检查 数组/结构体/联合体 的 void 成员
    protected void checkVoidMembers(ArrayType type, ErrorHandler handler){
        if(type.baseType.isVoid()){
            handler.error("array cannot contain void");
        }
    }

    protected void checkVoidMembers(CompositeType type, ErrorHandler handler){
        for (Slot slot: type.members()){
            if(slot.type().isVoid()){
                handler.error(type.location(), "struct/union cannot contain void");
            }
        }
    }

    // 检查 结构体/联合体 中名字重复的成员
    protected void checkDuplicatedMembers(CompositeType type, ErrorHandler handler){
        Map<String, Slot> map = new HashMap<String, Slot>();
        for (Slot slot: type.members()){
            String name = slot.name();
            if(map.containsKey(name)){
                handler.error(type.location(), type.toString() +
                        "has duplicated member: " + name);
            }
            map.put(name, slot);
        }
    }

    // 检查 结构体和联合体 的循环定义
    public void checkRecursiveDefinition(Type type, ErrorHandler handler){
        _checkRecursiveDefinition(type, new HashMap<Type, Object>(), handler);
    }

    static final protected Object checking = new Object();
    static final protected Object checked = new Object();

    protected void _checkRecursiveDefinition(Type type, Map<Type, Object> marks, ErrorHandler handler){
        if(marks.get(type) == checking ){
            handler.error(((NamedType)type).location(), "recursive type definition: " + type);
            return;
        }else if(marks.get(type) == checked){
            return;
        }else {
            marks.put(type, checking);
            if(type instanceof ArrayType){
                _checkRecursiveDefinition(((ArrayType) type).baseType, marks, handler);
            }else if(type instanceof CompositeType){
                for (Slot slot : ((CompositeType) type).members()){
                    _checkRecursiveDefinition(slot.type(), marks, handler);
                }
            }else if(type instanceof UserType){
                _checkRecursiveDefinition(((UserType) type).realType(), marks, handler);
            }
            marks.put(type, checked);
        }
    }
}
