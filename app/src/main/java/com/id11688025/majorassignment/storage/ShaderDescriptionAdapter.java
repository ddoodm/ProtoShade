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

import com.id11688025.majorassignment.R;

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
        return database.count() + 1;
    }

    @Override
    public Object getItem(int position)
    {
        if(position >= database.count())
            return null;

        ShaderDescription description = database.load(position);

        // Load data from the filesystem
        description.loadShaderAndRender(context);

        return description;
    }

    @Override
    public long getItemId(int position) {
        return position;
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

        // Set text of each list item View.
        tvTitle.setText(shader.getTitle());
        tvPath.setText(shader.getPath());

        // Set the preview icon
        ivPreview.setImageBitmap(shader.getRender());

        return itemView;
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
        request.addTestDevice("83995836C3F076F2374445E91DFFC7B9");

        adView.loadAd(request.build());
        return adView;
    }
}
