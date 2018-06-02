package net.loveruby.cflat.sysdep;


/**
 * @author 刘科  2018/6/2
 */
public interface CodeGenerator {

    AssemblyCode generate(net.loveruby.cflat.ir.IR ir);
}
