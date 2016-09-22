package com.github.fi3te.iliasdownloader.controller.courses;

import java.util.List;

import de.whiledo.iliasdownloader2.util.TwoObjectsX;

/**
 * Created by wennier on 12.09.2015.
 */
public abstract class AbstractDataProvider<G, C> {

    public abstract int getGroupCount();
    public abstract int getChildCount(int groupPosition);

    public abstract List<G> getGroupItems();
    public abstract G getGroupItem(int groupPosition);
    public abstract C getChildItem(int groupPosition, int childPosition);

    public abstract int addGroupItem(G groupItem);

    public abstract int addChildItem(int groupPosition, C childItem);
    public abstract TwoObjectsX<Integer, Integer> addChildItem(C childItem);
    public abstract TwoObjectsX<Integer, Integer> indexOfChildItem(C childItem);
    public abstract void updateChildItem(int groupPosition, int childPosition, C childItem);

    public abstract void removeGroupItem(int groupPosition);
    public abstract void removeChildItem(int groupPosition, int childPosition);

    public abstract void clear();

    public abstract void updateData(List<C> childItems);

}
