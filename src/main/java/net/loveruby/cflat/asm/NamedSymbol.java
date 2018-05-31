package net.loveruby.cflat.asm;

import net.loveruby.cflat.utils.TextUtils;

/**
 * @author 刘科  2018/5/31
 */
public class NamedSymbol extends BaseSymbol {

    protected String name;

    public NamedSymbol(String name) {
        this.name = name;
    }

    public String name() {
        return null;
    }

    public String toSource() {
        return name;
    }

    public String toSource(SymbolTable table) {
        return name;
    }

    @Override
    public String toString() {
        return "#" + name;
    }

    public String dump() {
        return "(NamedSymbol " + TextUtils.dumpString(name) + ")";
    }

    public int cmp(IntegerLiteral i) {
        return 1;
    }

    public int cmp(NamedSymbol symbol) {
        return name.compareTo(symbol.name());
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
