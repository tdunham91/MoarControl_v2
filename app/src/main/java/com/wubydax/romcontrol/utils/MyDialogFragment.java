package com.wubydax.romcontrol.utils;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

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

@SuppressWarnings("unused")
public class MyDialogFragment extends DialogFragment implements View.OnClickListener {
    private int mRequestCode;
    private OnDialogFragmentListener mOnDialogFragmentListener;

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
            case Constants.REBOOT_MENU_DIALOG_REQUEST_CODE:
                setRetainInstance(false);
                return getRebootMenuDialog();
            default:
                return super.onCreateDialog(savedInstanceState);
        }


    }


    private Dialog getRebootMenuDialog() {
        @SuppressLint("InflateParams") View view = LayoutInflater.from(new ContextThemeWrapper(Constants.CONTEXT, R.style.AppTheme)).inflate(R.layout.reboot_layout, null);
        view.findViewById(R.id.rebootDevice).setOnClickListener(this);
        view.findViewById(R.id.rebootRecovery).setOnClickListener(this);
        view.findViewById(R.id.rebootUI).setOnClickListener(this);
        view.findViewById(R.id.protectiveView).setOnClickListener(this);
        Dialog dialog = new AlertDialog.Builder(getActivity(), R.style.MyDialogTheme)
                .setView(view)
                .create();
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN);
        dialog.getWindow().setBackgroundDrawable(Utils.getDrawable(mOnDialogFragmentListener.getDecorView()));
        dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ERROR);
        return dialog;
    }


    private AlertDialog getNoSuDialog() {
        return new AlertDialog.Builder(getActivity())
                .setTitle(R.string.no_su_dialog_title)
                .setMessage(R.string.no_su_dialog_message)
                .setNegativeButton(R.string.no_su_dialog_dismiss_button_text, null)
                .create();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        String message = "protective layer clicked";
        switch (id) {
            case R.id.rebootDevice:
                message = "reboot device clicked";
                break;
            case R.id.rebootRecovery:
                message = "reboot recovery clicked";
                break;
            case R.id.rebootUI:
                message = "reboot ui clicked";
                break;

        }
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
        getDialog().dismiss();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mOnDialogFragmentListener = (OnDialogFragmentListener) context;
    }

    @Override
    public void onPause() {
        this.dismiss();
        super.onPause();
    }

    public interface OnDialogFragmentListener {
        void onDialogResult(int requestCode);

        View getDecorView();
    }
}
