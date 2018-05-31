package net.loveruby.cflat.ir;

import net.loveruby.cflat.asm.MemoryReference;
import net.loveruby.cflat.asm.Operand;
import net.loveruby.cflat.asm.Type;
import net.loveruby.cflat.entity.Entity;

/**
 * @author 刘科  2018/5/31
 */
public class Addr extends Expr {

    Entity entity;

    public Addr(Type type, Entity entity) {
        super(type);
        this.entity = entity;
    }

    public boolean isAddr(){
        return true;
    }

    public Entity entity() {
        return super.getEntityForce();
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
