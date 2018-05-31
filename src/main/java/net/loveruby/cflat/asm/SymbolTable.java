package net.loveruby.cflat.asm;

import java.util.HashMap;
import java.util.Map;

/**
 * @author 刘科  2018/5/31
 */
public class SymbolTable {

    protected String base;
    protected Map<UnnamedSymbol, String> map;
    protected long seq = 0;

    private static final String DUMMY_SYMBOL_BASE = "L";
    private static final SymbolTable dummy = new SymbolTable(DUMMY_SYMBOL_BASE);

    static public SymbolTable dummy(){
        return dummy;
    }

    public SymbolTable(String base) {
        this.base = base;
        this.map = new HashMap<UnnamedSymbol, String>();
    }

    public String symbolString(UnnamedSymbol symbol){
        String s = map.get(symbol);
        if(s != null){
            return s;
        }else {
            String newStr = newString();
            map.put(symbol, newStr);
            return newStr;
        }
    }

    public Symbol newSymbol(){
        return new NamedSymbol(newString());
    }

    public String newString(){
        return base + seq++;
    }
}
