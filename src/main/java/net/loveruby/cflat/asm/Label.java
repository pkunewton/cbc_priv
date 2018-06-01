package net.loveruby.cflat.asm;

/**
 * @author 刘科  2018/6/1
 */
public class Label extends Assembly {

    protected Symbol symbol;

    public Label(){
        this(new UnnamedSymbol());
    }

    public Label(Symbol symbol) {
        this.symbol = symbol;
    }

    public Symbol symbol() {
        return symbol;
    }

    @Override
    public boolean isLabel() {
        return true;
    }

    public String toSource(SymbolTable table) {
        return symbol.toSource(table) + ":";
    }

    public String dump() {
        return "(Label " + symbol.dump() + ")";
    }
}
