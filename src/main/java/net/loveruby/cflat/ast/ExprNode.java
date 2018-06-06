package net.loveruby.cflat.ast;

import net.loveruby.cflat.exception.SemanticError;
import net.loveruby.cflat.type.Type;

/**
 * @author 刘科 2018/5/27
 */
abstract public class ExprNode extends Node {

    public ExprNode() {
        super();
    }

    abstract public Type type();
    // 比如数组的元素a[i] 的 origType 类型 应该是 a 的元素类型
    // 而不是 a数组 的类型
    protected Type origType() { return type(); }

    public long allocSize() { return type().allocSize(); }

    public boolean isConstant() { return false; }
    public boolean isParameter() { return false; }

    public boolean isLvalue() { return false; }
    public boolean isAssignable() { return false; }
    //isLoadable() 判断是否是 数组和函数 ——> 数组和函数本身就是指针,是否可以与整数加减
    public boolean isLoadable() { return false; }

    public boolean isCallable() {
        try {
            return type().isCallable();
        }catch (SemanticError error){
            return false;
        }
    }

    public boolean isPointer() {
        try {
            return type().isPointer();
        }catch (SemanticError error){
            return false;
        }
    }

    abstract public <S,E> E accept(ASTVisitor<S,E> visitor);
}
