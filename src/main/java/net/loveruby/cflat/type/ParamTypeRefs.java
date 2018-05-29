package net.loveruby.cflat.type;

import net.loveruby.cflat.ast.Location;
import net.loveruby.cflat.entity.ParamSlots;

import java.util.ArrayList;
import java.util.List;

/**
 * @author 刘科 2018/5/29
 */
public class ParamTypeRefs extends ParamSlots<TypeRef> {

    public ParamTypeRefs(List<TypeRef> paramDescriptors) {
        super(paramDescriptors);
    }

    public ParamTypeRefs(Location location, List<TypeRef> paramDescriptors, boolean vararg) {
        super(location, paramDescriptors, vararg);
    }

    public List<TypeRef> typeRefs(){
        return paramDescriptors;
    }

    /**
     * @see net.loveruby.cflat.type.TypeTable get方法
     * @param typeTable
     * @return
     */
    public ParamTypes internTypes(TypeTable typeTable){
        List<Type> types = new ArrayList<Type>();
        for (TypeRef ref : paramDescriptors) {
            types.add(typeTable.getParamType(ref));
        }
        return new ParamTypes(location, types, vararg);
    }

    public boolean equals(Object other){
        return (other instanceof ParamTypeRefs) &&
                equals((ParamTypeRefs)other);
    }

    public boolean equals(ParamTypeRefs other){
        return (vararg == other.vararg) &&
                (paramDescriptors.equals(other.paramDescriptors));
    }
}
