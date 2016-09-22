package com.github.fi3te.iliasdownloader.view.fragment.task;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

import com.afollestad.materialdialogs.MaterialDialog;

import com.github.fi3te.iliasdownloader.R;
import com.github.fi3te.iliasdownloader.ilias.IliasController;

/**
 * Created by wennier on 24.06.2015.
 */
public class LoginDialogFragment extends DialogFragment {

    public static final String USERNAME = "username";
    private String username;
    public static final String PASSWORD = "password";
    private String password;

    private LoginListener loginListener;
    private LoginTask task;

    private IliasController ilias;

    public LoginDialogFragment() {
        ilias = IliasController.getInstance(null); // funktioniert, da die Instanz schon existiert
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRetainInstance(true);

        Bundle bundle = getArguments();
        username = bundle.getString(USERNAME);
        password = bundle.getString(PASSWORD);

        if (username != null && password != null) {
            task = new LoginTask();
            task.execute();
        }
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
                .title(R.string.login)
                .content(R.string.please_wait)
                .progress(true, 0)
                .build();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        loginListener = (LoginListener) activity;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        loginListener = null;
    }

    private class LoginTask extends AsyncTask<Void, Void, Boolean> {
        @Override
        public Boolean doInBackground(Void... ignore) {
            return ilias.login(username, password);
        }

        @Override
        public void onPostExecute(Boolean successful) {
            if (loginListener != null) {
                loginListener.loggedIn(successful);
            }
        }
    }

    public interface LoginListener {
        void loggedIn(boolean successful);
    }
}