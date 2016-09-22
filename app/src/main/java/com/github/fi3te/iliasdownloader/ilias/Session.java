package com.github.fi3te.iliasdownloader.ilias;

import android.content.Context;
import android.os.Environment;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import de.whiledo.iliasdownloader2.util.FileObject;
import com.github.fi3te.iliasdownloader.controller.DatabaseController;
import com.github.fi3te.iliasdownloader.model.xml.Downloads;

/**
 * Created by wennier on 31.05.2015.
 */
public class Session {
    private static Downloads lastDownloads = new Downloads();

    private static List<FileObject> synchronisationFiles = Collections.synchronizedList(new LinkedList<FileObject>());

    public static void loadLastDownloads(Context context) {

        // TODO Datei loeschen, kann in einem spaeteren Release wieder entfernt werden
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            String base = IliasPropertiesUtil.readIliasProperties(context).getBaseDirectory();
            if (!base.endsWith("/"))
                base += "/";
            File file = new File(base + "lastDownloads.xml");
            if (file.exists()) {
                file.delete();
            }
        }

        DatabaseController databaseController = DatabaseController.getInstance(context);
        lastDownloads = new Downloads(databaseController.getAllDownloads());
    }

    public static void saveLastDownloads(Context context) {
//        if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
//            String base = IliasPropertiesUtil.readIliasProperties(context).getBaseDirectory();
//            if (!base.endsWith("/"))
//                base += "/";
//
//            File dir = new File(base);
//            if (!dir.exists()) {
//                dir.mkdirs();
//            }
//
//            File file = new File(base + LAST_DOWNLOADS_FILE_NAME);
//            Functions.writeXmlObject(lastDownloads, file);
//        }
        DatabaseController databaseController = DatabaseController.getInstance(context);
        databaseController.setAllDownloads(lastDownloads.getPathList());
    }

    public static void addDownload(int index, String path) {
        lastDownloads.getPathList().add(index, path);
    }

    public static void deleteDownloadHistory() {
        lastDownloads.deleteHistory();
    }

    public static void removeFromHistory(File file) {
        lastDownloads.getPathList().remove(file.getPath());
    }

    public static List<File> getLastDownloads() {
        List<File> list = new ArrayList<>();
        for (String path : lastDownloads.getPathList()) {
            list.add(new File(path));
        }
        return list;
    }


    public static List<FileObject> getSynchronisationFiles() {
        return synchronisationFiles;
    }

    public static void clearSynchronisationFiles() {
        synchronisationFiles = Collections.synchronizedList(new LinkedList<FileObject>());
    }

    public static void addOrUpdateSynchronisationFiles(FileObject fileObject) {
        synchronisationFiles.remove(fileObject);
        synchronisationFiles.add(0, fileObject);
    }
}
