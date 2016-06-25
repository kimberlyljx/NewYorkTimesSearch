package com.codepath.newyorktimesearch.fragments;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Spinner;

import com.codepath.newyorktimesearch.R;
import com.codepath.newyorktimesearch.models.Setting;

import org.parceler.Parcels;

import butterknife.BindView;
import butterknife.ButterKnife;

// Why textView.OnEditorActionListener
// TextView.OnEditorActionListener
public class EditSettingDialogFragment extends DialogFragment implements DatePickerFragment.DatePickerDialogListener, AdapterView.OnItemSelectedListener {
    //private EditText mEditText;

    private int spinnerIndex;

    @BindView(R.id.cbArtsAndLeisure) CheckBox cbArtsAndLeisure;
    @BindView(R.id.cbMagazine) CheckBox cbMagazine;
    @BindView(R.id.cbMovies) CheckBox cbMovies;
    @BindView(R.id.spinnerOrder) Spinner spinnerOrder;
    @BindView(R.id.btnSetDate) Button btnSetDate;
    @BindView(R.id.btnDialogSubmit) Button btnDialogSubmit;
    @BindView(R.id.btnCancel) Button btnCancel;

    Setting original;
    String formatDate = ""; // default empty

    public int getSpinnerIndex() {
        return spinnerIndex;
    }

    // 1. Defines the listener interface with a method passing back data result.
    public interface EditSettingDialogListener {

        // 3. This method is invoked in the activity when the listener is triggered
        // Access the data result passed to the activity here
        void onFinishEditDialog(Setting newSetting);
        void onFinishEditDialog(String cancel, Setting oldSetting);
    }

    public EditSettingDialogFragment() {
        // Empty constructor is required for DialogFragment
        // Make sure not to add arguments to the constructor
        // Use `newInstance` instead as shown below
    }

    public static EditSettingDialogFragment newInstance(Setting originalSetting) {
        EditSettingDialogFragment frag = new EditSettingDialogFragment();
        Bundle args = new Bundle();

        // in case the person doesn't hit save
        args.putParcelable("originalSetting", Parcels.wrap(originalSetting));
        frag.setArguments(args);

        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // remove titlebar on dialog
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        View view = inflater.inflate(R.layout.fragment_edit_setting, container);
        ButterKnife.bind(this, view);
        return view;
    }

    private void showEditDialog() {
        DialogFragment picker = new DatePickerFragment();
        // SETS the target fragment for use later when sending results
        picker.setTargetFragment(EditSettingDialogFragment.this, 300);
        picker.show(getFragmentManager(), "datePicker");
    }

    // This is called when the dialog is completed and the results have been passed
    @Override
    public void onFinishEditDialog(String formatDate) {
        btnSetDate.setText("Begin from " + formatDate);
        this.formatDate = formatDate;
    }

    @Override
    public void onFinishEditDialog(boolean cancelled) {
        btnSetDate.setText("No begin date");
        this.formatDate = "";
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Get field from view bring to oncreateview

        // 2. Setup a callback when the "Done" button is pressed on keyboard
        //mEditText = (EditText) view.findViewById(R.id.etQuery);

        original = Parcels.unwrap(getArguments().getParcelable("originalSetting"));

        cbArtsAndLeisure.setChecked(original.filterArts);
        cbMagazine.setChecked(original.filterMagazines);
        cbMovies.setChecked(original.filterMovies);

        spinnerOrder.setSelection(original.spinnerIndex);
        spinnerOrder.setOnItemSelectedListener(this);

        if (original.beginDate.contentEquals("")) {
            btnSetDate.setText(R.string.begin_date);
        } else {
            formatDate = original.beginDate;
            btnSetDate.setText("Begin from " + original.beginDate);
        }

        btnSetDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showEditDialog();
            }
        });

        btnDialogSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Return input text back to activity through the implemented listener
                EditSettingDialogListener listener = (EditSettingDialogListener) getActivity();
                // pass few things

                Setting newSetting = new Setting(formatDate, cbArtsAndLeisure.isChecked(), cbMagazine.isChecked(), cbMovies.isChecked(), getSpinnerIndex() );

                listener.onFinishEditDialog(newSetting);
                // Close the dialog and return back to the parent activity
                dismiss();

            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Return input text back to activity through the implemented listener
                EditSettingDialogListener listener = (EditSettingDialogListener) getActivity();
                // When cancelled, keep original data
                listener.onFinishEditDialog( "Cancel", original );
                dismiss();

            }
        });
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        // Return input text back to activity through the implemented listener
        EditSettingDialogListener listener = (EditSettingDialogListener) getActivity();

        // When cancelled, keep original data
        listener.onFinishEditDialog( "Cancel", original );
    }

    // Spinner
    public void onItemSelected(AdapterView<?> parent, View view,
                               int pos, long id) {
        // An item was selected. You can retrieve the selected item using
        if (pos == 0) {
            spinnerIndex = 0;
        } else if (pos == 1) {
            spinnerIndex = 1;
        } else if (pos == 2) {
            spinnerIndex = 2;
        }
    }

    public void onNothingSelected(AdapterView<?> parent) {
        // Another interface callback
        spinnerIndex = 0;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

}
