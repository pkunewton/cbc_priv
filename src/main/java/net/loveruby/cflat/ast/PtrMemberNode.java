package net.loveruby.cflat.ast;

import net.loveruby.cflat.exception.SemanticError;
import net.loveruby.cflat.type.CompositeType;
import net.loveruby.cflat.type.PointerType;
import net.loveruby.cflat.type.Type;

/**
 * @author 刘科 2018/5/28
 * @see net.loveruby.cflat.ast.MemberNode 和 MemberNode 的处理方式基本一致
 */
public class PtrMemberNode extends LHSNode {

    protected ExprNode expr;
    protected String member;

    public PtrMemberNode(ExprNode expr, String member){
        this.expr = expr;
        this.member = member;
    }

    public ExprNode expr() {
        return expr;
    }

    public String member() {
        return member;
    }


    // 该成员 所在的 指针 解引用后的类型，并将该 类型 转型为 CompositeType
    public CompositeType dereferedCompositeType(){
        try {
            PointerType pt = expr.type().getPointerType();
            return pt.baseType().getCompositeType();
        } catch (ClassCastException e) {
            throw new SemanticError(e.getMessage());
        }
    }

    // 该成员 所在的 指针 解引用后的类型
    public Type dereferedType(){
        try {
            PointerType pt = expr.type().getPointerType();
            return pt.baseType();
        } catch (ClassCastException e) {
            throw new SemanticError(e.getMessage());
        }
    }

    public long offset(){
        return dereferedCompositeType().memberOffset(member);
    }

    protected Type origType() {
        return dereferedCompositeType().memberType(member);
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
