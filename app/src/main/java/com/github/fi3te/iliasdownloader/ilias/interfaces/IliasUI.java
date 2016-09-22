package com.github.fi3te.iliasdownloader.ilias.interfaces;

import de.whiledo.iliasdownloader2.util.FileObject;

/**
 * Created by wennier on 30.05.2015.
 */
public interface IliasUI {
    void showMessage(String message);

    void publishProgress(int percent);

    void newDownload();

    void updateCurrentSynchronisation(FileObject fileObject);

    void syncFinishedOrStopped();
}
