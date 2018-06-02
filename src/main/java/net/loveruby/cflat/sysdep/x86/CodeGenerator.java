package net.loveruby.cflat.sysdep.x86;

import net.loveruby.cflat.ir.IR;
import net.loveruby.cflat.sysdep.AssemblyCode;

import java.util.List;

public class CodeGenerator implements net.loveruby.cflat.sysdep.CodeGenerator {




    static final private long STACK_WORD_SIZE = 4;

    public AssemblyCode generate(IR ir) {
        return null;
    }

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

}
