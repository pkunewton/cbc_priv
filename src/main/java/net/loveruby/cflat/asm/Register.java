package net.loveruby.cflat.asm;

/**
 * @author 刘科  2018/6/1
 */
abstract public class Register extends Operand {

    @Override
    public boolean isRegister() {
        return true;
    }

    @Override
    public void collectStatistics(Statistics stats) {
        stats.registerUsed(this);
    }

    abstract public String toSource(SymbolTable table);
    abstract public String dump();
}
