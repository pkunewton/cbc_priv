package net.loveruby.cflat.entity;

/**
 * @author 刘科 2018/02/08
 * */
public interface EntityVisitor<T> {

    T visit(DefinedVariable var);
    T visit(UndefinedVariable var);
    T visit(DefinedFunction func);
    T visit(UndefinedFunction func);
    T visit(Constant constant);
}
