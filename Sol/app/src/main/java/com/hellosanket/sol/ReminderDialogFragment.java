package com.hellosanket.sol;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * The dialog that allows users to pick
 * when a reminder ought to be set
 *
 */
public class ReminderDialogFragment extends DialogFragment{
    private static final String TAG = "ReminderDialog";

    public ReminderDialogFragment() {
        // Adding empty constructor as per
        // https://stackoverflow.com/questions/25984054/android-fragments-is-empty-constructor-really-required
    }

    public interface ReminderDialogListener {
        void onReminderSet(boolean enabled);
    }

    public static ReminderDialogFragment newInstance(String title) {
        Bundle args = new Bundle();
        args.putString("title", title);
        ReminderDialogFragment fragment = new ReminderDialogFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_dialog_reminder, container);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        String title = getArguments().getString("title");
        getDialog().setTitle(title);
        L.d(TAG, "view created");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        L.d(TAG, "being destroyed");
        ReminderDialogListener listener = (ReminderDialogListener) getActivity();
        listener.onReminderSet(false);
    }
}
