package net.loveruby.cflat.asm;

import net.loveruby.cflat.utils.TextUtils;

/**
 * @author 刘科  2018/6/1
 * 汇编伪操作
 */
public class Directive extends Assembly {

    protected String content;

    public Directive(String content) {
        this.content = content;
    }

    @Override
    public boolean isComment() {
        return true;
    }

    public String toSource(SymbolTable table) {
        return this.content;
    }

    public String dump() {
        return "(Directive " + TextUtils.dumpString(content.trim()) + ")";
    }
}
