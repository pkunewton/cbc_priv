package net.loveruby.cflat.utils;


/**
 * @author 刘科 2018/02/04
 *
 */
public final class AsmUtils {

    private AsmUtils() {}

    /**
     * @param n 申请的stack大小
     * @param alignment 对齐的单位
     * 用于把 申请的栈stack的大小n 安装alignment的大小对齐， 比如
     * */
    public static long align(long n, long alignment){
        return (n + alignment - 1) / alignment * alignment;
    }
}
