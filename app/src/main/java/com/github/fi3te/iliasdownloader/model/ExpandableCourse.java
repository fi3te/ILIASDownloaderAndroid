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
