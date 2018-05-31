package net.loveruby.cflat.ir;

import net.loveruby.cflat.asm.ImmediateValue;
import net.loveruby.cflat.asm.IntegerLiteral;
import net.loveruby.cflat.asm.MemoryReference;
import net.loveruby.cflat.asm.Type;

/**
 * @author 刘科  2018/5/31
 */
public class Int extends Expr {

    protected long value;

    public Int(Type type, long value) {
        super(type);
        this.value = value;
    }

    public long value() {
        return value;
    }

    @Override
    public boolean isConstant() {
        return true;
    }

    @Override
    public ImmediateValue asmValue() {
        return new ImmediateValue(new IntegerLiteral(value));
    }

    @Override
    public MemoryReference memref() {
        throw new Error("must not happen: IntValue#memoryRef");
    }

    public <S, E> E accept(IRVisitor<S, E> visitor) {
        return visitor.visit(this);
    }

    protected void _dump(Dumper d) {
        d.printMember("value", value);
    }
}
