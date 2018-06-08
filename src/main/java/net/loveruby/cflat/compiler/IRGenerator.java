package net.loveruby.cflat.compiler;

import com.sun.org.apache.bcel.internal.generic.LADD;
import net.loveruby.cflat.asm.Label;
import net.loveruby.cflat.ast.*;
import net.loveruby.cflat.entity.DefinedFunction;
import net.loveruby.cflat.entity.DefinedVariable;
import net.loveruby.cflat.entity.Entity;
import net.loveruby.cflat.entity.LocalScope;
import net.loveruby.cflat.exception.JumpError;
import net.loveruby.cflat.exception.SemanticException;
import net.loveruby.cflat.ir.*;
import net.loveruby.cflat.type.Type;
import net.loveruby.cflat.type.TypeTable;
import net.loveruby.cflat.utils.ErrorHandler;

import java.util.*;

/**
 * @author 刘科  2018/6/8
 */
public class IRGenerator implements ASTVisitor<Void, Expr> {

    private final TypeTable typeTable;
    private final ErrorHandler errorHandler;

    public IRGenerator(TypeTable typeTable, ErrorHandler errorHandler) {
        this.typeTable = typeTable;
        this.errorHandler = errorHandler;
    }

    // 启动类
    public IR generate(AST ast) throws SemanticException{

        for(DefinedVariable variable: ast.definedVariables()){
            if(variable.hasInitializer()){
                variable.setIr(transformExpr(variable.initializer()));
            }
        }

        for(DefinedFunction function: ast.definedFunctions()){
            function.setIr(compileFunctionBody(function));
        }

        if(errorHandler.errorOccured()){
            throw new SemanticException("IR generation failed.");
        }
        return ast.ir();
    }


    //
    // Definitions
    //

    List<Stmt> stmts;
    LinkedList<LocalScope> scopeStack;      // 生成临时变量时，获取作用域
    LinkedList<Label> breakStack;           // break 语句跳转目的地 的栈
    LinkedList<Label> continueStack;        // continue 语句跳转目的地 的栈
    Map<String, JumpEntry> jumpMap;         // 保存 goto 语句的标签

    public List<Stmt> compileFunctionBody(DefinedFunction function) {
        stmts = new ArrayList<Stmt>();
        scopeStack = new LinkedList<LocalScope>();
        breakStack = new LinkedList<Label>();
        continueStack = new LinkedList<Label>();
        jumpMap = new HashMap<String, JumpEntry>();
        transformStmt(function.body());
        checkJumpLinks(jumpMap);
        return stmts;
    }

    private void transformStmt(StmtNode node){
        node.accept(this);
    }

    // 表达式是独立的语句  如 x=y;
    private void transformStmt(ExprNode node){
        node.accept(this);
    }

    // 表达式是其他表达式的一部分 如 printf("%d\n", (x=y));
    private int exprNestLevel = 0;

    private Expr transformExpr(ExprNode node){
        exprNestLevel++;
        Expr expr = node.accept(this);
        exprNestLevel--;
        return expr;
    }

    private boolean isStmtement(){
        return (exprNestLevel == 0);
    }


    // 生成 赋值 语句
    private void assign(Location location, Expr lhs, Expr rhs){
        stmts.add(new Assign(location, lhs, rhs));
    }

    private DefinedVariable tmpVar(Type type){
        return scopeStack.getLast().allocateTmp(type);
    }

    private void label(Label label){
        stmts.add(new LabelStmt(null, label));
    }

    private void jump(Location location, Label label){
        stmts.add(new Jump(location, label));
    }

    private void jump(Label label){
        stmts.add(new Jump(null, label));
    }

    private void cjump(Location location, Expr cond, Label thenLabel, Label elseLabel){
        stmts.add(new CJump(location, cond, thenLabel, elseLabel));
    }

    private void pushBreak(Label label){
        breakStack.add(label);
    }

    private void popBreak(){
        if(breakStack.isEmpty()){
            throw new Error("unmatched push/pop for break stack");
        }
        breakStack.removeLast();
    }

    private Label currentBreakTarget(){
        if(breakStack.isEmpty()){
            throw new JumpError("break from out of loop");
        }
        return breakStack.getLast();
    }

    private void pushContinue(Label label){
        continueStack.add(label);
    }

    private void popContinue(){
        if(continueStack.isEmpty()){
            throw new Error("unmatched push/pop for break stack");
        }
        continueStack.removeLast();
    }

    private Label currentContinueTarget(){
        if(continueStack.isEmpty()){
            throw new JumpError("break from out of loop");
        }
        return continueStack.getLast();
    }

    //
    // Statements
    //

    public Void visit(BlockNode node) {
        scopeStack.add(node.scope());
        for(DefinedVariable variable: node.variables()){
            if(variable.hasInitializer()){
                if(variable.isPrivate()){
                    // 静态变量 不在堆栈中 存储
                    variable.setIr(transformExpr(variable.initializer()));
                }else {
                    assign(node.location(),
                            ref(variable), transformExpr(variable.initializer()));
                }
            }
        }

        for(StmtNode stmt: node.stmts()){
            transformStmt(stmt);
        }
        scopeStack.removeLast();
        return null;
    }

    public Void visit(ExprStmtNode node) {
        // do not use transformStmt here, to receive compiled tree.
        Expr expr = node.expr().accept(this);
        if(expr != null){
            //stmts.add(new ExprStmt(node.expr().location(), e));
            errorHandler.warn(node.location(), "useless expression");
        }
        return null;
    }

    public Void visit(IfNode node) {
        Label thenLabel = new Label();
        Label elseLabel = new Label();
        Label endLabel = new Label();
        Expr cond = transformExpr(node.cond());
        if(node.elseBody() == null){
            cjump(node.location(), cond, thenLabel, elseLabel);
            label(thenLabel);
            transformStmt(node.thenBody());
            label(endLabel);
        }else {
            cjump(node.location(), cond, thenLabel, elseLabel);
            label(thenLabel);
            transformStmt(node.thenBody());
            jump(elseLabel);
            label(elseLabel);
            transformStmt(node.elseBody());
            label(endLabel);
        }

        return null;
    }

    public Void visit(SwitchNode node) {
        List<Case> cases = new ArrayList<Case>();
        Label end = new Label();
        Label defaultLabel = end;

        Expr cond = transformExpr(node.cond());

        for(CaseNode caseNode: node.cases()){
            if(caseNode.isDefault()){
                defaultLabel = caseNode.label();
            }else {
                for(ExprNode exprNode: caseNode.values()){
                    Expr expr = transformExpr(exprNode);
                    cases.add(new Case(((Int)expr).value(), caseNode.label()));
                }
            }
        }

        stmts.add(new Switch(node.location(),
                cond, cases, defaultLabel, end));
        pushBreak(end);
        for(CaseNode caseNode: node.cases()){
            label(caseNode.label());
            transformStmt(caseNode.body());
        }
        popBreak();
        label(end);
        return null;
    }

    public Void visit(CaseNode node) {
        throw new Error("caseNode visit must not happen");
    }

    public Void visit(WhileNode node) {
        Label begin = new Label();
        Label body = new Label();
        Label end = new Label();

        Expr cond = transformExpr(node.cond());
        label(begin);
        pushContinue(begin);
        pushBreak(end);
        cjump(node.location(), cond, body, end);
        popBreak();
        popContinue();
        jump(begin);
        label(body);

        transformStmt(node.body());
        label(end);

        return null;
    }

    public Void visit(DoWhileNode node) {
        Label begin = new Label();
        Label contiuneLabel = new Label();  // before cond (end of body)
        Label end = new Label();

        label(begin);
        pushContinue(contiuneLabel);
        pushBreak(end);
        transformStmt(node.body());
        popBreak();
        popContinue();
        label(contiuneLabel);
        cjump(node.location(), transformExpr(node.cond()), begin, end);
        label(end);
        return null;
    }

    public Void visit(ForNode node) {

        Label begin = new Label();
        Label body = new Label();
        Label contiuneLabel = new Label();
        Label end = new Label();

        transformStmt(node.init());
        Expr cond = transformExpr(node.cond());
        label(begin);
        cjump(node.location(), cond, body, end);
        label(body);
        pushContinue(contiuneLabel);
        pushBreak(end);
        transformStmt(node.body());
        popBreak();
        popContinue();
        label(contiuneLabel);
        transformStmt(node.incr());
        jump(begin);
        label(end);
        return null;
    }

    public Void visit(BreakNode node) {
        try {
            jump(node.location(), currentBreakTarget());
        } catch (JumpError e) {
            error(node, e.getMessage());
        }
        return null;
    }

    public Void visit(ContinueNode node) {
        try {
            jump(node.location(), currentContinueTarget());
        } catch (JumpError e) {
            error(node, e.getMessage());
        }
        return null;
    }

    public Void visit(GotoNode node) {
        jump(node.location(), referLabel(node.target()));
        return null;
    }

    class JumpEntry{

        public Label label;
        public long numRefered;
        public boolean isDefined;
        public Location location;

        public JumpEntry(Label label) {
            this.label = label;
            this.numRefered = 0;
            this.isDefined = false;
        }
    }

    private Label defineLabel(String name, Location location)
            throws SemanticException{
        JumpEntry entry = getJumpEntry(name);
        if(entry.isDefined){
            throw new SemanticException("duplicated jump labels in "
                    + name + "(): " + name);
        }
        entry.isDefined = true;
        entry.location = location;
        return entry.label;
    }

    private Label referLabel(String name){
        JumpEntry entry = getJumpEntry(name);
        entry.numRefered++;
        return entry.label;
    }

    private JumpEntry getJumpEntry(String name){
        JumpEntry jumpEntry = jumpMap.get(name);
        if(jumpEntry == null){
            jumpEntry = new JumpEntry(new Label());
            jumpMap.put(name, jumpEntry);
        }
        return jumpEntry;
    }

    private void checkJumpLinks(Map<String, JumpEntry> jumpMap){

    }

    public Void visit(LabelNode node) {
        try {
            stmts.add(new LabelStmt(node.location(),
                    defineLabel(node.name(), node.location())));
            if(node.stmt() != null){
                transformStmt(node.stmt());
            }
        } catch (SemanticException e) {
            error(node, e.getMessage());
        }
        return null;
    }

    public Void visit(ReturnNode node) {
        stmts.add(new Return(node.location(),
                (node.expr() == null ? null: transformExpr(node.expr()))));
        return null;
    }


    //
    // Expressions (with branches)
    //

    public Expr visit(CondExprNode node) {
        return null;
    }

    public Expr visit(LogicalOrNode node) {
        return null;
    }

    public Expr visit(LogicalAndNode node) {
        return null;
    }

    //
    // Expressions (with side effects)
    //

    public Expr visit(AssignNode node) {
        return null;
    }

    public Expr visit(OpAssignNode node) {
        return null;
    }


    public Expr visit(PrefixOpNode node) {
        return null;
    }

    public Expr visit(SuffixOpNode node) {
        return null;
    }

    public Expr visit(FuncallNode node) {
        return null;
    }

    //
    // Expressions (no side effects)
    //

    public Expr visit(BinaryOpNode node) {
        return null;
    }

    public Expr visit(UnaryOpNode node) {
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

    //
    // Utilities
    //


    private boolean isPointerDiff(Op op, Type left, Type right){
        return op == Op.SUB && left.isPointer() && right.isPointer();
    }

    // p++ p--
    private boolean isPointerArithmetic(Op op, Type operandType){
        switch (op){
            case ADD:
            case SUB: return operandType.isPointer();
            default:
                    return false;
        }
    }

    private Int ptrBaseSize(Type type){
        return new Int(ptrdiff_t(), type.baseType().size());
    }

    private Op binOp(String uniOp){
        return uniOp.equals("++") ? Op.ADD : Op.SUB;
    }

    private Expr addressOf(Expr expr){
        return expr.addressNode();
    }

    private Var ref(Entity entity){
        return new Var(varType(entity.type()), entity);
    }

    private Mem mem(Entity entity){
        return new Mem(asmType(entity.type().baseType()), ref(entity));
    }

    private Mem men(Expr expr, Type type){
        return new Mem(asmType(type), expr);
    }

    private Int pitdiff(long n){
        return new Int(ptrdiff_t(), n);
    }

    // 数组 大小
    private Int size(long n){
        return new Int(size_t(), n);
    }

    // var++ <=> var = var + imm(1)
    private Int imm(Type operandType, long n){
        if(operandType.isPointer()){
            return new Int(ptrdiff_t(), n);
        }else {
            return new Int(int_t(), n);
        }
    }

    private Type pointerTo(Type type){
        return typeTable.pointerTo(type);
    }

    private net.loveruby.cflat.asm.Type asmType(Type type){
        if(type.isVoid()) return int_t();
        return net.loveruby.cflat.asm.Type.get(type.size());
    }

    // var节点的类型
    private net.loveruby.cflat.asm.Type varType(Type type){
        if(!type.isScalar()){
            return null;
        }
        return net.loveruby.cflat.asm.Type.get(type.size());
    }

    private net.loveruby.cflat.asm.Type int_t(){
        return net.loveruby.cflat.asm.Type.get(typeTable.intSize());
    }

    private net.loveruby.cflat.asm.Type size_t(){
        return net.loveruby.cflat.asm.Type.get(typeTable.longSize());
    }

    private net.loveruby.cflat.asm.Type ptr_t(){
        return net.loveruby.cflat.asm.Type.get(typeTable.pointerSize());
    }

    private net.loveruby.cflat.asm.Type ptrdiff_t(){
        return net.loveruby.cflat.asm.Type.get(typeTable.longSize());
    }

    private void error(Node node, String msg){
        errorHandler.error(node.location(), msg);
    }

}
