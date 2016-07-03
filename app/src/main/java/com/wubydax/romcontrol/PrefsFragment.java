package com.wubydax.romcontrol;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceGroup;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.wubydax.romcontrol.prefs.OpenAppPreference;
import com.wubydax.romcontrol.prefs.UriSelectionPreference;
import com.wubydax.romcontrol.utils.Constants;


@SuppressWarnings("WeakerAccess")
public class PrefsFragment extends PreferenceFragment implements Preference.OnPreferenceClickListener,
        UriSelectionPreference.OnUriSelectionRequestedListener {
    private static final String LOG_TAG = PrefsFragment.class.getSimpleName();
    private Context mContext;
    private SharedPreferences mSharedPreferences;
    private String mUriPreferenceKey;

    public PrefsFragment() {
        //empty public constructor
    }

    static PrefsFragment newInstance(String prefName) {
        PrefsFragment prefsFragment = new PrefsFragment();
        Bundle args = new Bundle();
        args.putString(Constants.PREF_NAME_KEY, prefName);
        prefsFragment.setArguments(args);
        return prefsFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = MyApp.getContext();
        String prefName = getArguments().getString(Constants.PREF_NAME_KEY);
        int prefId = mContext.getResources().getIdentifier(prefName, "xml", mContext.getPackageName());
        if (prefId != 0) {
            getPreferenceManager().setSharedPreferencesName(prefName);
            addPreferencesFromResource(prefId);
            mSharedPreferences = getPreferenceManager().getSharedPreferences();
            PreferenceManager.getDefaultSharedPreferences(MyApp.getContext()).edit().putString(Constants.LAST_FRAGMENT, prefName).apply();
            iteratePrefs(getPreferenceScreen());
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        switch (requestCode) {
            case 46:
                Settings.System.putString(mContext.getContentResolver(), mUriPreferenceKey, data.getData().toString());
                mSharedPreferences.edit().putString(mUriPreferenceKey, data.getData().toString()).apply();
                ((UriSelectionPreference) findPreference(mUriPreferenceKey)).attemptToSetIcon(data.getData().toString());
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);

        }

    }

    private void iteratePrefs(PreferenceGroup preferenceGroup) {
        for (int i = 0; i < preferenceGroup.getPreferenceCount(); i++) {
            Preference preference = preferenceGroup.getPreference(i);
            Log.d(LOG_TAG, "iteratePrefs " + preference.getClass().getSimpleName());
            if (preference instanceof PreferenceGroup) {
                if (preference instanceof PreferenceScreen) {
                    preference.setOnPreferenceClickListener(this);
                }
                if (((PreferenceGroup) preference).getPreferenceCount() > 0) {
                    iteratePrefs((PreferenceGroup) preference);
                }
            } else if (preference instanceof OpenAppPreference) {
                if (!((OpenAppPreference) preference).isInstalled()) {
                    preferenceGroup.removePreference(preference);
                }
            } else if (preference instanceof UriSelectionPreference) {
                ((UriSelectionPreference) preference).setOnUriSelectionRequestedListener(this);
            }
        }
    }

    @Override
    public boolean onPreferenceClick(Preference preference) {
        if (((PreferenceScreen) preference).getPreferenceCount() > 0) {
            setUpNestedPreferenceLayout((PreferenceScreen) preference);
        }
        return true;
    }

    private void setUpNestedPreferenceLayout(PreferenceScreen preference) {
        final Dialog dialog = preference.getDialog();
        if (dialog != null) {
            LinearLayout rootView = (LinearLayout) dialog.findViewById(android.R.id.list).getParent();
            View decorView = dialog.getWindow().getDecorView();
            if (decorView != null && rootView != null) {
                Toolbar toolbar = (Toolbar) LayoutInflater.from(getActivity()).inflate(R.layout.nested_preference_toolbar_layout, rootView, false);
                toolbar.setTitle(preference.getTitle());
                decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
                rootView.addView(toolbar, 0);
                toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
            }
        }
    }


    @Override
    public void onUriSelectionRequested(String key) {
        mUriPreferenceKey = key;
        Intent getContentIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        getContentIntent.setType("image/*");
        startActivityForResult(getContentIntent, 46);
    }


}
