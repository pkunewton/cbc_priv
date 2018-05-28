package net.loveruby.cflat.ast;

import net.loveruby.cflat.entity.DefinedVariable;
import net.loveruby.cflat.entity.Entity;
import net.loveruby.cflat.type.Type;

/**
 * @author 刘科 2018/5/28
 * 语法树的 terminal 节点（叶子节点）
 * 也是javacc解析出来的一个 <IDENTIFIER> TOKEN, 构造方式要指出其所在位置
 * VariableNode 也对应一个 DefinedVariable 实体
 */
public class VariableNode extends LHSNode {

    protected String name;
    protected Location location;
    protected Entity entity;

    public VariableNode(Location location, String name){

        this.location = location;
        this.name = name;
    }

    public VariableNode(DefinedVariable var){
        this.entity = var;
        this.name = var.name();
    }

    public String name(){
        return name;
    }

    public boolean isResolved(){
        return (entity != null);
    }

    public Entity entity() {
        if(entity == null){
            throw new Error("VariableNode.entity == null");
        }
        return entity;
    }

    public void setEntity(Entity entity){
        this.entity = entity;
    }

    public TypeNode typeNode(){
        return entity.typeNode();
    }

    @Override
    public boolean isParameter() {
        return entity().isParameter();
    }

    protected Type origType() {
        return entity().type();
    }

    public <S, E> E accept(ASTVisitor<S, E> visitor) {
        return visitor.visit(this);
    }

    public Location location() {
        return location;
    }

    protected void _dump(Dumper d) {
        if(type != null){
            d.printMember("type", type);
        }
        d.printMember("name", name, isResolved());
    }
}
