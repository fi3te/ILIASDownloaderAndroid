package com.github.fi3te.iliasdownloader.controller;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.github.fi3te.iliasdownloader.R;

/**
 * Created by wennier on 31.05.2015.
 */
public class Util {

    public static <T> List<T> arrayToArrayList(T[] array) {
        List<T> list = new ArrayList<>();
        if (array != null) {
            for (int i = 0; i < array.length; i++) {
                list.add(array[i]);
            }
        }
        return list;
    }

    public static void openFile(File file, Activity forMessages) {
        if (file != null && forMessages != null && file.isFile()) {
            String extension = FilenameUtils.getExtension(file.getPath());
            if (extension.length() > 0) {
                try {
                    extension = extension.toLowerCase();
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setDataAndType(Uri.fromFile(file), MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension));
                    intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                    forMessages.startActivity(intent);
                } catch (ActivityNotFoundException anfe) {
                    Toast.makeText(forMessages, forMessages.getResources().getString(R.string.unknown_type), Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}
