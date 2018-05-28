package net.loveruby.cflat.ast;

import net.loveruby.cflat.exception.SemanticError;
import net.loveruby.cflat.type.CompositeType;
import net.loveruby.cflat.type.Type;

/**
 * @author 刘科 2018/5/28
 */
public class MemberNode extends LHSNode {

    protected ExprNode expr;
    protected String member;

    public MemberNode(ExprNode expr, String member){
        this.expr = expr;
        this.member = member;
    }

    // 获取该成员 所在的 结构体或者联合体 的类型
    public CompositeType baseType(){
        try{
            return expr.type().getCompositeType();
        }catch (ClassCastException err){
            throw new SemanticError(err.getMessage());
        }
    }

    public ExprNode expr() {
        return expr;
    }

    public String member() {
        return member;
    }

    // 在 结构体或联合体 中的 偏移量
    public long offset(){
        return baseType().memberOffset(member);
    }

    // 获取 成员 本身的类型
    protected Type origType() {
        return baseType().memberType(member);
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
        d.printMember("member", member);
    }
}
