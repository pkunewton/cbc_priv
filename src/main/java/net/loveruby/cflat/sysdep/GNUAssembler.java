package net.loveruby.cflat.sysdep;

import net.loveruby.cflat.exception.IPCException;
import net.loveruby.cflat.utils.ErrorHandler;

public class GNUAssembler implements Assembler {

    ErrorHandler errorHandler;

    public GNUAssembler(ErrorHandler errorHandler) {
        this.errorHandler = errorHandler;
    }

    public void assemble(String path, String destPath, AssemblerOption option) throws IPCException {

    }
}
