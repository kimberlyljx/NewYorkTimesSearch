package com.codepath.newyorktimesearch;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Spinner;

// Why textView.OnEditorActionListener
// TextView.OnEditorActionListener
public class EditSettingDialogFragment extends DialogFragment implements DatePickerFragment.DatePickerDialogListener, AdapterView.OnItemSelectedListener {
    //private EditText mEditText;

    private Spinner spinnerOrder;
    private int spinnerIndex;
    private int originalSpinnerIndex;

    private CheckBox cbMovies;
    private CheckBox cbArtsAndLeisure;
    private CheckBox cbMagazine;

    private Button btnDialogSubmit;
    private Button btnCancel;
    Button btnSetDate;

    boolean originalArt;
    boolean originalMagazine;
    boolean originalMovie;

    String formatDate;

    public int getSpinnerIndex() {
        return spinnerIndex;
    }

    // 1. Defines the listener interface with a method passing back data result.
    public interface EditSettingDialogListener {

        // 3. This method is invoked in the activity when the listener is triggered
        // Access the data result passed to the activity here
        void onFinishEditDialog(int spinnerIndex, boolean filterArt, boolean filterMagazine, boolean filterMovies);
        void onFinishEditDialog(String cancel, int spinnerIndex, boolean filterArt, boolean filterMagazine, boolean filterMovies);
    }

    public EditSettingDialogFragment() {
        // Empty constructor is required for DialogFragment
        // Make sure not to add arguments to the constructor
        // Use `newInstance` instead as shown below
    }

    public static EditSettingDialogFragment newInstance(int spinnerIndex, boolean filterArt, boolean filterMagazines, boolean filterMovies) {
        EditSettingDialogFragment frag = new EditSettingDialogFragment();
        Bundle args = new Bundle();

        // in case the person doesn't hit save
        frag.originalArt = filterArt;
        frag.originalMagazine = filterMagazines;
        frag.originalMovie = filterMovies;
        frag.originalSpinnerIndex = spinnerIndex;

        args.putBoolean("arts", filterArt);
        args.putBoolean("magazines", filterMagazines);
        args.putBoolean("movies", filterMovies);
        args.putInt("order", spinnerIndex);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_edit_setting, container);

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
        btnSetDate.setText(formatDate);
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Get field from view bring to oncreateview

        // 2. Setup a callback when the "Done" button is pressed on keyboard
        //mEditText = (EditText) view.findViewById(R.id.etQuery);
        cbArtsAndLeisure = (CheckBox) view.findViewById(R.id.cbArtsAndLeisure);
        cbMagazine = (CheckBox) view.findViewById(R.id.cbMagazine);
        cbMovies = (CheckBox) view.findViewById(R.id.cbMovies);

        cbArtsAndLeisure.setChecked(getArguments().getBoolean("arts"));
        cbMagazine.setChecked(getArguments().getBoolean("magazines"));
        cbMovies.setChecked(getArguments().getBoolean("movies"));

        spinnerOrder = (Spinner) view.findViewById(R.id.spinnerOrder);
        spinnerOrder.setOnItemSelectedListener(this);

        btnSetDate = (Button) view.findViewById(R.id.btnSetDate);
        btnSetDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showEditDialog();
            }
        });

        btnDialogSubmit = (Button) view.findViewById(R.id.btnDialogSubmit);
        btnDialogSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Return input text back to activity through the implemented listener
                EditSettingDialogListener listener = (EditSettingDialogListener) getActivity();
                // pass few things
                listener.onFinishEditDialog(getSpinnerIndex(), cbArtsAndLeisure.isChecked(), cbMagazine.isChecked(), cbMovies.isChecked() );
                // Close the dialog and return back to the parent activity
                dismiss();

            }
        });

        btnCancel = (Button) view.findViewById(R.id.btnCancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Return input text back to activity through the implemented listener
                EditSettingDialogListener listener = (EditSettingDialogListener) getActivity();
                // When cancelled, keep original data
                listener.onFinishEditDialog( "Cancel", originalSpinnerIndex, originalArt, originalMagazine, originalMovie );
                dismiss();

            }
        });

        // For editor lister
        // mEditText.setOnEditorActionListener(this);

        // Fetch arguments from bundle and set title
//        String query = getArguments().getString("query", "Search");
//        getDialog().setTitle(query);

        // Show soft keyboard automatically and request focus to field
        //mEditText.requestFocus();
//        getDialog().getWindow().setSoftInputMode(
//                WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

    }

    @Override
    public void onCancel(DialogInterface dialog) {
        // Return input text back to activity through the implemented listener
        EditSettingDialogListener listener = (EditSettingDialogListener) getActivity();

        // When cancelled, keep original data
        listener.onFinishEditDialog( "Cancel", this.originalSpinnerIndex , this.originalArt, this.originalMagazine, this.originalMovie );
    }

    // For editor lister
    // Fires whenever the text field has an action performed
    // In this case, when the a key is stamped down
//    @Override
//    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
//
//        if (actionId == EditorInfo.IME_NULL
//                && event.getAction() == KeyEvent.ACTION_DOWN
//                // set to specified 0 because of dialog and searchActionView
//                && event.getKeyCode() == KeyEvent.KEYCODE_0 ) {
//            // Return input text back to activity through the implemented listener
//            EditSettingDialogListener listener = (EditSettingDialogListener) getActivity();
//            // pass few things
//            listener.onFinishEditDialog(mEditText.getText().toString()    );
//            // Close the dialog and return back to the parent activity
//            dismiss();
//            return true;
//        }
//        return false;
//    }


//    private void showEditDialog() {
//        FragmentManager fm = getFragmentManager();
//        DatePickerFragment datePickerDialogFragment = new DatePickerFragment();
//
//        // SETS the target fragment for use later when sending results
//        datePickerDialogFragment.setTargetFragment(EditSettingDialogFragment.this, 300);
//        datePickerDialogFragment.show(fm, "fragment_date_picker");
//    }
//
//
//    // Call this method to send the data back to the parent fragment
//    public void sendBackResult() {
//        // Notice the use of `getTargetFragment` which will be set when the dialog is displayed
//        EditNameDialogListener listener = (EditNameDialogListener) getTargetFragment();
//        listener.onFinishEditDialog(mEditText.getText().toString());
//        dismiss();
//    }

    // Spinner
    public void onItemSelected(AdapterView<?> parent, View view,
                               int pos, long id) {
        // An item was selected. You can retrieve the selected item using
        if (pos == 0) {
            spinnerIndex = 0;
        } else if (pos == 1) {
            spinnerIndex = 1;
        }
    }

    public void onNothingSelected(AdapterView<?> parent) {
        // Another interface callback
    }

}