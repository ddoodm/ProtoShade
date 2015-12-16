package com.id11688025.majorassignment.objparser;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import com.id11688025.majorassignment.ContentManager;
import com.id11688025.majorassignment.CustomGLSurfaceView;
import com.id11688025.majorassignment.R;

/**
 * The AsyncTask that loads an OBJ model
 */
public class OBJLoaderTask extends AsyncTask<String, Integer, OBJModel>
{
    /** An application context */
    private Context context;

    /** The dialog on which a progress bar is displayed */
    private final ProgressDialog dialog;

    /** The Content Manager that will load asset data */
    private ContentManager contentManager;

    /** The view inside which the OpenGL rendering occurs */
    private CustomGLSurfaceView glSurface;

    public OBJLoaderTask(Context context, ContentManager contentManager, CustomGLSurfaceView glSurface)
    {
        this.context = context;
        this.contentManager = contentManager;
        this.glSurface = glSurface;

        // Configure the dialog
        dialog = new ProgressDialog(context);
        dialog.setMessage(context.getString(R.string.loading_obj));
        dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

    @Override
    protected OBJModel doInBackground(String... params) {
        String modelPath = params[0];

        // Display the name of the model that is being loaded
        String[] pathParts = modelPath.split("/");
        String message = String.format(
                context.getString(R.string.loading_obj),
                pathParts[pathParts.length-1]);
        dialog.setMessage(message);

        return new OBJModel(contentManager, modelPath, this);
    }

    @Override protected void onProgressUpdate(Integer... progress)
    {
        dialog.setProgress(progress[0]);
    }

    @Override protected void onPostExecute(OBJModel objModel)
    {
        // Hide the dialog
        dialog.dismiss();

        // Supply the model to the renderer
        glSurface.provideUserOBJModel(contentManager, objModel);
    }

    /** Allows external classes to set the dialog's maximum value.
     * @param maxProgress The maximum progress value.
     */
    public void setMaxProgress(int maxProgress)
    {
        dialog.setMax(maxProgress);
    }

    /** Allows external classes to publish progress through an object reference.
     * The publishProgress() method is protected, and as such,
     * it is not possible to call it through an object reference.
     * @param progress The progress value
     */
    public void setProgress(int progress)
    {
        publishProgress(progress);
    }
}