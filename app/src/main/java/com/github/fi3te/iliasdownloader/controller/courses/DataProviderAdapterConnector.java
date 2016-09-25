/*
 * Copyright (C) 2015-2016 Fiete Wennier
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 *
 * Dieses Programm ist Freie Software: Sie können es unter den Bedingungen
 * der GNU General Public License, wie von der Free Software Foundation,
 * Version 3 der Lizenz oder (nach Ihrer Wahl) jeder neueren
 * veröffentlichten Version, weiterverbreiten und/oder modifizieren.
 *
 * Dieses Programm wird in der Hoffnung, dass es nützlich sein wird, aber
 * OHNE JEDE GEWÄHRLEISTUNG, bereitgestellt; sogar ohne die implizite
 * Gewährleistung der MARKTFÄHIGKEIT oder EIGNUNG FÜR EINEN BESTIMMTEN ZWECK.
 * Siehe die GNU General Public License für weitere Details.
 *
 * Sie sollten eine Kopie der GNU General Public License zusammen mit diesem
 * Programm erhalten haben. Wenn nicht, siehe <http://www.gnu.org/licenses/>.
 *
 */

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
