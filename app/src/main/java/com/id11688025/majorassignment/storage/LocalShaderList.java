package com.id11688025.majorassignment.storage;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import com.id11688025.majorassignment.Constants;
import com.id11688025.majorassignment.MainActivity;

/**
 * The List Activity that displays locally stored shaders.
 */
public class LocalShaderList extends ListActivity
{
    private ShaderDescriptionAdapter shaderAdapter;
    private LocalShaderDatabase database;

    @Override protected void onCreate (Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        // Create the database wrapper
        database = new LocalShaderDatabase(this);

        // Create an adapter that reads data from the database
        shaderAdapter = new ShaderDescriptionAdapter(this, database);

        // Provide the List Adapter with an adapter
        setListAdapter(shaderAdapter);
    }

    @Override protected void onListItemClick(ListView l, View v, int position, long id)
    {
        // Get shader metadata
        // NEW: List item may be an ad, so consider that
        // TODO: Make ad handling better!
        ShaderDescription shaderDescription = (ShaderDescription)shaderAdapter.getItem(position);
        if(shaderDescription == null)
            return;

        String shaderSource = shaderDescription.getShaderSource();

        // Set the result data
        Intent resultData = new Intent();
        resultData.putExtra(Constants.EXTRA_SHADER_SOURCE, shaderSource);
        setResult(Constants.RESULT_CODE_LOADED, resultData);

        // Close this List Activity
        finish();
    }

    @Override public void onBackPressed()
    {
        // Set the result first
        setResult(Constants.RESULT_CODE_EXIT);

        super.onBackPressed();
    }
}
