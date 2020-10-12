package Khack.Q.Kkakkumi.JustClass;

import android.content.Context;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

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
import java.util.List;

import Khack.Q.Kkakkumi.R;

public class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.GalleryViewHolder> {
    private Context context;
    private List<String> nameList;
    private File[] filepathList;
    private ValManagement valM;

    //public GalleryAdapter(Context context, List<String> list, File[] file, int cnt) {
    public GalleryAdapter(Context context, ValManagement valM){
        this.context = context;
        this.valM = valM;
        this.filepathList = valM.getVideoList();
        this.nameList = new ArrayList<>();
        for (File i : this.filepathList) {
            String[] tmp = i.getName().split("_");
            this.nameList.add(tmp[2].substring(0, tmp[2].indexOf("."))+" ("+tmp[0]+")");
        }
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
        holder.tvName.setText(String.format("%s", item));
        holder.player = ExoPlayerFactory.newSimpleInstance(context, new DefaultTrackSelector());
        //플레이어뷰에게 플레이어 설정
        holder.tv.setPlayer(holder.player);
        //플레이어 컨트럴뷰와 플레이어 연동
        holder.tvcontroller.setPlayer(holder.player);

        //비디오데이터 소스를 관리하는 DataSource 객체를 만들어주는 팩토리 객체 생성
        DataSource.Factory factory= new DefaultDataSourceFactory(context,"Ex89VideoAndExoPlayer");
        //비디오데이터를 Uri로 부터 추출해서 DataSource객체 (CD or LP판 같은 ) 생성
        ProgressiveMediaSource mediaSource= new ProgressiveMediaSource.Factory(factory).createMediaSource(Uri.parse(filepathList[index].toString()));

        //만들어진 비디오데이터 소스객체인 mediaSource를
        //플레이어 객체에게 전당하여 준비하도록!![ 로딩하도록 !!]
        holder.player.prepare(mediaSource);
/*
        //비디오뷰의 재생, 일시정지 등을 할 수 있는 '컨트롤바'를 붙여주는 작업
        holder.vBanner.setMediaController(new MediaController(context));

        //VideoView가 보여줄 동영상의 경로 주소(Uri) 설정하기
        holder.vBanner.setVideoURI(Uri.parse(filepathList[index].toString()));
        //holder.vBanner.setVideoURI(Uri.parse("android.resource://Khack.Q.Kkakumi/raw/tess.mp4"));
        //동영상을 읽어오는데 시간이 걸리므로..
        //비디오 로딩 준비가 끝났을 때 실행하도록..
        //리스너 설정
        holder.vBanner.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                //비디오 시작
                holder.vBanner.start();
            }
        });*/
    }

    public class GalleryViewHolder extends RecyclerView.ViewHolder {
        TextView tvName;
        PlayerView tv;
        //실제 비디오를 플레이하는 객체의 참조 변수
        SimpleExoPlayer player;
        //컨트롤러 뷰 참조 변수
        PlayerControlView tvcontroller;
        //VideoView vBanner;

        public GalleryViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
            tv = itemView.findViewById(R.id.tv);
            tvcontroller = itemView.findViewById(R.id.tvcontroller);
        }
    }
}
