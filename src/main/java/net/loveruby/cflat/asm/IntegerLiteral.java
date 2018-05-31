package net.loveruby.cflat.asm;

/**
 * @author 刘科  2018/5/31
 */
public class IntegerLiteral implements Literal {

    protected long value;

    public IntegerLiteral(long value) {
        this.value = value;
    }

    public long value() {
        return value;
    }

    public IntegerLiteral integerLiteral(){
        return this;
    }

    @Override
    public boolean equals(Object obj) {
        return (obj instanceof IntegerLiteral) &&
                (equals((IntegerLiteral)obj));
    }

    public boolean equals(IntegerLiteral other) {
        return other.value == value;
    }

    public String toSource() {
        return new Long(value).toString();
    }

    public String toSource(SymbolTable table) {
        return toSource();
    }

    @Override
    public String toString() {
        return new Long(value).toString();
    }

    public String dump() {
        return "(IntegerLiteral " + new Long(value).toString() + ")";
    }

    public void collectStatistics(Statistics stats) {

    }

    public boolean isZero() {
        return value == 0;
    }

    public Literal plus(long diff) {
        return new IntegerLiteral(value + diff);
    }

    public int cmp(IntegerLiteral i) {
        return (new Long(value).compareTo(new Long(i.value)));
    }

    public int cmp(NamedSymbol symbol) {
        return -1;
    }

    public int cmp(UnnamedSymbol symbol) {
        return -1;
    }

    public int cmp(SuffixedSymbol symbol) {
        return -1;
    }

    public int compareTo(Literal o) {
        return -(o.cmp(this));
    }
}
