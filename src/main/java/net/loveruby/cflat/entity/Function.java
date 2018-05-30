package net.loveruby.cflat.entity;

import net.loveruby.cflat.asm.Label;
import net.loveruby.cflat.asm.Symbol;
import net.loveruby.cflat.ast.TypeNode;
import net.loveruby.cflat.type.Type;

import java.util.List;

/**
 * @author 刘科  2018/5/30
 * */
abstract public class Function extends Entity {

    // 汇编代码中函数代码起始标签
    protected Symbol callingSymbol;
    protected Label label;

    public Function(boolean isPrivate, TypeNode typeNode, String name) {
        super(isPrivate, typeNode, name);
    }

    public boolean isInitialized(){
        return true;
    }

    abstract public boolean isDefined();
    abstract public List<Parameter> parameters();

    public Type returnType(){
        return type().getFunctionType().returnType();
    }

    public boolean isVoid(){
        return returnType().isVoid();
    }

    public void setCallingSymbol(Symbol callingSymbol) {
        if(callingSymbol != null){
            throw new Error("must not happen: Function#callingSymbol was set twice");
        }
        this.callingSymbol = callingSymbol;
    }

    public Symbol callingSymbol() {
        if(callingSymbol == null){
            throw new Error("must not happen: Function#callingSymbol called but null");
        }
        return callingSymbol;
    }

    public Label label(){
        if(label != null){
            return label;
        }else {
            return label = new Label(callingSymbol());
        }
    }
}
