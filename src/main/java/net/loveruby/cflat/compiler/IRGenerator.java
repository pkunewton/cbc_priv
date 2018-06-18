package net.loveruby.cflat.compiler;

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
import net.loveruby.cflat.utils.ListUtils;

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


    // 生成 赋值 语句, 赋值语句左值是地址
    private void assign(Location location, Expr lhs, Expr rhs){
        stmts.add(new Assign(location, addressOf(lhs), rhs));
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

        // 不存在 default 标签时， default  设置为 end 标签
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
        for(Map.Entry<String, JumpEntry> entry: jumpMap.entrySet()){
            String name = entry.getKey();
            JumpEntry jump = entry.getValue();
            if(!jump.isDefined){
                errorHandler.error(jump.location,
                        "undefined label: " + name);
            }
            if(jump.numRefered == 0){
                errorHandler.warn(jump.location,
                        "useless label: " + name);
            }
        }
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
        Label thenLabel = new Label();
        Label elseLable = new Label();
        Label end = new Label();
        DefinedVariable var = tmpVar(node.type());

        Expr cond = transformExpr(node.cond());
        cjump(node.location(), cond, thenLabel, elseLable);
        label(thenLabel);
        assign(node.location(), ref(var), transformExpr(node.thenExpr()));
        jump(end);
        label(elseLable);
        assign(node.location(), ref(var), transformExpr(node.elseExpr()));
        label(end);
        return isStmtement() ? null : ref(var);
    }

    public Expr visit(LogicalOrNode node) {
        Label right = new Label();
        Label end = new Label();
        DefinedVariable var = tmpVar(node.type());

        assign(node.left().location(),
                ref(var), transformExpr(node.left()));
        // and 和 or 不一样的地方，
        // and 为真时继续计算 right 节点的是否为真，or 则直接跳过 right 节点
        // or  为假时继续计算 left  节点是否为真，  and则直接跳过 right 节点
        cjump(node.location(), ref(var), right, end);
        label(right);
        assign(node.right().location(),
                ref(var), transformExpr(node.right()));
        label(end);
        return isStmtement() ? null : ref(var);
    }

    public Expr visit(LogicalAndNode node) {
        Label right = new Label();
        Label end = new Label();
        DefinedVariable var = tmpVar(node.type());

        assign(node.left().location(),
                ref(var), transformExpr(node.left()));
        cjump(node.location(), ref(var), end, right);
        label(right);
        assign(node.right().location(),
                ref(var), transformExpr(node.right()));
        label(end);
        return isStmtement() ? null : ref(var);
    }

    //
    // Expressions (with side effects)
    //

    public Expr visit(AssignNode node) {
        Location lloc = node.lhs().location();
        Location rloc = node.rhs().location();

        if(isStmtement()){
            // Evaluate RHS before LHS.
            Expr rhs = transformExpr(node.rhs());
            assign(lloc, transformExpr(node.lhs()), rhs);
            return null;
        }else {
            // lhs = rhs -> tmp = rhs, lhs = tmp, tmp
            DefinedVariable var = tmpVar(node.rhs().type());
            assign(rloc, ref(var), transformExpr(node.rhs()));
            assign(lloc, transformExpr(node.lhs()), ref(var));
            return ref(var);
        }
    }

    public Expr visit(OpAssignNode node) {
        // Evaluate RHS before LHS.
        Expr rhs = transformExpr(node.rhs());
        Expr lhs = transformExpr(node.rhs());

        Type type = node.lhs().type();
        Op op = Op.internBinary(node.operator(), type.isSigned());
        return transformOpAssign(node.location(), op, type, lhs, rhs);
    }


    public Expr visit(PrefixOpNode node) {
        // ++expr -> expr += 1
        Type type = node.expr().type();
        return transformOpAssign(node.location(), binOp(node.operator()),
                type, transformExpr(node.expr()), imm(type, 1));
    }

    public Expr visit(SuffixOpNode node) {
        Expr expr = transformExpr(node.expr());
        Type type = node.expr().type();
        Op op = binOp(node.operator());
        Location location = node.location();

        if(isStmtement()){
            // expr++; -> expr += 1;
            transformOpAssign(location, op, type, expr, imm(type, 1));
            return null;
        }else if(expr.isVar()){
            // cont(expr++) -> v = expr; expr = v + 1; cont(v)
            DefinedVariable v = tmpVar(type);
            assign(location, ref(v), expr);
            assign(location, expr, bin(op, type, ref(v), imm(type, 1)));
            return ref(v);
        }else {
            // cont(expr++) -> a = &expr; v = *a; *a = *a + 1; cont(v)
            DefinedVariable p = tmpVar(pointerTo(type));
            DefinedVariable v = tmpVar(type);
            assign(location, ref(p), addressOf(expr));
            assign(location, ref(v), mem(p));
            assign(location, mem(p), bin(op, type, mem(p), imm(type, 1)));
            return ref(v);
        }
    }

    private Expr transformOpAssign(Location location, Op op,
                                   Type lhsType, Expr lhs, Expr rhs){
        if(lhs.isVar()){
            // cont(lhs += rhs) -> lhs = lhs + rhs; cont(lhs)
            assign(location, lhs, bin(op, lhsType, lhs, rhs));
            return isStmtement() ? null : lhs;
        }else {
            // cont(lhs += rhs) -> a = &lhs; *a = *a + rhs; cont(*a)
            DefinedVariable pointer = tmpVar(pointerTo(lhsType));
            assign(location, ref(pointer), addressOf(lhs));
            assign(location, mem(pointer), bin(op, lhsType, mem(pointer), rhs));
            return isStmtement() ? null : mem(pointer);
        }
    }

    private Bin bin(Op op, Type leftType, Expr left, Expr right){
        if(isPointerArithmetic(op, leftType)){
            return new Bin(left.type(), op, left,
                    new Bin(right.type(), Op.MUL,
                            right, ptrBaseSize(leftType)));
        }else {
            return new Bin(left.type(), op, left, right);
        }
    }

    public Expr visit(FuncallNode node) {
        List<Expr> args = new ArrayList<Expr>();
        // 压栈的时候，参数是按照倒叙入栈的
        for(ExprNode arg: ListUtils.reverse(node.args())){
            args.add(0, transformExpr(arg));
        }
        Expr call = new Call(asmType(node.type()),
                transformExpr(node.expr()), args);
        if(isStmtement()){
            stmts.add(new ExprStmt(node.location(), call));
            return null;
        }else {
            DefinedVariable f = tmpVar(node.type());
            assign(node.location(), ref(f), call);
            return ref(f);
        }
    }

    //
    // Expressions (no side effects)
    //

    public Expr visit(BinaryOpNode node) {
        Expr left = transformExpr(node.left());
        Expr right = transformExpr(node.right());
        Op op = Op.internBinary(node.operator(), node.type().isSigned());

        Type type = node.type();
        Type l = node.left().type();
        Type r = node.right().type();

        if(isPointerDiff(op, l, r)){
            // pointer - pointer --> (pointer - pointer) pointerBaseSize
            Expr tmp = new Bin(asmType(type), op, left, right);
            return new Bin(asmType(type), Op.SUB, tmp, ptrBaseSize(l));
        }else if(isPointerArithmetic(op, l)){
            // ptr + int => ptr + (int * ptrBaseSize)
            Expr tmp = new Bin(asmType(r), Op.MUL, right, ptrBaseSize(l));
            return new Bin(asmType(type), op, left, tmp);
        }else if(isPointerArithmetic(op, r)){
            // int + ptr -> (int * ptrBaseSize) + ptr
            Expr tmp = new Bin(asmType(l), Op.MUL, left, ptrBaseSize(r));
            return new Bin(asmType(type), op, tmp, right);
        }else {
            return new Bin(asmType(type), op, left, right);
        }
    }

    // + - ! ~
    public Expr visit(UnaryOpNode node) {
        if(node.operator().equals("+")){
            return transformExpr(node.expr());
        }else {
            return new Uni(asmType(node.type()),
                    Op.internUnary(node.operator()),
                    transformExpr(node.expr()));
        }
    }

    public Expr visit(ArefNode node) {
        Expr expr = transformExpr(node.expr());
        Expr offset = new Bin(ptrdiff_t(), Op.MUL,
                size(node.elementSize()), transformIndex(node));

        Bin addr = new Bin(ptr_t(), Op.ADD, expr, offset);
        return mem(addr, node.type());
    }

    // For multidimension array: t[e][d][c][b][a] ary;
    // &ary[a0][b0][c0][d0][e0]
    //     = &ary + edcb*a0 + edc*b0 + ed*c0 + e*d0 + e0
    //     = &ary + (((((a0)*b + b0)*c + c0)*d + d0)*e + e0) * sizeof(t)
    //
    private Expr transformIndex(ArefNode node){
        if(node.isMutiDimesion()){
            return new Bin(int_t(), Op.ADD,
                    transformExpr(node.index()),
                    new Bin(int_t(), Op.MUL,
                        new Int(int_t(), node.elementSize()),
                         transformIndex((ArefNode) node.expr())));
        }else {
            return transformExpr(node.index());
        }
    }

    public Expr visit(MemberNode node) {
        Expr expr = transformExpr(node.expr());
        Expr base = addressOf(expr);
        Expr offset = ptrdiff(node.offset());
        Bin addr = new Bin(ptr_t(), Op.ADD, base, offset);
        return node.isLoadable() ? mem(addr, node.type()) : addr;
    }

    public Expr visit(PtrMemberNode node) {
        Expr expr = transformExpr(node.expr());
        Expr offset = ptrdiff(node.offset());
        Bin addr = new Bin(ptr_t(), Op.ADD, expr, offset);
        return node.isLoadable() ? mem(addr, node.type()) : addr;
    }

    public Expr visit(DereferenceNode node) {
        Expr addr = transformExpr(node.expr());
        return node.isLoadable() ? mem(addr, node.type()) : addr;
    }

    public Expr visit(AddressNode node) {
        Expr expr = transformExpr(node.expr());
        return node.expr().isLoadable() ? addressOf(expr) : expr;
    }

    public Expr visit(CastNode node) {
        if(node.isEffectiveCast()){
            return new Uni(asmType(node.type()),
                    node.expr().type().isSigned() ? Op.S_CAST : Op.U_CAST,
                    transformExpr(node.expr()));
        }else if(isStmtement()){
            transformStmt(node.expr());
            return null;
        }else {
            return transformExpr(node.expr());
        }
    }

    public Expr visit(SizeofExprNode node) {
        return new Int(size_t(), node.expr().allocSize());
    }

    public Expr visit(SizeofTypeNode node) {
        return new Int(size_t(), node.operand().allocSize());
    }

    public Expr visit(VariableNode node) {
        if(node.entity().isConstant()){
            return transformExpr(node.entity().value());
        }
        Var var = ref(node.entity());
        return node.isLoadable() ? var : addressOf(var);
    }

    public Expr visit(IntegerLiteralNode node) {
        return new Int(asmType(node.type()), node.value());
    }

    public Expr visit(StringLiteralNode node) {
        return new Str(asmType(node.type()), node.entry());
    }

    //
    // Utilities
    //


    // 指针 减法
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
        return expr.addressNode(ptr_t());
    }

    private Var ref(Entity entity){
        return new Var(varType(entity.type()), entity);
    }

    private Mem mem(Entity entity){
        return new Mem(asmType(entity.type().baseType()), ref(entity));
    }

    private Mem mem(Expr expr, Type type){
        return new Mem(asmType(type), expr);
    }

    private Int ptrdiff(long n){
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
