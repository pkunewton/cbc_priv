package net.loveruby.cflat.ast;

import net.loveruby.cflat.type.Type;

/**
 * @author 刘科 2018/5/28
 */
abstract public class LHSNode extends ExprNode {

    protected Type type, origType;

    @Override
    public Type type() {
        return (type!=null) ? type : origType;
    }

    public void setType(Type type) {
        this.type = type;
    }

    @Override
    abstract protected Type origType();

    @Override
    public long allocSize(){
        return origType().allocSize();
    }

    @Override
    public boolean isLvalue(){
        return true;
    }

    // 数组和函数不可赋值
    @Override
    public boolean isAssignable() {
        return isLoadable();
    }

    @Override
    public boolean isLoadable() {
        Type t = origType();
        return !t.isArray() && !t.isFunction();
    }
}
