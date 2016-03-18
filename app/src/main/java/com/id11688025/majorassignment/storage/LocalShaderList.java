package com.id11688025.majorassignment.storage;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import com.id11688025.majorassignment.Constants;
import com.id11688025.majorassignment.ContentManager;
import com.id11688025.majorassignment.MainActivity;
import com.id11688025.majorassignment.R;

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

        // Handle broken links to missing files
        if(shaderDescription.isFileMissing())
        {
            handleMissingItemClick(position, shaderDescription);
            return;
        }

        String shaderSource = shaderDescription.getShaderSource();

        // Set the result data
        Intent resultData = new Intent();
        resultData.putExtra(Constants.EXTRA_SHADER_SOURCE, shaderSource);
        setResult(Constants.RESULT_CODE_LOADED, resultData);

        // Close this List Activity
        finish();
    }

    private void handleMissingItemClick(final int position, ShaderDescription shaderDescription)
    {
        String shaderDirectory = ContentManager.getShaderStoreDirectory().toString();
        String errorMessage = String.format(
                getString(R.string.error_format_shader_source_not_found),
                shaderDescription.getPath(),
                shaderDirectory
        );

        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.source_file_missing))
                .setMessage(errorMessage)
                .setNegativeButton(R.string.cancel, null)
                .setNeutralButton(getString(R.string.delete_entry), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        database.remove(position);
                        shaderAdapter.notifyDataSetChanged();
                    }
                })
                .show();
    }

    @Override public void onBackPressed()
    {
        // Set the result first
        setResult(Constants.RESULT_CODE_EXIT);

        super.onBackPressed();
    }

    @Override public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.shader_menu_actionbar, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.action_shader_list_help:
                showHelp();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void showHelp()
    {
        String message = String.format(
                getString(R.string.shader_list_help_message),
                ContentManager.getShaderStoreDirectory().toString());

        new AlertDialog.Builder(this)
                .setMessage(message)
                .setPositiveButton(R.string.ok, null)
                .show();
    }
}
