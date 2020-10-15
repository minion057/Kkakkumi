package Khack.Q.Kkakkumi.JustClass;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.ui.PlayerControlView;
import com.google.android.exoplayer2.ui.PlayerView;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import Khack.Q.Kkakkumi.R;

public class GalleryAdapter2 extends RecyclerView.Adapter<GalleryAdapter2.GalleryViewHolder> {
    private Context context;
    private List<String> nameList;
    public List<File> filepathList;
    private ValManagement valM;

    public List<PlayerView> tvList;
    public List<PlayerControlView> tvcontrollerList;

    public GalleryAdapter2(Context context, ValManagement valM){
        this.context = context;
        this.valM = valM;
        this.filepathList = Arrays.asList(valM.getVideoList());
        Collections.reverse(this.filepathList);
        this.nameList = new ArrayList<>();
        for (File i : this.filepathList) {
            String[] tmp = i.getName().split("_");
            this.nameList.add(tmp[2].substring(0, tmp[2].indexOf("."))+" ("+tmp[0]+")");
        }
        this.tvList = new ArrayList<>();
        this.tvcontrollerList = new ArrayList<>();
    }

    @NonNull
    @Override
    public GalleryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.activity_gallery_item, parent, false);
        return new GalleryViewHolder(view);
    }

    @Override
    public int getItemCount() {
        return nameList.size();
    }

    @Override
    public void onBindViewHolder(@NonNull GalleryViewHolder holder, int position) {
        int index = position % nameList.size();
        String item = nameList.get(index);
        holder.tvName.setText(String.format("%s", item));


    }

    public class GalleryViewHolder extends RecyclerView.ViewHolder {
        TextView tvName;

        public GalleryViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
        }
    }
}
