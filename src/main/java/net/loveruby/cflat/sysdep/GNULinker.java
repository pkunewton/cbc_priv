package net.loveruby.cflat.sysdep;

import net.loveruby.cflat.exception.IPCException;
import net.loveruby.cflat.utils.ErrorHandler;

import java.util.List;

public class GNULinker implements Linker {

    ErrorHandler errorHandler;

    public GNULinker(ErrorHandler errorHandler) {
        this.errorHandler = errorHandler;
    }

    public void generateExecutable(List<String> args, String destPath, LinkerOptions options) throws IPCException {

    }

    public void generateSharedLibrary(List<String> args, String destPath, LinkerOptions options) throws IPCException {

    }
}
