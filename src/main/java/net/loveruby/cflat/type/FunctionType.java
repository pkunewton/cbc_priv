package net.loveruby.cflat.type;

import com.sun.xml.internal.ws.wsdl.writer.document.ParamType;

/**
 * Created by Administrator on 2018/2/9.
 */
public class FunctionType extends Type {

    protected Type returnType;
    protected ParamTypes paramTypes;

    public long size() {
        return 0;
    }

    public boolean isSameType(Type other) {
        return false;
    }

    public boolean isCompatible(Type other) {
        return false;
    }

    public boolean isCastableTo(Type target) {
        return false;
    }

    public Type returnType() {
        return returnType;
    }
}
