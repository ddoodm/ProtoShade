package com.id11688025.majorassignment;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.widget.Toast;

import com.id11688025.majorassignment.graphics.Texture2D;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * A mechanism for loading various types of
 * game resources from the Android system.
 */
public class ContentManager
{
    /** The application context */
    private Context context;
    private SharedPreferences preferences;

    /**
     * Create a context manager.
     * @param context An application context.
     */
    public ContentManager(Context context)
    {
        this.context = context;
        this.preferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    /**
     * Get the input stream for a resource.
     * @param path The resource path to the file to be read.
     * @return The input stream for the file.
     */
    public InputStream getResourceStreamFromPath(String path)
    {
        try
        {
            return context.getAssets().open(path);
        }
        catch (IOException ex)
        {
            ex.printStackTrace();
            return null;
        }
    }

    public InputStream getFileFromUri(Uri uri)
    {
        InputStream inputStream;

        if(uri.toString().equals(""))
        {
            Toast.makeText(context, context.getString(R.string.pref_texture_could_not_be_loaded), Toast.LENGTH_LONG).show();
            return null;
        }

        try {
            inputStream = context.getContentResolver().openInputStream(uri);
        } catch (Exception e) {
            Toast.makeText(context, context.getString(R.string.pref_texture_could_not_be_loaded), Toast.LENGTH_LONG).show();
            return null;
        }

        return inputStream;
    }

    /**
     * Get the input stream reader for a resource.
     * @param path The resource path to the file to be read.
     * @return The input stream reader for the file.
     */
    public InputStreamReader getInputStreamReader(String path)
    {
        return new InputStreamReader(getResourceStreamFromPath(path));
    }

    /**
     * Get a buffered reader for a file.
     * @param path The resource path to the file to be read.
     * @return A buffered reader that reads the file.
     */
    public BufferedReader getBufferedReader(String path)
    {
        return new BufferedReader(getInputStreamReader(path));
    }

    /**
     * Read a resource file as a String.
     * @param path The The resource path to the file to be read.
     * @return A string that contains the file's contents.
     */
    public String fileAsString(String path)
    {
        BufferedReader reader = getBufferedReader(path);
        String result = "", line;

        // Read in each line of the file
        try {
            while ((line = reader.readLine()) != null)
                result += line + "\n";
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        return result;
    }

    /** Count the number of lines in a plaintext file.
     * @param path The resource path to the file to be counted.
     * @return The number of lines in the file.
     */
    public int countLines(String path)
    {
        BufferedReader reader = getBufferedReader(path);

        int lineCount = 0;

        try {
            while(reader.readLine() != null)
                lineCount++;
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return lineCount;
    }

    /**
     * Construct a filename from a human-written title.
     * Removes special characters, converts to lower case,
     * replaces spaces with underscores.
     * @param title A human-written title.
     * @return A Unix-friendly filename.
     */
    public static String filenameFromTitle(String title)
    {
        /* Filename is the lower-case shader title,
        with spaces and special characters replaced
        with underscores. */

        // Replace all but "a-z", "A-Z", "0-9" and '-' with '_'
        return title.toLowerCase().replaceAll("[^a-zA-Z0-9\\-]", "_");
    }

    /**
     * Determines whether a given filename is valid.
     * @param filename The filename to check.
     * @return True if the filename is invalid.
     */
    public static boolean invalidFilename(String filename)
    {
        return filename.isEmpty() || filename.replaceAll("_", "").isEmpty();
    }

    public Resources getResources()
    {
        return context.getResources();
    }

    public Texture2D loadTexture2D(final int resourceID)
    {
        return new Texture2D(preferences, this, resourceID);
    }

    public Texture2D loadTexture2D(InputStream texture)
    {
        return new Texture2D(preferences, texture);
    }

    public Texture2D loadTexture2D(Uri uri)
    {
        return new Texture2D(preferences, getFileFromUri(uri));
    }

    /**
     * Gets the user's texture file from preferences
     * @return The desired texture as a Texture2D
     */
    public Texture2D getTextureFileFromPreference()
    {
        InputStream texStream;
        try {
            Uri textureUri = Uri.parse(preferences.getString(Constants.KEY_TEXTURE_IMAGE_PATH, ""));
            texStream = context.getContentResolver().openInputStream(textureUri);
        } catch (Exception e) {
            Toast.makeText(context, context.getString(R.string.pref_texture_could_not_be_loaded), Toast.LENGTH_LONG).show();

            // Return default concrete texture
            return new Texture2D(preferences, R.drawable.concrete);
        }

        return new Texture2D(preferences, texStream);
    }
}
