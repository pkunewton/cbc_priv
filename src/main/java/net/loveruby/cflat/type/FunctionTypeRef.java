package net.loveruby.cflat.type;

/**
 * @author 刘科 2018/5/29
 */
public class FunctionTypeRef extends TypeRef {

    protected TypeRef returnType;
    protected ParamTypeRefs params;

    public FunctionTypeRef(TypeRef returnType, ParamTypeRefs params) {
        super(returnType.location());
        this.returnType = returnType;
        this.params = params;
    }

    public boolean isFunction(){
        return true;
    }

    public boolean equals(Object other){
        return (other instanceof FunctionTypeRef) &&
                (equals((FunctionTypeRef)other));
    }

    public boolean equals(FunctionTypeRef other){
        return (returnType.equals(other.returnType)) &&
                (params.equals(other.params));
    }

    public TypeRef returnType() {
        return returnType;
    }

    public ParamTypeRefs params() {
        return params;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(returnType.toString());
        sb.append(" (");
        String seq = "";
        for (TypeRef ref : this.params.typeRefs()) {
            sb.append(seq);
            sb.append(ref.toString());
            seq = ", ";
        }
        sb.append(")");
        return sb.toString();
    }
}
