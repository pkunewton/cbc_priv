package net.loveruby.cflat;

import net.loveruby.cflat.exception.IPCException;
import net.loveruby.cflat.utils.CommandUtils;
import net.loveruby.cflat.utils.Cursor;
import net.loveruby.cflat.utils.ErrorHandler;
import net.loveruby.cflat.utils.TextUtils;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Test {

    public static void main(String[] args) {
//        List<String> list;
//        list = Arrays.asList("a", "b", "c");
//        Cursor<String> cursor = new Cursor<String>(list);
//        while (cursor.hasNext()){
//            System.out.println(list.get(0));
//            cursor.next();
//        }
//        System.out.println((byte)'A');
//        String s = "张三";
//        try {
//            byte[] bytes = s.getBytes("gb2312");
//            String ns = new String(bytes, "gb2312");
//            System.out.println(ns);
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        }
//        byte[] b1 = {(byte)34, (byte)65, (byte)3} ;
//        try {
//            s = new String(b1, "ASCII");
//            System.out.println(TextUtils.dumpString(s));
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        }
        List<String> cmdArgs = new ArrayList<String>();
        cmdArgs.add("ipconfig");
        ErrorHandler errorHandler = new ErrorHandler("1");
        try {
            CommandUtils.invoke(cmdArgs, errorHandler, false);
        } catch (IPCException e) {
            e.printStackTrace();
        }
    }
}
