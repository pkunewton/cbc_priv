package net.loveruby.cflat.utils;

import java.io.UnsupportedEncodingException;

/**
 * @author 刘科 2018/02/03
 * 处理文本，把原始文本处理成 字符串格式
 * 用于 汇编指令生成
 */
public abstract class TextUtils {

    static final private byte vtab = 013; // ascii码中的垂直制表符

    static public String dumpString(String content){
        try {
            // 此处的编码格式应该是 Parser.SOURCE_ENCODING
            // TODO
            return dumpString(content, "UTF-8");
        }catch (UnsupportedEncodingException ex){
            throw new Error("UTF-8 is not supported??: " + ex.getMessage());
        }
    }


    /**
     * 把输入的内容按照java字符串的格式转换
     * */
    static public String dumpString(String content, String encoding)
        throws UnsupportedEncodingException{
        byte[] src = content.getBytes(encoding);
        StringBuilder buffer = new StringBuilder();
        buffer.append("\"");
        for (int n = 0; n < src.length; n++) {
            int c = toUnsigned(src[n]);
            if (c == '"') buffer.append("\\\"");
            else if (isPrintable(c)) buffer.append((char)c);
            else if (c == '\b') buffer.append("\\b");
            else if (c == '\t') buffer.append("\\t");
            else if (c == '\n') buffer.append("\\n");
            else if (c == vtab) buffer.append("\\v");
            else if (c == '\f') buffer.append("\\f");
            else if (c == '\r') buffer.append("\\r");
            else {
                // 其他的字节按照 反斜杠+八进制的形式打印出来
                buffer.append("\\" + Integer.toOctalString(c));
            }
        }
        buffer.append("\"");
        return buffer.toString();
    }

    /**
     * 把 byte 型数据转换成 unsigned 类型
     * */
    static private int toUnsigned(byte b){
        return b >= 0 ? b : b + 256;
    }

    /**
     * 检查整数c是否在byte中可打印的范围 32 - 126
     * */
    static public boolean isPrintable(int c){
        return (' ' <= c) && ('~' >= c);
    }
}
