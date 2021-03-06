package net.loveruby.cflat.ast;


public interface ASTVisitor<S, E> {
    // statements
    S visit(BlockNode node);
    S visit(ExprStmtNode node);
    S visit(IfNode node);
    S visit(SwitchNode node);
    S visit(CaseNode node);
    S visit(WhileNode node);
    S visit(DoWhileNode node);
    S visit(ForNode node);
    S visit(BreakNode node);
    S visit(ContinueNode node);
    S visit(GotoNode node);
    S visit(LabelNode node);
    S visit(ReturnNode node);

    // expressions
    E visit(AssignNode node);
    E visit(OpAssignNode node);
    E visit(CondExprNode node);
    E visit(LogicalOrNode node);
    E visit(LogicalAndNode node);
    E visit(BinaryOpNode node);
    E visit(UnaryOpNode node);
    E visit(PrefixOpNode node);
    E visit(SuffixOpNode node);
    E visit(ArefNode node);
    E visit(MemberNode node);
    E visit(PtrMemberNode node);
    E visit(FuncallNode node);
    E visit(DereferenceNode node);
    E visit(AddressNode node);
    E visit(CastNode node);
    E visit(SizeofExprNode node);
    E visit(SizeofTypeNode node);
    E visit(VariableNode node);
    E visit(IntegerLiteralNode node);
    E visit(StringLiteralNode node);
}
