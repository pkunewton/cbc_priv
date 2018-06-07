package net.loveruby.cflat.ast;

import net.loveruby.cflat.type.Type;

/**
 * @author 刘科 2018/5/28
 */
public class BinaryOpNode extends ExprNode {

    protected ExprNode lhs, rhs;
    protected String operator;
    protected Type type;

    public BinaryOpNode(ExprNode lhs, String operator, ExprNode rhs){
        super();
        this.lhs = lhs;
        this.rhs = rhs;
        this.operator = operator;
    }

    public BinaryOpNode(ExprNode lhs, String operator, ExprNode rhs, Type type){
        super();
        this.lhs = lhs;
        this.rhs = rhs;
        this.operator = operator;
        this.type = type;
    }

    public String operator(){
        return operator;
    }

    public Type type() {
        return (type==null)?lhs.type():type;
    }

    public void setType(Type type){
        if(type != null){
            throw new Error("BinaryNodeType set twice!");
        }
        this.type = type;
    }

    public ExprNode left() {
        return lhs;
    }

    public void setLeft(ExprNode lhs){
        this.lhs = lhs;
    }

    public ExprNode right() {
        return rhs;
    }

    public void setRight(ExprNode rhs){
        this.rhs = rhs;
    }

    public <S, E> E accept(ASTVisitor<S, E> visitor) {
        return visitor.visit(this);
    }

    public Location location() {
        return lhs.location();
    }

    protected void _dump(Dumper d) {
        d.printMember("operator", operator);
        d.printMember("lhs", lhs);
        d.printMember("rhs", rhs);
    }
}
