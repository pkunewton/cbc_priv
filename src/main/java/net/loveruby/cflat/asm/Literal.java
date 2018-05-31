package net.loveruby.cflat.asm;

/**
 * @author 刘科  2018/5/31
 */
public interface Literal extends Comparable<Literal> {

    public String toSource();
    public String toSource(SymbolTable table);
    public String dump();
    public void collectStatistics(Statistics stats);
    public boolean isZero();
    public Literal plus(long diff);
    public int cmp(IntegerLiteral i);
    public int cmp(NamedSymbol symbol);
    public int cmp(UnnamedSymbol symbol);
    public int cmp(SuffixedSymbol symbol);
}
