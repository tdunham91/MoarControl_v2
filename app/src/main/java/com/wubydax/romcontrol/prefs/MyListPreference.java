package com.wubydax.romcontrol.prefs;

import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.Button;
import android.widget.ListView;

import com.wubydax.romcontrol.R;

import java.util.Arrays;
import java.util.List;

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
public class MyListPreference extends ListPreference implements Preference.OnPreferenceChangeListener {
    private ContentResolver mContentResolver;
    private List<CharSequence> mEntries, mValues;


    public MyListPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContentResolver = context.getContentResolver();
        mEntries = Arrays.asList(getEntries());
        mValues = Arrays.asList(getEntryValues());
        setOnPreferenceChangeListener(this);
    }

    @Override
    protected void onAttachedToHierarchy(PreferenceManager preferenceManager) {
        super.onAttachedToHierarchy(preferenceManager);

    }

    @Override
    protected void onSetInitialValue(boolean restoreValue, Object defaultValue) {
        String dbValue = Settings.System.getString(mContentResolver, getKey());
        String value = "";
        if (!restoreValue) {
            if (dbValue != null) {
                value = dbValue;
                persistString(value);
            } else {
                if (defaultValue != null) {
                    value = (String) defaultValue;
                    Settings.System.putString(mContentResolver, getKey(), (String) defaultValue);
                }
            }
        } else {
            value = getPersistedString(null);
            if(dbValue != null && !dbValue.equals(value)) {
                persistString(dbValue);
                value = dbValue;
            }
        }

        int index = mValues.indexOf(value);
        if(index != -1) {
            setSummary(mEntries.get(index));
            setValue(value);
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
        cancel.setTextColor(typedValue.data);
        dialog.getWindow().setBackgroundDrawableResource(R.drawable.dialog_bg);
        ListView lv = dialog.getListView();
        int padding = Math.round(getContext().getResources().getDimension(R.dimen.dialog_listView_top_padding));
        lv.setPadding(0, padding, 0, 0);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        Settings.System.putString(mContentResolver, getKey(), (String) newValue);

        int index = mValues.indexOf(newValue);
        if(index != -1) {
            setSummary(mEntries.get(index));
        }
        return true;
    }
}
