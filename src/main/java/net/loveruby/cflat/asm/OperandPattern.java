package net.loveruby.cflat.asm;

/**
 * @author 刘科  2018/5/31
 */
public interface OperandPattern {
    public boolean match(Operand operand);
}
