package com.id11688025.majorassignment;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.id11688025.majorassignment.objparser.OBJLoaderTask;
import com.id11688025.majorassignment.storage.LocalShaderList;
import com.id11688025.majorassignment.storage.SaveDialog;

import java.io.InputStream;

public class MainActivity extends Activity {

    /** The compiler error tag for the broadcast receiver */
    public static final String TAG_COMPILER_ERROR = "com.id11688025.majorassignment.COMPILER_ERROR";

    /** The Surface View which manages the renderer thread */
    private CustomGLSurfaceView glSurface;

    /** The EditText in which the user edits the GLSL code */
    private EditText codeEditor;

    /** The system that loads assets */
    private ContentManager contentManager;

    /** The system that responds to compiler error messages from the renderer. */
    private CompileErrorReceiver compileErrorReceiver;

    /** The object that stores application-wide preferences */
    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Obtain a preferece manager
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        //preferences = getPreferences(Context.MODE_PRIVATE);

        // Initialize the Content Manager
        contentManager = new ContentManager(this);

        // Store a reference to the code editor
        codeEditor = (EditText)findViewById(R.id.et_code);

        // Configure the code editor's listeners
        setEditorListeners();

        // Create and register the broadcast receiver for compiler errors
        compileErrorReceiver = new CompileErrorReceiver((Button)findViewById(R.id.btn_errors));
        registerReceiver(compileErrorReceiver, new IntentFilter(TAG_COMPILER_ERROR));

        // The renderer parameters may have already been set if this is a screen rotation
        if(glSurface == null)
            configureRenderer();

        configurePreferenceListener();

        // Hide title from the ActionBar
        getActionBar().setDisplayShowTitleEnabled(false);
    }

    /** Initialize the OpenGL surface and renderer with a model and a sample shader */
    private void configureRenderer()
    {
        // Store a reference to the OpenGL view (renderer thread owner)
        glSurface = (CustomGLSurfaceView) findViewById(R.id.gl_viewport);

        // Load the model specified in the preferences, or a bunny if none is specified
        loadModel(preferences.getString(Constants.PREFERENCE_MODEL, Constants.FALLBACK_MODEL));

        // Load a sample shader when the activity starts
        loadShaderAsset(Constants.FALLBACK_SHADER);
    }

    /** Configure the preference changed listener */
    private void configurePreferenceListener()
    {
        new SharedPreferences.OnSharedPreferenceChangeListener()
        {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                // If the model preference changes, we must invalidate the renderer
                if(key.equals(Constants.PREFERENCE_MODEL))
                    invalidateRenderer();
            }
        };
    }

    @Override protected void onResume()
    {
        super.onResume();

        // Re-configure if the renderer has been invalidated
        if(glSurface == null)
            configureRenderer();

        // Prevent the timer from losing precision
        glSurface.resetTime();
    }

    @Override protected void onDestroy()
    {
        // Unregister the broadcast receiver
        unregisterReceiver(compileErrorReceiver);

        super.onDestroy();
    }

    /** Configure the code editor's listeners */
    private void setEditorListeners()
    {
        // Text changed (compile) listener:
        codeEditor.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                provideUserShader();
            }
        });

        // Focus listener:
        final View dimmerView = findViewById(R.id.view_dimmer);
        codeEditor.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                // Make the dimmer visible when the code editor is focused
                int visibility = hasFocus ? View.VISIBLE : View.GONE;
                dimmerView.setVisibility(visibility);

                // Rotate the model automatically when the code editor is selected
                boolean rotatePreference = preferences.getBoolean(Constants.PREFERENCE_AUTO_ROTATE, true);
                glSurface.rotateModelAutomatically(hasFocus && rotatePreference);
            }
        });
    }

    /** Load an OBJ model using an AsyncTask, and display a progress dialog.
     * @param asset The path to the *.OBJ asset to load.
     */
    private void loadModel(String asset)
    {
        new OBJLoaderTask(this, contentManager, glSurface).execute(asset);
    }

    /** Load a shader from asset */
    private void loadShaderAsset(String asset)
    {
        // Load shader from asset
        String source = contentManager.fileAsString(asset);

        // Set code editor source
        codeEditor.setText(source);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.action_texture:
                // Show the sampler selection dialog
                new SamplerDialog(this, glSurface.getRenderer(), this, contentManager).show();
                break;

            case R.id.action_load:
                // Show the list of saved shaders
                Intent intent = new Intent(this, LocalShaderList.class);
                startActivityForResult(intent, Constants.REQUEST_CODE_LOAD_SHADER);
                break;

            case R.id.action_save:
                // Copy the display buffer to a Bitmap
                Bitmap render = glSurface.renderToBitmap();

                // Show the "save" dialog
                SaveDialog saveDialog = new SaveDialog(this, glSurface.getCurrentShader(), render);
                saveDialog.show();
                break;

            case R.id.action_settings:
                // Display the preferences activity
                startActivityForResult(new Intent(this, MainPreferenceActivity.class), Constants.REQUEST_CODE_MODEL_CHANGED);
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        // A shader has been selected from the "Load" List Activity
        switch(requestCode)
        {
            case Constants.REQUEST_CODE_LOAD_SHADER:
                if (resultCode == Constants.RESULT_CODE_LOADED) {
                    String shaderSource = data.getStringExtra(Constants.EXTRA_SHADER_SOURCE);
                    codeEditor.setText(shaderSource);
                }
                break;

            case Constants.REQUEST_CODE_MODEL_CHANGED:
                if(resultCode == Constants.RESULT_CODE_CHANGED)
                    invalidateRenderer();
                break;

            case Constants.REQUEST_CODE_PICK_SAMPLER_IMAGE:
                updateTextureImageFromIntent(resultCode, data);
                break;
        }
    }

    private void updateTextureImageFromIntent(int resultCode, Intent data)
    {
        if(resultCode == Activity.RESULT_OK)
        {
            InputStream inputStream = contentManager.getFileFromUri(data.getData());
            if(inputStream == null)
                return;

            glSurface.getRenderer().setTexture(inputStream);

            // Save image URI to preferences
            preferences.edit()
                    .putString(Constants.KEY_TEXTURE_IMAGE_PATH, data.getData().toString())
                    .apply();
        }
    }

    /** Called when the 'edit' button is pressed.
     * Displays the code editor. */
    public void onClick_edit(View view)
    {
        if(codeEditor.hasFocus())
            codeEditor.setVisibility(View.GONE);
        else
        {
            codeEditor.setVisibility(View.VISIBLE);
            codeEditor.requestFocus();
        }
    }

    @Override public void onBackPressed()
    {
        // Close the code editor first if it has focus
        if(codeEditor.hasFocus())
            codeEditor.setVisibility(View.GONE);
        else
            super.onBackPressed();
    }

    /** Compile the user's fragment shader, and use it
     * if it compiles correctly. */
    private void provideUserShader()
    {
        // Fragment shader source code
        String userSource = codeEditor.getText().toString();

        // Provide the source code to the custom OpenGL surface
        glSurface.provideUserShader(userSource);
    }

    /** Reset the renderer's timer */
    public void onClick_resetTime(View view)
    {
        glSurface.resetTime();
    }

    /** The onClick listener for the pause animation button */
    public void onClick_pauseTime(View view)
    {
        ImageButton button = (ImageButton)view;

        // Toggle pause
        glSurface.setPaused(!glSurface.isPaused());

        // Toggle button icon
        if(glSurface.isPaused())
            button.setImageResource(R.drawable.ic_pause);
        else
            button.setImageResource(R.drawable.ic_play);
    }

    /** Notifies the activity that the renderer is no longer valid, and should be re-initialized. */
    public void invalidateRenderer()
    {
        glSurface = null;
    }
}
