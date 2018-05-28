package net.loveruby.cflat.type;

import net.loveruby.cflat.ast.Location;

public class UserTypeRef extends TypeRef {

    protected String name;

    public UserTypeRef(String name){
        this(null, name);
    }

    public UserTypeRef(Location location, String name){
        super(location);
        this.name = name;
    }
}
