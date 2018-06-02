package net.loveruby.cflat.asm;

/**
 * @author 刘科  2018/6/1
 */
public class IndirectMemoryReference extends MemoryReference {

    Literal offset;
    Register base;
    boolean fixed;

    public IndirectMemoryReference(long offset, Register base) {
        this(new IntegerLiteral(offset), base, true);
    }

    public IndirectMemoryReference(Symbol offset, Register base) {
        this(offset, base, true);
    }

    public IndirectMemoryReference(Literal offset, Register base, boolean fixed) {
        this.offset = offset;
        this.base = base;
        this.fixed = fixed;
    }

    public static IndirectMemoryReference relocatable(long offset, Register base){
        return new IndirectMemoryReference(new IntegerLiteral(offset), base, false);
    }

    public Literal offset() {
        return offset;
    }

    public void fixOffset(long diff) {
        if(fixed){
            throw new Error("must not happen: fixed = true");
        }
        this.offset = new IntegerLiteral(((IntegerLiteral)offset).value() + diff);
        this.fixed = true;
    }

    public Register base() {
        return base;
    }

    protected int cmp(DirectMemoryReference mem) {
        return -1;
    }

    protected int cmp(IndirectMemoryReference mem) {
        return offset.compareTo(mem.offset);
    }

    public int compareTo(MemoryReference o) {
        return -(o.compareTo(this));
    }

    @Override
    public String toString() {
        return toSource(SymbolTable.dummy());
    }

    public String toSource(SymbolTable table) {
        if(!fixed){
            throw new Error("must not happen: writing unfixed variable");
        }
        return (offset.isZero() ? "" : offset.toSource(table))
                + "(" + base.toSource(table) + ")";
    }

    public String dump() {
        return "(IndirectMemoryReference " + (fixed ? "" : "*")
                + offset.dump() + " " + base.dump() + ")";
    }

    public void collectStatistics(Statistics stats) {
        base.collectStatistics(stats);
    }
}
