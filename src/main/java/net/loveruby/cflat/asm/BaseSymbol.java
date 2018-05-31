package net.loveruby.cflat.asm;

/**
 * @author 刘科  2018/5/31
 */
abstract public class BaseSymbol implements Symbol {

    public boolean isZero() {
        return false;
    }

    public Literal plus(long diff) {
        throw new Error("must not happen: BaseSymbol.plus called");
    }

    public void collectStatistics(Statistics stats) {
        stats.symbolUsed(this);
    }
}
