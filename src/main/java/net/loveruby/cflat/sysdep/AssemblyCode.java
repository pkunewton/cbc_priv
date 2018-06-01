package net.loveruby.cflat.sysdep;

import java.io.PrintStream;

/**
 * @author 刘科  2018/6/1
 */
public interface AssemblyCode {

    String toSource();
    void dump();
    void dump(PrintStream stream);

}
