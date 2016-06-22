package com.codepath.newyorktimesearch;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;

import java.util.Calendar;

// Why textView.OnEditorActionListener
// TextView.OnEditorActionListener
public class EditSettingDialogFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {
    //private EditText mEditText;
    private CheckBox cbMovies;
    private CheckBox cbArtsAndLeisure;
    private CheckBox cbMagazine;

    private Button btnDialogSubmit;
    private Button btnCancel;

    boolean originalArt;
    boolean originalMagazine;
    boolean originalMovie;

    // 1. Defines the listener interface with a method passing back data result.
    public interface EditSettingDialogListener {

        // 3. This method is invoked in the activity when the listener is triggered
        // Access the data result passed to the activity here
        void onFinishEditDialog(boolean filterArt, boolean filterMagazine, boolean filterMovies);
        void onFinishEditDialog(String cancel, boolean filterArt, boolean filterMagazine, boolean filterMovies);
    }

    public EditSettingDialogFragment() {
        // Empty constructor is required for DialogFragment
        // Make sure not to add arguments to the constructor
        // Use `newInstance` instead as shown below
    }

    public static EditSettingDialogFragment newInstance(boolean filterArt, boolean filterMagazines, boolean filterMovies) {
        EditSettingDialogFragment frag = new EditSettingDialogFragment();
        Bundle args = new Bundle();

        // in case the person doesn't hit save
        frag.originalArt = filterArt;
        frag.originalMagazine = filterMagazines;
        frag.originalMovie = filterMovies;

        args.putBoolean("arts", filterArt);
        args.putBoolean("magazines", filterMagazines);
        args.putBoolean("movies", filterMovies);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_edit_setting, container);

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

        btnDialogSubmit = (Button) view.findViewById(R.id.btnDialogSubmit);
        btnDialogSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Return input text back to activity through the implemented listener
                EditSettingDialogListener listener = (EditSettingDialogListener) getActivity();
                // pass few things
                listener.onFinishEditDialog( cbArtsAndLeisure.isChecked(), cbMagazine.isChecked(), cbMovies.isChecked() );
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
                listener.onFinishEditDialog( "Cancel", originalArt, originalMagazine, originalMovie );
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
        listener.onFinishEditDialog( "Cancel", this.originalArt, this.originalMagazine, this.originalMovie );
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

    // handle the date selected
    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        // store the values selected into a Calendar instance
        final Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, monthOfYear);
        c.set(Calendar.DAY_OF_MONTH, dayOfMonth);

    }

}