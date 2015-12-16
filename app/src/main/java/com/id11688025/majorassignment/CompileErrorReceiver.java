package com.id11688025.majorassignment;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;

/** Class that responds to GLSL compiler error broadcasts */
public class CompileErrorReceiver extends BroadcastReceiver
{
    /** The "Errors" button */
    private Button btnErrors;

    /** Create a compiler error receiver.
     * @param btnErrors The "Errors" button
     */
    public CompileErrorReceiver(Button btnErrors)
    {
        this.btnErrors = btnErrors;
    }

    @Override
    public void onReceive(final Context context, Intent intent)
    {
        // Was there an error compiling the user's shader?
        final String compilerLog = intent.getStringExtra(Constants.EXTRA_COMPILER_LOG);
        boolean compilerError = !compilerLog.isEmpty();

        // Split errors by the token "ERROR: "
        final String errors[] = compilerLog.split("ERROR: ");

        // Make the button clickable if there is a compiler error
        btnErrors.setEnabled(compilerError);

        // Highlight the button
        btnErrors.setTextAppearance(context,compilerError? R.style.BoldRedText : R.style.NormalText);

        if(compilerError)
        {
            // Show the error count
            String errorCount = errors[errors.length-1].split("\\s+")[0];
            btnErrors.setText(errorCount + " " +
                    context.getString(R.string.btn_compile_error) +
                    (errorCount.equals("1")? "" : "s"));

            // Configure the error dialog
            btnErrors.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Create an array adapter for the array of error strings
                    final ArrayAdapter<String> errorAdapter = new ArrayAdapter<String>(
                            context, R.layout.error_row, R.id.error_row_textview
                    );

                    // Provide the error array to the adapter
                    errorAdapter.addAll(errors);

                    // Create the dialog
                    new AlertDialog.Builder(context)
                            .setTitle(context.getString(R.string.compiler_log))
                            .setAdapter(errorAdapter, null)
                            .setPositiveButton(R.string.dismiss, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            })
                            .show();
                }
            });
        }
        else
            // Show that there are no errors
            btnErrors.setText(R.string.btn_no_compile_errors);
    }
}