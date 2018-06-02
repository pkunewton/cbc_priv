package net.loveruby.cflat.sysdep;

import net.loveruby.cflat.type.TypeTable;
import net.loveruby.cflat.utils.ErrorHandler;

/**
 * @author 刘科  2018/6/2
 */
public interface Platform {

    TypeTable typeTable();
    CodeGenerator codeGenerator(CodeGeneratorOptions options, ErrorHandler handler);
    Assembler assembler(ErrorHandler handler);
    Linker linker(ErrorHandler handler);
}
