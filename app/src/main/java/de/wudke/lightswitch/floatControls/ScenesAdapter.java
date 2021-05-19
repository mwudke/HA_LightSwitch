package de.wudke.lightswitch.floatControls;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.List;

import de.wudke.lightswitch.HAUtils;
import de.wudke.lightswitch.R;
import de.wudke.lightswitch.entity.SceneEntity;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;


class ScenesAdapter extends RecyclerView.Adapter<ScenesAdapter.ViewHolder> {

    private List<SceneEntity> mData;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;
    private Context context;
    private HAUtils haUtils;

    // data is passed into the constructor
    ScenesAdapter(Context context, List<SceneEntity> data) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
        this.context = context;

        haUtils = new HAUtils(context);
    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.item_quickaction_scene, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final SceneEntity sceneEntity = mData.get(position);
        holder.myTextView.setText(sceneEntity.friendlyName);
        holder.myImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Callback callback = new Callback() {
                    @Override
                    public void onFailure(@NotNull Call call, IOException e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onResponse(@NotNull Call call, final Response response) throws IOException {
                        if (!response.isSuccessful()) {
                            throw new IOException("Unexpected code " + response);
                        } else {

                        }
                    }
                };

                haUtils.setScene(sceneEntity, callback);
            }
        });
//        holder.myImageButton.setImageResource(R.drawable.ic_home_assistant);
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return mData.size();
    }


    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView myTextView;
        ImageButton myImageButton;

        ViewHolder(View itemView) {
            super(itemView);
            myTextView = itemView.findViewById(R.id.textView_qa_scene);
            myImageButton = itemView.findViewById(R.id.imageButton_qa_scene);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }
    }

    // convenience method for getting data at click position
//    String getItem(int id) {
//        return mData.get(id);
//    }

    // allows clicks events to be caught
    void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }
}