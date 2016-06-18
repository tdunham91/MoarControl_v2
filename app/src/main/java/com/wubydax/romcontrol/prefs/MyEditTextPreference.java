package com.wubydax.romcontrol.prefs;

import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.widget.Button;
import android.widget.EditText;

import com.wubydax.romcontrol.R;

/*      Created by Roberto Mariani and Anna Berkovitch, 08/06/15
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
public class MyEditTextPreference extends EditTextPreference implements Preference.OnPreferenceChangeListener {
    private static final String LOG_TAG = MyEditTextPreference.class.getName();
    private ContentResolver mContentResolver;
    String mValue;
    public MyEditTextPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContentResolver = context.getContentResolver();
        setOnPreferenceChangeListener(this);
    }


    @Override
    protected void onSetInitialValue(boolean restoreValue, Object defaultValue) {
        super.onSetInitialValue(restoreValue, defaultValue);
        String value = "";
        Log.d(LOG_TAG, "onSetInitialValue is called");
        Log.d(LOG_TAG, "onSetInitialValue persisted string at the beginning is " + getPersistedString(null));
        Log.d(LOG_TAG, "onSetInitialValue restoreValue is " + restoreValue);
        if(!restoreValue && defaultValue != null) {
            Log.d(LOG_TAG, "onSetInitialValue restore value is false condition triggered and default value is not null");
           String dbValue = Settings.System.getString(mContentResolver, getKey());
           if(dbValue != null && !dbValue.equals(defaultValue)) {
               value = dbValue;
           } else if (dbValue == null) {
               value = (String) defaultValue;
               Settings.System.putString(mContentResolver, getKey(), (String) defaultValue);
           }
        } else {
            value = getPersistedString(null);
        }
        setSummary(value);
        mValue = value;

        Log.d(LOG_TAG, "onSetInitialValue persisted string at the end is " + getPersistedString(null));
    }

    @Override
    public String getText() {
        String value = Settings.System.getString(mContentResolver, getKey());
        String persistedString = getPersistedString(null);
        if(value.equals(persistedString)) {
            return persistedString;
        } else {
            persistString(value);
            return value;
        }
    }

    @Override
    protected void onAttachedToHierarchy(PreferenceManager preferenceManager) {
        super.onAttachedToHierarchy(preferenceManager);
        Log.d(LOG_TAG, "onAttachedToHierarchy is called");
        Log.d(LOG_TAG, "onAttachedToHierarchy persisted string is " + getPersistedString(null));
        String value = Settings.System.getString(mContentResolver, getKey());
        String persistedString = getPersistedString(null);
        if(value != null && !value.equals(persistedString)) {
            persistString(value);
            setSummary(value);
            mValue = value;
        }
    }

    @Override
    protected void showDialog(Bundle state) {
        super.showDialog(state);
        TypedValue typedValue = new TypedValue();
        Resources.Theme theme = getContext().getTheme();
        theme.resolveAttribute(R.attr.colorAccent, typedValue, true);
        AlertDialog dialog = (AlertDialog) getDialog();
        dialog.show();
        Button cancel = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);
        Button ok = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
        cancel.setTextColor(typedValue.data);
        ok.setTextColor(typedValue.data);
        dialog.getWindow().setBackgroundDrawableResource(R.drawable.dialog_bg);

    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        Settings.System.putString(mContentResolver, getKey(), (String) newValue);
        setSummary((String) newValue);
        return true;
    }
}
