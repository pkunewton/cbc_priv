package net.loveruby.cflat.sysdep.x86;

import net.loveruby.cflat.asm.SymbolTable;
import net.loveruby.cflat.asm.Type;

/**
 * @author 刘科  2018/6/1
 */
public class Register extends net.loveruby.cflat.asm.Register {

    RegisterClass _class;
    Type type;

    public Register(RegisterClass _class, Type type) {
        this._class = _class;
        this.type = type;
    }

    public Register forType(Type type){
        return new Register(_class, type);
    }

    @Override
    public boolean isRegister() {
        return true;
    }

    @Override
    public boolean equals(Object obj) {
        return (obj instanceof Register) && equals((Register)obj);
    }

    // size difference does NOT matter. 不关注大小
    public boolean equals(Register other) {
        return _class.equals(other._class);
    }

    @Override
    public int hashCode() {
        return _class.hashCode();
    }

    public RegisterClass registerClass() {
        return _class;
    }

    public String baseName(){
        return _class.toString().toLowerCase();
    }

    public String toSource(SymbolTable table) {
        // GNU assembler dependent
        // at&t汇编语法， inter语法前不需要%， inter语法寄存器一般大写
        return "%" + typeName();
    }

    /**
     *           16         8       8
     *   |             eax              |
     *   |              |      ax       |
     *   |              |   ah  |   al  |
     */
    private String typeName(){
        switch (type){
            case INT8: return lowerByteRegister();
            case INT16: return baseName();
            case INT32: return "e" + baseName();
            case INT64: return "r" + baseName();
            default: throw new Error("unknow register type " + type);
        }
    }

    private String lowerByteRegister(){
        switch (_class){
            case AX:
            case BX:
            case CX:
            case DX: return baseName().toLowerCase().substring(0, 1) + "l";
            default: throw new Error("does not have lower-byte register " + _class);
        }
    }

    public String dump() {
        return "(Register " + _class.toString() + " " + type.toString() + ")";
    }
}
