package com.github.fi3te.iliasdownloader.ilias;

import android.content.Context;
import android.util.Base64;
import android.util.Base64InputStream;
import android.util.Log;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import de.whiledo.iliasdownloader2.exception.IliasAuthenticationException;
import de.whiledo.iliasdownloader2.service.FileSync;
import de.whiledo.iliasdownloader2.syncrunner.service.IliasProperties;
import de.whiledo.iliasdownloader2.syncrunner.service.IliasSyncListener;
import de.whiledo.iliasdownloader2.syncrunner.service.SyncService;
import de.whiledo.iliasdownloader2.util.DownloadMethod;
import de.whiledo.iliasdownloader2.util.FileObject;
import de.whiledo.iliasdownloader2.util.ObjectDoInterfaceX;
import de.whiledo.iliasdownloader2.util.SyncProgressListener;
import de.whiledo.iliasdownloader2.util.SyncState;
import de.whiledo.iliasdownloader2.xmlentities.course.XmlCourse;
import com.github.fi3te.iliasdownloader.R;
import com.github.fi3te.iliasdownloader.ilias.interfaces.IliasUI;
import com.github.fi3te.iliasdownloader.model.Key;

/**
 * Created by wennier on 07.05.2015.
 */
public class IliasController implements SyncProgressListener, IliasSyncListener {

    private static IliasController instance;

    private Context context;

    private IliasUI ui;
    private SyncService syncService;

    // Session
    private boolean loggedIn;
    private String password;

    private int progress;
    private String lastMessage;

    private ObjectDoInterfaceX<Void, IliasProperties> getIliasPropertiesCallback;
    private ObjectDoInterfaceX<Void, String> getPasswordCallback;
    private ObjectDoInterfaceX<Throwable, Void> asyncErrorHandler;


    private IliasController(final Context context) {
        this.context = context;

        IliasSyncListener iliasSyncListener = this;
        SyncProgressListener syncProgressListener = this;
        getIliasPropertiesCallback = new ObjectDoInterfaceX<Void, IliasProperties>() {
            @Override
            public IliasProperties doSomething(Void object) {
                return IliasPropertiesUtil.readIliasProperties(context);
            }
        };
        getPasswordCallback = new ObjectDoInterfaceX<Void, String>() {
            @Override
            public String doSomething(Void object) {
                return password;
            }
        };
        asyncErrorHandler = new ObjectDoInterfaceX<Throwable, Void>() {
            @Override
            public Void doSomething(Throwable e) {
                Log.e("Fehler", e.getMessage());
                return null;
            }
        };

        initSyncService(iliasSyncListener, syncProgressListener);
    }

    private void initSyncService(IliasSyncListener iliasSyncListener, SyncProgressListener syncProgressListener) {
        syncService = new SyncService(iliasSyncListener, syncProgressListener, getIliasPropertiesCallback, getPasswordCallback, asyncErrorHandler);
        syncService.setBase64InputStreamFactory(new ObjectDoInterfaceX<InputStream, InputStream>() {
            @Override
            public InputStream doSomething(InputStream object) {
                return new Base64InputStream(object, Base64.CRLF);
            }
        });
    }

    public static IliasController getInstance(Context context) {
        if (instance == null) {
            instance = new IliasController(context);
        }
        return instance;
    }

    public void registerUI(IliasUI ui) {
        this.ui = ui;

        ui.publishProgress(progress);
        ui.showMessage(lastMessage);
        ui.newDownload();
        if (!isRunning()) {
            ui.syncFinishedOrStopped();
        }
        ui.updateCurrentSynchronisation(null);
    }

    public void unregisterUI() {
        this.ui = null;
    }

    public boolean login(String username, String password) {
        try {
            IliasPropertiesUtil.putString(context, Key.USER_LOGIN, username);
            this.password = password;
            syncService.login();
            loggedIn = true;
            return true;
        } catch (IliasAuthenticationException iae) {
            Log.e("Fehler", iae.getMessage());
            loggedIn = false;
            return false;
        }
    }

    public boolean isLoggedIn() {
        return loggedIn;
    }

    public boolean logout() {
        if (isLoggedIn() && !isRunning()) {
            final SyncService oldSyncService = syncService;
            new Thread() {
                public void run() {
                    if(oldSyncService != null) {
                        oldSyncService.logoutIfNotRunning();
                    }
                }
            }.start();
            loggedIn = false;
            password = "";
            initSyncService(this, this); // TODO Kevin: logout funktioniert nicht richtig
            return true;
        } else {
            return false;
        }
    }

    public List<Long> getCourseIds() {
        if (loggedIn) {
            if (!syncService.getIliasSoapService().isLoggedIn()) {
                syncService.login();
            }
            return syncService.getIliasSoapService().getCourseIds();
        } else {
            return null;
        }
    }

    public List<String> getCourseNames(List<Long> courses) {
        List<String> courseNames = new ArrayList<>(courses.size());
        for (Long course : courses) {
            courseNames.add(getCourseName(course));
        }
        return courseNames;
    }

    private String getCourseName(long refId) {
        XmlCourse c = syncService.getIliasSoapService().getCourse(refId);
        return c.getMetaData().getGeneral().getTitle();
    }

    public void startSync() {
        if (loggedIn) {
            Session.clearSynchronisationFiles();

            if (ui != null) {
                ui.updateCurrentSynchronisation(null);
            }

            syncService.startOrStopSync();
        }
    }

    public void stopSync() {
        if (syncService.isRunning()) {
            FileSync fileSync = syncService.getFileSync();
            if (fileSync != null) {
                fileSync.stop();
                syncStopped();
            }
        }
    }

    public void downloadFile(final FileObject fileObject, final DownloadMethod downloadMethod) {
        if (loggedIn) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        syncService.login();
                        syncService.getFileSync().loadFileOrExerciseIgoreDownloadFileFlag(fileObject.getXmlObject(), downloadMethod);
                    } catch (Exception e) {
                        showMessageOrBuffer("Die Datei " + fileObject.getTargetFile().getAbsolutePath() + " konnte nicht heruntegeladen werden");
                    }
                    syncService.logoutIfNotRunning();
                }
            }).start();
        }
    }

    public boolean isRunning() {
        return syncService.isRunning();
    }


    @Override
    public void syncStarted() {
        showMessageOrBuffer(context.getResources().getString(R.string.synchronisation_starts));
    }

    @Override
    public void syncFinished() {
        showMessageOrBuffer(context.getResources().getString(R.string.synchronisation_completed));
        if (ui != null) {
            ui.syncFinishedOrStopped();
        }
    }

    @Override
    public void syncStopped() {
        showMessageOrBuffer(context.getResources().getString(R.string.synchronisation_cancelled));
        if (ui != null) {
            ui.syncFinishedOrStopped();
        }
    }

    @Override
    public void syncCoursesFound(Collection<Long> activeCourseIds, Collection<Long> allCourseIds) {
        String template = context.getResources().getString(R.string.sync_xxx_of_xxx_courses);
        template = template.replaceFirst("xxx", "" + activeCourseIds.size());
        template = template.replaceFirst("xxx", "" + allCourseIds.size());

        showMessageOrBuffer(template);
    }

    @Override
    public void progress(int percent) {
        publishProgressOrBuffer(percent);
    }

    @Override
    public void fileLoadStart(FileObject fileObject) {
        Session.addOrUpdateSynchronisationFiles(fileObject);

        if (ui != null) {
            ui.updateCurrentSynchronisation(fileObject);
        }

        showMessageOrBuffer(context.getResources().getString(R.string.file_is_loading) + ": " + fileObject.getTargetFile().getName());
    }

    @Override
    public void fileLoadEnd(FileObject fileObject) {
        Session.addOrUpdateSynchronisationFiles(fileObject);

        if (ui != null) {
            ui.updateCurrentSynchronisation(fileObject);
        }

        if (fileObject.getSyncState().equals(SyncState.UPDATED)) {

            Session.addDownload(0, fileObject.getTargetFile().getPath());
            if (ui != null) {
                ui.newDownload();
            }
        }

        showMessageOrBuffer(fileObject.getSyncState().getReadableName() + ": " + fileObject.getTargetFile().getName());
    }


    public synchronized void showMessageOrBuffer(String message) {
        lastMessage = message;
        if (ui != null) {
            ui.showMessage(message);
        }
    }

    public synchronized void publishProgressOrBuffer(int percent) {
        progress = percent;
        if (ui != null) {
            ui.publishProgress(percent);
        }
    }
}
