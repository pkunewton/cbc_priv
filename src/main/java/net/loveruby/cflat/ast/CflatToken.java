package net.loveruby.cflat.ast;

import net.loveruby.cflat.parser.ParserConstants;
import net.loveruby.cflat.parser.Token;
import net.loveruby.cflat.utils.TextUtils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author 刘科 2018/02/07
 */
public class CflatToken implements Iterable<CflatToken> {

    protected Token token;
    protected boolean isSpecial;

    public CflatToken(Token token){
        this(token, false);
    }

    public CflatToken(Token token, boolean isSpecial){
        this.token = token;
        this.isSpecial = isSpecial;
    }

    public String toString(){
        return token.image;
    }

    public boolean isSpecial(){
        return this.isSpecial;
    }

    public int kindID(){
        return token.kind;
    }

    public String kindName(){
        return ParserConstants.tokenImage[token.kind];
    }

    public int lineno(){
        return token.beginLine;
    }

    public int column(){
        return token.beginColumn;
    }

    public String image(){
        return token.image;
    }

    public String dumpImage(){
        return TextUtils.dumpString(token.image);
    }

    /**
     * 构建一个 CflatToken 迭代器
     * */
    public Iterator<CflatToken> iterator() {
        return buildTokenList(token, false).iterator();
    }

    /**
     * 构建 忽略最开始的 specialToken 的 CflatToken 列表
     * */
    public List<CflatToken> tokensWithoutFirstSpecials(){
        return buildTokenList(token, true);
    }

    /**
     * @param rejectFirstSpecials 是否忽略最开始的 specialToken
     *                            用 SPECIAL_TOKEN 扫描得到的 token 会被存放到之后用 TOKEN 扫描到的 token 的
     *                            specialToken 属性中
     */
    protected List<CflatToken> buildTokenList(Token first, boolean rejectFirstSpecials){
        List<CflatToken> reslt = new ArrayList<CflatToken>();
        boolean rejectSpecials = rejectFirstSpecials;
        for (Token t = first; t != null; t = t.next){
            // 当前面的 sepcialToken 不为空且不忽略specialToken时，将specialToken的内容保存在列表中
            if(t.specialToken != null && !rejectSpecials){
                Token s = specialTokenHead(t);
                for (; s != null; s = s.next){
                    reslt.add(new CflatToken(s));
                }
            }
            reslt.add(new CflatToken(t));
            // 第一个specialToken之后，不在忽略specialToken
            rejectSpecials = false;
        }
        return reslt;
    }

    /**
     * 当前Token的 specialToken 属性不为空时
     * 进入 specialToken
     * 并返回 specialToken 属性为空的 specialToken
     * */
    protected Token specialTokenHead(Token firstSpecial){
        Token s = firstSpecial;
        while (s.specialToken != null){
            s = s.specialToken;
        }
        return s;
    }

    /**
     * 获取token所在的行的内容
     * */
    public String includedLine(){
        StringBuilder buffer = new StringBuilder();
        // 忽略这个Token对象之前的specialToken
        for ( CflatToken t : tokensWithoutFirstSpecials()){
            int index = t.image().indexOf("\n");
            if(index > 0){
                buffer.append(t.image().substring(0, index));
                break;
            }
            buffer.append(t.image());
        }
        return buffer.toString();
    }
}
