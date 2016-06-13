package com.wubydax.romcontrol.prefs;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.DialogPreference;
import android.preference.Preference;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.wubydax.romcontrol.R;
import com.wubydax.romcontrol.utils.Constants;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

/*      Created by Roberto Mariani and Anna Berkovitch, 27/06/15
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
@SuppressWarnings({"deprecation", "unused"})
public class IntentDialogPreference extends DialogPreference implements AdapterView.OnItemClickListener {
    boolean mIsSearch;
    private String mSummary;
    private Context mContext;
    private ListView mListView;
    private ProgressBar mProgressBar;
    private AppListAdapter mAppListAdapter;
    private AsyncTask<Void, Void, Void> mAsyncTask;
    private String mSeparator;
    private PackageManager mPackageManager;

    public IntentDialogPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        mPackageManager = context.getPackageManager();
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.IntentDialogPreference);
        mSeparator = typedArray.getString(R.styleable.IntentDialogPreference_intentSeparator);
        mSeparator = mSeparator != null ? mSeparator : "##";
        mIsSearch = typedArray.getBoolean(R.styleable.IntentDialogPreference_showSearch, true);
        typedArray.recycle();

        setDialogLayoutResource(R.layout.intent_dialog_layout);
        setWidgetLayoutResource(R.layout.intent_preference_app_icon);
    }

    private String getStringForAttr(AttributeSet attrs, String ns, String attrName, String defaultValue) {
        String value = attrs.getAttributeValue(ns, attrName);
        if (value == null)
            value = defaultValue;
        return value;
    }

    @Override
    protected void onBindView(View view) {
        super.onBindView(view);
        getAppIcon();
        ImageView prefAppIcon = (ImageView) view.findViewById(R.id.iconForApp);
        prefAppIcon.setImageDrawable(getAppIcon());
    }

    @Override
    protected void onBindDialogView(View view) {
        super.onBindDialogView(view);
        mListView = (ListView) view.findViewById(R.id.appsList);
        mListView.setOnItemClickListener(this);
        mListView.setFastScrollEnabled(true);
        mListView.setFadingEdgeLength(1);
        mListView.setDivider(null);
        mListView.setDividerHeight(0);
        mListView.setScrollingCacheEnabled(false);
        mProgressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        if (mIsSearch) {
            EditText search = (EditText) view.findViewById(R.id.searchApp);
            search.setVisibility(View.VISIBLE);
            createList();
            search.addTextChangedListener(new TextWatcher() {

                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    mAppListAdapter.getFilter().filter(s);
                }

                @Override
                public void afterTextChanged(Editable s) {
                }
            });
        } else {
            createList();
        }


    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        if (mAsyncTask != null && mAsyncTask.getStatus() == AsyncTask.Status.RUNNING) {
            mAsyncTask.cancel(true);
            mAsyncTask = null;
        }
    }

    @Override
    protected Object onGetDefaultValue(TypedArray a, int index) {
        return a.getString(index);
    }

    @Override
    protected void onSetInitialValue(boolean restoreValue, Object defaultValue) {
        String value = Settings.System.getString(getContext().getContentResolver(), getKey());
        String appName = null;
        if (value != null) {
            appName = getAppName(value);
        }
        setSummary(appName == null ? mSummary : appName);
        persistString(value);

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
        ok.setVisibility(View.GONE);
        cancel.setTextColor(typedValue.data);
        dialog.getWindow().setBackgroundDrawableResource(R.drawable.dialog_bg);
    }

    @Override
    public void onParentChanged(Preference parent, boolean disableChild) {
        super.onParentChanged(parent, disableChild);
    }

    public void setDefaultSummary(String summary) {
        mSummary = summary;
    }

    private String getAppName(String value) {
        String appName = null;
        if (value != null) {
            String[] split = value.split(mSeparator);
            String pkgName = split[0];
            String activity = split[1];
            Intent intent = new Intent();
            intent.setComponent(new ComponentName(pkgName, activity));
            ResolveInfo resolveInfo = mPackageManager.resolveActivity(intent, 0);
            if (resolveInfo != null) {
                appName = resolveInfo.activityInfo.loadLabel(mPackageManager).toString();
            }

        }

        return appName;
    }


    public Drawable getAppIcon() {
        Drawable appIcon = mContext.getResources().getDrawable(R.mipmap.ic_launcher);
        String intentString = getPersistedString(null);
        if (intentString != null) {
            String[] splitValue = intentString.split(mSeparator);
            String pkg = splitValue[0];
            String activity = splitValue[1];
            Intent intent = new Intent();
            intent.setComponent(new ComponentName(pkg, activity));
            ResolveInfo resolveInfo = mPackageManager.resolveActivity(intent, 0);
            if (resolveInfo != null) {
                appIcon = resolveInfo.activityInfo.loadIcon(mPackageManager);
            }
        }
        return appIcon;

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        AppInfo appInfo = (AppInfo) parent.getItemAtPosition(position);
        Intent intent = appInfo.mIntent;
        ResolveInfo ri = mPackageManager.resolveActivity(intent, 0);
        String intentString = String.format("%1$s%2$s%3$s", appInfo.mPackageName, mSeparator, ri.activityInfo.name);
        setSummary(intentString == null ? mSummary : appInfo.mAppName);
        persistString(intentString);
        Drawable appIcon = appInfo.mIcon;
        getDialog().dismiss();

    }

    @Override
    protected boolean persistString(String value) {
        if (getKey() != null) {
            Settings.System.putString(getContext().getContentResolver(), getKey(), value);
        }
        return super.persistString(value);
    }

    private void createList() {
        mAsyncTask = new AsyncTask<Void, Void, Void>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                mProgressBar.setVisibility(View.VISIBLE);
                mProgressBar.refreshDrawableState();
            }

            @Override
            protected Void doInBackground(Void... params) {

                mAppListAdapter = new AppListAdapter(createAppList());
                return null;
            }

            private List<AppInfo> createAppList() {
                PackageManager packageManager = Constants.CONTEXT.getPackageManager();
                ArrayList<AppInfo> appList = new ArrayList<>();
                Intent intent = new Intent(Intent.ACTION_MAIN, null);
                intent.addCategory(Intent.CATEGORY_LAUNCHER);
                List<ResolveInfo> resolveInfoList = packageManager.queryIntentActivities(intent, 0);

                for (ResolveInfo resolveInfo : resolveInfoList) {
                    AppInfo appInfo = new AppInfo();
                    appInfo.mAppName = resolveInfo.activityInfo.loadLabel(packageManager).toString();
                    appInfo.mIcon = resolveInfo.activityInfo.loadIcon(packageManager);
                    appInfo.mPackageName = resolveInfo.activityInfo.packageName;
                    Intent explicitIntent = new Intent();
                    explicitIntent.setComponent(new ComponentName(appInfo.mPackageName, resolveInfo.activityInfo.name));
                    appInfo.mIntent = explicitIntent;
                    appList.add(appInfo);
                }
                Collections.sort(appList, new Comparator<AppInfo>() {

                    @Override
                    public int compare(AppInfo lhs, AppInfo rhs) {
                        return String.CASE_INSENSITIVE_ORDER.compare(lhs.mAppName, rhs.mAppName);
                    }

                });
                return appList;

            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                mProgressBar.setVisibility(View.GONE);
                mListView.setAdapter(mAppListAdapter);
            }
        }.execute();
    }

    private class AppListAdapter extends BaseAdapter implements SectionIndexer, Filterable {

        List<AppInfo> mAppList, filteredList;
        private HashMap<String, Integer> alphaIndexer;
        private String[] sections;

        public AppListAdapter(List<AppInfo> appList) {

            this.mAppList = appList;
            filteredList = mAppList;
            //adding Indexer to display the first letter of an app while using fast scroll
            alphaIndexer = new HashMap<>();
            for (int i = 0; i < filteredList.size(); i++) {
                String s = filteredList.get(i).mAppName;
                String s1 = s.substring(0, 1).toUpperCase();
                if (!alphaIndexer.containsKey(s1))
                    alphaIndexer.put(s1, i);
            }

            Set<String> sectionLetters = alphaIndexer.keySet();
            ArrayList<String> sectionList = new ArrayList<>(sectionLetters);
            Collections.sort(sectionList);
            sections = new String[sectionList.size()];
            for (int i = 0; i < sectionList.size(); i++)
                sections[i] = sectionList.get(i);

        }

        @Override
        public Object[] getSections() {
            return sections;
        }

        @Override
        public int getPositionForSection(int sectionIndex) {
            return alphaIndexer.get(sections[sectionIndex]);
        }

        @Override
        public int getSectionForPosition(int position) {
            for (int i = sections.length - 1; i >= 0; i--) {
                if (position >= alphaIndexer.get(sections[i])) {
                    return i;
                }
            }
            return 0;
        }

        @Override
        public Filter getFilter() {
            return new Filter() {

                @Override
                protected FilterResults performFiltering(CharSequence constraint) {
                    FilterResults fr = new FilterResults();
                    ArrayList<AppInfo> ai = new ArrayList<>();

                    for (int i = 0; i < mAppList.size(); i++) {
                        String label = mAppList.get(i).mAppName;
                        if (label.toLowerCase().contains(constraint.toString().toLowerCase())) {
                            ai.add(mAppList.get(i));
                        }
                    }

                    fr.count = ai.size();
                    fr.values = ai;

                    return fr;
                }

                @Override
                protected void publishResults(CharSequence constraint, FilterResults results) {
                    filteredList = (List<AppInfo>) results.values;
                    notifyDataSetChanged();
                }
            };
        }

        @Override
        public int getCount() {
            if (filteredList != null) {
                return filteredList.size();
            }
            return 0;
        }

        @Override
        public AppInfo getItem(int position) {
            if (filteredList != null) {
                return filteredList.get(position);
            }
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                LayoutInflater inflater = LayoutInflater.from(mContext);
                convertView = inflater.inflate(R.layout.app_item, parent, false);
                ViewHolder viewHolder = new ViewHolder();
                viewHolder.mAppNames = (TextView) convertView.findViewById(R.id.appName);
                viewHolder.mAppPackage = (TextView) convertView.findViewById(R.id.appPackage);
                viewHolder.mAppIcon = (ImageView) convertView.findViewById(R.id.appIcon);
                convertView.setTag(viewHolder);
            }
            final ViewHolder holder = (ViewHolder) convertView.getTag();
            final AppInfo appInfo = filteredList.get(position);

            holder.mAppNames.setText(appInfo.mAppName);
            holder.mAppPackage.setText(appInfo.mPackageName);
            holder.mAppIcon.setImageDrawable(appInfo.mIcon);

            return convertView;
        }

        public class ViewHolder {
            public TextView mAppNames;
            public TextView mAppPackage;
            public ImageView mAppIcon;
        }
    }

    class AppInfo {
        String mAppName;
        String mPackageName;
        Drawable mIcon;
        Intent mIntent;
    }
}
