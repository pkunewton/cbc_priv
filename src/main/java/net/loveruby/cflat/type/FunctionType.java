package net.loveruby.cflat.type;

import java.util.List;

/**
 * @author 刘科 2018/5/29
 */
public class FunctionType extends Type {

    protected Type returnType;
    protected ParamTypes paramTypes;

    public FunctionType(Type returnType, ParamTypes paramTypes) {
        this.returnType = returnType;
        this.paramTypes = paramTypes;
    }

    @Override
    public boolean isFunction() {
        return true;
    }

    @Override
    public boolean isCallable() {
        return true;
    }

    public Type returnType(){
        return returnType;
    }

    public boolean isVararg(){
        return paramTypes.isVararg();
    }

    public boolean acceptArgc(long numArgs){
        if(isVararg()){
            return numArgs >= paramTypes.minArgc();
        }else {
            return numArgs == paramTypes.argc();
        }
    }


    /**
     * Returns iterator of mandatory parameter types.
     * This method does NOT include types for varargs.
     */
    public List<Type> paramTypes(){
        return paramTypes.types();
    }
    public long size() {
        throw new Error("FunctionType#size called");
    }

    public long alignment(){
        throw new Error("FunctionType#alignment called");
    }

    public boolean isSameType(Type other) {
        if(!other.isFunction()){
            return false;
        }
        FunctionType otherType = other.getFunctionType();
        return (returnType.isSameType(otherType.returnType)) &&
                (paramTypes.isSameType(otherType.paramTypes));
    }

    public boolean isCompatible(Type other) {
        if(!other.isFunction()){
            return false;
        }
        FunctionType otherType = other.getFunctionType();
        return (returnType.isCompatible(otherType.returnType)) &&
                (paramTypes.isSameType(otherType.paramTypes));
    }

    public boolean isCastableTo(Type target) {
        return target.isFunction();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(returnType.toString());
        sb.append("(");
        String seq = "";
        for(Type type: paramTypes()){
            sb.append(seq);
            sb.append(type.toString());
            seq = ", ";
        }
        sb.append(")");
        return sb.toString();
    }
}
