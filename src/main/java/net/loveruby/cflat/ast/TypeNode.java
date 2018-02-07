package net.loveruby.cflat.ast;

import net.loveruby.cflat.type.Type;
import net.loveruby.cflat.type.TypeRef;

/**
 * Created by Administrator on 2018/2/7.
 */
public class TypeNode {

    TypeRef typeRef;
    Type type;

    public TypeRef typeRef() {
        return typeRef;
    }

    public boolean isResolved(){
        return (type != null);
    }
}
