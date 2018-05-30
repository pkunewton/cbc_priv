package net.loveruby.cflat.entity;

import net.loveruby.cflat.exception.SemanticException;

import java.util.ArrayList;
import java.util.List;

/**
 * @author 刘科  2018/5/30
 */
abstract public class Scope {

    protected List<LocalScope> children;

    public Scope() {
        this.children = new ArrayList<LocalScope>();
    }

    abstract public boolean isToplevel();
    abstract public ToplevelScope toplevel();
    abstract public Scope parent();

    public void addChild(LocalScope scope){
        children.add(scope);
    }

    abstract public Entity get(String name) throws SemanticException;
}
