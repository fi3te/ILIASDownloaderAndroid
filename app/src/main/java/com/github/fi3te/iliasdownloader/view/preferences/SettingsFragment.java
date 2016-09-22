package com.github.fi3te.iliasdownloader.view.preferences;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.text.InputType;
import android.util.Log;
import android.view.View;

import com.afollestad.materialdialogs.MaterialDialog;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import de.whiledo.iliasdownloader2.service.IliasUtil;
import com.github.fi3te.iliasdownloader.R;
import com.github.fi3te.iliasdownloader.ilias.IliasPropertiesUtil;
import com.github.fi3te.iliasdownloader.model.Key;

/**
 * Created by wennier on 31.05.2015.
 */
public class SettingsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener, Preference.OnPreferenceClickListener {

    private Preference chooseIlias;
    private Preference iliasServerUrl;
    private Preference iliasClient;
    private Preference baseDir;
    private Preference maxSize;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // TODO change
        getPreferenceManager().setSharedPreferencesName("de.whiledo.iliasdownloaderandroid");
        getPreferenceManager().setSharedPreferencesMode(Context.MODE_PRIVATE);
        addPreferencesFromResource(R.xml.preferences);

        SharedPreferences preferences = getPreferenceManager().getSharedPreferences();

        chooseIlias = getPreferenceManager().findPreference(Key.CHOOSE_ILIAS);
        chooseIlias.setOnPreferenceClickListener(this);

        iliasServerUrl = getPreferenceManager().findPreference(Key.ILIAS_SERVER_URL);
        iliasServerUrl.setSummary(preferences.getString(iliasServerUrl.getKey(), IliasPropertiesUtil.DEFAULT_ILIAS_SERVER_URL));
        iliasServerUrl.setOnPreferenceClickListener(this);

        iliasClient = getPreferenceManager().findPreference(Key.ILIAS_CLIENT);
        iliasClient.setSummary(preferences.getString(iliasClient.getKey(), IliasPropertiesUtil.DEFAULT_ILIAS_CLIENT));
        iliasClient.setOnPreferenceClickListener(this);

        baseDir = getPreferenceManager().findPreference(Key.BASE_DIR);
        baseDir.setSummary(preferences.getString(baseDir.getKey(), IliasPropertiesUtil.DEFAULT_BASE_DIR));
        baseDir.setOnPreferenceClickListener(this);

        maxSize = getPreferenceManager().findPreference(Key.MAX_SIZE);
        maxSize.setSummary(preferences.getLong(maxSize.getKey(), IliasPropertiesUtil.DEFAULT_MAX_SIZE) + " MiB");
        maxSize.setOnPreferenceClickListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {
        getPreferenceManager().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
        super.onPause();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        switch (key) {
            case Key.ILIAS_SERVER_URL:
                iliasServerUrl.setSummary(sharedPreferences.getString(iliasServerUrl.getKey(), IliasPropertiesUtil.DEFAULT_ILIAS_SERVER_URL));
                break;
            case Key.ILIAS_CLIENT:
                iliasClient.setSummary(sharedPreferences.getString(iliasClient.getKey(), IliasPropertiesUtil.DEFAULT_ILIAS_CLIENT));
                break;
            case Key.BASE_DIR:
                baseDir.setSummary(sharedPreferences.getString(baseDir.getKey(), IliasPropertiesUtil.DEFAULT_BASE_DIR));
                break;
            case Key.MAX_SIZE:
                maxSize.setSummary(String.valueOf(sharedPreferences.getLong(maxSize.getKey(), IliasPropertiesUtil.DEFAULT_MAX_SIZE)) + " MiB");
                break;
        }
    }

    @Override
    public boolean onPreferenceClick(Preference preference) {
        if (preference.getKey().equals(Key.CHOOSE_ILIAS)) {
            chooseIlias();
            return true;
        } else if (preference.getKey().equals(Key.ILIAS_SERVER_URL)) {
            changeIliasServerURL();
            return true;
        } else if (preference.getKey().equals(Key.ILIAS_CLIENT)) {
            changeIliasClient();
        } else if (preference.getKey().equals(Key.BASE_DIR)) {
            changeBaseDir();
        } else if (preference.getKey().equals(Key.MAX_SIZE)) {
            changeMaxSize();
        }
        return false;
    }

    public void chooseIlias() {
        SharedPreferences preferences = getPreferenceManager().getSharedPreferences();
        chooseIlias(preferences, getActivity());
    }

    public static void chooseIlias(final SharedPreferences preferences, Activity activity) {
        List<String> itemList = new ArrayList<>();
        final List<String> clientIDs = new ArrayList<>();
        final List<String> serverURLs = new ArrayList<>();

        BufferedReader bis = null;
        try {
            InputStream is = activity.getAssets().open("ilias.csv");
            bis = new BufferedReader(new InputStreamReader(is));

            String line;
            while ((line = bis.readLine()) != null) {
                String[] configuration = line.split(";");
                if (configuration.length == 3) {
                    itemList.add(configuration[0]);
                    clientIDs.add(configuration[1]);
                    serverURLs.add(configuration[2]);
                }
            }
        } catch (IOException e) {
            Log.e("chooseIlias", "IOException");
        } finally {
            if (bis != null) {
                try {
                    bis.close();
                } catch (IOException e) {
                    // schade Banane
                }
            }
        }

        CharSequence[] items = new CharSequence[itemList.size()];
        for (int i = 0; i < itemList.size(); i++) {
            items[i] = itemList.get(i);
        }

        new MaterialDialog.Builder(activity)
                .title(R.string.choose_ilias)
                .items(items)
                .itemsCallback(new MaterialDialog.ListCallback() {
                    @Override
                    public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putString(Key.ILIAS_SERVER_URL, serverURLs.get(which));
                        editor.putString(Key.ILIAS_CLIENT, clientIDs.get(which));
                        editor.commit();
                    }
                })
                .show();
    }

    public void changeIliasServerURL() {
        final SharedPreferences preferences = getPreferenceManager().getSharedPreferences();

        new MaterialDialog.Builder(getActivity())
                .title(R.string.ilias_server_url)
                .content(R.string.please_enter_the_ilias_soap_webservice_url_or_the_ilias_login_page_url)
                .inputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_URI)
                .input(IliasPropertiesUtil.DEFAULT_ILIAS_SERVER_URL, preferences.getString(Key.ILIAS_SERVER_URL, IliasPropertiesUtil.DEFAULT_ILIAS_SERVER_URL), new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(MaterialDialog dialog, CharSequence input) {
                        String url = input.toString();
                        if (!url.isEmpty()) {
                            SharedPreferences.Editor editor = preferences.edit();
                            url = IliasUtil.findSOAPWebserviceByLoginPage(url);
                            editor.putString(Key.ILIAS_SERVER_URL, url);
                            editor.commit();
                        }
                    }
                }).show();
    }

    public void changeIliasClient() {
        final SharedPreferences preferences = getPreferenceManager().getSharedPreferences();

        new MaterialDialog.Builder(getActivity())
                .title(R.string.ilias_client)
                .content(R.string.the_client_id_can_be_read_from_the_url_of_the_ilias_login_page)
                .inputType(InputType.TYPE_CLASS_TEXT)
                .input(IliasPropertiesUtil.DEFAULT_ILIAS_CLIENT, preferences.getString(Key.ILIAS_CLIENT, IliasPropertiesUtil.DEFAULT_ILIAS_CLIENT), new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(MaterialDialog dialog, CharSequence input) {
                        if (!input.toString().isEmpty()) {
                            SharedPreferences.Editor editor = preferences.edit();
                            editor.putString(Key.ILIAS_CLIENT, input.toString());
                            editor.commit();
                        }
                    }
                }).show();
    }

    public void changeBaseDir() {
        final SharedPreferences preferences = getPreferenceManager().getSharedPreferences();

        new MaterialDialog.Builder(getActivity())
                .title(R.string.base_directory)
                .content(getResources().getString(R.string.e_g_) + " " + IliasPropertiesUtil.DEFAULT_BASE_DIR)
                .inputType(InputType.TYPE_CLASS_TEXT)
                .input(IliasPropertiesUtil.DEFAULT_BASE_DIR, preferences.getString(Key.BASE_DIR, IliasPropertiesUtil.DEFAULT_BASE_DIR), new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(MaterialDialog dialog, CharSequence input) {
                        String dir = input.toString();
                        if (!dir.isEmpty()) {
                            SharedPreferences.Editor editor = preferences.edit();
                            if (!dir.endsWith("/"))
                                dir += "/";
                            editor.putString(Key.BASE_DIR, dir);
                            editor.commit();
                        }
                    }
                }).show();
    }

    public void changeMaxSize() {
        final SharedPreferences preferences = getPreferenceManager().getSharedPreferences();

        new MaterialDialog.Builder(getActivity())
                .title(R.string.max_file_size)
                .content("max. " + IliasPropertiesUtil.MAX_MAX_SIZE + "MiB")
                .inputType(InputType.TYPE_CLASS_NUMBER)
                .input(String.valueOf(IliasPropertiesUtil.DEFAULT_MAX_SIZE), String.valueOf(preferences.getLong(Key.MAX_SIZE, IliasPropertiesUtil.DEFAULT_MAX_SIZE)), new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(MaterialDialog dialog, CharSequence input) {
                        Long size = null;
                        try {
                            size = Long.valueOf(input.toString());
                        } catch (NumberFormatException nfe) {
                            // not possible
                        }
                        if (size != null && size <= IliasPropertiesUtil.MAX_MAX_SIZE) {
                            SharedPreferences.Editor editor = preferences.edit();
                            editor.putLong(Key.MAX_SIZE, size);
                            editor.commit();
                        }
                    }
                }).show();
    }
}
