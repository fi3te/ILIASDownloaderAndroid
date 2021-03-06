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

package com.github.fi3te.iliasdownloader.view.fragment.task;

import android.support.v4.app.DialogFragment;

/**
 * Created by wennier on 02.06.2015.
 */
public class SyncFragmentXXX extends DialogFragment {


//    public static final String BUNDLE_PASSWORD = "password";
//
//    private SynchronisationTask mTask;
//    private SyncServiceA syncService;
//    private SyncUI syncUI;
//
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//
//        setRetainInstance(true);
//
//
//
//
//        Bundle bundle = getArguments();
//        final String password = bundle.getString(BUNDLE_PASSWORD);
//
//
//
//        IliasSyncListener iliasSyncListener = new IliasSyncListener() {
//            @Override
//            public void syncStarted() {
//                Log.i("syncStarted", "aaaa");
//            }
//
//            @Override
//            public void syncFinished() {
//                Log.i("syncFinished", "aaaa");
//            }
//
//            @Override
//            public void syncStopped() {
//                Log.i("syncStopped", "aaaa");
//            }
//
//            @Override
//            public void syncCoursesFound(Collection<Long> activeCourseIds, Collection<Long> allCourseIds) {
//                Log.i("syncCoursesFound", "aaaa");
//            }
//        };
//        SyncProgressListener syncProgressListener = new SyncProgressListener() {
//            @Override
//            public void progress(int percent) {
//                Log.i("progress", percent + "");
//            }
//
//            @Override
//            public void fileLoadStart(FileObject fileObject) {
//                Log.i("fileLoadStart", "aaaa");
//            }
//
//            @Override
//            public void fileLoadEnd(FileObject fileObject) {
//                Log.i("fileLoadEnd", "aaaa");
//            }
//        };
//        ObjectDoInterfaceX<Void, IliasProperties> getIliasPropertiesCallback = new ObjectDoInterfaceX<Void, IliasProperties>() {
//            @Override
//            public IliasProperties doSomething(Void object) {
//                return IliasPropertiesUtil.readIliasProperties(getActivity());
//            }
//        };
//        ObjectDoInterfaceX<Void, String> getPasswordCallback = new ObjectDoInterfaceX<Void, String>() {
//            @Override
//            public String doSomething(Void object) {
//                return password;
//            }
//        };
//        ObjectDoInterfaceX<Throwable, Void> asyncErrorHandler = new ObjectDoInterfaceX<Throwable, Void>() {
//            @Override
//            public Void doSomething(Throwable e) {
//                Log.i("Fehler", e.getMessage());
//                return null;
//            }
//        };
//
//        syncService = new SyncServiceA(iliasSyncListener, syncProgressListener, getIliasPropertiesCallback, getPasswordCallback, asyncErrorHandler);
//        syncService.setBase64InputStreamFactory(new ObjectDoInterfaceX<InputStream, InputStream>() {
//            @Override
//            public InputStream doSomething(InputStream object) {
//                return new Base64InputStream(object, Base64.CRLF);
//            }
//        });
//
//
//
//        if(password != null) {
//            mTask = new SynchronisationTask();
//            mTask.execute();
//        }
//    }
//
//    @Override
//    public void onDestroyView() {
//        if (getDialog() != null && getRetainInstance())
//            getDialog().setDismissMessage(null);
//        super.onDestroyView();
//    }
//
//    @Override
//    public MaterialDialog onCreateDialog(Bundle savedInstanceState) {
//        setCancelable(false);
//        return new MaterialDialog.Builder(getActivity())
//                .title("Synchronisation TODO")
//                .content("bitte warten... TODO")
//                .progress(false, 0)
//                .build();
//    }
//
//    @Override
//    public void onAttach(Activity activity) {
//        super.onAttach(activity);
////        syncUI = (SyncUI) activity; TODO
//    }
//
//    @Override
//    public void onDetach() {
//        super.onDetach();
//        syncUI = null;
//    }
//
//
//
//    private class SynchronisationTask extends AsyncTask<Void, Integer, Void> implements CancelledListener {
//
//        @Override
//        public void onPreExecute() {
//            if(syncUI != null) {
//                syncUI.syncStarted();
//            }
//        }
//
//        @Override
//        protected Void doInBackground(Void... ignore) {
//            syncService.startSync(this);
//            return null;
//        }
//
//        @Override
//        protected void onProgressUpdate(Integer... values) {
//
//        }
//
//        @Override
//        protected void onPostExecute(Void ignore) {
//            if(syncUI != null) {
//                syncUI.syncFinished();
//            }
//        }
//    }
//
//    public interface SyncUI extends SyncProgressListener, IliasSyncListener {}
}

