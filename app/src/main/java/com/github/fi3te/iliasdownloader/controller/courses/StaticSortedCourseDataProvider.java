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

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import de.whiledo.iliasdownloader2.util.FileObject;
import de.whiledo.iliasdownloader2.util.TwoObjectsX;
import com.github.fi3te.iliasdownloader.model.ExpandableCourse;

/**
 * Created by wennier on 12.09.2015.
 */
public class StaticSortedCourseDataProvider extends AbstractDataProvider<ExpandableCourse, FileObject> {

    private static StaticSortedCourseDataProvider instance;
    private static List<ExpandableCourse> courses = new LinkedList<ExpandableCourse>();

    private StaticSortedCourseDataProvider() {

    }

    public static StaticSortedCourseDataProvider getInstance() {
        if (instance == null) {
            instance = new StaticSortedCourseDataProvider();
        }
        return instance;
    }

    @Override
    public int getGroupCount() {
        return courses.size();
    }

    @Override
    public int getChildCount(int groupPosition) {
        return courses.get(groupPosition).size();
    }

    @Override
    public List<ExpandableCourse> getGroupItems() {
        return courses;
    }

    @Override
    public ExpandableCourse getGroupItem(int groupPosition) {
        return courses.get(groupPosition);
    }

    @Override
    public FileObject getChildItem(int groupPosition, int childPosition) {
        return courses.get(groupPosition).getFileObjects().get(childPosition);
    }

    @Override
    public int addGroupItem(ExpandableCourse groupItem) {
        int position = courses.size();
        courses.add(groupItem);
        return position;
    }

    @Override
    public int addChildItem(int groupPosition, FileObject childItem) {
        List<FileObject> fileObjects = courses.get(groupPosition).getFileObjects();
        ExpandableCourse course = courses.get(groupPosition);
        int position = -1;
        for (int i = 0; i < fileObjects.size(); i++) {
            int vergleich = childItem.getTargetFile().getName().compareToIgnoreCase(fileObjects.get(i).getTargetFile().getName());
            if (vergleich < 0) {
                course.addFileObject(i, childItem);
                position = i;
                break;
            }/* else if(vergleich == 0) {
                if(fileObjects.get(i).getRefId() == childItem.getRefId()) {
                    course.setFileObject(i, childItem);
                    position = i;
                    break;
                } else { // not the same file
                    course.addFileObject(i, childItem);
                    position = i;
                    break;
                }
            }*/
        }
        if (position < 0) {
            position = fileObjects.size();
            course.addFileObject(childItem);
        }
        return position;
    }

    @Override
    public TwoObjectsX<Integer, Integer> addChildItem(FileObject childItem) {
        String courseName = childItem.getXmlObject().getCourseName();

        List<String> courseNames = new ArrayList<>();
        for (ExpandableCourse c : courses) {
            courseNames.add(c.getTitle());
        }

        int groupPosition = courseNames.indexOf(courseName);

        if (groupPosition == -1) {
            courseNames.add(courseName);
            courses.add(new ExpandableCourse(childItem.getRefId(), courseName, new ArrayList<FileObject>()));
            groupPosition = courseNames.indexOf(courseName);
        }

        int childPosition = addChildItem(groupPosition, childItem);

        return new TwoObjectsX<>(groupPosition, childPosition);
    }

    @Override
    public TwoObjectsX<Integer, Integer> indexOfChildItem(FileObject childItem) {
        String courseName = childItem.getXmlObject().getCourseName();

        List<String> courseNames = new ArrayList<>();
        for (ExpandableCourse c : courses) {
            courseNames.add(c.getTitle());
        }

        int groupPosition = courseNames.indexOf(courseName);

        if (groupPosition != -1) {
            List<FileObject> course = courses.get(groupPosition).getFileObjects();
            for (int i = 0; i < course.size(); i++) {
                int vergleich = course.get(i).getTargetFile().getName().compareToIgnoreCase(childItem.getTargetFile().getName());
                if (vergleich == 0 && course.get(i).getLastUpdated() == childItem.getLastUpdated()) {
                    return new TwoObjectsX<>(groupPosition, i);
                }
            }
        }

        return new TwoObjectsX<>(-1, -1);
    }

    @Override
    public void updateChildItem(int groupPosition, int childPosition, FileObject childItem) {
        courses.get(groupPosition).setFileObject(childPosition, childItem);
    }

    @Override
    public void removeGroupItem(int groupPosition) {
        courses.remove(groupPosition);
    }

    @Override
    public void removeChildItem(int groupPosition, int childPosition) {
        courses.get(groupPosition).remove(childPosition);
    }

    @Override
    public void clear() {
        courses.clear();
    }

    @Override
    public void updateData(List<FileObject> childItems) {
        courses = ExpandableCourse.createCourses(childItems);
    }
}
