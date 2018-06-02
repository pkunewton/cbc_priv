package net.loveruby.cflat.sysdep.x86;

import net.loveruby.cflat.asm.*;
import net.loveruby.cflat.ir.Op;
import net.loveruby.cflat.utils.TextUtils;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

/**
 * @author 刘科  2018/6/1
 */
public class AssemblyCode implements net.loveruby.cflat.sysdep.AssemblyCode {

    final Type natureType;
    final long stackWordSize;
    final SymbolTable labelSymbols;
    final boolean verbose;
    final VirtualStack virtualStack = new VirtualStack();
    private List<Assembly> assemblies = new ArrayList<Assembly>();
    private int commentIndentLevel = 0;
    private Statistics statistics;

    public AssemblyCode(Type natureType, long stackWordSize, SymbolTable table, boolean verbose) {
        this.natureType = natureType;
        this.stackWordSize = stackWordSize;
        this.labelSymbols = table;
        this.verbose = verbose;
    }

    public List<Assembly> assemblies() {
        return assemblies;
    }

    void addAll(List<Assembly> assemblies){
        this.assemblies.addAll(assemblies);
    }

    public String toSource() {
        StringBuilder buf = new StringBuilder();
        for(Assembly assembly : assemblies){
            buf.append(assembly.toSource(labelSymbols));
            buf.append("\n");
        }
        return buf.toString();
    }

    public void dump() {
        dump(System.out);
    }

    public void dump(PrintStream stream) {
        for(Assembly assembly: assemblies){
            stream.println(assembly.dump());
        }
    }

    // 窥视孔优化
    void apply(PeepholeOptimizer optimizer){
        assemblies = optimizer.optimize(assemblies);
    }

    private Statistics statistics(){
        if (statistics == null){
            statistics = Statistics.collect(assemblies);
        }
        return statistics;
    }

    boolean doesUses(Register register){
        return statistics().doesRegisterUsed(register);
    }

    /**
     *
     *
     * 汇编代码中 添加注释
     *
     *
     */
    void comment(String str){
        assemblies.add(new Comment(str, commentIndentLevel));
    }

    void indentComment(){
        commentIndentLevel++;
    }

    void unindentComment(){
        commentIndentLevel--;
    }

    /**
     *
     *
     * 汇编代码中 添加标签
     *
     *
     */
    void label(Symbol symbol){
        assemblies.add(new Label(symbol));
    }

    void label(Label label){
        assemblies.add(label);
    }

    void reduceLabels(){
        Statistics stats = statistics();
        List<Assembly> result = new ArrayList<Assembly>();
        for(Assembly assembly: assemblies){
            // 清除无用标签
            if(assembly.isLabel() && stats.doesSymbolUsed((Label) assembly)){
                continue;
            }else {
                result.add(assembly);
            }
        }
        assemblies = result;
    }

    /**
     *
     *
     * 生成指令
     *
     *
     */
    protected void insn(String mnemonic){
        assemblies.add(new Instruction(mnemonic));
    }

    protected void insn(String mnemonic, Operand a){
        assemblies.add(new Instruction(mnemonic, "", a));
    }

    protected void insn(String mnemonic, String suffix, Operand a){
        assemblies.add(new Instruction(mnemonic, suffix, a));
    }

    protected void insn(Type type, String mnemonic, Operand a){
        assemblies.add(new Instruction(mnemonic, typeSuffix(type), a));
    }

    protected void insn(String mnemonic, String suffix, Operand a, Operand b){
        assemblies.add(new Instruction(mnemonic, suffix, a, b));
    }

    protected void insn(Type type, String mnemonic, Operand a, Operand b){
        assemblies.add(new Instruction(mnemonic, typeSuffix(type), a, b));
    }

    protected String typeSuffix(Type t1, Type t2){
        return typeSuffix(t1) + typeSuffix(t2);
    }

    protected String typeSuffix(Type type){
        switch (type){
            case INT8: return "b";
            case INT16: return "w";
            case INT32: return "l";
            case INT64: return "q";
            default:
                throw new Error("unknown register size: " + type.size());
        }
    }

    /**
     *
     * 生成汇编伪指令
     * .rodata 字符串字面量等不能更新的数据
     * .bss节 没有初始值的全局变量 指令 .comm 符号 大小 对齐量
     */
    protected void directive(String direc){
        assemblies.add(new Directive(direc));
    }

    void _file(String name){
        directive(".file\t" + TextUtils.dumpString(name));
    }

    void _text() {
        directive("\t.text");       // 代码段
    }

    void _data() {
        directive("\t.data");       // 数据段，有初始值的全局变量
    }

    void _section(String name){
        directive("\t.section\t" + name);       // 节
    }

    void _section(String name, String flag, String type, String group, String linkage){
        directive("\t.section\t" + name + "," + type + "," + group + "," + linkage);
    }

    void _global(Symbol symbol){
        directive(".global " + symbol.name());
    }

    void _local(Symbol symbol){
        directive(".local " + symbol.name());
    }

    void _hidden(Symbol symbol){
        directive("\t.hidden\t" + symbol.name());
    }

    void _comm(Symbol symbol, long size, long alignment){
        directive("\t.comm\t" + symbol.name() + "," + size + "," + alignment);
    }

    void _align(long n){
        directive("\t.align\t" + n);
    }

    void _type(Symbol symbol, String type){
        // 只有两种 @function @object
        directive("\t.type\t" + symbol.name() + "," + type);
    }

    void _size(Symbol symbol, long size){
        _size(symbol, new Long(size).toString());
    }

    void _size(Symbol symbol, String size){
        directive("\t.size\t" + symbol.name() + "," +size);
    }

    void _byte(long value){
        directive(".byte\t" + new IntegerLiteral((byte)value).toSource());
    }

    void _value(long value){
        directive(".value\t" + new IntegerLiteral((short)value).toSource());
    }

    void _long(long value){
        directive(".long\t" + new IntegerLiteral((int)value).toSource());
    }

    void _quad(long value){
        directive(".quad\t" + new IntegerLiteral(value).toSource());
    }

    void _byte(Literal literal){
        directive(".byte\t" + literal.toSource());
    }

    void _value(Literal literal){
        directive(".value\t" + literal.toSource());
    }

    void _long(Literal literal){
        directive(".long\t" + literal.toSource());
    }

    void _quad(Literal literal){
        directive(".quad\t" + literal.toSource());
    }

    void _string(String str){
        directive("\t.string\t" + TextUtils.dumpString(str));
    }

    /**
     * 虚拟栈
     * push 指令转换成 mov 质量
     * pushl %eax  <===>  movl %eax -(offset+4)(%ebp)
     *
     */
    class VirtualStack {

        protected long offset;
        protected long max;
        protected List<IndirectMemoryReference> memrefs =
                new ArrayList<IndirectMemoryReference>();

        VirtualStack() {

        }

        void reset(){
            this.offset = 0;
            this.max = 0;
            this.memrefs.clear();
        }

        long maxSize(){
            return max;
        }

        void extend(long len){
            this.offset += len;
            this.max = Math.max(max, offset);
        }

        void rewind(long len){
            this.offset -= len;
        }

        // 获取栈顶元素 -offset(%ebp)
        IndirectMemoryReference top(){
            IndirectMemoryReference mem = relocatableMem(-offset, bp());
            memrefs.add(mem);
            return mem;
        }

        private IndirectMemoryReference relocatableMem(long offset, Register base){
            return IndirectMemoryReference.relocatable(offset, base);
        }

        private Register bp(){
            return new Register(RegisterClass.BP, natureType);
        }

        void fixOffset(long diff){
            for (IndirectMemoryReference memref: memrefs){
                memref.fixOffset(diff);
            }
        }
    }

    // mov 指令代替 push 和 pop 指令， 使用虚拟栈实现
    void virtualPush(Register register){
        if(verbose){
            comment("push " + register.baseName() + " -> " + virtualStack.top());
        }
        virtualStack.extend(stackWordSize);
        mov(register, virtualStack.top());
    }

    void virtualPop(Register register){
        if(verbose){
            comment("pop " + virtualStack.top() + " -> " + register.baseName());
        }
        mov(virtualStack.top(), register);
        virtualStack.rewind(stackWordSize);

    }


    /**
     * instruction 指令
     *
     */

    void jmp(Label label){
        insn("jmp", new DirectMemoryReference(label.symbol()));
    }

    void jnz(Label label){
        insn("jnz", new DirectMemoryReference(label.symbol()));
    }

    void je(Label label){
        insn("je", new DirectMemoryReference(label.symbol()));
    }

    void cmp(Operand a, Register b){
        insn(b.type, "cmp", a, b);
    }

    void sete(Register register){
        insn("sete", register);
    }

    void setne(Register register){
        insn("setne", register);
    }

    void seta(Register register){
        insn("seta", register);
    }

    void setae(Register register){
        insn("setae", register);
    }

    void setb(Register register){
        insn("setb", register);
    }

    void setbe(Register register){
        insn("setbe", register);
    }

    void setg(Register register){
        insn("setg", register);
    }

    void setge(Register register){
        insn("setge", register);
    }

    void setl(Register register){
        insn("setl", register);
    }

    void setle(Register register){
        insn("setle", register);
    }

    //  bitwise AND 不改变操作数的值  运算结果为0 ZF=1  SF=结果最高位
    void test(Register a, Register b){
        insn(b.type, "test", a, b);
    }

    void push(Register register){
        insn("push", typeSuffix(natureType), register);
    }

    void pop(Register register){
        insn("pop", typeSuffix(natureType), register);
    }

    // call function by relative address
    void call(Symbol symbol){
        insn("call", new DirectMemoryReference(symbol));
    }
    // call function by absolute address
    // 函数指针
    void callAbsolute(Register register){
        insn("call", new AbsoluteAddress(register));
    }

    void ret(){
        insn("ret");
    }

    void mov(Register src, Register dst){
        insn(natureType, "mov", src, dst);
    }

    // 加载内存数据到寄存器  load
    void mov(Operand src, Register dst){
        insn(dst.type, "mov", src, dst);
    }

    // 保存寄存器数据到内存 save
    void mov(Register src, Operand dst){
        insn(src.type, "mov", src, dst);
    }
    // for stack access
    void relocatableMov(Register src, Operand dst){
        assemblies.add(new Instruction("mov",
                typeSuffix(src.type), src, dst, true));
    }

    // 符号扩展
    void movsx(Register src, Register dst){
        insn("movs", typeSuffix(src.type, dst.type), src, dst);
    }
    // 零扩展
    void movzx(Register src, Register dst){
        insn("movz", typeSuffix(src.type, dst.type), src, dst);
    }

    void movzb(Register src, Register dst){
        insn("movz", "b" + typeSuffix(dst.type), src, dst);
    }

    void lea(Operand src, Register dst){
        insn(natureType, "lea", src, dst);
    }

    void neg(Register register){
        insn(register.type, "neg", register);
    }

    void add(Operand diff, Register base){
        insn(base.type, "add", diff, base);
    }

    void sub(Operand diff, Register base){
        insn(base.type, "sub", diff, base);
    }

    void imul(Operand m, Register base){
        insn(base.type, "imul", m, base);
    }

    /**
     * 除法 ：idiv 和 div是对位宽两倍与除数的被除数进行运算
     * 位数   被除数     除数       商     余数
     * 8     ax      第一操作数    al     ah
     * 16    dx+ax   第一操作数    ax     dx
     * 32    edx+eax 第一操作数    eax    edx
     */
    void cltd(){
        // 32位除法时 对 edx 寄存器 进行符号扩展（零扩展 只需 将 edx 设置为 0）
        insn("ctld");
    }

    void idiv(Register base){
        insn(base.type, "idiv", base);
    }

    void div(Register base){
        insn(base.type, "div", base);
    }

    void not(Register register){
        insn(register.type, "not", register);
    }

    void and(Operand bits, Register base){
        insn(base.type, "and", bits, base);
    }

    void or(Operand bits, Register base){
        insn(base.type, "or", bits, base);
    }

    void xor(Operand bits, Register base){
        insn(base.type, "xor", bits, base);
    }

    void sar(Operand bits, Register base){
        insn(base.type, "sar", bits, base);
    }

    void sal(Operand bits, Register base){
        insn(base.type, "sal", bits, base);
    }

    void shr(Operand bits, Register base){
        // 算数右移， 高位零扩展
        insn(base.type, "shr", bits, base);
    }

}
