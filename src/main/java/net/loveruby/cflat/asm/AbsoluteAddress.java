package net.loveruby.cflat.asm;

/**
 * @author 刘科  2018/6/1
 */
public class AbsoluteAddress extends Operand {

    protected Register register;

    public AbsoluteAddress(Register register) {
        this.register = register;
    }

    public Register register() {
        return register;
    }

    public String toSource(SymbolTable table) {
        return "*" + register.toSource(table);
    }

    public String dump() {
        return "(AbsoluteAddress " + register.dump() + ")";
    }

    public void collectStatistics(Statistics stats) {
        register.collectStatistics(stats);
    }
}
