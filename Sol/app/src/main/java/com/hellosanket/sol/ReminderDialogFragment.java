package com.hellosanket.sol;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.NavUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.NumberPicker;

/**
 * The dialog that allows users to pick
 * when a reminder ought to be set
 *
 */
public class ReminderDialogFragment extends DialogFragment{
    private static final String TAG = "ReminderDialog";
    private NumberPicker mPicker;
    private boolean mPicked;
    private int mReminderTime;
    private String mDialogType;

    public ReminderDialogFragment() {
        // Adding empty constructor as per
        // https://stackoverflow.com/questions/25984054/android-fragments-is-empty-constructor-really-required
    }

    public interface ReminderDialogListener {
        void onReminderSet(final String type, final boolean enabled);
    }

    public static ReminderDialogFragment newInstance(String type) {
        Bundle args = new Bundle();
        args.putString("reminder_type", type);
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
        mDialogType = getArguments().getString("reminder_type");
        getDialog().setTitle(mDialogType);

        // Use the 'View' luke!
        final NumberPicker picker = (NumberPicker) view.findViewById(R.id.number_picker);
        picker.setMaxValue(59);
        picker.setMinValue(0);

        Button btn_ok = (Button) view.findViewById(R.id.dialog_btn_ok);
        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPicked = true;
                mReminderTime = picker.getValue();
                L.d(TAG, "picked " + mReminderTime);
                dismiss();
            }
        });

        Button btn_cancel = (Button) view.findViewById(R.id.dialog_btn_cancel);
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                L.d(TAG, "dismissing");
                dismiss();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ReminderDialogListener listener = (ReminderDialogListener) getActivity();
        listener.onReminderSet(mDialogType, mPicked);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return super.onCreateDialog(savedInstanceState);
    }
}
