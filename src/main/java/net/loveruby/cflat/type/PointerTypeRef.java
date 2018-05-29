package net.loveruby.cflat.type;

/**
 * @author 刘科 2018/5/29
 */
public class PointerTypeRef extends TypeRef {

    protected TypeRef baseType;

    public PointerTypeRef(TypeRef baseType) {
        super(baseType.location());
        this.baseType = baseType;
    }

    public boolean isPointer(){
        return true;
    }

    public TypeRef baseType() {
        return baseType;
    }

    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof PointerTypeRef)){
            return false;
        }
        return baseType.equals(((PointerTypeRef) obj).baseType);
    }

    @Override
    public String toString() {
        return baseType.toString() + "*";
    }
}
