package com.github.fi3te.iliasdownloader.model;

import java.io.File;
import java.io.FileFilter;

/**
 * Created by wennier on 31.05.2015.
 */
public class IgnoreFilter implements FileFilter {

    private String[] suffix = {"ds_store"};

    public boolean accept(File f) {
        if (f == null)
            return false;

        if (f.isDirectory())
            return true;

        for (int i = 0; i < suffix.length; i++) {
            if (!f.getName().toLowerCase().endsWith(suffix[i]))
                return true;
        }
        return false;
    }
}