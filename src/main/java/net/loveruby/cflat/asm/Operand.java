package net.loveruby.cflat.asm;

/**
 * @author 刘科  2018/5/31
 */
abstract public class Operand implements OperandPattern {

    abstract public String toSource(SymbolTable table);
    abstract public String dump();

    public boolean isRegister(){
        return false;
    }

    public boolean isMemoryReference(){
        return false;
    }

    public boolean integerLiteral(){
        return false;
    }

    abstract public void collectStatistics(Statistics stats);

    public boolean match(Operand operand) {
        return equals(operand);
    }
}
