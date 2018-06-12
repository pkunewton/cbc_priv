package net.loveruby.cflat.sysdep.x86;

import net.loveruby.cflat.asm.*;
import net.loveruby.cflat.entity.*;
import net.loveruby.cflat.ir.*;
import net.loveruby.cflat.sysdep.CodeGeneratorOptions;
import net.loveruby.cflat.utils.AsmUtils;
import net.loveruby.cflat.utils.ErrorHandler;

import java.util.List;

/**
 * @author 刘科  2018/6/10
 * generate
 *      locateSymbols
 *          locateStringLiteral
 *          locateGlobalVariable
 *          locateFunction
 *      compileIR
 *          compileGlobalVariable
 *          compileStringLiteral
 *          compileFunction
 *              compileFunctionBody
 *                  compileStmts
 *                  generateFunctionBody
 *          compileCommonSymbol
 *          PICThunk
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
        // 确定 每个变量，字符串和函数 设置符号，代码生成过程中会使用这些符号
        // 对于变量，则把这些符号作为地址
        locateSymbol(ir);
        return generateAssemblyCode(ir);
    }

    static final String LABEL_SYMBOL_BASE = ".L";
    static final String CONST_SYMBOL_BASE = ".LC";


    //
    // locateSymbols
    //

    private void locateSymbol(IR ir){
        SymbolTable symbolTable = new SymbolTable(CONST_SYMBOL_BASE);
        for(ConstantEntry entry: ir.constantTable().entries()){
            locateStringLiteral(entry, symbolTable);
        }
        for(Variable variable: ir.allGlobalVarialbes()){
            locateGlobalVariable(variable);
        }

        for(Function function: ir.allFunctions()){
            locateFunction(function);
        }
    }

    private void locateStringLiteral(ConstantEntry entry, SymbolTable symbolTable){
        entry.setSymbol(symbolTable.newSymbol());
        if(options.isPositionIndependent()){
            Symbol offset = localGOTSymbol(entry.symbol());     // var@GOTOFF
            entry.setMemref(mem(offset, GOTBaseReg()));         // var@GOTOFF(%ebx)
        }else {
            entry.setMemref(mem(entry.symbol()));    // symbol    直接地址引用
            entry.setAddress(imm(entry.symbol()));   // $symbol   地址
        }
    }

    /**
     *  定义时有初值的全局变量 是 一般符号 .data 默认可见性 local
     *  没有初值 是 通用符号            .comm  默认可见性 global
     */
    private void locateGlobalVariable(Entity entity){
        Symbol symbol = sym(entity.symbolString(), entity.isPrivate());
        if(options.isPositionIndependent()){
            // static变量 和 已定义的变量
            // 私有变量 和 已定义的变量 在文件内部
            // 声明的变量 不在当前文件
            if(entity.isPrivate() || optimizeGvarAccess(entity)){
                entity.setMemref(mem(localGOTSymbol(symbol), GOTBaseReg()));   // var@GOT
            }else {
                entity.setAddress(mem(globalGOTSymbol(symbol), GOTBaseReg())); // var@GOTOFF
            }
        }else {
            entity.setMemref(mem(symbol));
            entity.setAddress(imm(symbol));
        }
    }

    private void locateFunction(Function function){
        function.setCallingSymbol(callingSymbol(function));
        locateGlobalVariable(function);
    }

    private Symbol sym(String sym, boolean isPrivate){
        return isPrivate ? privateSymbol(sym) : globalSymbol(sym);
    }

    private Symbol globalSymbol(String sym){
        return new NamedSymbol(sym);
    }

    private Symbol privateSymbol(String sym){
        return new NamedSymbol(sym);
    }

    private Symbol callingSymbol(Function function){
        if(function.isPrivate()){
            return privateSymbol(function.symbolString());
        }else {
            Symbol symbol = globalSymbol(function.symbolString());
            return shouldUsePLT(function) ? PLTSymbol(symbol) : symbol;
        }
    }

    // 未定义的函数才会使用PLT
    private boolean shouldUsePLT(Entity entity){
        return options.isPositionIndependent() && !optimizeGvarAccess(entity);
    }

    private boolean optimizeGvarAccess(Entity entity){
        return options.isPIERequired() && entity.isDefined();
    }


    //
    // generateAssemblyCode
    //

    private AssemblyCode generateAssemblyCode(IR ir){
        AssemblyCode file = newAssemblyCode();
        file._file(ir.fileName());
        if(ir.isGlobalVariableDefined()){
            generateDataSection(file, ir.definedGlobalVariables());
        }
        if(ir.isStringLiteralDefined()){
            generateReadOnlyDataSection(file, ir.constantTable());
        }
        if(ir.isFunctionDefined()){
            generateTextSection(file, ir.definedFunctions());
        }
        if(ir.isCommonSymbolDefined()){
            generateCommonSymbols(file, ir.definedCommonSymbols());
        }
        if(options.isPositionIndependent()){
            PICThunk(file, GOTBaseReg());
        }
        return file;
    }

    private AssemblyCode newAssemblyCode(){
        return new AssemblyCode(natureType,
                STACK_WORD_SIZE,
                new SymbolTable(CONST_SYMBOL_BASE),
                options.isVerboseAsm());
    }

    /** Generates initialized entries */
    private void generateDataSection(AssemblyCode file, List<DefinedVariable> gvars){
        file._data();
        for(DefinedVariable var: gvars){
            Symbol symbol = globalSymbol(var.symbolString());
            // 通用符号 默认可见性 是 local
            if(!var.isPrivate()){
                file._global(symbol);
            }
            file._align(var.alignment());
            file._type(symbol, "@object");
            file._size(symbol, var.allocSize());
            file.label(symbol);
            generateImmediate(file, var.type().allocSize(), var.ir());
        }
    }

    /** Generates immediate values for .data section */
    private void generateImmediate(AssemblyCode file, long size, Expr node){
        if(node instanceof Int){
            Int expr = (Int)node;
            switch ((int) size){
                case 1: file._byte(expr.value()); break;
                case 2: file._value(expr.value()); break;
                case 4: file._long(expr.value()); break;
                case 8: file._quad(expr.value()); break;
                default:
                    throw new Error("int entry size must be 1, 2, 4, 8");
            }
        }else if(node instanceof Str){
            Str expr = (Str)node;
            // str 是数组类型， size() 是指针大小
            switch ((int)size){
                case 4: file._long(expr.symbol()); break;
                case 8: file._quad(expr.symbol()); break;
                default:
                    throw new Error("pointer size must be 4, 8");
            }
        }else {
            throw new Error("unknown literal node type: " + node.getClass());
        }
    }

    /** Generates .rodata entries (constant strings) */
    private void generateReadOnlyDataSection(AssemblyCode file, ConstantTable constants){
        file._section(".rodata");
        for (ConstantEntry entry: constants){
            file.label(entry.symbol());
            file._string(entry.value());
        }
    }

    private void generateTextSection(AssemblyCode file, List<DefinedFunction> functions){
        file._text();
        for(DefinedFunction function: functions){
            Symbol symbol = globalSymbol(function.symbolString());
            if(!function.isPrivate()){
                file._global(symbol);
            }
            file._type(symbol, "@function");
            file.label(symbol);
            compileFunctionBody(file, function);
            // "." 表示当前位置， . - symbol 表示当前位置 到 符号 的长度
            file._size(symbol, ".-" + symbol.toSource());
        }
    }

    /** Generates BSS entries */
    private void generateCommonSymbols(AssemblyCode file, List<DefinedVariable> variables){
        for (DefinedVariable var: variables){
            Symbol symbol = globalSymbol(var.symbolString());
            // 通用符号可见性为 global
            if(var.isPrivate()){
                file._local(symbol);
            }
            // .comm 符号, 大小, 对齐量
            file._comm(symbol, var.allocSize(), var.alignment());
        }
    }

    //
    // PIC/PIE related constants and codes
    //

    private static final Symbol GOT = new NamedSymbol("_GLOBAL_OFFSET_TABLE");

    private void loadGOTBaseAddress(AssemblyCode file, Register register){
        file.call(PICThunkSymbol(register));
        file.add(imm(GOT), register);
    }

    private Register GOTBaseReg(){
        return bx();
    }

    private Symbol globalGOTSymbol(Symbol base){
        return new SuffixedSymbol(base, "@GOT");
    }

    private Symbol localGOTSymbol(Symbol base){
        return new SuffixedSymbol(base, "@GOTOFF");
    }

    private Symbol PLTSymbol(Symbol base){
        return new SuffixedSymbol(base, "@PLT");
    }

    private Symbol PICThunkSymbol(Register register){
        return new NamedSymbol("__i686.get_pc_thunk." + register.baseName());
    }

    private static final String PICThunkSectionFlag = ELFConstants.SectionFlag_allocatable
                                    + ELFConstants.SectionFlag_executable
                                    + ELFConstants.SectionFlag_sectiongroup; // "axG"

    /**
     * Output PIC thunk.
     * ELF section declaration format is:
     *
     *     .section NAME, FLAGS, TYPE, flag_arguments
     *
     * FLAGS, TYPE, flag_arguments are optional.
     * For "M" flag (a member of a section group),
     * following format is used:
     *
     *     .section NAME, "...M", TYPE, section_group_name, linkage
     *
     *  result:
     *      .section    .text.__i686.get_pc_thunk.bx, "axG", @progbits, __i686.get_pc_thunk.bx, comdat
     *      .global __i686.get_pc_thunk.bx
     *              .hidden __i686.get_pc_thunk.bx
     *              .type   __i686.get_pc_thunk.bx, @function
     *      __i686.get_pc_thunk.bx:
     *          movl    (%esp)  %ebx
     *          ret
     */
    private void PICThunk(AssemblyCode file, Register register){
        Symbol symbol = PICThunkSymbol(register);
        // section    .text.__i686.get_pc_thunk.bx    多出来的 "." 没看出来什么意思
        file._section(".text" + "." + symbol.toSource(),
                "\"" + PICThunkSectionFlag + "\"",
                ELFConstants.SectionType_bits,          // This section contains data
                symbol.toSource(),                      // The name of section group
                ELFConstants.Linkage_linkonce);         // Only 1 copy should be generated
        file._global(symbol);
        file._hidden(symbol);
        file._type(symbol, ELFConstants.SymbolType_function);
        file.label(symbol);
        file.mov(mem(sp()), register);                  // fetch saved EIP to the GOT base register
        file.ret();
    }


    //
    // compile function
    //

    /** Standard IA-32 stack frame layout
     *
     * ======================= esp #3 (stack top just before function call)
     * next arg 1
     * ---------------------
     * next arg 2
     * ---------------------
     * next arg 3
     * ---------------------   esp #2 (stack top after alloca call)
     * alloca area
     * ---------------------   esp #1 (stack top just after prelude)
     * temporary
     * variables...
     * ---------------------   -16(%ebp)
     * lvar 3
     * ---------------------   -12(%ebp)
     * lvar 2
     * ---------------------   -8(%ebp)
     * lvar 1
     * ---------------------   -4(%ebp)
     * callee-saved register
     * ======================= 0(%ebp)
     * saved ebp
     * ---------------------   4(%ebp)
     * return address
     * ---------------------   8(%ebp)
     * arg 1
     * ---------------------   12(%ebp)
     * arg 2
     * ---------------------   16(%ebp)
     * arg 3
     * ...
     * ...
     * ======================= stack bottom
     */

    static final private long STACK_WORD_SIZE = 4;

    private long assignStack(long size){
        return AsmUtils.align(size, STACK_WORD_SIZE);
    }

    private long stackSizeFromWordNum(long numWords){
        return STACK_WORD_SIZE + numWords;
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

    private void compileFunctionBody(AssemblyCode file, DefinedFunction function){
        StackFrameInfo frame = new StackFrameInfo();


    }


    private AssemblyCode as;
    private Label epilogue;

    /**
     * ======================= 0(%ebp)
     * saved ebp
     * ---------------------   4(%ebp)
     * return address
     * ---------------------   8(%ebp)
     * arg 1
     * ---------------------   12(%ebp)
     * arg 2
     * ---------------------   16(%ebp)
     * arg 3
     * ...
     * 函数参数从 8(%ebp)开始
     */
    private static final long PARAM_START_WORD = 2;

    private void locateParameters(List<Parameter> parameters){
        long numWords = PARAM_START_WORD;
        for(Parameter var: parameters){
            var.setMemref(mem(stackSizeFromWordNum(numWords), bp()));
            numWords++;
        }
    }

    /**
     * Allocates addresses of local variables, but offset is still
     * not determined, assign unfixed IndirectMemoryReference.
     */
    private long locateLocalVariable(LocalScope scope){
        return locateLocalVarialbe(scope, 0);
    }

    private long locateLocalVarialbe(LocalScope scope, long parentStackLen){

        return 0;
    }

    //
    // Statements
    //


    //
    // Expressions
    //


    //
    // Assignable expressions
    //




    //
    // Utilities
    //

    /**
     * Loads constant value.  You must check node by #isConstant
     * before calling this method.
     */
    private void loadConstant(Expr expr, Register register){
        if(expr.asmValue() != null){
            as.mov(expr.asmValue(), register);
        }else if(expr.memref() != null) {
            as.lea(expr.memref(), register);
        }else {
            throw new Error("must not happen: constant has no asm value");
        }
    }

    /** Loads variable content to the register. */
    private void loadVariable(Var var, Register dest){
        if(var.memref() == null){
            Register reg = dest.forType(natureType);
            as.mov(var.address(), reg);
            load(mem(reg), dest.forType(var.type()));
        }else {
            load(var.memref(), dest.forType(var.type()));
        }
    }

    /** Loads the address of the variable to the register. */
    private void loadAddress(Entity var, Register dest){
        if(var.address() != null){
            as.mov(var.address(), dest);
        }else {
            as.lea(var.memref(), dest);
        }
    }


    private Register ax(){
        return ax(natureType);
    }

    private Register al(){
        return ax(Type.INT8);
    }

    private Register bx(){
        return bx(natureType);
    }

    private Register cx(){
        return cx(natureType);
    }

    private Register cl(){
        return cx(Type.INT8);
    }

    private Register dx(){
        return dx(natureType);
    }

    private Register ax(Type type){
        return new Register(RegisterClass.AX, type);
    }

    private Register bx(Type type){
        return new Register(RegisterClass.BX, type);
    }

    private Register cx(Type type){
        return new Register(RegisterClass.CX, type);
    }

    private Register dx(Type type){
        return new Register(RegisterClass.DX, type);
    }

    private Register si(){
        return new Register(RegisterClass.SI, natureType);
    }

    private Register di(){
        return new Register(RegisterClass.DI, natureType);
    }

    private Register bp(){
        return new Register(RegisterClass.BP, natureType);
    }

    private Register sp(){
        return new Register(RegisterClass.SP, natureType);
    }


    private DirectMemoryReference mem(Symbol symbol){
        return new DirectMemoryReference(symbol);
    }

    private IndirectMemoryReference mem(Register register){
        return new IndirectMemoryReference(0, register);
    }

    private IndirectMemoryReference mem(long offset, Register register){
        return new IndirectMemoryReference(offset, register);
    }

    private IndirectMemoryReference mem(Symbol offset, Register register){
        return new IndirectMemoryReference(offset, register);
    }

    private ImmediateValue imm(long n){
        return new ImmediateValue(n);
    }

    private ImmediateValue imm(Symbol symbol){
        return new ImmediateValue(symbol);
    }

    private ImmediateValue imm(Literal literal){
        return new ImmediateValue(literal);
    }



    private void load(MemoryReference mem, Register reg){
        as.mov(mem, reg);
    }

    private void store(Register reg, MemoryReference mem){
        as.mov(reg, mem);
    }

}
