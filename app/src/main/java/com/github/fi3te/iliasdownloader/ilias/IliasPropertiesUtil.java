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

package com.github.fi3te.iliasdownloader.ilias;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;

import java.util.HashSet;
import java.util.Set;

import de.whiledo.iliasdownloader2.service.IliasUtil;
import de.whiledo.iliasdownloader2.syncrunner.service.IliasProperties;
import com.github.fi3te.iliasdownloader.controller.DatabaseController;
import com.github.fi3te.iliasdownloader.model.Key;

/**
 * Created by wennier on 31.05.2015.
 */
public class IliasPropertiesUtil {

    public static final String DEFAULT_ILIAS_SERVER_URL = IliasUtil.findSOAPWebserviceByLoginPage("https://www.ilias.fh-dortmund.de/ilias/login.php?target=&soap_pw=&ext_uid=&cookies=nocookies&client_id=ilias-fhdo&lang=de");
    public static final String DEFAULT_ILIAS_CLIENT = "ilias-fhdo";
    public static final String DEFAULT_USER_LOGIN = "";
    public static final String DEFAULT_BASE_DIR = Environment.getExternalStorageDirectory().getPath() + "/ilias/";
    public static final boolean DEFAULT_ALLOW_DOWNLOAD = true;
    public static final long DEFAULT_MAX_SIZE = 20;
    public static final long MAX_MAX_SIZE = 200;

    public static final boolean DEFAULT_SYNC_ALL = true;
    public static final Set<String> DEFAULT_ACTIVE_COURSES = new HashSet<>();

    public static IliasProperties readIliasProperties(Context context) {
        // TODO change
        SharedPreferences preferences = context.getSharedPreferences("de.whiledo.iliasdownloaderandroid", Context.MODE_PRIVATE);
        IliasProperties iliasProperties = new IliasProperties();

        iliasProperties.setIliasServerURL(preferences.getString(Key.ILIAS_SERVER_URL, DEFAULT_ILIAS_SERVER_URL));
        iliasProperties.setIliasClient(preferences.getString(Key.ILIAS_CLIENT, DEFAULT_ILIAS_CLIENT));
        iliasProperties.setUserName(preferences.getString(Key.USER_LOGIN, DEFAULT_USER_LOGIN));
        iliasProperties.setBaseDirectory(preferences.getString(Key.BASE_DIR, DEFAULT_BASE_DIR));
        iliasProperties.setAllowDownload(preferences.getBoolean(Key.ALLOW_DOWNLOAD, DEFAULT_ALLOW_DOWNLOAD));
        iliasProperties.setMaxFileSize(preferences.getLong(Key.MAX_SIZE, DEFAULT_MAX_SIZE) * 1024 * 1024);

        Set<String> courses = preferences.getStringSet(Key.ACTIVE_COURSES, DEFAULT_ACTIVE_COURSES);
        Set<Long> courseIds = new HashSet<>();
        for (String course : courses) {
            courseIds.add(Long.parseLong(course));
        }
        iliasProperties.setActiveCourses(courseIds);
        iliasProperties.setSyncAll(preferences.getBoolean(Key.SYNC_ALL, DEFAULT_SYNC_ALL));

        DatabaseController dController = DatabaseController.getInstance(context);
        iliasProperties.setBlockedFiles(dController.getAllBlockedFileRefIds());

        iliasProperties.setUseThreads(false);

//        iliasProperties.getActiveCourses().add(338991L);

        return iliasProperties;
    }

    public static void putString(Context context, String key, String value) {
        // TODO change
        SharedPreferences preferences = context.getSharedPreferences("de.whiledo.iliasdownloaderandroid", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public static void setSyncAll(Context context, boolean syncAll) {
        // TODO change
        SharedPreferences preferences = context.getSharedPreferences("de.whiledo.iliasdownloaderandroid", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(Key.SYNC_ALL, syncAll);
        editor.commit();
    }

    public static void setActiveCourses(Context context, Set<String> activeCourses) {
        if (activeCourses != null) {
            // TODO change
            SharedPreferences preferences = context.getSharedPreferences("de.whiledo.iliasdownloaderandroid", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putStringSet(Key.ACTIVE_COURSES, activeCourses);
            editor.commit();
        }
    }

//    public static void saveIliasProperties(Context context, IliasProperties properties) {
//        // TODO change
//        SharedPreferences preferences = context.getSharedPreferences("de.whiledo.iliasdownloaderandroid", Context.MODE_PRIVATE);
//
//        SharedPreferences.Editor editor = preferences.edit();
//
//        editor.putString(Key.ILIAS_SERVER_URL, properties.getIliasServerURL());
//        editor.putString(Key.ILIAS_CLIENT, properties.getIliasClient());
//        editor.putString(Key.USER_LOGIN, properties.getUserName());
//        editor.putString(Key.BASE_DIR, properties.getBaseDirectory());
//        editor.putLong(Key.MAX_SIZE, properties.getMaxFileSize());
//
//        editor.commit();
//    }
}
