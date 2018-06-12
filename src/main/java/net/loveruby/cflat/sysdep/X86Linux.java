package net.loveruby.cflat.sysdep;

import net.loveruby.cflat.asm.Type;
import net.loveruby.cflat.type.TypeTable;
import net.loveruby.cflat.utils.ErrorHandler;

/**
 * @author 刘科  2018/6/2
 */
public class X86Linux implements Platform {


    public TypeTable typeTable() {
        return TypeTable.ilp32();
    }

    public CodeGenerator codeGenerator(CodeGeneratorOptions options, ErrorHandler handler) {
        return null;
    }

    private Type naturalType(){
        return Type.INT32;
    }

    public Assembler assembler(ErrorHandler handler) {
        return new GNUAssembler(handler);
    }

    public Linker linker(ErrorHandler handler) {
        return new GNULinker(handler);
    }
}
