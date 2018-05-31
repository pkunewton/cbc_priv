package net.loveruby.cflat.asm;

/**
 * @author 刘科  2018/5/31
 */
public interface Symbol extends Literal {

    public String name();
    public String toString();
    public String dump();
}
