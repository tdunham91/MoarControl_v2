package com.wubydax.romcontrol.prefs;

import android.content.Context;
import android.content.res.TypedArray;
import android.preference.Preference;
import android.util.AttributeSet;
import android.widget.Toast;

import com.stericson.RootShell.exceptions.RootDeniedException;
import com.stericson.RootShell.execution.Command;
import com.stericson.RootTools.RootTools;
import com.wubydax.romcontrol.R;
import com.wubydax.romcontrol.utils.Constants;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeoutException;

/*      Created by Roberto Mariani and Anna Berkovitch, 12/06/2016
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

public class RunScriptPreference extends Preference {
    private String mFilePath;

    public RunScriptPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.RunScriptPreference);
        String scriptName = typedArray.getString(R.styleable.RunScriptPreference_scriptFileName);
        mFilePath = Constants.FILES_SCRIPTS_FOLDER_PATH + File.separator + scriptName;
        typedArray.recycle();
    }

    @Override
    protected void onClick() {
        super.onClick();
        Command command = new Command(0, mFilePath) {
            @Override
            public void commandCompleted(int id, int exitCode) {
                super.commandCompleted(id, exitCode);
                if (exitCode != 0) {
                    Toast.makeText(getContext(), String.valueOf(exitCode), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), R.string.command_executed_success_toast, Toast.LENGTH_SHORT).show();

                }
            }
        };
        try {
            RootTools.getShell(true).add(command);
        } catch (IOException | TimeoutException | RootDeniedException e) {
            e.printStackTrace();
            Toast.makeText(getContext(), R.string.root_tools_exception_toast, Toast.LENGTH_SHORT).show();
        }
    }
}
