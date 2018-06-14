package net.loveruby.cflat.sysdep.x86;

import net.loveruby.cflat.asm.*;
import net.loveruby.cflat.entity.*;
import net.loveruby.cflat.ir.*;
import net.loveruby.cflat.sysdep.CodeGeneratorOptions;
import net.loveruby.cflat.type.NamedType;
import net.loveruby.cflat.utils.AsmUtils;
import net.loveruby.cflat.utils.ErrorHandler;
import net.loveruby.cflat.utils.ListUtils;

import java.util.*;

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
public class CodeGenerator implements net.loveruby.cflat.sysdep.CodeGenerator, IRVisitor<Void, Void> {

    final CodeGeneratorOptions options;
    final Type natureType;
    final ErrorHandler errorHandler;

    public CodeGenerator(CodeGeneratorOptions options, Type natureType, ErrorHandler errorHandler) {
        this.options = options;
        this.natureType = natureType;
        this.errorHandler = errorHandler;
    }

    /**
     *  alloca实现
     *      .text
     *  .global alloca
     *      .type   alloca, @function
     *  alloca:
     *      popl    %ecx
     *      movl    (%esp), %eax
     *      addl    $3, %eax
     *      andl    $-4, %eax
     *      subl    %eax, %esp
     *      leal    4(%esp), %eax
     *      jmp     *%ecx
     *      .size   alloca, .-alloca
     */

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
        // cfb_locate
        // 确定 参数 的内存位置
        locateParameters(function.parameters());
        // 确定 局部变量的位置 和 使用的最大栈长度
        frame.lvarSize = locateLocalVariable(function.lvarScope());

        // cfb_offset
        // 编译函数体 并进行优化， body保存了 函数体代码 和 虚拟栈中临时变量的数据
        AssemblyCode body = optimize(compileStmts(function));
        frame.saveRegs = usedCalleeSaveRegisters(body);
        frame.tempSize = body.virtualStack.maxSize();

        // 修正 局部变量和临时变量内存地址
        fixLocalVariableOffsets(function.lvarScope(), frame.lvarOffset());
        fixTempVariableOffsets(body, frame.tempOffset());

        if(options.isVerboseAsm()){
            // 打印帧栈布局
            printStackFrameLayout(file, frame, function.localVariables());
        }
        generateFunctionBody(file, body, frame);
    }

    private AssemblyCode optimize(AssemblyCode body){
        if(options.optimizeLevel() < 1){
            return body;
        }
        body.apply(PeepholeOptimizer.defaultSet());
        body.reduceLabels();
        return body;
    }

    private void printStackFrameLayout(AssemblyCode file,
                                       StackFrameInfo frame, List<DefinedVariable> lvars){
        List<MemInfo> vars = new ArrayList<MemInfo>();
        for(DefinedVariable variable: lvars){
            vars.add(new MemInfo(variable.memref(), variable.name()));
        }
        vars.add(new MemInfo(mem(0, bp()), "saved %ebp"));
        vars.add(new MemInfo(mem(4, bp()), "return address"));
        if(frame.saveRegisters() > 0){
            vars.add(new MemInfo(mem(-frame.saveRegisters(), bp()),
                    "saved callee-saved registers (" + frame.saveRegisters() + " bytes)"));
        }
        if(frame.tempSize > 0){
            vars.add(new MemInfo(mem(-frame.frameSize(), bp()),
                    "temp variables (" + frame.tempSize + " bytes)"));
        }
        Collections.sort(vars, new Comparator<MemInfo>() {
            public int compare(MemInfo o1, MemInfo o2) {
                return o1.mem.compareTo(o2.mem);
            }
        });
        file.comment("----- Stack Frame Layout -------------------");
        for(MemInfo memInfo: vars){
            file.comment(memInfo.mem.toString() + ": " + memInfo.name);
        }
        file.comment("---------------------------------------------");
    }

    class MemInfo {
        MemoryReference mem;
        String name;

        public MemInfo(MemoryReference mem, String name) {
            this.mem = mem;
            this.name = name;
        }
    }


    private AssemblyCode as;
    private Label epilogue;

    private AssemblyCode compileStmts(DefinedFunction function){
        // AssemblyCode 这个对象保存函数体代码和虚拟栈中临时变量信息
        as = newAssemblyCode();
        epilogue = new Label();
        for(Stmt stmt: function.ir()){
            compileStmt(stmt);
        }
        // 用于 return 关键字
        as.label(epilogue);
        return as;
    }

    // does NOT include BP
    private List<Register> usedCalleeSaveRegisters(AssemblyCode body){
        List<Register> registers = new ArrayList<Register>();
        for(Register register: callSaveRegisters()){
            if(body.doesUses(register)){
               registers.add(register);
            }
        }
        registers.remove(bp());
        return registers;
    }

    static final RegisterClass[] CALLEE_SAVE_REGISTERS = {
      RegisterClass.BX, RegisterClass.BP,
      RegisterClass.SI, RegisterClass.DI
    };

    private List<Register> callSaveRegistersCache = null;

    private List<Register> callSaveRegisters(){
        if(callSaveRegistersCache == null){
            List<Register> registers = new ArrayList<Register>();
            for(RegisterClass r: CALLEE_SAVE_REGISTERS){
                registers.add(new Register(r, natureType));
            }
            callSaveRegistersCache = registers;
        }
        return callSaveRegistersCache;
    }

    private void generateFunctionBody(AssemblyCode file, AssemblyCode body, StackFrameInfo frame){
        file.virtualStack.reset();
        prologue(file, frame.saveRegs, frame.frameSize());
        if(options.isPositionIndependent() && body.doesUses(GOTBaseReg())){
            loadGOTBaseAddress(file, GOTBaseReg());
        }
        file.addAll(body.assemblies());
        epilogue(file, frame.saveRegs);
        file.virtualStack.fixOffset(0);
    }

    // 函数序言
    private void prologue(AssemblyCode file,
                          List<Register> saveRegs, long frameSize){
        file.push(bp());
        file.mov(sp(), bp());
        for(Register reg: saveRegs){
            file.virtualPush(reg);
        }
        extendStack(file, frameSize);
    }

    // 函数尾声
    private void epilogue(AssemblyCode file, List<Register> saveRegs){
        // 弹出的时候 寄存器 顺序要改变
        for(Register reg: ListUtils.reverse(saveRegs)){
            file.virtualPop(reg);
        }
        file.mov(bp(), sp());
        file.pop(bp());
        file.ret();
    }

    private void extendStack(AssemblyCode file, long len){
        if(len > 0){
            file.sub(imm(len), sp());
        }
    }

    private void rewindStack(AssemblyCode file, long len){
        if(len > 0){
            file.add(imm(len), sp());
        }
    }

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
     *
     * --------------------   esp #2 (stack top after alloca call)
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
     */
    private long locateLocalVariable(LocalScope scope){
        return locateLocalVarialbe(scope, 0);
    }
    // 确定局部变量的位置
    private long locateLocalVarialbe(LocalScope scope, long parentStackLen){
        long len = parentStackLen;
        for(DefinedVariable variable: scope.localVariables()){
            len = assignStack(len + variable.allocSize());
            variable.setMemref(relocatableMem(-len, bp()));
        }
        long maxLen = len;
        for(LocalScope s : scope.children()){
            long child = locateLocalVarialbe(s, len);
            maxLen = Math.max(maxLen, child);
        }
        return maxLen;
    }

    private IndirectMemoryReference relocatableMem(long offset, Register base){
        return IndirectMemoryReference.relocatable(offset, base);
    }
    // 考虑 callee-save register后修正局部变量的内存地址
    private void fixLocalVariableOffsets(LocalScope scope, long len){
        for(DefinedVariable variable: scope.allLocalVariables()){
            variable.memref().fixOffset(-len);
        }
    }

    private void fixTempVariableOffsets(AssemblyCode asm, long len){
        asm.virtualStack.fixOffset(-len);
    }

    /**
     * Implements cdecl function call:
     *    * All arguments are on stack.
     *    * Caller rewinds stack pointer.
     */
    public Void visit(Call node){
        for(Expr arg: ListUtils.reverse(node.args())){
            compile(arg);
            as.push(ax());
        }
        // 是否函数指针
        if(node.isStaticCall()){
            as.call(node.function().callingSymbol());
        }else {
            compile(node.expr());
            as.callAbsolute(ax());
        }
        rewindStack(as, stackSizeFromWordNum(node.numArgs()));
        return null;
    }

    public Void visit(Return node){
        if(node.expr() != null){
            compile(node.expr());
        }
        as.jmp(epilogue);
        return null;
    }
    //
    // Statements
    //

    private void compileStmt(Stmt stmt){
        if(options.isVerboseAsm()){
            if(stmt.location() != null){
                as.comment(stmt.location().numberedLine());
            }
        }
        stmt.accept(this);
    }

    public Void visit(ExprStmt node){
        compile(node.expr());
        return null;
    }

    public Void visit(LabelStmt s) {
        as.label(s.label());
        return null;
    }

    public Void visit(Jump s) {
        as.jmp(s.label());
        return null;
    }

    public Void visit(CJump s) {
        compile(s.cond());
        Type type = s.cond().type();
        as.test(ax(type), ax(type));
        // test 结果 非0 时 跳转jump if no zero
        as.jnz(s.thenLabel());
        as.jmp(s.elseLabel());
        return null;
    }

    public Void visit(Switch s) {
        compile(s.cond());
        Type type = s.cond().type();
        for(Case c: s.cases()){
            as.mov(imm(c.value), cx());
            as.cmp(cx(type), ax(type));
            as.je(c.label);
        }
        as.jmp(s.defaultLabel());
        return null;
    }

    //
    // Expressions
    //

    private void compile(Expr expr){
        if(options.isVerboseAsm()){
            as.comment(expr.getClass().getSimpleName() + " {");
            as.indentComment();
        }
        expr.accept(this);
        if(options.isVerboseAsm()){
            as.unindentComment();
            as.comment("}");
        }
    }

    public Void visit(Bin node) {
        Op op = node.op();
        Type type = node.type();
        if(node.right().isConstant() && !doesRequireRegisterOperand(op)){
            compile(node.left());
            compileBinaryOp(op, ax(type), node.right().asmValue());
        }else if(node.right().isConstant()){
            compile(node.left());
            loadConstant(node.right(), cx());
            compileBinaryOp(op, ax(type), cx(type));
        }else if(node.right().isVar()){
            compile(node.left());
            loadVariable((Var) node.right(), cx());
            compileBinaryOp(op, ax(type), cx(type));
        }else if(node.right().isAddr()){
            compile(node.left());
            loadAddress(node.right().getEntityForce(), cx());
            compileBinaryOp(op, ax(type), cx(type));
        }else if(node.left().isConstant()
                || node.left().isVar()
                || node.left().isAddr()){
            compile(node.right());
            as.mov(ax(), cx());
            compile(node.left());
            compileBinaryOp(op, ax(type), cx(type));
        }else {
            compile(node.right());
            as.virtualPush(ax());
            compile(node.left());
            as.virtualPop(cx());
            compileBinaryOp(op, ax(type), cx(type));
        }
        return null;
    }

    private boolean doesRequireRegisterOperand(Op op){
        switch (op){
            case S_DIV:
            case U_DIV:
            case S_MOD:
            case U_MOD:
            case BIT_LSHIFT:
            case BIT_RSHIFT:
            case ARITH_RSHIFT:
                return true;
            default:
                return false;
        }
    }

    private void compileBinaryOp(Op op, Register left, Operand right){
        switch (op){
            case ADD:
                as.add(right, left);
                break;
            case SUB:
                as.sub(right, left);
                break;
            case MUL:
                as.imul(right, left);
                break;
            case S_DIV:
            case S_MOD:
                // 符号扩展 edx
                as.cltd();
                as.idiv(cx(left.type));
                if(op == Op.S_MOD){
                    as.mov(dx(), left);
                }
                break;
            case U_DIV:
            case U_MOD:
                // 零扩展 edx
                as.mov(imm(0), dx());
                as.div(cx(left.type));
                if(op == Op.U_MOD){
                    as.mov(dx(), left);
                }
                break;
            case BIT_AND:
                as.and(right, left);
                break;
            case BIT_OR:
                as.or(right, left);
                break;
            case BIT_XOR:
                as.xor(right, left);
                break;
            case BIT_LSHIFT:
                as.sal(cl(), left);
                break;
            case BIT_RSHIFT:
                as.shr(cl(), left);
                break;
            case ARITH_RSHIFT:
                as.sar(cl(), left);
                break;
            default:
                as.cmp(right, ax(left.type));
                switch (op){
                    case EQ: as.sete(al()); break;
                    case NEQ: as.setne(al()); break;
                    case S_GT: as.setg(al()); break;
                    case S_GTEQ: as.setge(al()); break;
                    case S_LT: as.setl(al()); break;
                    case S_LTEQ: as.setle(al()); break;
                    case U_GT: as.seta(al()); break;
                    case U_GTEQ: as.setae(al()); break;
                    case U_LT: as.setb(al()); break;
                    case U_LTEQ: as.setbe(al()); break;
                    default:
                        throw new Error("unknown binary operator: " + op);
                }
                as.movzx(al(), left);
        }

    }

    public Void visit(Uni node){
        Type src = node.expr().type();
        Type dest = node.type();

        compile(node.expr());
        switch (node.op()){
            case UMINUS:
                as.neg(ax(src));
                break;
            case BIT_NOT:
                as.not(ax(src));
                break;
            case NOT:
                as.test(ax(src), ax(dest));
                as.sete(al());
                as.movzx(al(), ax(dest));
                break;
            case S_CAST:
                as.movsx(ax(src), ax(dest));
                break;
            case U_CAST:
                as.movzx(ax(src), ax(dest));
                break;
            default:
                throw new Error("unknown unary operator: " + node.op());
        }
        return null;
    }

    public Void visit(Var node){
        loadVariable(node, ax());
        return null;
    }

    public Void visit(Int node){
        as.mov(imm(node.value()), ax());
        return null;
    }

    public Void visit(Str node){
        loadConstant(node, ax());
        return null;
    }

    //
    // Assignable expressions
    //

    public Void visit(Assign node){
        if(node.lhs().isAddr() && node.lhs().memref() != null){
            compile(node.rhs());
            store(ax(node.lhs().type()), node.lhs().memref());
        }else if(node.rhs().isConstant()){
            compile(node.lhs());
            as.mov(ax(), cx());
            loadConstant(node.rhs(), ax());
            store(ax(node.lhs().type()), mem(cx()));
        }else {
            compile(node.rhs());
            as.virtualPush(ax());
            compile(node.lhs());
            as.mov(ax(), cx());
            as.virtualPop(ax());
            store(ax(node.lhs().type()), mem(cx()));
        }
        return null;
    }

    public Void visit(Mem node) {
        compile(node.expr());
        load(mem(ax()), ax(node.type()));
        return null;
    }

    public Void visit(Addr node) {
        loadAddress(node.entity(), ax());
        return null;
    }

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
