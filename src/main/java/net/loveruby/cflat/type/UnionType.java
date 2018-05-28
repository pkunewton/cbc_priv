package net.loveruby.cflat.type;

import net.loveruby.cflat.ast.Location;
import net.loveruby.cflat.ast.Slot;

import java.util.List;

/**
 * Created by Administrator on 2018/2/9.
 */
public class UnionType extends CompositeType {
    public UnionType(String name, List<Slot> members, Location location) {
        super(name, members, location);
    }
}
