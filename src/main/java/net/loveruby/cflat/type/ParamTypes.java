package net.loveruby.cflat.type;

import net.loveruby.cflat.ast.Location;
import net.loveruby.cflat.entity.ParamSlots;

import java.util.Iterator;
import java.util.List;

/**
 * @author 刘科 2018/5/29
 */
public class ParamTypes extends ParamSlots<Type> {

    public ParamTypes(Location location, List<Type> paramDescriptors, boolean vararg){
        super(location, paramDescriptors, vararg);
    }

    public List<Type> types(){
        return paramDescriptors;
    }

    public boolean isSameType(ParamTypes other){
        if(vararg != other.vararg){
            return false;
        }
        if(minArgc() != other.minArgc()){
            return false;
        }
        Iterator<Type> otherTypes = other.types().iterator();
        for(Type type : paramDescriptors){
            if(!type.isSameType(otherTypes.next())){
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof ParamTypes)){
            return false;
        }
        return equals((ParamTypes) obj);
    }

    public boolean equals(ParamTypes other) {
        return (vararg == other.vararg) &&
                (paramDescriptors.equals(other.paramDescriptors));
    }
}
