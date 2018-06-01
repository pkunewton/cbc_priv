package net.loveruby.cflat.asm;

/**
 * @author 刘科  2018/5/31
 */
public class UnnamedSymbol extends BaseSymbol {

    public UnnamedSymbol() {
    }

    public String name() {
        throw new Error("unamed symbol");
    }

    public String toSource() {
        throw new Error("UnnamedSymbol#toString() called");
    }

    public String toSource(SymbolTable table) {
        return table.symbolString(this);
    }

    @Override
    public String toString() {
        return super.toString();
    }

    public String dump() {
        return "(UnnamedSymbol @" + Integer.toHexString(hashCode()) + ")";
    }

    public int cmp(IntegerLiteral i) {
        return 1;
    }

    public int cmp(NamedSymbol symbol) {
        return 1;
    }

    public int cmp(UnnamedSymbol symbol) {
        return toString().compareTo(symbol.toString());
    }

    public int cmp(SuffixedSymbol symbol) {
        return 1;
    }

    public int compareTo(Literal o) {
        return -(o.cmp(this));
    }
}
