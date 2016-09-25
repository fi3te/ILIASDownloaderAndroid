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

package com.github.fi3te.iliasdownloader.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import de.whiledo.iliasdownloader2.util.FileObject;

/**
 * Created by wennier on 26.06.2015.
 */
public class ExpandableCourse {

    private long refId;
    private String title;
    private List<FileObject> fileObjects;

    private int alreadyUpToDate;
    private int updated;
    private int ignored;
    private int loading;
    private int corrupt;
    private int error;

    public ExpandableCourse(long refId, String title, List<FileObject> fileObjects) {
        this.refId = refId;
        this.title = title;
        this.fileObjects = new ArrayList<>();
        addFileObjects(fileObjects);
    }

    public void addFileObject(FileObject fileObject) {
        fileObjects.add(fileObject);
        incCounter(fileObject);
    }

    public void addFileObject(int location, FileObject fileObject) {
        fileObjects.add(location, fileObject);
        incCounter(fileObject);
    }

    public void addFileObjects(List<FileObject> fileObjects) {
        this.fileObjects.addAll(fileObjects);
        for (FileObject f : fileObjects) {
            incCounter(f);
        }
    }

    public void setFileObject(int location, FileObject fileObject) {
        fileObjects.set(location, fileObject);
//        remove(location); TODO
//        addFileObject(location, fileObject);
        updateCounters();
    }

    private void updateCounters() {
        resetCounters();
        for (FileObject f : fileObjects) {
            incCounter(f);
        }
    }

    public int size() {
        return fileObjects.size();
    }

    public void clear() {
        fileObjects.clear();
        resetCounters();
    }

    public void remove(int position) {
        FileObject f = fileObjects.get(position);
        switch (f.getSyncState()) {
            case ALREADY_UP_TO_DATE:
                alreadyUpToDate--;
                break;
            case LOADING:
                loading--;
                break;
            case UPDATED:
                updated--;
                break;
            case CORRUPT:
                corrupt--;
                break;
            case ERROR:
                error--;
                break;
            default: // IGNORED_GREATER_MAX_SIZE || IGNORED_IGNORE_LIST || IGNORED_NO_DOWNLOAD_ALLOWED
                ignored--;
                break;
        }
        fileObjects.remove(position);
    }

    public boolean isInList(List<ExpandableCourse> list) {
        for (ExpandableCourse c : list) {
            if (c.getRefId() == refId)
                return true;
        }
        return false;
    }

    private void incCounter(FileObject fileObject) {
        switch (fileObject.getSyncState()) {
            case ALREADY_UP_TO_DATE:
                alreadyUpToDate++;
                break;
            case LOADING:
                loading++;
                break;
            case UPDATED:
                updated++;
                break;
            case CORRUPT:
                corrupt++;
                break;
            case ERROR:
                error++;
                break;
            default: // IGNORED_GREATER_MAX_SIZE || IGNORED_IGNORE_LIST || IGNORED_NO_DOWNLOAD_ALLOWED
                ignored++;
                break;
        }
    }

    private void resetCounters() {
        alreadyUpToDate = 0;
        updated = 0;
        ignored = 0;
        loading = 0;
        corrupt = 0;
        error = 0;
    }

    public long getRefId() {
        return refId;
    }

    public String getTitle() {
        return title;
    }

    public List<FileObject> getFileObjects() {
        return fileObjects;
    }

    public int getAlreadyUpToDate() {
        return alreadyUpToDate;
    }

    public int getUpdated() {
        return updated;
    }

    public int getIgnored() {
        return ignored;
    }

    public int getLoading() {
        return loading;
    }

    public int getCorrupt() {
        return corrupt;
    }

    public int getError() {
        return error;
    }

    public static List<ExpandableCourse> createCourses(List<FileObject> fileObjects) {
        List<String> courseNames = new ArrayList<>();
        List<ExpandableCourse> courses = new ArrayList<>();

        Collections.sort(fileObjects, new Comparator<FileObject>() {
            @Override
            public int compare(FileObject lhs, FileObject rhs) {
                return lhs.getTargetFile().getName().compareToIgnoreCase(rhs.getTargetFile().getName());
            }
        });

        for (FileObject fileObject : fileObjects) {
            String courseName = fileObject.getXmlObject().getCourseName();
            int index = courseNames.indexOf(courseName);
            if (index == -1) {
                courseNames.add(courseName);
                courses.add(new ExpandableCourse(fileObject.getRefId(), courseName, new ArrayList<FileObject>()));
                index = courseNames.indexOf(courseName);
            }

            courses.get(index).addFileObject(fileObject);
        }

        return courses;
    }
}
