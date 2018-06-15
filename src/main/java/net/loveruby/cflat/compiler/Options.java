package net.loveruby.cflat.compiler;

import com.sun.org.apache.regexp.internal.RE;
import net.loveruby.cflat.exception.OptionParserError;
import net.loveruby.cflat.parser.LibraryLoader;
import net.loveruby.cflat.sysdep.*;
import net.loveruby.cflat.type.TypeTable;
import net.loveruby.cflat.utils.ErrorHandler;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ListIterator;

/**
 * @author 刘科  2018/6/15
 */
public class Options {

    static Options parse(String[] args){
        Options options = new Options();

        return options;
    }

    private CompilerMode mode;
    private Platform platform = new X86Linux();
    private String outputFileName;
    private boolean verbose = false;
    private LibraryLoader loader = new LibraryLoader();
    private boolean debugParser = false;
    private CodeGeneratorOptions cgOptions = new CodeGeneratorOptions();
    private AssemblerOptions asOptions = new AssemblerOptions();
    private LinkerOptions ldOptions = new LinkerOptions();
    private List<LdArg> ldArgs;
    private List<SourceFile> sourceFiles;

    CompilerMode mode(){
        return mode;
    }

    boolean isAssembleRequired(){
        return mode.requires(CompilerMode.Assemble);
    }

    boolean isLinkRequired(){
        return mode.requires(CompilerMode.Link);
    }

    List<SourceFile> sourceFiles(){
        return sourceFiles;
    }

    String asmFileNameOf(SourceFile sourceFile){
        if(outputFileName != null && mode == CompilerMode.Compile){
            return outputFileName;
        }
        return sourceFile.asmFileName();
    }

    String objectNameOf(SourceFile sourceFile){
        if(outputFileName != null && mode == CompilerMode.Assemble){
            return outputFileName;
        }
        return sourceFile.objectFileName();
    }

    String exeFileName(){
        return linkedFileName("");
    }

    String soNameFile(){
        return linkedFileName(".so");
    }


    static private final String DEFAULT_LINKER_OUTPUT = "a.out";

    private String linkedFileName(String newExt){
        if(outputFileName != null){
            return outputFileName;
        }else if(sourceFiles.size() == 1){
            return sourceFiles.get(0).linkedFileName(newExt);
        }else {
            return DEFAULT_LINKER_OUTPUT;
        }

    }

    String outputFileName(){
        return this.outputFileName;
    }

    boolean isVerboseMode(){
        return this.verbose;
    }

    boolean doesDebugParser(){
        return this.debugParser;
    }

    LibraryLoader loader(){
        return this.loader;
    }

    TypeTable typeTable(){
        return platform.typeTable();
    }

    CodeGenerator codeGenerator(ErrorHandler errorHandler){
        return platform.codeGenerator(cgOptions, errorHandler);
    }

    Assembler assembler(ErrorHandler errorHandler){
        return platform.assembler(errorHandler);
    }

    AssemblerOptions asOptions(){
        return this.asOptions;
    }

    Linker linker(ErrorHandler errorHandler){
        return platform.linker(errorHandler);
    }

    LinkerOptions ldOptions(){
        return this.ldOptions;
    }

    List<String> ldArgs(){
        List<String> result = new ArrayList<String>();
        for(LdArg arg: ldArgs){
            result.add(arg.toString());
        }
        return result;
    }

    boolean isGeneratingSharedLibrary(){
        return ldOptions.generatingSharedLibary;
    }

    void parseArgs(String[] origArgs){
        sourceFiles = new ArrayList<SourceFile>();
        ldArgs = new ArrayList<LdArg>();
        ListIterator<String> args = Arrays.asList(origArgs).listIterator();
        while (args.hasNext()){
            String arg = args.next();
            if(arg.equals("--")){
                // "--" Stops command line processing
                break;
            }else if(arg.startsWith("-")){
                if(CompilerMode.isModeOption(arg)){
                    if(mode != null){
                        parserError(mode.toOption() +
                                " option and " + arg + " option is exclusive");
                        mode = CompilerMode.fromOption(arg);
                    }else if (arg.startsWith("-I")){
                        // 头文件查找路径
                        loader.addLoadPath(getOptArg(arg, args));
                    }else if (arg.equals("--debug-parser")){
                        debugParser = true;
                    }else if (arg.startsWith("-o")){
                        outputFileName = getOptArg(arg, args);
                    }else if (arg.equals("-fPIC")||arg.equals("-fpic")){
                        cgOptions.generatePIC();
                    }else if (arg.equals("-fPIE")||arg.equals("-fpie")){
                        cgOptions.generatePIE();
                    }else if (arg.startsWith("-O")){
                        String level = arg.substring(2);
                        if (!level.matches("^([0123s])$")){
                            parserError("unknown optimization switch: " + arg);
                        }
                        cgOptions.setOptimizeLevel(level.equals("0") ? 0 : 1);
                    }else if (arg.equals("-fverbose-asm")||arg.equals("--verbose-asm")){
                        cgOptions.generateVerboseAsm();
                    }else if (arg.startsWith("-Wa,")){
                        for(String a: parseCommaSeparatedOptions(arg)){
                            asOptions.addArg(a);
                        }
                    }else if (arg.equals("-Xassembler")){
                        asOptions.addArg(nextArg(arg, args));
                    }else if (arg.equals("-static")){
                        addLdArg(arg);
                    }else if (arg.equals("--shared")){
                        ldOptions.generatingSharedLibary = true;
                    }else if (arg.equals("-pie")){
                        ldOptions.generatingPIE = true;
                    }else if (arg.equals("--readonly-got")){
                        addLdArg("-z");
                        addLdArg("combreloc");
                        addLdArg("-z");
                        addLdArg("now");
                        addLdArg("-z");
                        addLdArg("relro");
                    }else if (arg.startsWith("-L")){
                        addLdArg("-L" + getOptArg(arg, args));
                    }else if (arg.startsWith("-l")){
                        addLdArg("-l" + getOptArg(arg, args));
                    }else if (arg.equals("-nostartfiles")){
                        ldOptions.noStartFiles = true;
                    }else if (arg.equals("-nodefaultlibs")){
                        ldOptions.noDefaultLibs = true;
                    }else if (arg.equals("-nostdlib")){
                        ldOptions.noDefaultLibs = true;
                        ldOptions.noStartFiles = true;
                    }else if (arg.startsWith("-Wl,")){
                        for (String opt: parseCommaSeparatedOptions(arg)){
                            addLdArg(opt);
                        }
                    }else if (arg.equals("-Xlinker")){
                        addLdArg(nextArg(arg, args));
                    }else if (arg.equals("-v")){
                        verbose = true;
                        asOptions.verbose = true;
                        ldOptions.verbose = true;
                    }else if (arg.equals("--version")){
                        System.out.printf("%s version %s\n",
                                Compiler.ProgramName, Compiler.Version);
                        System.exit(0);
                    }else if (arg.equals("--help")){
                        printUsage(System.out);
                        System.exit(0);
                    }else {
                        parserError("unknown option: " + arg);
                    }
                }
            }else {
                ldArgs.add(new SourceFile(arg));
            }
        }
    }

    private void parserError(String msg){
        throw new OptionParserError(msg);
    }

    private void addLdArg(String arg){
        ldArgs.add(new LdOption(arg));
    }

    private List<SourceFile> selectSourceFiles(List<LdArg> args){
        List<SourceFile> results = new ArrayList<SourceFile>();
        for(LdArg arg: args){
            if(arg.isSourceFile()){
                results.add((SourceFile)arg);
            }
        }
        return results;
    }

    private String getOptArg(String opt, ListIterator<String> args){
        String path = opt.substring(2);
        if(path.length() != 0){
            return path;    // -Ipath
        }else {
            return nextArg(opt, args);
        }
    }

    private String nextArg(String opt, ListIterator<String> args){
        if(!args.hasNext()){
            parserError("missing argument for opt: " + opt);
        }
        return args.next();
    }

    /**
     * 处理逗号分隔的参数
     * -Wl,-rpath,/usr/local/lib" -> ["-rpath", "/usr/local/lib"]
     * -Wl 表示向 链接器 传递参数， 参数逗号隔开,m没有空格
     */
    private List<String> parseCommaSeparatedOptions(String opt){
        String[] opts = opt.split(",");
        if(opts.length <= 1){
            parserError("missing argument for opt: " + opt);
        }
        List<String> result = new ArrayList<String>();
        for(int i = 1; i < opts.length; i++){
            result.add(opts[i]);
        }
        return result;
    }

    void printUsage(PrintStream out) {
        out.println("Usage: cbc [options] file...");
        out.println("Global Options:");
        out.println("  --check-syntax   Checks syntax and quit.");
        out.println("  --dump-tokens    Dumps tokens and quit.");
        // --dump-stmt is a hidden option.
        // --dump-expr is a hidden option.
        out.println("  --dump-ast       Dumps AST and quit.");
        out.println("  --dump-semantic  Dumps AST after semantic checks and quit.");
        // --dump-reference is a hidden option.
        out.println("  --dump-ir        Dumps IR and quit.");
        out.println("  --dump-asm       Dumps AssemblyCode and quit.");
        out.println("  --print-asm      Prints assembly code and quit.");
        out.println("  -S               Generates an assembly file and quit.");
        out.println("  -c               Generates an object file and quit.");
        out.println("  -o PATH          Places output in file PATH.");
        out.println("  -v               Turn on verbose mode.");
        out.println("  --version        Shows compiler version and quit.");
        out.println("  --help           Prints this message and quit.");
        out.println("");
        out.println("Optimization Options:");
        out.println("  -O               Enables optimization.");
        out.println("  -O1, -O2, -O3    Equivalent to -O.");
        out.println("  -Os              Equivalent to -O.");
        out.println("  -O0              Disables optimization (default).");
        out.println("");
        out.println("Parser Options:");
        out.println("  -I PATH          Adds PATH as import file directory.");
        out.println("  --debug-parser   Dumps parsing process.");
        out.println("");
        out.println("Code Generator Options:");
        out.println("  -O               Enables optimization.");
        out.println("  -O1, -O2, -O3    Equivalent to -O.");
        out.println("  -Os              Equivalent to -O.");
        out.println("  -O0              Disables optimization (default).");
        out.println("  -fPIC            Generates PIC assembly.");
        out.println("  -fpic            Equivalent to -fPIC.");
        out.println("  -fPIE            Generates PIE assembly.");
        out.println("  -fpie            Equivalent to -fPIE.");
        out.println("  -fverbose-asm    Generate assembly with verbose comments.");
        out.println("");
        out.println("Assembler Options:");
        out.println("  -Wa,OPT          Passes OPT to the assembler (as).");
        out.println("  -Xassembler OPT  Passes OPT to the assembler (as).");
        out.println("");
        out.println("Linker Options:");
        out.println("  -l LIB           Links the library LIB.");
        out.println("  -L PATH          Adds PATH as library directory.");
        out.println("  -shared          Generates shared library rather than executable.");
        out.println("  -static          Linkes only with static libraries.");
        out.println("  -pie             Generates PIE.");
        out.println("  --readonly-got   Generates read-only GOT (ld -z combreloc -z now -z relro).");
        out.println("  -nostartfiles    Do not link startup files.");
        out.println("  -nodefaultlibs   Do not link default libraries.");
        out.println("  -nostdlib        Enables -nostartfiles and -nodefaultlibs.");
        out.println("  -Wl,OPT          Passes OPT to the linker (ld).");
        out.println("  -Xlinker OPT     Passes OPT to the linker (ld).");
    }

}
