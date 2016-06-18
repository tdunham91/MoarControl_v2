package com.wubydax.romcontrol;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceGroup;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import com.wubydax.romcontrol.prefs.OpenAppPreference;
import com.wubydax.romcontrol.prefs.UriSelectionPreference;
import com.wubydax.romcontrol.utils.Constants;
import com.wubydax.romcontrol.utils.Utils;


public class PrefsFragment extends PreferenceFragment implements Preference.OnPreferenceClickListener,
        SharedPreferences.OnSharedPreferenceChangeListener,
        UriSelectionPreference.OnUriSelectionRequestedListener{
    private static final String LOG_TAG = PrefsFragment.class.getSimpleName();
    private Context mContext;
    private SharedPreferences mSharedPreferences;
    private String  mUriPreferenceKey;

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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode != Activity.RESULT_OK) {
            return;
        }
        switch (requestCode) {
            case 46:
                Drawable drawable = Utils.getIconDrawable(data.getData());
                Settings.System.putString(mContext.getContentResolver(), mUriPreferenceKey, data.getData().toString());
                if(drawable != null) {
                    mSharedPreferences.edit().putString(mUriPreferenceKey, data.getData().toString()).apply();
                    findPreference(mUriPreferenceKey).setIcon(drawable);
                }
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);

        }

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
            } else if (preference instanceof UriSelectionPreference) {
                ((UriSelectionPreference) preference).setOnUriSelectionRequestedListener(this);
            }
        }
    }

    @Override
    public boolean onPreferenceClick(Preference preference) {
        if(preference.getKey() != null && preference.getKey().equals("open_reboot")) {
            Intent intent = new Intent();
            intent.setComponent(new ComponentName("com.wubydax.gearreboot", "com.wubydax.gearreboot.GearRebootDialogService"));
            if(getActivity().getPackageManager().resolveService(intent, 0) != null) {
                getActivity().startService(intent);
            } else {
                Toast.makeText(getActivity(), "Service not found", Toast.LENGTH_SHORT).show();
            }
        }
        return true;
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

    }

    @Override
    public void onUriSelectionRequested(String key) {
        mUriPreferenceKey = key;
        Intent getContentIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        getContentIntent.setType("image/*");
        startActivityForResult(getContentIntent, 46);
    }
}
