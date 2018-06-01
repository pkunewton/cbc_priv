package net.loveruby.cflat.asm;

/**
 * @author 刘科  2018/6/1
 */
public class DirectMemoryReference extends MemoryReference {

    protected Literal value;

    public DirectMemoryReference(Literal value) {
        this.value = value;
    }

    public Literal value() {
        return value;
    }

    @Override
    public String toString() {
        return toSource(SymbolTable.dummy());
    }

    public String toSource(SymbolTable table) {
        return this.value.toSource(table);
    }

    public String dump() {
        return "(DirectMemoryReference " + value.dump() + ")";
    }

    public void collectStatistics(Statistics stats) {
        value.collectStatistics(stats);
    }

    public void fixOffset(long diff) {
        throw new Error("DirectMemoryReference#fixOffset");
    }

    protected int cmp(DirectMemoryReference mem) {
        return value.compareTo(mem.value);
    }

    protected int cmp(IndirectMemoryReference mem) {
        return 1;
    }

    public int compareTo(MemoryReference o) {
        return -(o.cmp(this));
    }
}
