package com.wubydax.romcontrol.utils;

import android.app.IntentService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Map;


public class BackupRestoreIntentService extends IntentService {


    private static final String LOG_TAG = BackupRestoreIntentService.class.getSimpleName();


    public BackupRestoreIntentService() {
        super("RomControlWorkingThread");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(LocalReceiver.BACKUP_COMPLETE_ACTION);
        intentFilter.addAction(LocalReceiver.RESTORE_COMPLETE_ACTION);
        registerReceiver(new LocalReceiver(), intentFilter);
        if (intent != null) {
            switch (intent.getAction()) {
                case Constants.SERVICE_INTENT_ACTION_BACKUP:
                    handleMainFolder();
                    iteratePrefsAndBackup();
                    break;
                case Constants.SERVICE_INTENT_ACTION_RESTORE:
                    String filePath = intent.getStringExtra(Constants.BACKUP_FILE_PATH_EXTRA_KEY);
                    restoreValues(filePath);
                    break;
            }
        }
    }

    private void restoreValues(String filePath) {

    }

    private void iteratePrefsAndBackup() {
        File prefsFolder = new File(Constants.SHARED_PREFS_FOLDER_PATH);
        File[] allPrefFilesList = prefsFolder.listFiles();
        StringBuilder stringBuilder = new StringBuilder();
        for (File file : allPrefFilesList) {
            if (!file.getName().contains(getPackageName() + "_preferences")) {
                String prefName = file.getName().substring(0, file.getName().length() - 4);
                Log.d(LOG_TAG, "iteratePrefsAndBackup file name is " + file.getName().substring(0, file.getName().length() - 4));
                SharedPreferences sharedPreferences = getSharedPreferences(prefName, MODE_PRIVATE);
                Map<String, ?> prefMap = sharedPreferences.getAll();
                for (Map.Entry<String, ?> singlePref : prefMap.entrySet()) {
                    String key = singlePref.getKey();
                    String dbValue = Settings.System.getString(getContentResolver(), key);
                    if (dbValue != null) {
                        stringBuilder.append(key).append(": ").append(dbValue).append("\n");
                    }
                }
            }
        }
        Log.d(LOG_TAG, "iteratePrefsAndBackup backup is " + stringBuilder.toString());
        makeBackup(stringBuilder.toString());
    }

    private void makeBackup(String backupString) {
        String currentDate = new SimpleDateFormat("dd-MMM-yyyy_HH:mm", Locale.ENGLISH).format(Calendar.getInstance().getTime());
        String fileName = currentDate + "_" + Build.DISPLAY;
        String backupFileName = fileName + "_RCBackup";
        File newBackupFile = new File(Constants.BACKUP_FOLDER_PATH + File.separator + backupFileName);
        try {
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(newBackupFile));
            bufferedWriter.write(backupString);
            bufferedWriter.close();
            Intent intent = new Intent(LocalReceiver.BACKUP_COMPLETE_ACTION);
            intent.putExtra(Constants.BACKUP_FILE_PATH_EXTRA_KEY, newBackupFile.getAbsolutePath());
            sendBroadcast(intent);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void handleMainFolder() {
        File mainFolder = new File(Constants.BACKUP_FOLDER_PATH);
        if (!mainFolder.exists()) {
            if (mainFolder.mkdirs()) {
                Log.d(LOG_TAG, "handleMainFolder main folder created successfully");
            } else {
                Log.d(LOG_TAG, "handleMainFolder problem creating main folder");
            }
        } else {
            Log.d(LOG_TAG, "handleMainFolder main folder exists");
        }
    }

    public class UiThreadRunnable implements Runnable {
        private String mFilePath;

        public UiThreadRunnable(String filePath) {
            mFilePath = filePath;
        }

        @Override
        public void run() {
            Toast.makeText(Constants.CONTEXT, "Backup finished successfully and can be found at " + mFilePath, Toast.LENGTH_SHORT).show();
        }
    }

    private class LocalReceiver extends BroadcastReceiver {
        public static final String BACKUP_COMPLETE_ACTION = "com.wubydax.action.BACKUP_COMPLETE";
        public static final String RESTORE_COMPLETE_ACTION = "com.wubydax.action.RESTORE_COMPLETE";

        @Override
        public void onReceive(Context context, Intent intent) {
            Handler handler = new Handler();
            switch (intent.getAction()) {
                case BACKUP_COMPLETE_ACTION:
                    String filePath = intent.getStringExtra(Constants.BACKUP_FILE_PATH_EXTRA_KEY);
                    handler.post(new UiThreadRunnable(filePath));
                    break;
                case RESTORE_COMPLETE_ACTION:
                    break;
            }
            unregisterReceiver(this);
        }
    }


}
