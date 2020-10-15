package Khack.Q.Kkakkumi;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import android.app.Activity;
import android.content.Context;
import android.media.browse.MediaBrowser;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;

import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.DefaultMediaSourceFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.MediaSourceFactory;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.ui.PlayerControlView;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DataSpec;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.FileDataSource;
import com.google.android.exoplayer2.util.Util;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import Khack.Q.Kkakkumi.JustClass.GalleryAdapter;
import Khack.Q.Kkakkumi.JustClass.ValManagement;

public class GalleryActivity extends AppCompatActivity {

    GalleryAdapter pageradapter;
    ViewPager2 viewpager;
    Integer pageLimit;
    Float pageMargin, pageOffset;
    Context cont;
    DefaultRenderersFactory renderersFactory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

        cont = this;
        DefaultRenderersFactory renderersFactory = new DefaultRenderersFactory(this.getApplicationContext());

        //pageLimit = filesNameList.size()-1;
        pageMargin = (float) getResources().getDimensionPixelOffset(R.dimen.pageMargin);
        pageOffset = (float) getResources().getDimensionPixelOffset(R.dimen.offset);

        pageradapter = new GalleryAdapter(this, new ValManagement(this));
        viewpager = findViewById(R.id.viewPager2);
        viewpager.setAdapter(pageradapter);
        //viewpager.setCurrentItem(2000); 무한대로 돌리는것처럼 보이려고 큰 숫자 지정
        viewpager.setOffscreenPageLimit(3); // 넘길때 아이템 앞 뒤로 보이려고
        viewpager.setPageTransformer(new ViewPager2.PageTransformer() {
            @Override
            public void transformPage(@NonNull View page, float position) {
                float offset = position * -(2 * pageOffset + pageMargin);
                if(position < -1){
                    page.setTranslationX(-offset);
                }else if(position <= 1){
                    float scaleFactor = Math.max(0.7f, 1 - Math.abs(position - 0.14285715f));
                    page.setTranslationX(offset);
                    page.setScaleX(scaleFactor);
                    page.setAlpha(scaleFactor);
                }else{
                    page.setAlpha(0f);
                    page.setTranslationX(offset);
                }
            }
        });

        viewpager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                try{/*
                    for(int i = 0 ; i < pageradapter.tvList.size() ; i++){
                        PlayerView tv = pageradapter.tvList.get(i);
                        SimpleExoPlayer player = ExoPlayerFactory.newSimpleInstance(cont, new DefaultTrackSelector());
                        PlayerControlView tvcontroller = pageradapter.tvcontrollerList.get(i);

                        //비디오데이터 소스를 관리하는 DataSource 객체를 만들어주는 팩토리 객체 생성
                        DataSource.Factory factory= new DefaultDataSourceFactory(cont,"@string/app_name");
                        //비디오데이터를 Uri로 부터 추출해서 DataSource객체 (CD or LP판 같은 ) 생성
                        ProgressiveMediaSource mediaSource= new ProgressiveMediaSource.Factory(factory).createMediaSource(Uri.parse(pageradapter.filepathList[position].toString()));

                        if(tv == null) continue;
                        if( i == position ) {
                            tv.setPlayer(player);
                            tvcontroller.setPlayer(player);
                            player.prepare(mediaSource);
                        } else{
                            //right stop
                            tv.setPlayer(null);
                            player.release();
                            player = null;
                        }
                    }*/

                    PlayerView tv = pageradapter.tvList.get(position);
                    PlayerControlView tvcontroller = pageradapter.tvcontrollerList.get(position);

                    MediaSourceFactory mediaSourceFactory =
                            new DefaultMediaSourceFactory(new DefaultDataSourceFactory(cont, Util.getUserAgent(cont, "ExoPlayerInfo")))
                                                                        .setAdViewProvider(tv);

                    SimpleExoPlayer player = new SimpleExoPlayer.Builder(cont)
                            .build();//ExoPlayerFactory.newSimpleInstance(cont, new DefaultTrackSelector());

                    //비디오데이터 소스를 관리하는 DataSource 객체를 만들어주는 팩토리 객체 생성
                    DataSource.Factory factory= new DefaultDataSourceFactory(cont,"@string/app_name");
                    //비디오데이터를 Uri로 부터 추출해서 DataSource객체 (CD or LP판 같은 ) 생성
                    ProgressiveMediaSource mediaSource= new ProgressiveMediaSource.Factory(factory).createMediaSource(Uri.parse(pageradapter.filepathList[position].toString()));

                    tv.setPlayer(player);
                    //tvcontroller.setPlayer(player);
                    player.prepare(mediaSource);

                    if(position == 0 && pageradapter.tvList.get(position+1) != null){
                        pageradapter.tvList.get(position+1).getPlayer().release();
                        pageradapter.tvList.get(position+1).setPlayer(null);
                    }else if(position == pageradapter.tvList.size()-1 && pageradapter.tvList.get(position-1) != null){
                        pageradapter.tvList.get(position-1).getPlayer().release();
                        pageradapter.tvList.get(position-1).setPlayer(null);
                    }else{
                        if(pageradapter.tvList.get(position+1).getPlayer() != null){
                            pageradapter.tvList.get(position+1).getPlayer().release();
                            pageradapter.tvList.get(position+1).setPlayer(null);
                        }
                        if(pageradapter.tvList.get(position-1).getPlayer() != null){
                            pageradapter.tvList.get(position-1).getPlayer().release();
                            pageradapter.tvList.get(position-1).setPlayer(null);
                        }
                    }
                }catch (Exception e){
                    Log.e("Video", "Memory Error : " + e.getLocalizedMessage()+"\n"+ e.getMessage());
                }


            }
        });
    }
}