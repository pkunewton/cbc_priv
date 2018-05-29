package net.loveruby.cflat.entity;

import net.loveruby.cflat.ast.Location;

import java.util.List;

/**
 * @author 刘科 2018/5/29
 * @param <T>
 */
abstract public class ParamSlots<T> {

    protected Location location;
    protected List<T> paramDescriptors;
    // 可变参数
    protected boolean vararg;

    public ParamSlots(List<T> paramDescriptors) {
        this(null, paramDescriptors);
    }

    public ParamSlots(Location location, List<T> paramDescriptors) {
        this(location, paramDescriptors,false);
    }

    public ParamSlots(Location location, List<T> paramDescriptors, boolean vararg) {
        this.location = location;
        this.paramDescriptors = paramDescriptors;
        this.vararg = vararg;
    }

    public int argc(){
        if(vararg){
            throw new Error("must not happen: Param#argc for vararg 可变参数");
        }
        return paramDescriptors.size();
    }

    public int minArgc(){
        return paramDescriptors.size();
    }

    public void acceptVarargs(){
        this.vararg = true;
    }

    public boolean isVararg(){
        return vararg;
    }

    public Location location() {
        return location;
    }
}
