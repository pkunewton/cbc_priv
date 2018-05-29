package net.loveruby.cflat.type;

import net.loveruby.cflat.ast.Location;
import net.loveruby.cflat.ast.Slot;
import net.loveruby.cflat.exception.SemanticError;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author 刘科 2018/5/29
 */
abstract public class CompositeType extends NamedType {

    protected List<Slot> members;
    protected long cacheSize;
    protected long cacheAlign;
    /**
     * 结构体和联合体是否存在循环引用的问题
     * @see net.loveruby.cflat.type.TypeTable  checkRecursiveDefinition 方法
     */
    protected boolean isRecuisiveChecked;

    public CompositeType(String name, List<Slot> members, Location location){
        super(name, location);
        this.members = members;
        this.cacheSize = Type.sizeUnknown;
        this.cacheAlign = Type.sizeUnknown;
        this.isRecuisiveChecked = false;
    }

    @Override
    public boolean isCompositeType() {
        return true;
    }

    public boolean isSameType(Type other) {
        return compareMemberTypes(other, "isSameType");
    }

    public boolean isCompatible(Type other) {
        return compareMemberTypes(other, "isCompatible");
    }

    public boolean isCastableTo(Type target) {
        return compareMemberTypes(target, "isCastableTo");
    }

    protected boolean compareMemberTypes(Type other, String compareMethod){
        if(isStruct() && !other.isStruct()){
            return false;
        }
        if(isUnion() && !other.isUnion()){
            return false;
        }
        CompositeType otherType = other.getCompositeType();
        // 原始代码是注释掉的部分，感觉有问题
//        if(members.size() != other.size()){
//            return false;
//        }
        if(members.size() != other.getCompositeType().members().size()){
            return false;
        }
        Iterator<Type> otherTypes = otherType.memberTypes().iterator();
        for (Type type: memberTypes()) {
            if(!compareTypesBy(compareMethod, type, otherTypes.next())){
                return false;
            }
        }
        return true;
    }

    protected boolean compareTypesBy(String compareMethod, Type t, Type tt){
        try {
            Method method = Type.class.getMethod(compareMethod, new Class[]{Type.class});
            Boolean result = (Boolean) method.invoke(t, new Object[]{ tt });
            return result.booleanValue();
        } catch (Exception e) {
            throw new Error(e.getMessage());
        }
    }

    // 以下是 CompositeType 内存布局

    public long size() {
        if(cacheSize == Type.sizeUnknown){
            computeOffset();
        }
        return cacheSize;
    }

    public long alignment(){
        if(cacheAlign == Type.sizeUnknown){
            computeOffset();
        }
        return cacheAlign;
    }

    public List<Slot> members(){
        return members;
    }

    public List<Type> memberTypes(){
        List<Type> result = new ArrayList<Type>();
        for (Slot slot : members) {
            result.add(slot.type());
        }
        return result;
    }

    public boolean hasMember(String name){
        return (get(name) != null);
    }

    public Type memberType(String name){
        return fetch(name).type();
    }

    public long memberOffset(String name){
        Slot slot = fetch(name);
        if(slot.offset() == Type.sizeUnknown){
            computeOffset();
        }
        return slot.offset();
    }

    abstract protected void computeOffset(); // 计算偏移量

    protected Slot fetch(String name){
        Slot slot = get(name);
        if(slot == null){
            throw new SemanticError("no such member is "
                    + toString() + ": " + name);
        }
        return slot;
    }

    public Slot get(String name){
        for (Slot slot : members){
            if(slot.name().equals(name)){
                return slot;
            }
        }
        return null;
    }
}
