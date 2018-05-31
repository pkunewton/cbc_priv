package net.loveruby.cflat.asm;

import net.loveruby.cflat.utils.TextUtils;

/**
 * @author 刘科  2018/5/31
 */
public class SuffixedSymbol implements Symbol {

    protected Symbol base;
    protected String suffix;

    public SuffixedSymbol(Symbol base, String suffix) {
        this.base = base;
        this.suffix = suffix;
    }

    public String name() {
        return base.name();
    }

    public String toSource() {
        return base.toSource() + suffix;
    }

    public String toSource(SymbolTable table) {
        return base.toSource(table) + suffix;
    }

    @Override
    public String toString() {
        return base.toSource() + suffix;
    }

    public String dump() {
        return "(SuffixedSymbol " + base.dump() + " "
                + TextUtils.dumpString(suffix) + ")";
    }

    public void collectStatistics(Statistics stats) {
        base.collectStatistics(stats);
    }

    public boolean isZero() {
        return false;
    }

    public Literal plus(long diff) {
        throw new Error("must not happen: SuffixedSymbol.plus called");
    }

    public int cmp(IntegerLiteral i) {
        return 1;
    }

    public int cmp(NamedSymbol symbol) {
        return toString().compareTo(symbol.toString());
    }

    public int cmp(UnnamedSymbol symbol) {
        return -1;
    }

    public int cmp(SuffixedSymbol symbol) {
        return toString().compareTo(symbol.toString());
    }

    public int compareTo(Literal o) {
        return -(o.cmp(this));
    }
}
