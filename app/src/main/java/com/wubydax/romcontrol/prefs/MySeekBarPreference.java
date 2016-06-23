package com.wubydax.romcontrol.prefs;

import android.content.ContentResolver;
import android.content.Context;
import android.content.res.TypedArray;
import android.preference.Preference;
import android.provider.Settings;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.wubydax.romcontrol.R;
import com.wubydax.romcontrol.utils.Constants;
import com.wubydax.romcontrol.utils.Utils;

import java.util.Locale;

/**
 * Created by Anna Berkovitch on 20/06/2016.
 */
public class MySeekBarPreference extends Preference implements SeekBar.OnSeekBarChangeListener {
    private static final String LOG_TAG = MySeekBarPreference.class.getSimpleName();
    private final String mPackageToKill;
    private final boolean mIsSilent;
    private int mMinValue, mMaxValue, mDefaultValue;
    private String mUnitValue, mFormat = "%d%s";
    private TextView mValueText;
    private SeekBar mSeekBar;
    private ContentResolver mContentResolver;


    public MySeekBarPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        Log.d(LOG_TAG, "MySeekBarPreference constructor is called");
        mContentResolver = context.getContentResolver();
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.MySeekBarPreference);
        mMaxValue = typedArray.getInt(R.styleable.MySeekBarPreference_maxValue, 100);
        mMinValue = typedArray.getInt(R.styleable.MySeekBarPreference_minValue, 0);
        mPackageToKill = typedArray.getString(R.styleable.MyPreference_packageNameToKill);
        mIsSilent = typedArray.getBoolean(R.styleable.MyPreference_isSilent, true);
        mDefaultValue = mMaxValue/2;
        mUnitValue = typedArray.getString(R.styleable.MySeekBarPreference_unitsValue);
        if(mUnitValue == null) {
            mUnitValue = "";
        }
        typedArray.recycle();
        setWidgetLayoutResource(R.layout.seekbar_preference_layout);
    }

    @Override
    protected Object onGetDefaultValue(TypedArray a, int index) {
        Log.d(LOG_TAG, "onGetDefaultValue is called");
        int value = mDefaultValue;
        try {
            value = Settings.System.getInt(Constants.CONTEXT.getContentResolver(), getKey());
        } catch (Settings.SettingNotFoundException e) {
            value = a.getInt(index, value);
        }
        return value;
    }

    @Override
    protected void onSetInitialValue(boolean restorePersistedValue, Object defaultValue) {
        int value;
        try {
            value = Settings.System.getInt(mContentResolver, getKey());

        } catch (Settings.SettingNotFoundException e) {
            value = !restorePersistedValue && defaultValue != null ? (int) defaultValue : getPersistedInt(mDefaultValue);
        }
        persistInt(value);
    }

    @Override
    protected View onCreateView(ViewGroup parent) {
        Log.d(LOG_TAG, "onCreateView is called");
        LinearLayout view = (LinearLayout) super.onCreateView(parent);
        view.setOrientation(LinearLayout.VERTICAL);
        View widgetView = view.findViewById(android.R.id.widget_frame);

        widgetView.setPadding(0,0,0,0);
        return view;
    }

    @Override
    protected void onBindView(View view) {
        Log.d(LOG_TAG, "onBindView is called");
        mSeekBar = (SeekBar) view.findViewById(R.id.seekBarPrefSlider);
        mSeekBar.setMax(mMaxValue - mMinValue);
        TextView maxText = (TextView) view.findViewById(R.id.maxValueText);
        TextView minText = (TextView) view.findViewById(R.id.minValueText);
        mValueText = (TextView) view.findViewById(R.id.valueText);
        maxText.setText(String.format(Locale.getDefault(), mFormat, mMaxValue, mUnitValue));
        minText.setText(String.format(Locale.getDefault(), mFormat, mMinValue, mUnitValue));
        mValueText.setText(String.format(Locale.getDefault(), mFormat, getPersistedInt(mMaxValue/2), mUnitValue));
        mSeekBar.setOnSeekBarChangeListener(this);
        mSeekBar.setProgress(getPersistedInt(mDefaultValue) - mMinValue);
        super.onBindView(view);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        int updatedProgress = mMinValue + progress;
        mValueText.setText(String.format(Locale.getDefault(), mFormat, updatedProgress, mUnitValue));
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        persistInt(seekBar.getProgress() + mMinValue);
        onPreferenceChange(seekBar.getProgress() + mMinValue);
    }

    private void onPreferenceChange(int newValue) {
        Log.d(LOG_TAG, "onPreferenceChange is called " + newValue);
        Settings.System.putInt(mContentResolver, getKey(), newValue);
        if(mPackageToKill != null) {
            if (getContext().getPackageManager().getLaunchIntentForPackage(mPackageToKill) != null) {
                if (mIsSilent) {
                    Utils.killPackage(mPackageToKill);
                } else {
                    Utils.showKillPackageDialog(mPackageToKill, getContext());
                }
            }
        }
    }
}
