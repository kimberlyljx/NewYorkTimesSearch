package com.codepath.newyorktimesearch.fragments;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.widget.DatePicker;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

    // Defines the listener interface
    public interface DatePickerDialogListener {
        void onFinishEditDialog(String formatDate);
        void onFinishEditDialog(boolean cancelled);
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current time as the default values for the picker
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        // Activity needs to implement this interface
//        DatePickerDialog.OnDateSetListener listener = (DatePickerDialog.OnDateSetListener) getActivity();

        // Create a new instance of TimePickerDialog and return it
        return new DatePickerDialog(getActivity(), this, year, month, day);
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int day) {
        Calendar c = Calendar.getInstance();
        c.set(year, month, day);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String formattedDate = sdf.format(c.getTime());

        DatePickerDialogListener listener = (DatePickerDialogListener) getTargetFragment();
        listener.onFinishEditDialog(formattedDate);
        dismiss();

    }

    @Override
    public void onCancel(DialogInterface dialog) {
        DatePickerDialogListener listener = (DatePickerDialogListener) getTargetFragment();
        listener.onFinishEditDialog(false);
        super.onCancel(dialog);
    }
}