package net.loveruby.cflat.compiler;

import net.loveruby.cflat.ast.*;
import net.loveruby.cflat.entity.*;
import net.loveruby.cflat.ir.Str;
import net.loveruby.cflat.type.*;
import net.loveruby.cflat.utils.ErrorHandler;

import java.util.ArrayList;
import java.util.List;

/**
 * @author 刘科  2018/6/5
 * TypeRef 解析成 Type
 */
public class TypeResolver extends Visitor implements EntityVisitor<Void>, DeclarationVisitor<Void>{

    private final TypeTable typeTable;
    private final ErrorHandler errorHandler;

    public TypeResolver(TypeTable typeTable, ErrorHandler errorHandler) {
        this.typeTable = typeTable;
        this.errorHandler = errorHandler;
    }

    public void resolve(AST ast){
        defineTypes(ast.types());
        for (TypeDefinition def: ast.types()){
            def.accept(this);
        }
        for (Entity entity: ast.entities()){
            entity.accept(this);
        }

    }

    private void defineTypes(List<TypeDefinition> defTypes){
        for(TypeDefinition defType: defTypes){
            if(typeTable.isDefined(defType.typeRef())){
                error(defType, "duplicated type definition " + defType.typeRef());
            }else {
                typeTable.put(defType.typeRef(), defType.definingType());
            }
        }
    }

    private void bindType(TypeNode typeNode){
        if(typeNode.isResolved()){
            return;
        }
        typeNode.setType(typeTable.get(typeNode.typeRef()));
    }

    //
    // Declaration
    //
    public Void visit(StructNode struct) {
        resolveCompositeType(struct);
        return null;
    }

    public Void visit(UnionNode union) {
        resolveCompositeType(union);
        return null;
    }

    public void resolveCompositeType(CompositeTypeDefinition def){
        CompositeType type = (CompositeType) typeTable.get(def.typeNode().typeRef());
        if(type == null){
            throw new Error("can not intern struct/union: " + type.name());
        }
        for(Slot slot: type.members()){
            bindType(slot.typeNode());
        }
    }

    public Void visit(TypedefNode typedef) {
        bindType(typedef.typeNode());
        bindType(typedef.realTypeNode());
        return null;
    }

    //
    // Entity
    //

    public Void visit(DefinedVariable var) {
        bindType(var.typeNode());
        if(var.hasInitializer()){
            visitExpr(var.initializer());
        }
        return null;
    }

    public Void visit(UndefinedVariable var) {
        bindType(var.typeNode());
        return null;
    }

    public Void visit(DefinedFunction func) {
        resolveFunctionheader(func);
        visitStmt(func.body());
        return null;
    }

    public Void visit(UndefinedFunction func) {
        resolveFunctionheader(func);
        return null;
    }

    private void resolveFunctionheader(Function function){
//        bindType(function.typeNode());
        List<Type> types = new ArrayList<Type>();
        for(Parameter parameter: function.parameters()){
            // 不使用bindType
            // 数组作为参数时，要将其转化为指针
            // getParamType 会做这样的转换
            Type type = typeTable.getParamType(parameter.typeNode().typeRef());
            parameter.typeNode().setType(type);
            types.add(type);
        }
        // 需要检查一下 是否重复定义
        Type returnType = typeTable.get(((FunctionTypeRef)function.typeNode().typeRef()).returnType());
        ParamTypes paramTypes = new ParamTypes(((FunctionTypeRef)function.typeNode().typeRef()).params().location(), types,
                ((FunctionTypeRef)function.typeNode().typeRef()).params().isVararg());
        Type functionType = new FunctionType(returnType, paramTypes);
        typeTable.put(function.typeNode().typeRef(), functionType);
    }

    public Void visit(Constant constant) {
        bindType(constant.typeNode());
        visitExpr(constant.value());
        return null;
    }

    //
    // Expressions
    //

    public Void visit(BlockNode node){
        for(DefinedVariable variable: node.variables()){
            variable.accept(this);
        }
        visitStmts(node.stmts());
        return null;
    }

    public Void visit(CastNode node){
        bindType(node.typeNode());
        super.visit(node);
        return null;
    }

    public Void visit(SizeofExprNode node){
        bindType(node.typeNode());
        super.visit(node);
        return null;
    }

    public Void visit(SizeofTypeNode node) {
        bindType(node.typeNode());
        bindType(node.operandTypeNode());
        super.visit(node);
        return null;
    }

    public Void visit(IntegerLiteralNode node){
        bindType(node.typeNode());
        super.visit(node);
        return null;
    }

    public Void visit(StringLiteralNode node) {
        bindType(node.typeNode());
        super.visit(node);
        return null;
    }

    private void error(Node node, String message){
        errorHandler.error(node.location(), message);
    }
}
