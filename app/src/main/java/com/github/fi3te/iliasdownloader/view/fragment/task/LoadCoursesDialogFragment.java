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

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

import com.afollestad.materialdialogs.MaterialDialog;

import java.util.List;

import de.whiledo.iliasdownloader2.util.TwoObjectsX;
import com.github.fi3te.iliasdownloader.R;
import com.github.fi3te.iliasdownloader.ilias.IliasController;

/**
 * Created by wennier on 16.06.2015.
 */
public class LoadCoursesDialogFragment extends DialogFragment {

    private LoadCourses loadCourses;
    private LoadCoursesTask task;

    private IliasController ilias;

    public LoadCoursesDialogFragment() {
        ilias = IliasController.getInstance(null); // funktioniert, da die Instanz schon existiert
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRetainInstance(true);

        task = new LoadCoursesTask();
        task.execute();
    }

    @Override
    public void onDestroyView() {
        if (getDialog() != null && getRetainInstance())
            getDialog().setDismissMessage(null);
        super.onDestroyView();
    }

    @Override
    public MaterialDialog onCreateDialog(Bundle savedInstanceState) {
        setCancelable(false);
        return new MaterialDialog.Builder(getActivity())
                .title(R.string.choose_courses)
                .content(R.string.please_wait)
                .progress(true, 0)
                .build();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        loadCourses = (LoadCourses) activity;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        loadCourses = null;
    }

    private class LoadCoursesTask extends AsyncTask<Void, Void, TwoObjectsX<List<Long>, List<String>>> {

        @Override
        public TwoObjectsX<List<Long>, List<String>> doInBackground(Void... ignore) {
            List<Long> courses = ilias.getCourseIds();
            List<String> courseNames = ilias.getCourseNames(courses);
            return new TwoObjectsX<>(courses, courseNames);
        }

        @Override
        protected void onPostExecute(TwoObjectsX<List<Long>, List<String>> coursesWithNames) {
            if(loadCourses != null)
                loadCourses.coursesLoaded(coursesWithNames);
        }
    }

    public interface LoadCourses {
        void coursesLoaded(TwoObjectsX<List<Long>, List<String>> coursesWithNames);
    }
}
