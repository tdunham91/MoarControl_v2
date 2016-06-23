package com.wubydax.romcontrol.prefs;

import android.content.ContentResolver;
import android.content.Context;
import android.content.res.TypedArray;
import android.preference.Preference;
import android.preference.SwitchPreference;
import android.provider.Settings;
import android.util.AttributeSet;

import com.wubydax.romcontrol.R;
import com.wubydax.romcontrol.utils.Utils;

/*      Created by Roberto Mariani and Anna Berkovitch, 13/06/2016
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

public class MySwitchPreference extends SwitchPreference implements Preference.OnPreferenceChangeListener {
    private ContentResolver mContentResolver;
    private String mPackageToKill;
    private boolean mIsSilent;

    public MySwitchPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.MyPreference);
        mPackageToKill = typedArray.getString(R.styleable.MyPreference_packageNameToKill);
        mIsSilent = typedArray.getBoolean(R.styleable.MyPreference_isSilent, true);
        typedArray.recycle();
        mContentResolver = context.getContentResolver();
        setOnPreferenceChangeListener(this);
    }

    @Override
    protected void onSetInitialValue(boolean restoreValue, Object defaultValue) {
        int dbInt = 0;
        try {
            dbInt = Settings.System.getInt(mContentResolver, getKey());
        } catch (Settings.SettingNotFoundException e) {
            if (defaultValue != null) {
                dbInt = (boolean) defaultValue ? 1 : 0;
                Settings.System.putInt(mContentResolver, getKey(), dbInt);
            }
        }
        persistBoolean(dbInt != 0);
        setChecked(dbInt != 0);

    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        boolean isTrue = (boolean) newValue;
        int dbInt = isTrue ? 1 : 0;
        Settings.System.putInt(mContentResolver, getKey(), dbInt);
        if(mPackageToKill != null) {
            if (getContext().getPackageManager().getLaunchIntentForPackage(mPackageToKill) != null) {
                if (mIsSilent) {
                    Utils.killPackage(mPackageToKill);
                } else {
                    Utils.showKillPackageDialog(mPackageToKill, getContext());
                }
            }
        }

        return true;
    }

}
