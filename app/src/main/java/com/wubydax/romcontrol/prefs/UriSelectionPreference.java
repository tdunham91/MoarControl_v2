package com.wubydax.romcontrol.prefs;

import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.util.AttributeSet;

import com.wubydax.romcontrol.R;
import com.wubydax.romcontrol.utils.Utils;

import java.io.FileNotFoundException;
import java.io.InputStream;

/**
 * Created by Anna Berkovitch on 14/06/2016.
 */
public class UriSelectionPreference extends Preference {
    private  Context mContext;
    private OnUriSelectionRequestedListener mOnUriSelectionRequestedListener;
    private ContentResolver mContentResolver;


    public UriSelectionPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        mContentResolver = mContext.getContentResolver();
    }

    public OnUriSelectionRequestedListener getOnUriSelectionRequestedListener() {
        return mOnUriSelectionRequestedListener;
    }

    public void setOnUriSelectionRequestedListener(OnUriSelectionRequestedListener onUriSelectionRequestedListener) {
        mOnUriSelectionRequestedListener = onUriSelectionRequestedListener;   }

    @Override
    protected void onAttachedToHierarchy(PreferenceManager preferenceManager) {
        super.onAttachedToHierarchy(preferenceManager);
        String uriString = Settings.System.getString(mContentResolver, getKey());
        if(uriString != null) {
            persistString(uriString);
            attemptToSetIcon(uriString);
        }
    }



    public void attemptToSetIcon(String uriString) {
        Uri uri = Uri.parse(uriString);
        if(uri != null) {
            SetImage setImage = new SetImage();
            setImage.execute(uri);
        }
    }

    @Override
    protected void onClick() {
        if(mOnUriSelectionRequestedListener != null) {
            mOnUriSelectionRequestedListener.onUriSelectionRequested(getKey());
        }

    }

    public interface OnUriSelectionRequestedListener {
        void onUriSelectionRequested(String key);
    }

    public class SetImage extends AsyncTask<Uri, Void, Drawable> {



        @Override
        protected Drawable doInBackground(Uri... params) {
            return Utils.getIconDrawable(params[0]);
        }

        @Override
        protected void onPostExecute(Drawable drawable) {
            setIcon(drawable);
        }


    }

}
