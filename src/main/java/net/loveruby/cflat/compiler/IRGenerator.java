package net.loveruby.cflat.compiler;

import net.loveruby.cflat.asm.Label;
import net.loveruby.cflat.ast.*;
import net.loveruby.cflat.entity.DefinedFunction;
import net.loveruby.cflat.entity.LocalScope;
import net.loveruby.cflat.exception.SemanticException;
import net.loveruby.cflat.ir.Expr;
import net.loveruby.cflat.ir.IR;
import net.loveruby.cflat.ir.Stmt;
import net.loveruby.cflat.type.TypeTable;
import net.loveruby.cflat.utils.ErrorHandler;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class IRGenerator implements ASTVisitor<Void, Expr> {

    private final TypeTable typeTable;
    private final ErrorHandler errorHandler;

    public IRGenerator(TypeTable typeTable, ErrorHandler errorHandler) {
        this.typeTable = typeTable;
        this.errorHandler = errorHandler;
    }

    public IR generate(AST ast) throws SemanticException{

        return ast.ir();
    }

    //
    // Definitions
    //

    List<Stmt> stmts;
    LinkedList<LocalScope> scopeStack;
    LinkedList<Label> breakStack;
    LinkedList<Label> continueStack;
    Map<String, JumpEntry> jumpMap;

    public List<Stmt> compileFunctionBody(DefinedFunction function) {
        return stmts;
    }

    public Void visit(BlockNode node) {
        return null;
    }

    public Void visit(ExprStmtNode node) {
        return null;
    }

    public Void visit(IfNode node) {
        return null;
    }

    public Void visit(SwitchNode node) {
        return null;
    }

    public Void visit(CaseNode node) {
        return null;
    }

    public Void visit(WhileNode node) {
        return null;
    }

    public Void visit(DoWhileNode node) {
        return null;
    }

    public Void visit(ForNode node) {
        return null;
    }

    public Void visit(BreakNode node) {
        return null;
    }

    public Void visit(ContinueNode node) {
        return null;
    }

    public Void visit(GotoNode node) {
        return null;
    }

    class JumpEntry{

    }

    public Void visit(LabelNode node) {
        return null;
    }

    public Void visit(ReturnNode node) {
        return null;
    }

    public Expr visit(AssignNode node) {
        return null;
    }

    public Expr visit(OpAssignNode node) {
        return null;
    }

    public Expr visit(CondExprNode node) {
        return null;
    }

    public Expr visit(LogicalOrNode node) {
        return null;
    }

    public Expr visit(LogicalAndNode node) {
        return null;
    }

    public Expr visit(BinaryOpNode node) {
        return null;
    }

    public Expr visit(UnaryOpNode node) {
        return null;
    }

    public Expr visit(PrefixOpNode node) {
        return null;
    }

    public Expr visit(SuffixOpNode node) {
        return null;
    }

    public Expr visit(ArefNode node) {
        return null;
    }

    public Expr visit(MemberNode node) {
        return null;
    }

    public Expr visit(PtrMemberNode node) {
        return null;
    }

    public Expr visit(FuncallNode node) {
        return null;
    }

    public Expr visit(DereferenceNode node) {
        return null;
    }

    public Expr visit(AddressNode node) {
        return null;
    }

    public Expr visit(CastNode node) {
        return null;
    }

    public Expr visit(SizeofExprNode node) {
        return null;
    }

    public Expr visit(SizeofTypeNode node) {
        return null;
    }

    public Expr visit(VariableNode node) {
        return null;
    }

    public Expr visit(IntegerLiteralNode node) {
        return null;
    }

    public Expr visit(StringLiteralNode node) {
        return null;
    }
}
