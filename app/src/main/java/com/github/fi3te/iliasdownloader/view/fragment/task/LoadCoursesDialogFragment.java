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
