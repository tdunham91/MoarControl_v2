package com.wubydax.romcontrol;

import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.wubydax.romcontrol.utils.BackupRestoreIntentService;
import com.wubydax.romcontrol.utils.Constants;
import com.wubydax.romcontrol.utils.MyDialogFragment;
import com.wubydax.romcontrol.utils.SuTask;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        SuTask.OnSuCompletedListener,
        MyDialogFragment.OnDialogFragmentListener {
    private ProgressDialog mProgressDialog;
    private FragmentManager mFragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(PreferenceManager.getDefaultSharedPreferences(Constants.CONTEXT).getInt(Constants.THEME_PREF_KEY, 0) == 0 ? R.style.AppTheme_NoActionBar : R.style.AppTheme_NoActionBar_Dark);
        setContentView(R.layout.activity_main);
        mFragmentManager = getFragmentManager();
        initViews();
        if (savedInstanceState == null) {
            loadPrefsFragment("ui_prefs");
        }
        SuTask suTask = new SuTask();
        suTask.setOnSuCompletedListener(this);
        suTask.execute();
        mProgressDialog = ProgressDialog.show(this, getString(R.string.root_progress_dialog_title), getString(R.string.root_progress_dialog_message), false);
    }

    private void initViews() {

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        assert drawer != null;
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        assert navigationView != null;
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer != null && drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        switch (id) {
            case R.id.rebootMenu:
                mFragmentManager.beginTransaction().add(MyDialogFragment.newInstance(Constants.REBOOT_MENU_DIALOG_REQUEST_CODE), "reboot_dialog").commit();
                break;
        }


        return super.onOptionsItemSelected(item);
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        switch (id) {

            case R.id.sys_ui_prefs:
                loadPrefsFragment("ui_prefs");
                break;
            case R.id.themes:
                mFragmentManager.beginTransaction().add(MyDialogFragment.newInstance(Constants.THEME_DIALOG_REQUEST_CODE), "theme_dialog").commit();
                break;
            case R.id.changeLog:
                mFragmentManager.beginTransaction().add(MyDialogFragment.newInstance(Constants.CHANGELOG_DIALOG_REQUEST_CODE), "changelog").commit();
                break;
            case R.id.about_us:
                startActivity(new Intent(this, AboutActivity.class));
                break;
            case R.id.backup_restore:
                mFragmentManager.beginTransaction().add(MyDialogFragment.newInstance(Constants.BACKUP_OR_RESTORE_DIALOG_REQUEST_CODE), "backup_restore").commit();
                break;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        assert drawer != null;
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void launchBackupRestoreService(int which, String filePath) {
        String action;
        Intent intent = new Intent(this, BackupRestoreIntentService.class);
        switch (which) {
            case 0:
                action = Constants.SERVICE_INTENT_ACTION_BACKUP;
                break;
            case 1:
                action = Constants.SERVICE_INTENT_ACTION_RESTORE;
                intent.putExtra(Constants.BACKUP_FILE_PATH_EXTRA_KEY, filePath);
                break;
            default:
                action = null;
        }
        if (action != null) {
            intent.setAction(action);
            startService(intent);
            if (which == 1) {
                finish();
            }
        }
    }

    private void loadPrefsFragment(String prefName) {
        mFragmentManager.beginTransaction().replace(R.id.fragment_container, PrefsFragment.newInstance(prefName)).commit();
    }


    @Override
    public void onTaskCompleted(boolean isGranted) {
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
        }
        if (!isGranted) {
            mFragmentManager.beginTransaction().add(MyDialogFragment.newInstance(Constants.NO_SU_DIALOG_REQUEST_CODE), "no_su_dialog_fragment").commit();
        }
    }


    @Override
    public void onDialogResult(int requestCode) {
        switch (requestCode) {
            case Constants.THEME_DIALOG_REQUEST_CODE:
                finish();
                this.overridePendingTransition(0, R.animator.fadeout);
                startActivity(new Intent(this, MainActivity.class));
                this.overridePendingTransition(R.animator.fadein, 0);
                break;
        }

    }

    @Override
    public void onBackupRestoreResult(int which) {
        switch (which) {
            case 0:
                launchBackupRestoreService(which, null);
                break;
            case 1:
                mFragmentManager.beginTransaction().add(MyDialogFragment.newInstance(Constants.RESTORE_FILE_SELECTOR_DIALOG_REQUEST_CODE), "restore_selector").commit();
                break;
        }
    }

    @Override
    public void onRestoreRequested(String filePath, boolean isConfirmed) {
        if (isConfirmed) {
            launchBackupRestoreService(1, filePath);
        } else {
            mFragmentManager.beginTransaction().add(MyDialogFragment.backupRestoreInstance(Constants.RESTORE_FILE_SELECTOR_DIALOG_REQUEST_CODE, true, filePath), "restore_confirm").commit();
        }
    }

    @Override
    public View getDecorView() {
        return getWindow().getDecorView();
    }
}
