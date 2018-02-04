package net.loveruby.cflat.utils;

import java.util.Iterator;
import java.util.List;

/**
 * @author 刘科 2018/02/03
 * 工具类：用于 Peephole窥视孔优化 的类
 * @see net.loveruby.cflat.sysdep.x86.PeepholeOptimizer
 */
public class Cursor<T> implements Iterator {

    protected List<T> list;
    protected int index;

    public Cursor(List<T> list){
        this(list, 0);
    }

    protected Cursor(List<T> list, int index){
        this.list = list;
        this.index = index;
    }

    public T next() {
        return list.get(index++);
    }

    public boolean hasNext() {
        return index < list.size();
    }

    public void remove() {
        list.remove(index);
    }

    public Cursor<T> clone(){
        return new Cursor<T>(list, index);
    }

    public T current(){
        if(index == 0)
            throw new Error("must be not happen: Cursor#current");
        return list.get(index - 1);
    }

    @Override
    public String toString() {
        return "#<Cursor list = " + list + "index" + index + ">";
    }

}
