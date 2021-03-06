package com.wubydax.romcontrol.prefs;

import android.content.Context;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.preference.SwitchPreference;
import android.util.AttributeSet;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/*      Created by Roberto Mariani and Anna Berkovitch, 29/06/15
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
public class FilePreference extends SwitchPreference implements Preference.OnPreferenceChangeListener {
    File mFile;

    public FilePreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        setOnPreferenceChangeListener(this);
    }

    @Override
    protected void onAttachedToHierarchy(PreferenceManager preferenceManager) {
        super.onAttachedToHierarchy(preferenceManager);
        mFile = new File(getContext().getFilesDir() + File.separator + getKey());
        boolean isOn = mFile.exists();
        setChecked(isOn);
    }


    @Override
    protected boolean persistBoolean(boolean value) {
        return false;
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        boolean isChecked = (boolean) newValue;
        if(isChecked) {
            try {
                mFile.createNewFile();
                BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(mFile), 16 * 1024);
                bufferedOutputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            mFile.delete();
        }
        return true;
    }
}
