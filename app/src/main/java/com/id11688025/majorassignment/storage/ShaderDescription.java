package com.id11688025.majorassignment.storage;

import android.content.ContentValues;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.widget.Toast;

import com.id11688025.majorassignment.ContentManager;
import com.id11688025.majorassignment.R;
import com.id11688025.majorassignment.shaders.Shader;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Stores shader metadata, such as its title.
 */
public class ShaderDescription
{
    /** The database key that refers to the "title" row */
    public static final String KEY_TITLE = "title";

    /** The database key that refers to the "isRequired" row */
    public static final String KEY_ISREQUIRED = "isRequired";

    /** The database key that refers to the "path" row */
    public static final String KEY_PATH = "path";

    /** The name of the shader */
    private String title;

    /** True if the shader cannot be deleted */
    private boolean isRequired;

    /** The shader's location on the filesystem */
    private String path = "Undefined";

    /** The shader source that this metadata describes. */
    private String shaderSource;

    /** An image of a render that was drawn with this shader */
    private Bitmap render;

    /**
     * Creates a new ShaderDescription from discrete values.
     */
    public ShaderDescription(String title, boolean isRequired, String shaderSource, Bitmap render) {
        setTitle(title);
        setRequired(isRequired);
        setShaderSource(shaderSource);
        setRender(render);
    }

    public ShaderDescription(String title, boolean isRequired, String sourcePath)
    {
        setTitle(title);
        setRequired(isRequired);
        setPath(sourcePath);
    }

    /**
     * Creates a new ShaderDescription from a ContentValues object,
     * obtained from the database.
     * @param values The ContentValues to convert to a ShaderDescription.
     */
    public ShaderDescription(ContentValues values) {
        setTitle(values.getAsString(KEY_TITLE));
        setRequired(values.getAsInteger(KEY_ISREQUIRED) > 0);  // getAsBoolean does not work.
        setPath(values.getAsString(KEY_PATH));
    }

    /** Write the shader data and its render to storage */
    public void saveShaderAndRender(Context context)
    {
        setPath(ContentManager.filenameFromTitle(title));

        // Do not write the file if the filename is invalid
        if(ContentManager.invalidFilename(path))
            return;

        saveShader(context);
        saveRender(context);
    }

    /** Prepare the file path, and write the shader to storage. */
    private void saveShader(Context context)
    {
        try {
            // Create a File Output Stream to save the shader source code
            FileOutputStream outStream = context.openFileOutput(path, Context.MODE_PRIVATE);

            // Write the shader data
            outStream.write(shaderSource.getBytes());

            // Close the stream
            outStream.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            Toast.makeText(context, context.getString(R.string.error_file_io_exception), Toast.LENGTH_LONG).show();
        }
    }

    /** Write the render (bitmap) to storage. */
    private void saveRender(Context context)
    {
        // Use the same directory as the shader source code
        String renderPath = path + "_render";

        try
        {
            // Create a File Output Stream to save the render
            FileOutputStream outStream = context.openFileOutput(renderPath, Context.MODE_PRIVATE);

            // Write the bitmap as a PNG
            render.compress(Bitmap.CompressFormat.PNG, 100, outStream);

            // Close the stream
            outStream.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /** Read the shader data and its render from storage */
    public void loadShaderAndRender(Context context)
    {
        if(isRequired)
        {
            ContentManager content = new ContentManager(context);
            loadShaderAsset(content);
            loadRenderAsset(context);
        }
        else
        {
            loadShader(context);
            loadRender(context);
        }
    }

    private void loadShader(Context context)
    {
        // Buffer
        String source = "";

        try
        {
            // Open file file in a read-only context
            FileInputStream inStream = context.openFileInput(path);

            byte[] buffer = new byte[1028];

            // Append each read to the source buffer
            int length = 0;
            while((length = inStream.read(buffer)) >= 0)
                source += new String(buffer, 0, length);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        // Create a shader from the source
        //shader = new Shader(new ContentManager(context), source);

        // Store the source code
        this.shaderSource = source;
    }

    private void loadShaderAsset(ContentManager content)
    {
        this.shaderSource = content.fileAsString(path);
    }

    private void loadRender(Context context)
    {
        render = BitmapFactory.decodeFile(context.getFilesDir() + "/" + path + "_render");
    }

    private void loadRenderAsset(Context context)
    {
        try {
            InputStream asset = context.getAssets().open(path + "_render");
            render = BitmapFactory.decodeStream(asset);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Creates a ContentValues object for this ShaderDescription.
     * @return The ContentValues object that represents this ShaderDescription.
     */
    public ContentValues getContentValues()
    {
        ContentValues values = new ContentValues();
        values.put(KEY_TITLE, title);
        values.put(KEY_ISREQUIRED, isRequired);
        values.put(KEY_PATH, path);
        return values;
    }

    /** @return  The shader's location on the filesystem */
    public String getPath() {
        return path;
    }

    /** @param path  The shader's location on the filesystem */
    public void setPath(String path) {
        this.path = path;
    }

    /** @return  True if the shader cannot be deleted */
    public boolean isRequired() {
        return isRequired;
    }

    /** @param isRequired True if the shader cannot be deleted */
    public void setRequired(boolean isRequired) {
        this.isRequired = isRequired;
    }

    /** @return  The name of the shader */
    public String getTitle() {
        return title;
    }

    /** @param title The name of the shader */
    public void setTitle(String title) {
        this.title = title;
    }

    /** @return The shader source that this metadata describes. */
    public String getShaderSource() {
        return shaderSource;
    }

    /** @param shaderSource The shader source that this metadata describes. */
    public void setShaderSource(String shaderSource) {
        this.shaderSource = shaderSource;
    }

    /** @return An image of a render that was drawn with this shader */
    public Bitmap getRender() {
        return render;
    }

    /** @param render An image of a render that was drawn with this shader */
    public void setRender(Bitmap render) {
        this.render = render;
    }
}
