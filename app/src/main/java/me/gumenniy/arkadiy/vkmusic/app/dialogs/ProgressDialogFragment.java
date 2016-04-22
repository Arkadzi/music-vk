package me.gumenniy.arkadiy.vkmusic.app.dialogs;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

import me.gumenniy.arkadiy.vkmusic.R;

/**
 * Created by Arkadiy on 22.04.2016.
 */
public class ProgressDialogFragment extends DialogFragment {

    public static DialogFragment newInstance() {
        DialogFragment fragment = new ProgressDialogFragment();

        Bundle args = new Bundle();
        fragment.setArguments(args);

        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(R.layout.dialog_progress);
        return builder.create();
    }
}
