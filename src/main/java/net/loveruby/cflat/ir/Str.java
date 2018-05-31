package net.loveruby.cflat.ir;

import net.loveruby.cflat.asm.*;
import net.loveruby.cflat.entity.ConstantEntry;

/**
 * @author 刘科  2018/5/31
 */
public class Str extends Expr {

    protected ConstantEntry entry;

    public Str(Type type, ConstantEntry entry) {
        super(type);
        this.entry = entry;
    }

    public ConstantEntry entry() {
        return entry;
    }

    public Symbol symbol(){
        return entry.symbol();
    }

    @Override
    public boolean isConstant() {
        return true;
    }

    @Override
    public Operand address() {
        return entry.address();
    }

    @Override
    public MemoryReference memref() {
        return entry.memref();
    }

    @Override
    public ImmediateValue asmValue() {
        return entry.address();
    }

    public <S, E> E accept(IRVisitor<S, E> visitor) {
        return visitor.visit(this);
    }

    protected void _dump(Dumper d) {
        d.printMember("entry", entry.value());
    }
}
