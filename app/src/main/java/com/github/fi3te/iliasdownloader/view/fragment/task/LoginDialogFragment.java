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