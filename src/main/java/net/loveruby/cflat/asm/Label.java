package net.loveruby.cflat.asm;

/**
 * Created by Administrator on 2018/2/8.
 */
public class Label extends Assembly {

    protected Symbol symbol;

    public Label(){

    }

    public Label(Symbol symbol) {
        this.symbol = symbol;
    }

    public Symbol symbol() {
        return symbol;
    }

    public String toSource(SymbolTable table) {
        return null;
    }

    public String dump() {
        return null;
    }
}
