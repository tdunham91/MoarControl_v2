package com.wubydax.romcontrol;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceGroup;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.util.Log;

import com.wubydax.romcontrol.prefs.OpenAppPreference;
import com.wubydax.romcontrol.utils.Constants;


public class PrefsFragment extends PreferenceFragment implements Preference.OnPreferenceClickListener, SharedPreferences.OnSharedPreferenceChangeListener {
    private static final String LOG_TAG = PrefsFragment.class.getSimpleName();
    private Context mContext;
    private SharedPreferences mSharedPreferences;

    public PrefsFragment() {
        //empty public constructor
    }

    public static PrefsFragment newInstance(String prefName) {
        PrefsFragment prefsFragment = new PrefsFragment();
        Bundle args = new Bundle();
        args.putString(Constants.PREF_NAME_KEY, prefName);
        prefsFragment.setArguments(args);
        return prefsFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = Constants.CONTEXT;
        String prefName = getArguments().getString(Constants.PREF_NAME_KEY);
        int prefId = mContext.getResources().getIdentifier(prefName, "xml", mContext.getPackageName());
        if(prefId != 0) {
            getPreferenceManager().setSharedPreferencesName(prefName);
            addPreferencesFromResource(prefId);
            mSharedPreferences = getPreferenceManager().getSharedPreferences();
            iteratePrefs(getPreferenceScreen());
        }
    }

    @Override
    public void onResume() {
        mSharedPreferences.registerOnSharedPreferenceChangeListener(this);
        super.onResume();
    }

    @Override
    public void onPause() {
        mSharedPreferences.unregisterOnSharedPreferenceChangeListener(this);
        super.onPause();
    }

    private void iteratePrefs(PreferenceGroup preferenceGroup) {
        for (int i = 0; i < preferenceGroup.getPreferenceCount(); i++) {
            Preference preference = preferenceGroup.getPreference(i);
            Log.d(LOG_TAG, "iteratePrefs " + preference.getClass().getSimpleName());
            if(preference instanceof PreferenceGroup) {
                if(preference instanceof PreferenceScreen) {
                    preference.setOnPreferenceClickListener(this);
                }
                if(((PreferenceGroup) preference).getPreferenceCount() > 0) {
                    iteratePrefs((PreferenceGroup) preference);
                }
            } else if (preference instanceof OpenAppPreference) {
                if(!((OpenAppPreference) preference).isInstalled()) {
                    preferenceGroup.removePreference(preference);
                }
            }
        }
    }

    @Override
    public boolean onPreferenceClick(Preference preference) {
        return false;
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

    }
}
