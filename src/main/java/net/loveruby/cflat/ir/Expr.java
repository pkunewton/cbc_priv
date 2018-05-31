package net.loveruby.cflat.ir;

import net.loveruby.cflat.asm.ImmediateValue;
import net.loveruby.cflat.asm.MemoryReference;
import net.loveruby.cflat.asm.Operand;
import net.loveruby.cflat.asm.Type;
import net.loveruby.cflat.entity.Entity;

/**
 * @author 刘科  2018/5/31
 */
abstract public class Expr implements Dumpable {
    final Type type;

    public Expr(Type type) {
        this.type = type;
    }

    public Type type() {
        return type;
    }

    public boolean isAddr(){
        return false;
    }

    public boolean isVar(){
        return false;
    }

    public boolean isConstant(){
        return false;
    }

    public ImmediateValue asmValue(){
        throw new Error("Expr#asmValue called");
    }

    public Operand address(){
        throw new Error("Expr#address called");
    }

    public MemoryReference memref(){
        throw new Error("Expr#memref called");
    }

    public Expr addressNode(){
        throw new Error("unexcepted node for LHS: " + this.getClass());
    }

    public Entity getEntityForce(){
        return null;
    }

    abstract public <S,E> E accept(IRVisitor<S,E> visitor);

    public void dump(Dumper d){
        d.printClass(this);
        d.printMember("type", type);
        _dump(d);
    }

    abstract protected void _dump(Dumper d);
}
