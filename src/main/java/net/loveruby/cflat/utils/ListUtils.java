package net.loveruby.cflat.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

/**
 * @author 刘科 2018/02/04
 * @see net.loveruby.cflat.compiler.IRGenerator
 * @see net.loveruby.cflat.sysdep.x86.CodeGenerator
 * */
abstract public class ListUtils {

    public static <T> List<T> reverse(List<T> list){
        List<T> result = new ArrayList<T>(list.size());
        ListIterator<T> iterator = list.listIterator(list.size());
        while (iterator.hasPrevious()){
            result.add(iterator.previous());
        }
        return result;
    }
}
