package net.loveruby.cflat.ir;

import net.loveruby.cflat.asm.MemoryReference;
import net.loveruby.cflat.asm.Operand;
import net.loveruby.cflat.asm.Type;
import net.loveruby.cflat.entity.Entity;

/**
 * @author 刘科  2018/5/31
 */
public class Var extends Expr {

    protected Entity entity;

    public Var(Type type, Entity entity) {
        super(type);
        this.entity = entity;
    }

    @Override
    public boolean isVar() {
        return true;
    }

    @Override
    public Type type() {
        if(super.type() == null){
            throw new Error("Var is too big to load by 1 insn");
        }
        return super.type;
    }

    public Entity entity() {
        return entity;
    }

    public String name(){
        return entity.name();
    }

    @Override
    public Operand address() {
        return entity.address();
    }

    @Override
    public MemoryReference memref() {
        return entity.memref();
    }

    @Override
    public Expr addressNode() {
        return new Addr(type, entity);
    }

    @Override
    public Entity getEntityForce() {
        return entity;
    }

    public <S, E> E accept(IRVisitor<S, E> visitor) {
        return visitor.visit(this);
    }

    protected void _dump(Dumper d) {
        d.printMember("entity", entity.name());
    }
}
