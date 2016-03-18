package com.id11688025.majorassignment.storage;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.ads.AdSize;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import com.id11688025.majorassignment.Constants;
import com.id11688025.majorassignment.R;

import java.io.IOException;

/**
 * A list adapter which can provide Shader Description entries
 * to a List View.
 */
public class ShaderDescriptionAdapter extends BaseAdapter
{
    /** An application context */
    private Context context;

    /** The database that stores shaders */
    private LocalShaderDatabase database;

    /** The stride of ads within the list */
    //private final int adStride = 8;
    private final int NUM_ADS = 1;

    /**
     * Create the Shader List adapter.
     * @param context An application context.
     * @param database The initialized database wrapper.
     */
    public ShaderDescriptionAdapter(Context context, LocalShaderDatabase database) {
        this.context = context;
        this.database = database;
    }

    @Override
    public int getCount() {
        return database.count() + NUM_ADS;
    }

    @Override
    public Object getItem(int position)
    {
        long id = getItemId(position);

        //if(id > database.count() - NUM_ADS)
        //    return null;

        ShaderDescription description = database.load(id);

        // Load data from the filesystem
        try { description.loadShaderAndRender(context); }
        catch (IOException e)
        {
            // TODO: Make a better database handler for deleted files
            description.setFileMissing(true);
            return description;
        }

        return description;
    }

    @Override
    public long getItemId(int position)
    {
        return database.getIdOfItemAtPosition(position);
    }

    @Override
    public View getView(int index, View currentView, ViewGroup parent)
    {
        View itemView;

        // If it's time for an ad, show one
        if(index >= database.count())
            return makeAd(index, currentView, parent);

        // If there are no existing views to recycle
        if(currentView == null || (currentView instanceof AdView))
        {
            // Create a new list item view by inflating the XML layout
            LayoutInflater inflater = (LayoutInflater)parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            // Do not supply the ViewGroup to inflate(), because we are returning the View ourselves
            itemView = inflater.inflate(R.layout.shader_row, null);
        }
        else
        {
            // Enough existing Views exist, and so we may recycle one
            itemView = currentView;
        }

        // Substitute data into the row
        itemView = substituteData(index, itemView, parent);

        return itemView;
    }

    /**
     * Substitute data into a row's View.
     * @param index The index of the current row
     * @param itemView The View which contains the row
     * @param parent The container view of the row
     * @return The View with substituted data
     */
    private View substituteData(final int index, View itemView, ViewGroup parent)
    {
        // Obtain the Shader that corresponds to the database entry at 'index'.
        ShaderDescription shader = (ShaderDescription)getItem(index);

        // Obtain the list item's Views which should be written to.
        ImageView ivPreview = (ImageView)itemView.findViewById(R.id.iv_preview);
        TextView tvTitle = (TextView)itemView.findViewById(R.id.tv_title);
        TextView tvPath = (TextView)itemView.findViewById(R.id.tv_path);

        // TODO: Work out what's happening with ID VS position, because we crash at ID 4 with null reference

        // Set text of each list item View.
        tvTitle.setText(shader.getTitle());
        tvPath.setText(shader.getPath());

        // Set the preview icon
        ivPreview.setImageBitmap(shader.getRender());

        // Handle items with errors
        handleErrorsForItem(shader, itemView);

        return itemView;
    }

    private void handleErrorsForItem(ShaderDescription shader, View itemView)
    {
        TextView errorTxt = (TextView)itemView.findViewById(R.id.tv_error);

        if(shader.isFileMissing())
        {
            errorTxt.setText(context.getString(R.string.source_file_missing));
            errorTxt.setVisibility(View.VISIBLE);
            return;
        }

        errorTxt.setVisibility(View.INVISIBLE);
    }

    private View makeAd(int index, View currentView, ViewGroup parent)
    {
        // Reuse existing AdViews
        if(currentView instanceof AdView)
            return currentView;

        // Create an ad
        AdView adView = new AdView(context);
        adView.setAdSize(com.google.android.gms.ads.AdSize.BANNER);
        adView.setAdUnitId(context.getString(R.string.admob_banner_id));

        // Set up some display stuff
        float density = context.getResources().getDisplayMetrics().density;
        int height = Math.round(AdSize.BANNER.getHeight() * density);
        AbsListView.LayoutParams params = new AbsListView.LayoutParams(
                AbsListView.LayoutParams.FILL_PARENT,
                height);
        adView.setLayoutParams(params);

        // Set up the ad request, and exclude some devices
        AdRequest.Builder request = new AdRequest.Builder();
        request.addTestDevice(AdRequest.DEVICE_ID_EMULATOR);
        request.addTestDevice(Constants.TEST_ANDROID_DEVICE_ID);

        adView.loadAd(request.build());
        return adView;
    }
}
