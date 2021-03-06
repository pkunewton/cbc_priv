package net.loveruby.cflat.type;

import net.loveruby.cflat.ast.Location;

/**
 * @author 刘科 2018/5/29
 */
public class UserTypeRef extends TypeRef {

    protected String name;

    public UserTypeRef(String name){
        this(null, name);
    }

    public UserTypeRef(Location location, String name){
        super(location);
        this.name = name;
    }

    public boolean isUserType(){
        return true;
    }

    public String name() {
        return name;
    }

    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof UserTypeRef)){
            return false;
        }
        return name.equals(((UserTypeRef) obj).name);
    }

    @Override
    public String toString() {
        return name;
    }
}
