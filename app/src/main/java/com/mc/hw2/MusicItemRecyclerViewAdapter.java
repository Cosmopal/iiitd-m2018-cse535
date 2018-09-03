package com.mc.hw2;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.mc.hw2.MusicItemFragment.OnListFragmentInteractionListener;
import com.mc.hw2.dummy.DummyContent.DummyItem;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link DummyItem} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class MusicItemRecyclerViewAdapter extends RecyclerView.Adapter<MusicItemRecyclerViewAdapter.ViewHolder> {

    private static final String TAG = "MusicRvA";
    private final List<MusicItem> mValues;
    private final OnListFragmentInteractionListener mListener;
    private MusicItem toPlay;
    private int toPlayPosition = -1;
    private int playingPosition = -1;
    private MusicItem currentlyPlaying;

    public MusicItemRecyclerViewAdapter(Context context, OnListFragmentInteractionListener listener) {
        List<MusicItem> items = new ArrayList<MusicItem>();
        items.add(new MusicItem("Sweet Flute Ringtone", MusicItem.TYPE_RES, R.raw.flute_ring));
        items.add(new MusicItem("Heart touch Flute Ringtone", MusicItem.TYPE_RES, R.raw.flute_ring_2));
        boolean downloaded = false;

        File sampleFile = context.getFileStreamPath("sampleFile.mp3");
        if (sampleFile.exists())
            downloaded = true;

        MusicItem sampleFileItem;
        if (downloaded){
            sampleFileItem = new MusicItem("Sample File", MusicItem.TYPE_STORE, sampleFile.toURI().toString());
        }
        else{
            sampleFileItem = new MusicItem("Sample File", MusicItem.TYPE_ONLINE, "http://faculty.iiitd.ac.in/~mukulika/s1.mp3");
        }
        items.add(sampleFileItem);
        //TODO: Add music resource files in items list
        //TODO: Search for music files in specified storage location.
        mValues = items;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_musicitem, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.setData(mValues.get(position), playingPosition==position);

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected
                    toPlay = holder.mItem;
                    toPlayPosition = position;
                    mListener.onListFragmentItemClick(holder.mItem);
                }
            }
        });
    }

    public void updatePlayingItem(MusicItem item){
        if (item==null || item.equals(toPlay)){
            //Play command successful, update list
            int prev = playingPosition;
            playingPosition = toPlayPosition;
            currentlyPlaying = toPlay;
            if (prev>=-1){
                notifyItemChanged(prev);
            }

            toPlay = null;
            toPlayPosition = -1;
            notifyItemChanged(playingPosition);
        }
    }

    public void onOnlineFileDownloaded(Context context, MusicItem item){
        int position = mValues.indexOf(item);
        Log.d(TAG,"the sample item is downloaded");
        //Check if true:
        File sampleFile = context.getFileStreamPath("sampleFile.mp3");
        if (sampleFile.exists()){
            if (item.name.equals("Sample File")) {
                mValues.remove(item);
                item.type = MusicItem.TYPE_STORE;
                item.path = sampleFile.toURI().toString();
                mValues.add(position,item);
                notifyItemChanged(position);
            }
        }
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mName;
        public final ImageView mBtn;
        public MusicItem mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mName = (TextView) view.findViewById(R.id.item_name);
            mBtn = (ImageView) view.findViewById(R.id.music_item_btn);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mName.getText() + "'";
        }

        public void setData(MusicItem data, boolean isPlaying) {
            this.mItem = data;
            this.mName.setText(data.name);
            int btn_res = R.drawable.baseline_play_circle_filled_black_36dp;
            /*if (data.type == MusicItem.TYPE_STORE){
                btn_res = R.drawable.ic_home_black_24dp;
            }*/
            if (data.type == MusicItem.TYPE_ONLINE){
                btn_res = R.drawable.baseline_save_alt_black_36dp;
            }
            if (isPlaying){
                btn_res = R.drawable.baseline_pause_circle_filled_black_36dp;
            }
            this.mBtn.setImageResource(btn_res);
        }
    }
}
