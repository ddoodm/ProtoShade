package com.id11688025.majorassignment;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.net.Uri;
import android.widget.Toast;

import com.id11688025.majorassignment.graphics.TextureFilteringMode;
import com.id11688025.majorassignment.graphics.TextureWrapMode;

import java.io.FileNotFoundException;
import java.io.InputStream;

/**
 * Created by Ddoodm on 14/01/2016.
 */
public class SamplerDialog extends AlertDialog
{
    private View childView;
    private CustomRenderer renderer;
    private Context context;
    private Activity mainActivity;

    protected SamplerDialog(Context context, CustomRenderer renderer, Activity activity)
    {
        super(context);
        this.renderer = renderer;
        this.context = context;
        this.mainActivity = activity;

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

        Button buttonSelectImage = (Button)childView.findViewById(R.id.btn_sampler_select_image);

        linkSpinnerToStringArray(spinnerSamplerFiltermode, R.array.filter_modes);
        linkSpinnerToStringArray(spinnerSamplerWrapmode, R.array.wrap_mode);

        // Set initial items from preferences
        final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        spinnerSamplerFiltermode.setSelection(preferences.getInt(Constants.KEY_SAMPLER_FILTER_MODE, 0));
        spinnerSamplerWrapmode.setSelection(preferences.getInt(Constants.KEY_SAMPLER_TEXTURE_WRAP_MODE, 0));

        // Filtering Mode Click Listener
        spinnerSamplerFiltermode.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                renderer.setTextureFilteringMode(TextureFilteringMode.values()[i]);

                // Save the preference
                preferences.edit()
                        .putInt(Constants.KEY_SAMPLER_FILTER_MODE, i)
                        .apply();
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

                // Save the preference
                preferences.edit()
                        .putInt(Constants.KEY_SAMPLER_TEXTURE_WRAP_MODE, i)
                        .apply();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        initPreviewImage(preferences);

        buttonSelectImage.setOnClickListener(new SelectImageClickListener(context));
    }

    private void initPreviewImage(SharedPreferences preferences)
    {
        // Set preview image
        ImageView imagePreview = (ImageView)childView.findViewById(R.id.iv_texturePreview);

        String imageUriStr = preferences.getString(Constants.KEY_TEXTURE_IMAGE_PATH, "");
        Uri imageUri = Uri.parse(imageUriStr);

        try
        {
            imagePreview.setImageURI(imageUri);
        }
        catch (Exception e)
        {
            Toast.makeText(context, context.getString(R.string.last_used_image_not_found), Toast.LENGTH_LONG).show();
        }
    }

    private class SelectImageClickListener implements View.OnClickListener
    {
        private Context context;

        public  SelectImageClickListener(Context context)
        {
            super();
            this.context = context;
        }

        @Override
        public void onClick(View view) {
            Intent getIntent = new Intent(Intent.ACTION_GET_CONTENT);
            getIntent.setType("image/*");

            Intent pickIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            pickIntent.setType("image/*");

            Intent chooserIntent = Intent.createChooser(getIntent, context.getString(R.string.select_image));
            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{pickIntent});

            mainActivity.startActivityForResult(chooserIntent, Constants.REQUEST_CODE_PICK_SAMPLER_IMAGE);

            SamplerDialog.this.dismiss();
        }
    }
}
