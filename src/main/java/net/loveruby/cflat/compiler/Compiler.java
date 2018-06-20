package net.loveruby.cflat.compiler;

import com.sun.org.apache.regexp.internal.RE;
import net.loveruby.cflat.ast.AST;
import net.loveruby.cflat.ast.ExprNode;
import net.loveruby.cflat.ast.StmtNode;
import net.loveruby.cflat.exception.*;
import net.loveruby.cflat.ir.IR;
import net.loveruby.cflat.parser.Parser;
import net.loveruby.cflat.sysdep.AssemblyCode;
import net.loveruby.cflat.type.TypeTable;
import net.loveruby.cflat.utils.ErrorHandler;

import java.io.*;
import java.util.List;

/**
 * @author 刘科  2018/6/20
 */
public class Compiler {

    static public final String ProgramName = "cbc";
    static public final String Version = "1.0.0";

    public static void main(String[] args) {
        new Compiler(ProgramName).commandMain(args);
    }

    private final ErrorHandler errorHandler;

    public Compiler(String programName) {
        this.errorHandler = new ErrorHandler(programName);
    }

    public void commandMain(String[] args){
        Options options = parseOptions(args);
        if(options.mode() == CompilerMode.CheckSyntax){

        }
    }

    private Options parseOptions(String[] args){
        try {
            return Options.parse(args);
        } catch (OptionParserError e) {
            errorHandler.error(e.getMessage());
            errorHandler.error("Try \"cbc --help\" for usage");
            System.exit(1);
            return null;
        }
    }

    private boolean checkSyntax(Options options){
        boolean failed = false;
        for(SourceFile src: options.sourceFiles()){
            if(isValidSyntax(src.path(), options)){
                System.out.println(src.path() + ": syntax OK");
            }else {
                System.out.println(src.path() + ": syntax error");
                failed = false;
            }
        }
        return !failed;
    }

    private boolean isValidSyntax(String path, Options options){
        try {
            parseFile(path, options);
            return true;
        } catch (SyntaxException e) {
            return false;
        } catch (FileException e) {
            errorHandler.error(e.getMessage());
            return false;
        }
    }

    public void bulid(List<SourceFile> sourceFiles, Options options)
        throws CompileException {
        for(SourceFile src: sourceFiles){
            if(src.isCflatSource()){
                String destPath = options.asmFileNameOf(src);
                compile(src.path(), destPath, options);
                src.setCurrentName(destPath);
            }
            if(!options.isAssembleRequired()) continue;
            if(src.isAssemblySource()){
                String destPath = options.objectNameOf(src);
                assemble(src.path(), destPath, options);
                src.setCurrentName(destPath);
            }
        }
        if(!options.isLinkRequired()){
            return;
        }
        link(options);
    }

    public void compile(String srcPath, String destPath, Options options)
        throws CompileException {
        AST ast = parseFile(srcPath, options);
        if(dumpAST(ast, options.mode())) return;
        TypeTable types = options.typeTable();
        AST sem = semanticAnalyze(ast, types, options);
        if(dumpSemant(sem, options.mode())) return;
        IR ir = new IRGenerator(types, errorHandler).generate(sem);
        if(dumpIR(ir, options.mode())) return;
        AssemblyCode asm = generateAssembly(ir, options);
        if(dumpAsm(asm, options.mode())) return;
        if(printAsm(asm, options.mode())) return;
        writeFile(destPath, asm.toSource());
    }

    public AST parseFile(String path, Options options)
        throws SyntaxException, FileException{
        return Parser.parseFile(new File(path), options.loader(), errorHandler, options.doesDebugParser());
    }

    public AST semanticAnalyze(AST ast, TypeTable typeTable, Options options) throws SemanticException{
        new LocalResolver(errorHandler).resolve(ast);
        new TypeResolver(typeTable, errorHandler).resolve(ast);
        typeTable.semanticCheck(errorHandler);
        if(options.mode() == CompilerMode.DumpReference){
            ast.dump();
            return ast;
        }
        new DereferenceChecker(typeTable, errorHandler).check(ast);
        new TypeChecker(typeTable, errorHandler).check(ast);
        return ast;
    }

    public AssemblyCode generateAssembly(IR ir, Options options){
        return options.codeGenerator(errorHandler).generate(ir);
    }

    public void assemble(String srcPath, String destPath, Options options)
        throws IPCException{
        options.assembler(errorHandler).assemble(srcPath, destPath, options.asOptions());
    }

    public void link(Options options) throws IPCException{
        if(!options.isGeneratingSharedLibrary()){
            generateExecutable(options);
        }else {
            generateSharedLibrary(options);
        }
    }

    public void generateExecutable(Options options) throws IPCException{
        options.linker(errorHandler).generateExecutable(options.ldArgs(), options.exeFileName(), options.ldOptions());
    }

    public void generateSharedLibrary(Options options) throws IPCException{
        options.linker(errorHandler).generateSharedLibrary(options.ldArgs(), options.soNameFile(), options.ldOptions());
    }

    private void writeFile(String path, String str) throws FileException {
        if(path.equals("-")){
            System.out.println(str);
            return;
        }
        try {
            BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(new FileOutputStream(path)));
            try {
                writer.write(str);
            }finally {
                writer.close();
            }
        } catch (FileNotFoundException e) {
            errorHandler.error("file not found: " + path);
            throw new FileException("file error");
        } catch (IOException e){
            errorHandler.error("IO error" + e.getMessage());
            throw new FileException("file error");
        }
    }

    private boolean dumpAST(AST ast, CompilerMode mode){
        switch (mode){
            case DumpTokens:
                ast.dumpTokens(System.out);
                return true;
            case DumpAST:
                ast.dump();
                return true;
            case DumpStmt:
                findStmt(ast).dump();
                return true;
            case DumpExpr:
                findExpr(ast).dump();
                return true;
            default:
                return false;
        }
    }

    private StmtNode findStmt(AST ast){
        StmtNode stmt = ast.getSingleMainStmt();
        if (stmt == null) {
            errorExit("source file does not contains main()");
        }
        return stmt;
    }

    private ExprNode findExpr(AST ast){
        ExprNode expr = ast.getSingleMainExpr();
        if (expr == null) {
            errorExit("source file does not contains single expression");
        }
        return expr;
    }

    private boolean dumpSemant(AST ast, CompilerMode mode){
        switch (mode){
            case DumpReference:
                return true;
            case DumpSemantic:
                ast.dump();
                return true;
            default:
                return false;
        }
    }

    private boolean dumpIR(IR ir, CompilerMode mode){
        if(mode == CompilerMode.DumpIR){
            ir.dump();
            return true;
        }
        return false;
    }

    private boolean dumpAsm(AssemblyCode assemblyCode, CompilerMode mode){
        if(mode == CompilerMode.DumpASM){
            assemblyCode.dump();
            return true;
        }
        return false;
    }

    private boolean printAsm(AssemblyCode asm, CompilerMode mode) {
        if (mode == CompilerMode.PrintASM) {
            System.out.print(asm.toSource());
            return true;
        }
        else {
            return false;
        }
    }

    private void errorExit(String msg){
        errorHandler.error(msg);
        System.exit(1);
    }
}
