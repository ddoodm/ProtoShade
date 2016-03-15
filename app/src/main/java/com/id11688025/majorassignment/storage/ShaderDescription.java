package com.id11688025.majorassignment.storage;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
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

        ContentManager.saveShader(context, this, shaderSource);
        ContentManager.saveRender(context, this, render);
    }

    /** Read the shader data and its render from storage */
    public void loadShaderAndRender(Context context)
    {
        if(isRequired)
        {
            shaderSource = ContentManager.loadShaderAsset(context, path);
            render = ContentManager.loadRenderAsset(context, path);
        }
        else
        {
            shaderSource = ContentManager.loadShader(context, path);
            render = ContentManager.loadRender(context, path);
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
