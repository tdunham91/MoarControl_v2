package com.wubydax.romcontrol.utils;

import android.content.Context;
import android.os.Environment;

import com.wubydax.romcontrol.MyApplication;

import java.io.File;

/*      Created by Roberto Mariani and Anna Berkovitch, 12/06/2016
        This program is free software: you can redistribute it and/or modify
        it under the terms of the GNU General Public License as published by
        the Free Software Foundation, either version 3 of the License, or
        (at your option) any later version.

        This program is distributed in the hope that it will be useful,
        but WITHOUT ANY WARRANTY; without even the implied warranty of
        MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
        GNU General Public License for more details.

        You should have received a copy of the GNU General Public License
        along with this program.  If not, see <http://www.gnu.org/licenses/>.*/
public class Constants {
    public static final Context CONTEXT = MyApplication.getContext();


    public static final int BACKUP_OR_RESTORE_DIALOG_REQUEST_CODE = 26;
    public static final int CHANGELOG_DIALOG_REQUEST_CODE = 25;
    public static final int NO_SU_DIALOG_REQUEST_CODE = 46;
    public static final int REBOOT_MENU_DIALOG_REQUEST_CODE = 58;
    public static final int RESTORE_FILE_SELECTOR_DIALOG_REQUEST_CODE = 65;
    public static final int THEME_DIALOG_REQUEST_CODE = 29;


    public static final String DIALOG_REQUEST_CODE_KEY = "dialog_request_code";
    public static final String PREF_NAME_KEY = "pref_key";
    public static final String SCRIPTS_FOLDER = "scripts";
    public static final String FILES_SCRIPTS_FOLDER_PATH = CONTEXT.getFilesDir().getPath() + File.separator + SCRIPTS_FOLDER;
    public static final String THEME_PREF_KEY = "theme_pref";
    public static final String SERVICE_INTENT_ACTION_BACKUP = "com.wubydax.action.BACKUP";
    public static final String SERVICE_INTENT_ACTION_RESTORE = "com.wubydax.action.RESTORE";
    public static final String BACKUP_FOLDER_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "RomControl" + File.separator + "Backups";
    public static final String SHARED_PREFS_FOLDER_PATH = CONTEXT.getFilesDir().getParent() + File.separator + "shared_prefs";
    public static final String BACKUP_FILE_PATH_EXTRA_KEY = "file_path";
    public static final String DIALOG_RESTORE_IS_CONFIRM_REQUIRED = "is_confirm";

    public static final String CURRENT_FRAGMENT = "last_fragment_used";
}

