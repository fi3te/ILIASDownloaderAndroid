package com.github.fi3te.iliasdownloader.controller;

import java.io.File;
import java.util.Comparator;

/**
 * Created by wennier on 24.06.2015.
 */
public class FileComparator implements Comparator<File> {
    @Override
    public int compare(File lhs, File rhs) {
        if (lhs.isDirectory() && !rhs.isDirectory()) {
            return -1;
        } else if (!lhs.isDirectory() && rhs.isDirectory()) {
            return 1;
        } else {
            return lhs.getName().compareTo(rhs.getName());
        }
    }
}
