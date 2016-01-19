package com.id11688025.majorassignment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import com.id11688025.majorassignment.graphics.Model;
import com.id11688025.majorassignment.graphics.TextureFilteringMode;
import com.id11688025.majorassignment.graphics.TextureWrapMode;
import com.id11688025.majorassignment.math.*;
import com.id11688025.majorassignment.shaders.Shader;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ShortBuffer;
import java.util.ArrayList;

/**
 * The class that makes rendering / drawing calls for
 * the custom surface view.
 */
public class CustomRenderer implements GLSurfaceView.Renderer
{
    /** An application context */
    private Context context;

    /** The viewer - computes and stores the projection and view matrices */
    private Camera camera;

    /** The single model in the world */
    private Model model;

    /** The time, in milliseconds since 1970, that the program was started */
    private double programStartTime = 0;

    /** The time, in seconds, since the program started */
    private float currentTime = 0;

    /** The size of the viewport in screen pixels. Updated when the viewport changes */
    private Vector2 viewportSize;

    /** Stores a "screenshot" of the display buffer when requested. */
    private Bitmap displayRender;

    /** Determines whether a new "screenshot" of the display buffer is available. */
    private boolean newScreenshot = false;

    /** A list of executable tasks to run in the OpenGL draw function */
    private ArrayList<Runnable> tasks;

    /** Is the animation paused? */
    private boolean isPaused = false;

    /**
     * Create a new custom OpenGL renderer
     * @param context The application context
     */
    public CustomRenderer(Context context)
    {
        this.context = context;

        tasks = new ArrayList<Runnable>();
    }

    @Override
    public void onSurfaceCreated(GL10 deprecated, EGLConfig config)
    {
        // Create a camera at (0,0,1.5) that faces (0,0,0).
        camera = new Camera(new Vector3(0,0,1.5f), Vector3.zero, Vector3.up);

        // Set the clear colour
        GLES20.glClearColor(0.1f, 0.1f, 0.1f, 1.0f);

        // Enable backface culling
        GLES20.glEnable(GLES20.GL_CULL_FACE);
        GLES20.glFrontFace(GLES20.GL_CCW);

        // Enable Z-buffering
        GLES20.glEnable(GLES20.GL_DEPTH_TEST);
        GLES20.glDepthFunc(GLES20.GL_LEQUAL);
    }

    @Override
    public void onSurfaceChanged(GL10 deprecated, int width, int height)
    {
        this.viewportSize = new Vector2(width, height);

        // Reshape the viewport to map normalized coordinates to device coordinates
        GLES20.glViewport(0, 0, width, height);

        // Set viewport dimensions for NDC coordinate mapping
        camera.setViewportWidth(width);
        camera.setViewportHeight(height);
        camera.createPerspectiveProjection();
    }

    @Override
    public void onDrawFrame(GL10 deprecated)
    {
        // Execute runnables, sent from other threads, on the OpenGL thread.
        for(int i=0; i<tasks.size(); i++)
        {
            tasks.get(i).run();
            tasks.remove(i);
        }

        // Clear the colour buffer with pre-set clear colour
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

        // Update time
        if(!isPaused)
            currentTime += 0.001f;

        // Be sure that the model is not still being parsed
        if(model != null)
        {
            model.update(currentTime);
            model.draw(camera, currentTime);
        }
    }

    /** Respond to drag motion input on the parent surface
     * @param position The touch input position as a unit vector.
     * @param prevPosition The touch input position as of the last call.*/
    public void dragInput (Vector2 position, Vector2 prevPosition)
    {
        // Be sure that the model is not still being parsed
        if(model == null)
            return;

        // Compute the difference (delta) between this and the previous position
        Vector2 posDelta = position.subtract(prevPosition);

        // Compute the two rotation angles (360-degrees across the view dimensions)
        float theta = posDelta.y * 2f * (float)Math.PI;
        float phi = posDelta.x * 2f * (float)Math.PI;

        model.transform(Matrix.multiply(
                Matrix.createRotationX(theta),
                Matrix.createRotationY(phi)
        ));
    }

    /** Respond to pinch motion input on the parent surface
     * @param distance The magnitude of the vector between the two fingers
     */
    public void pinchInput (float distance)
    {
        //TODO: Make pinch work, or kill it

        // Be sure that the model is not still being parsed
        if(model == null)
            return;

        //model.transform(Matrix.createScale(distance));
    }

    /** Supply a fragment shader to use on the object (from the user).
     * @param fragmentSource The fragment shader source as a String.
     * @return True if compilation was successful.
     */
    public boolean provideUserShader (final String fragmentSource)
    {
        // The shader should (must) be compiled during OpenGL draw time,
        // and so we must have onDraw() execute this task.
        tasks.add(new Runnable() {
            @Override
            public void run() {
                // Be sure that the model is not still being parsed
                if (model != null)
                {
                    boolean compilerErrors = model.provideUserShader(fragmentSource);

                    // Tell the main activity about compiler errors
                    Intent errorMessage = new Intent(MainActivity.TAG_COMPILER_ERROR);
                    errorMessage.putExtra(Constants.EXTRA_COMPILER_LOG, getFragmentShaderLog());
                    context.sendBroadcast(errorMessage);
                }
            }
        });

        return true;
    }

    /** Supply a Model to render.
     * @param model The model to render
     */
    public void provideUserModel(final Model model)
    {
        this.model = model;
    }

    /** Specify whether the model should rotate automatically,
     * or as a result of touch input.
     * @param condition Automatic rotation if true.
     */
    public void rotateModelAutomatically(final boolean condition)
    {
        model.rotateModelAutomatically(condition);
    }

    /** Reset the timer */
    public void resetTime()
    {
        currentTime = 0;
    }

    /**
     * Sets whether the animation is paused.
     * @param paused True if the animation is paused.
     */
    public void setPaused(boolean paused)
    {
        this.isPaused = paused;
    }

    /**
     * @return True if the animation is paused.
     */
    public boolean isPaused()
    {
        return isPaused;
    }

    /** @return The shader currently used to render the model */
    public Shader getCurrentShader()
    {
        return model.getCurrentShader();
    }

    /** @return The model's fragment shader compilation log */
    public String getFragmentShaderLog()
    {
        return model.getCurrentShader().getFragmentShaderLog();
    }

    /**
     * Renders the current OpenGL viewport to an Android bitmap.
     *
     * This function contains code from StackOverflow user "Gordon",
     * in response to the following StackOverflow question:
     * http://stackoverflow.com/questions/3310990/taking-screenshot-of-android-opengl
     *
     * I have attempted to provide better names, and comment where necessary, to
     * demonstrate my understanding of Gordon's code.
     *
     * @return The display rendered as a Bitmap.
     */
    public Bitmap renderToBitmap ()
    {
        int width = (int)viewportSize.x;
        int height = (int)viewportSize.y;
        int totalSize = width * height;

        // Create a Byte Buffer with size: (size of image) * (number of channels [RGBA])
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(totalSize * 4);
        byteBuffer.order(ByteOrder.nativeOrder());

        // Read the current display buffer from video memory, into the byte buffer
        GLES20.glReadPixels(
                0, 0,                   // Viewport position
                width, height,          // Viewport dimensions
                GLES20.GL_RGBA,         // Image format (Include alpha)
                GLES20.GL_UNSIGNED_BYTE,// Component format (one byte per channel)
                byteBuffer);            // Buffer to copy pixels into

        // Pack bytes into 4-byte integers
        int pixels[] = new int[totalSize];
        byteBuffer.asIntBuffer().get(pixels);

        // Create an Android Bitmap to store the pixel buffer
        displayRender = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);

        // Supply pixels to the bitmap
        displayRender.setPixels(
                pixels,                 // Pixel data
                totalSize - width,      // Offset - Start at last row
                -width,                 // Stride - Move up one row each stride
                0, 0, width, height);   // Fill image from (0,0) to (w,h)

        // Convert this BGR bitmap to RGB
        displayRender = BGRtoRGB(displayRender, totalSize);

        newScreenshot = true;
        return displayRender;
    }

    /** Convert a BGR-565 (Blue, Green, Red) bitmap to an RGB-565 bitmap.
     *
     * This function contains code from StackOverflow user "Gordon",
     * in response to the following StackOverflow question:
     * http://stackoverflow.com/questions/3310990/taking-screenshot-of-android-opengl
     *
     * I have attempted to provide better names, and comment where necessary, to
     * demonstrate my understanding of Gordon's code. */
    private Bitmap BGRtoRGB (Bitmap inBitmap, int size)
    {
        // The final converted pixels
        short[] finalPixels = new short[size];

        // Copy the pixels from the bitmap into the short buffer
        ShortBuffer shortBuffer = ShortBuffer.wrap(finalPixels);
        inBitmap.copyPixelsToBuffer(shortBuffer);

        // For each pixel
        for(int i=0; i < size; i++)
        {
            short pixel = finalPixels[i];

            // Flip channels by shifting bits
            finalPixels[i] =
                    (short) (((pixel&0x1f) << 11) | (pixel&0x7e0) | ((pixel&0xf800) >> 11));
        }

        // Reset the buffer pointer, and copy pixels back into the bitmap
        shortBuffer.rewind();
        inBitmap.copyPixelsFromBuffer(shortBuffer);

        return inBitmap;
    }

    /**
     * @return The most recently rendered "screenshot" of the display buffer.
     */
    public Bitmap getLastRenderedBitmap()
    {
        if(!newScreenshot)
            return null;

        newScreenshot = false;
        return displayRender;
    }

    public void setTextureFilteringMode(final TextureFilteringMode mode)
    {
        tasks.add(new Runnable() {
            @Override
            public void run() {
                model.setTextureFilteringMode(mode);
            }
        });
    }

    public void setTextureWrapMode(final TextureWrapMode textureWrapMode) {
        tasks.add(new Runnable() {
            @Override
            public void run() {
                model.setTextureWrapMode(textureWrapMode);
            }
        });
    }
}
