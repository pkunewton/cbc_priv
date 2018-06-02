package net.loveruby.cflat.sysdep;

import net.loveruby.cflat.exception.IPCException;

/**
 * @author 刘科  2018/6/2
 */
public interface Assembler {

    void assemble(String path, String destPath, AssemblerOption option)
            throws IPCException;
}
