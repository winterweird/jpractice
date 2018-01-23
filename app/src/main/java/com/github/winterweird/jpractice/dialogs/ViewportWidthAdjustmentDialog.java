package com.github.winterweird.jpractice.dialogs;

import android.support.v4.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.app.AlertDialog;
import android.app.Dialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.EditText;
import android.os.Bundle;
import android.app.Activity;
import android.text.TextWatcher;
import android.text.Editable;

import com.github.winterweird.jpractice.R;

public class ViewportWidthAdjustmentDialog extends DialogFragment {
    public interface ViewportWidthAdjustmentDialogListener {
        public void onSliderAdjustment(DialogFragment dialog, int newValue);
        public void onDialogDismiss(DialogFragment dialog);
    }
    
    private ViewportWidthAdjustmentDialogListener listener;
    private int PROGRESS_INITIAL_VALUE;
    private int MIN_VALUE;
    private int MAX_VALUE;

    public ViewportWidthAdjustmentDialog(int min, int max, int progressValue) {
        MIN_VALUE = min;
        MAX_VALUE = max;
        PROGRESS_INITIAL_VALUE = progressValue;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Activity act = getActivity();
        View view = act.getLayoutInflater().inflate(R.layout.viewport_width_dialog_layout, null);
        final SeekBar sb = view.findViewById(R.id.viewportWidthSeekBar);
        sb.setProgress(PROGRESS_INITIAL_VALUE - MIN_VALUE);
        final TextView value = view.findViewById(R.id.viewportWidthSeekBarValue);
        value.setText(""+PROGRESS_INITIAL_VALUE);
        value.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable e) {
                Integer x = null;
                try {
                    x = Integer.parseInt(value.getText().toString());
                } catch (NumberFormatException ex) {}
                if (x != null && x >= MIN_VALUE && x <= MAX_VALUE) {
                    if (x.intValue()-MIN_VALUE != sb.getProgress()) {
                        sb.setProgress(x.intValue()-MIN_VALUE);
                    }
                }
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
        });
        sb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekbar, int progressValue, boolean fromUser) {
                EditText et = (EditText)value;
                int pos = et.getSelectionStart();
                et.setText(""+(progressValue + MIN_VALUE));
                et.setSelection(pos);
                listener.onSliderAdjustment(ViewportWidthAdjustmentDialog.this, progressValue
                        + MIN_VALUE);
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekbar) { }
            @Override
            public void onStopTrackingTouch(SeekBar seekbar) {
//                listener.onDialogDismiss(ViewportWidthAdjustmentDialog.this);
//                ViewportWidthAdjustmentDialog.this.dismiss();
            }
        });
        return new AlertDialog.Builder(act).setView(view).create();
    }

    @Override
    public void onAttach(Context activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            listener = (ViewportWidthAdjustmentDialogListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement ViewportWidthAdjustmentDialogListener");
        }
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        listener.onDialogDismiss(this);
    }
}
