package net.loveruby.cflat.entity;

import net.loveruby.cflat.exception.SemanticException;
import net.loveruby.cflat.utils.ErrorHandler;

import java.util.*;

/**
 * @author 刘科  2018/5/30
 */
public class ToplevelScope extends Scope {

    protected Map<String, Entity> entities;
    protected List<DefinedVariable> staticLocalVariables; // 子定义域中所有的静态变量

    public ToplevelScope() {
        super();
        entities = new LinkedHashMap<String, Entity>();
        staticLocalVariables = null;
    }

    public boolean isToplevel() {
        return true;
    }

    public ToplevelScope toplevel() {
        return this;
    }

    public Scope parent() {
        return null;
    }

    // Declare variable or function globally.
    // 声明 全局变量 和 函数
    public void declareEntity(Entity entity) throws SemanticException {
        Entity e = entities.get(entity.name());
        if(e != null){
            // 重复声明变量
            throw new SemanticException("duplicated delaration: " +
                    entity.name() + " [" + e.location()
                    + " and " + entity.location() + "]");
        }
        entities.put(entity.name(), entity);
    }

    // Define variable or function globally.
    // 定义 全局变量 和 函数
    public void defineEntity(Entity entity) throws SemanticException {
        Entity e = entities.get(entity.name());
        if(e != null && e.isDefined()){
            // 重复定义变量
            throw new SemanticException("duplicated definition: " +
                    entity.name() + " [" + e.location()
                    + " and " + entity.location() + "]");
        }
        entities.put(entity.name(), entity);
    }

    // Searches and gets entity searching scopes upto ToplevelScope.
    // 查找变量定义， 并向上搜索到 ToplevelScope
    public Entity get(String name) throws SemanticException {
        Entity entity = entities.get(name);
        if(entity == null){
            throw new SemanticException("unresolved reference: " + name);
        }
        return entity;
    }

    /** Returns a list of all global variables. 返回所有全局变量
     * "All global variable" means: 包括
     *    * has global scope      global 定义域
     *    * defined or undefined  定义的和未定义的
     *    * public or private     静态的还是非静态的   static变量要向下搜索所有的LocalScope
     */
    public List<Variable> allGlobalVariables(){
        List<Variable> result = new ArrayList<Variable>();
        for(Entity entity: entities.values()){
            if(entity instanceof Variable){
                result.add((Variable)entity);
            }
        }
        result.addAll(staticLocalVariables());
        return result;
    }

    public List<DefinedVariable> definedGlobalScopeVariables(){
        List<DefinedVariable> result = new ArrayList<DefinedVariable>();
        for(Entity entity: entities.values()){
            if(entity instanceof DefinedVariable){
                result.add((DefinedVariable)entity);
            }
        }
        result.addAll(staticLocalVariables());
        return result;
    }

    // 子定义域中所有的静态变量，需要向下搜索所有的 LocalScope 定义域
    public List<DefinedVariable> staticLocalVariables() {
        if(staticLocalVariables == null){
            staticLocalVariables = new ArrayList<DefinedVariable>();
            for(LocalScope scope: children){
                staticLocalVariables.addAll(scope.staticLocalVariables());
            }
            Map<String, Integer> seqTable = new HashMap<String, Integer>();
            for(DefinedVariable variable: staticLocalVariables){
                // 标记 名字相同 静态变量 的声明次序
                Integer seq = seqTable.get(variable.name());
                if(seq == null){
                    seqTable.put(variable.name(), 1);
                    variable.setSequence(0);
                }else {
                    seqTable.put(variable.name(), seq + 1);
                    variable.setSequence(seq);
                }
            }
        }
        return staticLocalVariables;
    }

    // 递归检查 变量是否未被引用
    public void checkReferences(ErrorHandler handler){
        for(Entity entity: entities.values()){
            if(entity.isDefined()
                    && entity.isPrivate()
                    && !entity.isRefered()
                    && !entity.isConstant()){
                handler.warn(entity.location(), "unused variable: " + entity.name());
            }
        }
        // 递归检查 变量是否未被引用
        // 不要检查 函数参数,函数参数有一个单独的定义域空间
        for(LocalScope funcScope: children){
            for(LocalScope scope: funcScope.children){
                scope.checkReferences(handler);
            }
        }
    }
}
