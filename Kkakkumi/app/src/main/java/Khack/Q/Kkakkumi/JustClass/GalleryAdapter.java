package Khack.Q.Kkakkumi.JustClass;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.ui.PlayerControlView;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import Khack.Q.Kkakkumi.R;

public class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.GalleryViewHolder> {
    private Context context;
    private List<String> nameList;
    public File[] filepathList;
    private ValManagement valM;

    public List<PlayerView> tvList;
    public List<PlayerControlView> tvcontrollerList;

    public GalleryAdapter(Context context, ValManagement valM){
        this.context = context;
        this.valM = valM;
        List<File> list = Arrays.asList(valM.getVideoList());
        Collections.reverse(list);
        this.filepathList = list.toArray(new File[valM.getVideoList().length]); //valM.getVideoList();
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
        //return Integer.MAX_VALUE; >> 무한대로 돌리려고
    }

    @Override
    public void onBindViewHolder(@NonNull GalleryViewHolder holder, int position) {
        int index = position % nameList.size();
        String item = nameList.get(index);
        holder.tvName.setText(String.format("%s", item));/*
        holder.player = ExoPlayerFactory.newSimpleInstance(context, new DefaultTrackSelector());
        //플레이어뷰에게 플레이어 설정
        holder.tv.setPlayer(holder.player);
        //플레이어 컨트럴뷰와 플레이어 연동
        holder.tvcontroller.setPlayer(holder.player);*/
/*
        //비디오데이터 소스를 관리하는 DataSource 객체를 만들어주는 팩토리 객체 생성
        DataSource.Factory factory= new DefaultDataSourceFactory(context,"@string/app_name");
        //비디오데이터를 Uri로 부터 추출해서 DataSource객체 (CD or LP판 같은 ) 생성
        ProgressiveMediaSource mediaSource= new ProgressiveMediaSource.Factory(factory).createMediaSource(Uri.parse(filepathList[index].toString()));

        //만들어진 비디오데이터 소스객체인 mediaSource를
        //플레이어 객체에게 전당하여 준비하도록!![ 로딩하도록 !!]
        holder.player.prepare(mediaSource);

        //플레이어뷰 및 플레이어 객체 초기화
        holder.tv.setPlayer(null);
        holder.player.release();
        holder.player=null;
*/
        tvList.add(holder.tv);
        tvcontrollerList.add(holder.tvcontroller);
    }

    public class GalleryViewHolder extends RecyclerView.ViewHolder {
        TextView tvName;
        PlayerView tv;
        //실제 비디오를 플레이하는 객체의 참조 변수
        SimpleExoPlayer player;
        //컨트롤러 뷰 참조 변수
        PlayerControlView tvcontroller;

        public GalleryViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
            tv = itemView.findViewById(R.id.tv);
            tvcontroller = itemView.findViewById(R.id.tvcontroller);

            /*player = ExoPlayerFactory.newSimpleInstance(context, new DefaultTrackSelector());
            tv.setPlayer(player); //플레이어뷰에게 플레이어 설정
            tvcontroller.setPlayer(player); //플레이어 컨트럴뷰와 플레이어 연동*/
        }
    }
}
