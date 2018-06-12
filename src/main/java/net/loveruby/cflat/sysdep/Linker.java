package net.loveruby.cflat.sysdep;

import net.loveruby.cflat.exception.IPCException;

import java.util.List;

/**
 * @author 刘科  2018/6/11
 */
public interface Linker {

    void generateExecutable(List<String> args, String destPath,
                            LinkerOptions options) throws IPCException;

    void generateSharedLibrary(List<String> args, String destPath,
                            LinkerOptions options) throws IPCException;
}
