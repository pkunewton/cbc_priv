package net.loveruby.cflat.sysdep.x86;

import net.loveruby.cflat.asm.Label;
import net.loveruby.cflat.asm.MemoryReference;
import net.loveruby.cflat.asm.Type;
import net.loveruby.cflat.ir.IR;
import net.loveruby.cflat.sysdep.CodeGeneratorOptions;
import net.loveruby.cflat.utils.ErrorHandler;

import java.util.List;

/**
 * @author 刘科  2018/6/10
 */
public class CodeGenerator implements net.loveruby.cflat.sysdep.CodeGenerator {

    final CodeGeneratorOptions options;
    final Type natureType;
    final ErrorHandler errorHandler;

    public CodeGenerator(CodeGeneratorOptions options, Type natureType, ErrorHandler errorHandler) {
        this.options = options;
        this.natureType = natureType;
        this.errorHandler = errorHandler;
    }


    /** Compiles IR and generates assembly code. */
    public AssemblyCode generate(IR ir) {
        return null;
    }

    static final String LABEL_SYMBOL_BASE = ".L";
    static final String CONST_SYMBOL_BASE = ".LC";


    //
    // locateSymbol
    //

    private void locateSymbol(IR ir){

    }

    static final private long STACK_WORD_SIZE = 4;

    /**
     * 帧栈信息
     * saveRegs 需要保存的 callee—save 寄存器列表
     * lvarSize 局部变量大小
     * tempSize 临时变量大小
     */
    class StackFrameInfo {
        List<Register> saveRegs;
        long lvarSize;
        long tempSize;

        long saveRegisters() { return saveRegs.size() * STACK_WORD_SIZE; }
        long lvarOffset(){
            return saveRegisters();
        }
        long tempOffset(){
            return saveRegisters() + lvarSize;
        }
        long frameSize(){
            return saveRegisters() + lvarSize + tempSize;
        }
    }





    private AssemblyCode as;
    private Label epilogue;


    private void load(MemoryReference mem, Register reg){
        as.mov(mem, reg);
    }

    private void store(Register reg, MemoryReference mem){
        as.mov(reg, mem);
    }

}
