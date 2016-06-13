package com.wubydax.romcontrol.utils;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;

import com.wubydax.romcontrol.R;

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

public class MyDialogFragment extends DialogFragment {
    private int mRequestCode;

    public static MyDialogFragment newInstance(int requestCode) {
        MyDialogFragment myDialogFragment = new MyDialogFragment();
        Bundle args = new Bundle();
        args.putInt(Constants.DIALOG_REQUEST_CODE_KEY, requestCode);
        myDialogFragment.setArguments(args);
        return myDialogFragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        mRequestCode = getArguments().getInt(Constants.DIALOG_REQUEST_CODE_KEY);
        switch (mRequestCode) {
            case Constants.NO_SU_DIALOG_REQUEST_CODE:
                return getNoSuDialog();
            default:
                return super.onCreateDialog(savedInstanceState);
        }


    }

    private AlertDialog getNoSuDialog() {
        return new AlertDialog.Builder(getActivity())
                .setTitle(R.string.no_su_dialog_title)
                .setMessage(R.string.no_su_dialog_message)
                .setNegativeButton(R.string.no_su_dialog_dismiss_button_text, null)
                .create();
    }


}
