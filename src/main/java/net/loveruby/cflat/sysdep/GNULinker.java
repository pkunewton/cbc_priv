package net.loveruby.cflat.sysdep;

import com.sun.javafx.css.Declaration;
import net.loveruby.cflat.exception.IPCException;
import net.loveruby.cflat.utils.CommandUtils;
import net.loveruby.cflat.utils.ErrorHandler;

import java.util.ArrayList;
import java.util.List;

public class GNULinker implements Linker {


    // 32bit Linux dependent
    private final String LINKER = "/usr/bin/ld";
    private final String DYNAMIC_LINKER = "/lib/ld-linux.so.2";
    private final String C_RUNTIME_INIT = "/usr/lib/crti.o";
    private final String C_RUNTIME_START = "/usr/lib/crt1.o";
    private final String C_RUNTIME_START_PIE = "/usr/lib/Scrt1.o";
    private final String C_RUNTIME_FINI = "/usr/lib/crtn.o";


    ErrorHandler errorHandler;

    public GNULinker(ErrorHandler errorHandler) {
        this.errorHandler = errorHandler;
    }

    public void generateExecutable(List<String> args,
                                   String destPath, LinkerOptions options) throws IPCException {
        List<String> cmd = new ArrayList<String>();
        cmd.add(LINKER);
        cmd.add("-dynamic-linker");
        cmd.add(DYNAMIC_LINKER);
        if(options.generatingPIE){
            cmd.add("-pie");
        }
        if(!options.noStartFiles){
            cmd.add(options.generatingPIE ? C_RUNTIME_START_PIE : C_RUNTIME_START);
            cmd.add(C_RUNTIME_INIT);
        }
        cmd.addAll(args);
        if (!options.noDefaultLibs){
            cmd.add("-lc");
            cmd.add("lcbc");
        }
        if(!options.noStartFiles){
            cmd.add(C_RUNTIME_FINI);
        }
        cmd.add("-o");
        cmd.add(destPath);
        CommandUtils.invoke(cmd, errorHandler, options.verbose);
    }

    public void generateSharedLibrary(List<String> args,
                                      String destPath, LinkerOptions options) throws IPCException {
        List<String> cmd = new ArrayList<String>();
        cmd.add(LINKER);
        cmd.add("-shared");
        if (!options.noStartFiles){
            cmd.add(C_RUNTIME_INIT);
        }
        cmd.addAll(args);
        if (!options.noDefaultLibs){
            cmd.add("-lc");
            cmd.add("lcbc");
        }
        if(!options.noStartFiles){
            cmd.add(C_RUNTIME_FINI);
        }
        cmd.add("-o");
        cmd.add(destPath);
        CommandUtils.invoke(cmd, errorHandler, options.verbose);
    }
}
