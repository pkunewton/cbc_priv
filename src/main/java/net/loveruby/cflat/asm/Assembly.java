package net.loveruby.cflat.asm;

/**
 * @author 刘科  2018/5/31
 */
abstract public class Assembly {

    abstract public String toSource(SymbolTable table);
    abstract public String dump();

    public boolean isInstruction(){
        return false;
    }

    // 是否是汇编伪操作
    public boolean isDirective(){
        return false;
    }

    public boolean isLabel(){
        return false;
    }

    public boolean isComment(){
        return false;
    }

    public void collectStatistics(Statistics stats){
        // 默认不做任何事情
    }
}
