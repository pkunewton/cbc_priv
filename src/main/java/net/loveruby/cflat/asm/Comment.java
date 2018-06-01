package net.loveruby.cflat.asm;

import net.loveruby.cflat.utils.TextUtils;

/**
 * @author 刘科  2018/6/1
 */
public class Comment extends Assembly {

    protected String string;
    protected int indentLevel;

    public Comment(String string) {
        this(string, 0);
    }

    public Comment(String string, int indentLevel) {
        this.string = string;
        this.indentLevel = indentLevel;
    }

    @Override
    public boolean isComment() {
        return true;
    }

    protected String indent(){
        StringBuilder buf = new StringBuilder();
        for(int i = 0; i < indentLevel; i++){
            buf.append(" ");
        }
        return buf.toString();
    }

    public String toSource(SymbolTable table) {
        return "\t" + indent() + "#" + string;
    }

    public String dump() {
        return "(Comment " + TextUtils.dumpString(string) + ")";
    }
}
