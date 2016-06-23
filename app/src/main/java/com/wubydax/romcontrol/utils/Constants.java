package com.wubydax.romcontrol.utils;

import android.content.Context;

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
    public static final String PREF_NAME_KEY = "pref_key";
    public static final Context CONTEXT = MyApplication.getContext();
    public static final String DIALOG_REQUEST_CODE_KEY = "dialog_request_code";
    public static final int NO_SU_DIALOG_REQUEST_CODE = 46;
    public static final String SCRIPTS_FOLDER = "scripts";
    public static final String FILES_SCRIPTS_FOLDER_PATH = CONTEXT.getFilesDir().getPath() + File.separator + SCRIPTS_FOLDER;

    public static final int REBOOT_MENU_DIALOG_REQUEST_CODE = 58;
    public static final int THEME_DIALOG_REQUEST_CODE = 29;
    public static final String THEME_PREF_KEY = "theme_pref";
    public static final int CHANGELOG_DIALOG_REQUEST_CODE = 25;
}
