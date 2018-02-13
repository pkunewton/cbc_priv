package net.loveruby.cflat.entity;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author 刘科 on 2018/2/13.
 */
public class ConstantTable implements Iterable<ConstantEntry> {

    protected Map<String, ConstantEntry> table;

    public ConstantTable(){
        table = new LinkedHashMap<String, ConstantEntry>();
    }

    public boolean isEmpty(){
        return table.isEmpty();
    }

    /**
     * @see net.loveruby.cflat.compiler.LocalResolver
     * 当常量存在的时候直接读取常量池中的值，否则就创建一个，类似java中String的intern方法
     * */
    public ConstantEntry intern(String s){
        ConstantEntry entry = table.get(s);
        if(entry == null){
            entry = new ConstantEntry(s);
            table.put(s, entry);
        }
        return entry;
    }

    public Collection<ConstantEntry> entries(){
        return table.values();
    }

    public Iterator<ConstantEntry> iterator() {
        return table.values().iterator();
    }
}
