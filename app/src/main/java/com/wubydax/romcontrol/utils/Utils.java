package com.wubydax.romcontrol.utils;

import android.content.res.AssetManager;

import com.stericson.RootShell.exceptions.RootDeniedException;
import com.stericson.RootShell.execution.Command;
import com.stericson.RootTools.RootTools;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.concurrent.TimeoutException;

/*      Created by Roberto Mariani and Anna Berkovitch, 13/06/2016
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

@SuppressWarnings("ResultOfMethodCallIgnored")
public class Utils {


    public static void copyAssetFolder() {


        try {
            String[] scriptsInAssets = Constants.CONTEXT.getAssets().list(Constants.SCRIPTS_FOLDER);
            File scriptsFilesDir = new File(Constants.FILES_SCRIPTS_FOLDER_PATH);
            //Checking if the "scripts" directory exists in files
            if (!scriptsFilesDir.exists()) {
                new File(Constants.FILES_SCRIPTS_FOLDER_PATH).mkdirs();
            }
            for (String file : scriptsInAssets)
                //If the file name contains  a dot, it's most probably a single file and not dir. So treating it as copying file
                if (file.contains("."))
                    copyAsset(scriptsInAssets, Constants.SCRIPTS_FOLDER + File.separator + file, Constants.FILES_SCRIPTS_FOLDER_PATH + File.separator + file);
                else
                    //Otherwise treating as copying dir
                    copyAssetFolder();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void copyAsset(String[] scriptsInAssets, String from, String to) {
        boolean isCopied = false;
        InputStream in;
        OutputStream out;
        ArrayList<File> scriptsFiles = new ArrayList<>();
        //Creating list of File objects inside assets
        for (String scriptsInAsset : scriptsInAssets) {
            File f = new File(Constants.FILES_SCRIPTS_FOLDER_PATH + File.separator + scriptsInAsset);
            scriptsFiles.add(f);
        }
        for (int j = 0; j < scriptsFiles.size(); j++) {
            //If the file doesn't exist in files dir, we copy it
            if (!scriptsFiles.get(j).exists()) {
                try {
                    in = Constants.CONTEXT.getAssets().open(from);
                    new File(to).createNewFile();
                    out = new FileOutputStream(to);
                    copyFile(in, out);
                    in.close();
                    out.flush();
                    out.close();
                    isCopied = true;
                } catch (Exception e) {
                    e.printStackTrace();
                    isCopied = false;
                }
            }
        }
        //If the file was just copied, we make it executable
        if (isCopied) {

            try {
                Command c = new Command(0, "chmod -R 755 " + Constants.FILES_SCRIPTS_FOLDER_PATH);
                RootTools.getShell(false).add(c);

            } catch (IOException | RootDeniedException | TimeoutException e) {
                e.printStackTrace();
            }
        }

    }

    //Actual copying of the file
    public static void copyFile(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        int read;
        while ((read = in.read(buffer)) != -1) {
            out.write(buffer, 0, read);
        }
    }
}
