package net.loveruby.cflat.compiler;

/**
 * @author 刘科  2018/6/11
 */
public class LdOption implements LdArg {

    private final String arg;

    public LdOption(String arg) {
        this.arg = arg;
    }

    public boolean isSourceFile() {
        return false;
    }

    @Override
    public String toString() {
        return arg;
    }
}
