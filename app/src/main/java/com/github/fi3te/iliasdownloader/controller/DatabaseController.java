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

package com.github.fi3te.iliasdownloader.controller;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.github.fi3te.iliasdownloader.model.database.BlockedFile;

/**
 * Created by wennier on 01.07.2015.
 */
public class DatabaseController extends SQLiteOpenHelper {

    private static DatabaseController instance;

    private static final int DATABASE_VERSION = 2;

    // Datenbank
    private static final String DATABASE_NAME = "ilias";

    // Tabellen
    private static final String LAST_DOWNLOADS = "last_downloads", BLOCKED_FILES = "blocked_files";

    // Spalten
    private static final String ID = "id", PATH = "path",
            REF_ID = "ref_id", FILENAME = "filename", FILE_SIZE = "file_size";

    private DatabaseController(Context context) {
        super(context.getApplicationContext(), DATABASE_NAME, null, DATABASE_VERSION);
    }

    public static DatabaseController getInstance(Context context) {
        if (instance == null) {
            instance = new DatabaseController(context);
        }
        return instance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.v("DatabaseController", "onCreate");

        String CREATE_TABLE_LAST_DOWNLOADS = "CREATE TABLE IF NOT EXISTS " + LAST_DOWNLOADS + "(" + ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + PATH + " TEXT NOT NULL)";
        db.execSQL(CREATE_TABLE_LAST_DOWNLOADS);

        String CREATE_TABLE_BLOCKED_FILES = "CREATE TABLE IF NOT EXISTS " + BLOCKED_FILES + "(" + ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + REF_ID + " INTEGER NOT NULL, " + FILENAME + " TEXT NOT NULL, "
                + FILE_SIZE + " INTEGER NOT NULL)";
        db.execSQL(CREATE_TABLE_BLOCKED_FILES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Daten retten/sichern

        Log.v("DatabaseController", "onUpgrade");

        db.execSQL("DROP TABLE IF EXISTS " + LAST_DOWNLOADS);
        db.execSQL("DROP TABLE IF EXISTS " + BLOCKED_FILES);
        onCreate(db);
    }

    public boolean insertDownload(String path) {
        if (path != null && !path.isEmpty()) {
            SQLiteDatabase db = this.getWritableDatabase();

            ContentValues values = new ContentValues();
            values.put(PATH, path);

            db.insert(LAST_DOWNLOADS, null, values);
            db.close();
            return true;
        } else {
            return false;
        }
    }

    public void setAllDownloads(List<String> paths) {
        if (paths != null && paths.size() >= 0) {
            SQLiteDatabase db = this.getWritableDatabase();

            db.beginTransaction();
            db.execSQL("DELETE FROM " + LAST_DOWNLOADS);

            for (String path : paths) {
                ContentValues values = new ContentValues();
                values.put(PATH, path);

                db.insert(LAST_DOWNLOADS, null, values);
            }

            db.setTransactionSuccessful();
            db.endTransaction();

            db.close();
        }
    }

    public List<String> getAllDownloads() {
        List<String> lastDownloads = new ArrayList<String>(0);

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(LAST_DOWNLOADS, null, null, null, null, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                lastDownloads.add(cursor.getString(1));
            } while (cursor.moveToNext());
        }

        if (cursor != null)
            cursor.close();

        db.close();

        return lastDownloads;
    }

    public boolean insertBlockedFile(long refId, String filename, long fileSize) {
        if (filename != null && !filename.isEmpty() && fileSize > 0) {
            SQLiteDatabase db = this.getWritableDatabase();

            ContentValues values = new ContentValues();
            values.put(REF_ID, refId);
            values.put(FILENAME, filename);
            values.put(FILE_SIZE, fileSize);

            db.insert(BLOCKED_FILES, null, values);
            db.close();
            return true;
        } else {
            return false;
        }
    }

    // TODO Frage an Kevin: sind die refIds tatsaechlich eindeutig?
    public boolean deleteBlockedFileByRefId(long refId) {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            return db.delete(BLOCKED_FILES, REF_ID + "=" + refId, null) > 0;
        } finally {
            db.close();
        }
    }

    public List<BlockedFile> getAllBlockedFiles() {
        List<BlockedFile> blockedFiles = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(BLOCKED_FILES, null, null, null, null, null, FILENAME, null);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                blockedFiles.add(new BlockedFile(cursor.getLong(1), cursor.getString(2), cursor.getLong(3)));
            } while (cursor.moveToNext());
        }

        if (cursor != null)
            cursor.close();

        db.close();

        return blockedFiles;
    }

    public Set<Long> getAllBlockedFileRefIds() {
        Set<Long> blockedFiles = new HashSet<>();

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(BLOCKED_FILES, null, null, null, null, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                blockedFiles.add(cursor.getLong(1));
            } while (cursor.moveToNext());
        }

        if (cursor != null)
            cursor.close();

        db.close();

        return blockedFiles;
    }

}
