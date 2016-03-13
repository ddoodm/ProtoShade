package com.id11688025.majorassignment;

import android.content.Context;
import android.graphics.Bitmap;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.id11688025.majorassignment.graphics.Model;
import com.id11688025.majorassignment.graphics.Texture2D;
import com.id11688025.majorassignment.math.Vector2;
import com.id11688025.majorassignment.objparser.OBJModel;
import com.id11688025.majorassignment.shaders.Shader;

/**
 * The specialized GL surface view which
 * is the drawing context for this application.
 */
public class CustomGLSurfaceView extends GLSurfaceView
{
    /** The renderer object that draws the scene onto the viewport */
    private CustomRenderer renderer;

    /** The previous frame's motion input coordinates */
    private Vector2 prevMotionPosition = null;

    public CustomGLSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);

        // Set the GL context version to ES 2.0
        setEGLContextClientVersion(2);

        // Set the surface view's renderer to our custom renderer.
        setRenderer(renderer = new CustomRenderer(context));

        // Draw only when the buffers are outdated
        setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
    }

    /**
     * Listens for touch events on the surface, and
     * updates the renderer accordingly.
     */
    @Override
    public boolean onTouchEvent (MotionEvent motion)
    {
        // Reset the previous position if the action is not "drag"
        if(motion.getAction() != MotionEvent.ACTION_MOVE)
            prevMotionPosition = null;

        switch(motion.getAction())
        {
            case MotionEvent.ACTION_POINTER_DOWN:
                // Perform a "pinch" (zoom) operation
                pinch_motion(motion);
                break;
            case MotionEvent.ACTION_MOVE:
                // Perform a "drag" operation
                drag_motion(motion);
                break;
        }

        return true;
    }

    /** Respond to simple dragging motion events */
    private boolean drag_motion(MotionEvent motion)
    {
        // Get the current touch position in unit screen space
        Vector2 position = new Vector2(
                motion.getX() / (float)this.getWidth() - 0.5f,
                motion.getY() / (float)this.getHeight() - 0.5f);

        if(prevMotionPosition == null)
            prevMotionPosition = position;

        renderer.dragInput(position, prevMotionPosition);

        prevMotionPosition = position;
        return true;
    }

    /** Respond to pinching motion events */
    private boolean pinch_motion(MotionEvent motion)
    {
        // Get the current touch positions in unit screen space
        Vector2 positionA = new Vector2(
                motion.getX(0) / (float)this.getWidth() - 0.5f,
                motion.getY(0) / (float)this.getHeight() - 0.5f);
        Vector2 positionB = new Vector2(
                motion.getX(1) / (float)this.getWidth() - 0.5f,
                motion.getY(1) / (float)this.getHeight() - 0.5f);

        // Get the distance between the two points ( |B-A| )
        float distance = Vector2.subtract(positionB, positionA).magnitude();

        // Notify renderer
        renderer.pinchInput(distance);

        return true;
    }

    /** Supply a fragment shader to use on the object (from the user).
     * @param fragmentSource The fragment shader source as a String.
     */
    public void provideUserShader (final String fragmentSource)
    {
        // The call must be executed on the renderer thread,
        // and so, call the function from within a Runnable on that thread.
        queueEvent(new Runnable() {
            @Override
            public void run() {
                renderer.provideUserShader(fragmentSource);
            }
        });
    }

    /** Supply an OBJ Model to render.
     * @param objModel The OBJ model to render
     */
    public void provideUserOBJModel (final ContentManager contentManager, final OBJModel objModel)
    {
        queueEvent(new Runnable() {
            @Override
            public void run() {
                // Get desired texture from content manager (preferences)
                Texture2D texture = contentManager.getTextureFileFromPreference();

                Model model = new Model(contentManager, objModel, texture);
                renderer.provideUserModel(model);
            }
        });
    }

    /** Specify whether the model should rotate automatically,
     * or as a result of touch input.
     * @param condition Automatic rotation if true.
     */
    public void rotateModelAutomatically(final boolean condition)
    {
        queueEvent(new Runnable() {
            @Override
            public void run() {
                renderer.rotateModelAutomatically(condition);
            }
        });
    }

    public Shader getCurrentShader()
    {
        return renderer.getCurrentShader();
    }

    /** Copy the display buffer to a bitmap. */
    public Bitmap renderToBitmap()
    {
        queueEvent(new Runnable() {
            @Override
            public void run() {
                renderer.renderToBitmap();
            }
        });

        // Wait for the image to render (should take 1/60 seconds)
        Bitmap render = null;
        while((render = renderer.getLastRenderedBitmap()) == null) ;

        return render;
    }

    /** Reset the renderer's timer. */
    public void resetTime()
    {
        queueEvent(new Runnable() {
            @Override
            public void run() {
                renderer.resetTime();
            }
        });
    }

    /**
     * Sets whether the animation is paused.
     * @param paused True if the animation is paused.
     */
    public void setPaused(final boolean paused)
    {
        queueEvent(new Runnable() {
            @Override
            public void run() {
                renderer.setPaused(paused);
            }
        });
    }

    /**
     * @return True if the animation is paused.
     */
    public boolean isPaused()
    {
        return renderer.isPaused();
    }

    /** @return The model's fragment shader log */
    public String getFragmentShaderLog()
    {
        return renderer.getFragmentShaderLog();
    }

    /** @return The renderer object that draws the scene onto the viewport */
    public CustomRenderer getRenderer() {
        return renderer;
    }
}
