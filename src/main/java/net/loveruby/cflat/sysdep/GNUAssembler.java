package net.loveruby.cflat.sysdep;

import net.loveruby.cflat.exception.IPCException;
import net.loveruby.cflat.utils.CommandUtils;
import net.loveruby.cflat.utils.ErrorHandler;

import java.util.ArrayList;
import java.util.List;

/**
 * @author 刘科  2018/6/14
 */
public class GNUAssembler implements Assembler {

    ErrorHandler errorHandler;

    public GNUAssembler(ErrorHandler errorHandler) {
        this.errorHandler = errorHandler;
    }

    public void assemble(String srcPath, String destPath, AssemblerOption option) throws IPCException {
        List<String> cmd = new ArrayList<String>();
        cmd.add("as");
        cmd.addAll(option.args);
        cmd.add("-o");
        cmd.add(destPath);
        cmd.add(srcPath);
        CommandUtils.invoke(cmd, errorHandler, option.verbose);
    }
}
