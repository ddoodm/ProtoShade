package com.id11688025.majorassignment.graphics;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLUtils;

import com.id11688025.majorassignment.Constants;
import com.id11688025.majorassignment.ContentManager;

import java.io.InputStream;

/**
 * A structure that defines a two-dimensional texture,
 * and its OpenGL texture name.
 */
public class Texture2D
{
    private SharedPreferences preferences;

    /** The texture 'name' supplied by OpenGL */
    private int glTextureName;

    private TextureFilteringMode textureFilteringMode = TextureFilteringMode.LINEAR;
    private TextureWrapMode textureWrapMode = TextureWrapMode.REPEAT;

    /**
     * Create a Texture2D from an OpenGL texture that has already been initialized.
     * @param glTextureName The resource name supplied by OpenGL
     */
    public Texture2D(final int glTextureName)
    {
        this.glTextureName = glTextureName;
    }

    /** Create a Texture2D from an Android resource.
     * @param content The content manager.
     * @param resourceID The Android resource ID of the texture bitmap.
     */
    public Texture2D(SharedPreferences preferences, ContentManager content, final int resourceID)
    {
        this.preferences = preferences;

        // Decode the bitmap from the Android resource ID provided
        Bitmap textureBmp = BitmapFactory.decodeResource(content.getResources(), resourceID);

        initialize(textureBmp);
    }

    public Texture2D(SharedPreferences preferences, InputStream texture)
    {
        this.preferences = preferences;

        // Decode the bitmap from the InputStream data
        Bitmap textureBmp = BitmapFactory.decodeStream(texture);

        initialize(textureBmp);
    }

    private void initialize(Bitmap textureBmp)
    {
        // Allocate a new texture in the OpenGL environment.
        int[] glTextureNames = new int[1];
        GLES20.glGenTextures(1, glTextureNames, 0);
        glTextureName = glTextureNames[0];

        // A texture name of '0' indicates a failure
        if(glTextureName == 0)
            throw new RuntimeException("OpenGL ES could not allocate a new texture name.");

        // Bind the texture name to the texture-2D binding point
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, glTextureName);

        // Configure filtering mode
        setTextureFilteringMode(TextureFilteringMode.values()[preferences.getInt(Constants.KEY_SAMPLER_FILTER_MODE, 0)]);
        setTextureWrapMode(TextureWrapMode.values()[preferences.getInt(Constants.KEY_SAMPLER_TEXTURE_WRAP_MODE, 0)]);

        // Provide the texel data (mipmap 0, bitmap, 0 border)
        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, textureBmp, 0);

        // Free the bitmap memory; OpenGL has it now
        textureBmp.recycle();
    }

    /** @return The OpenGL texture name for this texture */
    public int getTextureName()
    {
        return glTextureName;
    }

    public void setTextureFilteringMode(TextureFilteringMode mode)
    {
        this.textureFilteringMode = mode;

        int glFilteringMode = -1;
        switch (mode)
        {
            case LINEAR: glFilteringMode = GLES20.GL_LINEAR; break;
            case NEAREST: glFilteringMode = GLES20.GL_NEAREST; break;
        }

        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, glFilteringMode);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, glFilteringMode);
    }

    public void setTextureWrapMode(TextureWrapMode textureWrapMode)
    {
        this.textureWrapMode = textureWrapMode;

        int glWrapMode = -1;
        switch (textureWrapMode)
        {
            case REPEAT: glWrapMode = GLES20.GL_REPEAT;   break;
            case REPEAT_MIRROR: glWrapMode = GLES20.GL_MIRRORED_REPEAT;   break;
            case CLAMP_EDGE: glWrapMode = GLES20.GL_CLAMP_TO_EDGE;   break;
        }

        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, glWrapMode);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, glWrapMode);
    }
}
