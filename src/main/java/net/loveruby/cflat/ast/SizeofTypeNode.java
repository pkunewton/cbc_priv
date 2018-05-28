package net.loveruby.cflat.ast;

import net.loveruby.cflat.type.Type;
import net.loveruby.cflat.type.TypeRef;

/**
 * @author 刘科 2018/5/28
 */
public class SizeofTypeNode extends ExprNode {

    protected TypeNode operand; // 需要计算大小的类型
    protected TypeNode type;    // sizeof 节点本身的类型 unsigned long

    public SizeofTypeNode(TypeNode operand, TypeRef type){
        this.operand = operand;
        this.type = new TypeNode(type);
    }

    public Type operand() {
        return operand.type();
    }

    public TypeNode operandTypeNode(){
        return operand;
    }

    public Type type(){
        return type.type();
    }

    public TypeNode typeNode() {
        return type;
    }

    public <S, E> E accept(ASTVisitor<S, E> visitor) {
        return visitor.visit(this);
    }

    public Location location() {
        return operand.location();
    }

    protected void _dump(Dumper d) {
        d.printMember("operand", operand);
    }
}
