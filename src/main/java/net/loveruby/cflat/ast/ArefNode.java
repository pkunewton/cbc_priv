package net.loveruby.cflat.ast;

import net.loveruby.cflat.type.ArrayType;
import net.loveruby.cflat.type.Type;

/**
 * @author 刘科 2018/5/28
 */
public class ArefNode extends LHSNode {

    protected ExprNode expr, index;

    public ArefNode(ExprNode expr, ExprNode index){
        this.expr = expr;
        this.index = index;
    }

    public ExprNode expr(){
        return expr;
    }

    public ExprNode index() {
        return index;
    }

    // 是否是多维数组
    public boolean isMutiDimesion(){
        return (expr instanceof ArefNode) && !expr.isPointer();
    }

    // Returns base expression of (multi-dimension) array
    // 多维数组最底层的表达式 例如 a[][][] 的 baseExpr 是 a
    public ExprNode baseExpr() {
        return isMutiDimesion() ? ((ArefNode)expr).baseExpr() : expr;
    }

    // element size of this (multi-dimension) array
    public long elementSize(){
        return origType().allocSize();
    }

    public long length(){
        return ((ArrayType)expr.origType()).length();
    }

    // 数组元素类型 expr.origType() 返回数组类型， baseType 返回数组元素类型
    protected Type origType() {
        return expr.origType().baseType();
    }

    public <S, E> E accept(ASTVisitor<S, E> visitor) {
        return visitor.visit(this);
    }

    public Location location() {
        return expr.location();
    }

    protected void _dump(Dumper d) {
        if(type != null){
            d.printMember("type", type);
        }
        d.printMember("expr", expr);
        d.printMember("index", index);
    }
}
