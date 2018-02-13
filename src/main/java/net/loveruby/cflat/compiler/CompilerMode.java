package net.loveruby.cflat.compiler;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2018/1/25.
 */
public enum CompilerMode {

    CheckSyntax("--check-syntax"),
    DumpTokens("--dump-tokens"),
    DumpAST("--dump-ast"),
    DumpStmt("--dump-stmt"),
    DumpExpr("--dump-expr"),
    DumpSemantic("--dump--semantic"),
    DumpReference("--dump-reference"),
    DumpIR("--dump-ir"),
    DumpASM("--dump-asm"),
    PrintASM("--print-asm"),
    Compile("-S"),
    Assemble("-c"),
    Link("--link");


    static private Map<String, CompilerMode> modes;
    static {
        modes = new HashMap<String, CompilerMode>();
        modes.put("--check_syntax", CheckSyntax);
        modes.put("--dump-tokens", DumpTokens);
        modes.put("--dump-ast", DumpAST);
        modes.put("--dump-stmt", DumpStmt);
        modes.put("--dump-expr", DumpExpr);
        modes.put("--dump-semantic", DumpSemantic);
        modes.put("--dump-reference", DumpReference);
        modes.put("--dump-ir", DumpIR);
        modes.put("--dump-asm", DumpASM);
        modes.put("--print-asm", PrintASM);
        modes.put("-S", Compile);
        modes.put("-c", Assemble);
        modes.put("--link", Link);
    }

    static public boolean isModeOption(String option){
        return modes.containsKey(option);
    }

    static public CompilerMode fromOption(String option){
        CompilerMode mode = modes.get(option);
        if(mode == null){
            throw new Error("must not happen: unknown mode option: " + option);
        }
        return mode;
    }

    private final String option;

    CompilerMode(String option){
        this.option = option;
    }

    public String toOption(){
        return option;
    }

    boolean requires(CompilerMode mode){
        return ordinal() > mode.ordinal();
    }

}
