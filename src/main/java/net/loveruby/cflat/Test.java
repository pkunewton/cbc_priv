package net.loveruby.cflat;

import net.loveruby.cflat.exception.IPCException;
import net.loveruby.cflat.utils.CommandUtils;
import net.loveruby.cflat.utils.Cursor;
import net.loveruby.cflat.utils.ErrorHandler;
import net.loveruby.cflat.utils.TextUtils;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Test {

    public static void main(String[] args) throws UnsupportedEncodingException {
//        List<String> list;
//        list = Arrays.asList("a", "b", "c");
//        Cursor<String> cursor = new Cursor<String>(list);
//        while (cursor.hasNext()){
//            System.out.println(list.get(0));
//            cursor.next();
//        }
//        System.out.println((byte)'A');
//        byte[] b1 = {(byte)'\b', (byte)65, (byte)3} ;
//        try {
//            String s = new String(b1, "ASCII");
//            System.out.println(TextUtils.dumpString(s));
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        }

        String image = "iaodakmcasca";
        int pos = image.indexOf("x", 2);
        System.out.println(pos);
        System.out.println("\345\244\247\345\256\266\345\245\275\343\200\202");
        byte[] bytes = "\345\244\247\345\256\266\345\245\275\343\200\202".getBytes("ISO8859-1");
        for (int i = 0; i < bytes.length; i++) {
//            if(bytes[i] < 0){
//                bytes[i] = (byte) (256 + bytes[i]);
//            }
            System.out.println((char)bytes[i]);
        }
        System.out.println(new String(bytes));
        byte[] bt = new byte[3];
        int a = Integer.valueOf("345", 8);
        bt[0] = (byte)a;
        a = Integer.valueOf("244", 8);
        bt[1] = (byte)a;
        a = Integer.valueOf("247", 8);
        bt[2] = (byte)a;
        System.out.println(new String(bt));

        File file = new File("./Test.class");
        System.out.println(file.getPath());
    }
}
