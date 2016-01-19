package com.id11688025.majorassignment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.id11688025.majorassignment.graphics.TextureFilteringMode;
import com.id11688025.majorassignment.graphics.TextureWrapMode;

/**
 * Created by Ddoodm on 14/01/2016.
 */
public class SamplerDialog extends AlertDialog
{
    private View childView;
    private CustomRenderer renderer;

    protected SamplerDialog(Context context, CustomRenderer renderer)
    {
        super(context);
        this.renderer = renderer;

        // Create a Layout Inflater to inflate the XML layout
        LayoutInflater inflater = getLayoutInflater();

        // Set the layout as the inflated XML layout
        childView = inflater.inflate(R.layout.dialog_sampler, null);
        setView(childView);

        // Set the dialog title
        setTitle(context.getString(R.string.sampler_dialog_title));

        // Configure the "OK" button
        setButton(BUTTON_NEUTRAL, context.getString(R.string.ok), new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dismiss();
            }
        });
    }

    @Override protected void onCreate(Bundle bundle)
    {
        super.onCreate(bundle);

        configureViews();
    }

    private void linkSpinnerToStringArray(Spinner spinner, int arrayId)
    {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                getContext(), arrayId, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }

    private void configureViews()
    {
        Spinner
                spinnerSamplerFiltermode = (Spinner)childView.findViewById(R.id.spinner_sampler_filtermode),
                spinnerSamplerWrapmode = (Spinner)childView.findViewById(R.id.spinner_sampler_wrapmode);

        linkSpinnerToStringArray(spinnerSamplerFiltermode, R.array.filter_modes);
        linkSpinnerToStringArray(spinnerSamplerWrapmode, R.array.wrap_mode);

        // Filtering Mode Click Listener
        spinnerSamplerFiltermode.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                renderer.setTextureFilteringMode(TextureFilteringMode.values()[i]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        // Wrap Mode click listener
        spinnerSamplerWrapmode.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                renderer.setTextureWrapMode(TextureWrapMode.values()[i]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }
}
