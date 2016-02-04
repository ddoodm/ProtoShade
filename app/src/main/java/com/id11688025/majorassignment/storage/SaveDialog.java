package com.id11688025.majorassignment.storage;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.id11688025.majorassignment.ContentManager;
import com.id11688025.majorassignment.R;
import com.id11688025.majorassignment.shaders.Shader;

/**
 * The dialog that is displayed to the user
 * to save a shader.
 */
public class SaveDialog extends AlertDialog
{
    /** The database wrapper */
    private LocalShaderDatabase database;

    /** An image of a render produced with the shader */
    private Bitmap render;

    /**
     * Create a "Save Shader" dialog
     * @param context An application context
     * @param shader The shader to save
     * @param render An image of a render produced with the shader
     */
    public SaveDialog(final Context context, final Shader shader, final Bitmap render)
    {
        super(context);

        // Crop and scale the bitmap to thumbnail size
        this.render = createThumbnail(render);

        // Create a Layout Inflater to inflate the XML layout
        LayoutInflater inflater = getLayoutInflater();

        // Set the layout as the inflated XML layout
        setView(inflater.inflate(R.layout.dialog_save, null));

        // Set the dialog title
        setTitle(context.getString(R.string.dialog_save_shader));

        // Configure the "Save" button
        setButton(BUTTON_POSITIVE, context.getString(R.string.save), new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Build a Shader Description from the data in the dialog
                ShaderDescription description = makeShaderDescription(shader);

                // Do not save if the filename is invalid
                if(ContentManager.invalidFilename(description.getPath()))
                {
                    Toast.makeText(context, R.string.error_empty_filename, Toast.LENGTH_LONG).show();
                    return;
                }

                // Initialize the database
                database = new LocalShaderDatabase(context);

                // Save the shader
                database.save(description);

                // Display a short Toast
                Toast.makeText(context, R.string.shader_saved, Toast.LENGTH_SHORT).show();
            }
        });

        // Configure the "Cancel" button
        setButton(BUTTON_NEGATIVE, context.getString(R.string.cancel), new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                dismiss();
            }
        });
    }

    /** Create a ShaderDescription from a shader, and this dialog */
    private ShaderDescription makeShaderDescription(Shader shader)
    {
        EditText etTitle = (EditText)findViewById(R.id.save_title);
        String title = etTitle.getText().toString();

        // Create the description
        ShaderDescription description = new ShaderDescription(
                title, false, shader.getFragmentSource(), render);

        // Save the shader and render data to storage
        description.saveShaderAndRender(getContext());

        return description;
    }

    /** Crops and resamples a bitmap to a 255 * 255 pixel square */
    private Bitmap createThumbnail(Bitmap original)
    {
        // 512 x 512 pixels
        final int SIZE = 512;

        int originalWidth = original.getWidth();
        int originalHeight = original.getHeight();

        // Determine which dimension should be constrained
        Bitmap cropped;
        if(originalWidth > originalHeight)
        {
            // Crop the image to a uniform square (Constrain width)
            cropped = Bitmap.createBitmap(
                    original,
                    originalWidth / 2 - originalHeight / 2, // Start X
                    0,                                      // Start Y
                    originalHeight,                         // Width
                    originalHeight);                        // Height
        }
        else
        {
            // Crop the image to a uniform square (Constrain height)
            cropped = Bitmap.createBitmap(
                    original,
                    0,                                      // Start X
                    originalHeight / 2 - originalWidth / 2, // Start Y
                    originalWidth,                          // Width
                    originalWidth                           // Height
            );
        }

        // Resample (and filter) the scaled image
        Bitmap resampled = Bitmap.createScaledBitmap(
                cropped, SIZE, SIZE, true);

        return resampled;
    }

    @Override protected void onCreate(Bundle bundle)
    {
        super.onCreate(bundle);

        // Set the preview ImageView image source
        ImageView ivPreview = (ImageView)findViewById(R.id.save_preview);
        ivPreview.setImageBitmap(render);
    }
}
