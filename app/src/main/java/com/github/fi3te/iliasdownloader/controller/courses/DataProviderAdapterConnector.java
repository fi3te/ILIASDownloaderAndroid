package com.github.fi3te.iliasdownloader.controller.courses;

import com.h6ah4i.android.widget.advrecyclerview.expandable.RecyclerViewExpandableItemManager;

import de.whiledo.iliasdownloader2.util.FileObject;
import de.whiledo.iliasdownloader2.util.TwoObjectsX;
import com.github.fi3te.iliasdownloader.ilias.Session;
import com.github.fi3te.iliasdownloader.model.ExpandableCourse;
import com.github.fi3te.iliasdownloader.model.adapter.CourseRecyclerAdapter;

/**
 * Created by wennier on 12.09.2015.
 */
public class DataProviderAdapterConnector {

    private AbstractDataProvider<ExpandableCourse, FileObject> dataProvider;
    private RecyclerViewExpandableItemManager itemManager;
    private CourseRecyclerAdapter adapter;

    public DataProviderAdapterConnector(AbstractDataProvider<ExpandableCourse, FileObject> dataProvider, RecyclerViewExpandableItemManager itemManager, CourseRecyclerAdapter adapter) {
        this.dataProvider = dataProvider;
        this.itemManager = itemManager;
        this.adapter = adapter;
    }

    public void insertOrUpdate(FileObject fileObject) {
        TwoObjectsX<Integer, Integer> position = dataProvider.indexOfChildItem(fileObject);
        if (position.getObjectA().equals(-1)) {
            int groupCount = dataProvider.getGroupCount();
            position = dataProvider.addChildItem(fileObject);
            int groupCountAfterInsert = dataProvider.getGroupCount();
            if (groupCountAfterInsert > groupCount) {
                itemManager.notifyGroupItemInserted(groupCount);
            } else {
                itemManager.notifyChildItemInserted(position.getObjectA(), position.getObjectB());
            }
        } else {
            dataProvider.updateChildItem(position.getObjectA(), position.getObjectB(), fileObject);
            itemManager.notifyChildItemChanged(position.getObjectA(), position.getObjectB());
        }
        itemManager.notifyGroupItemChanged(position.getObjectA());
    }

    public void synchronizeWithSession() {
        dataProvider.updateData(Session.getSynchronisationFiles());
        adapter.notifyDataSetChanged();
    }

}
