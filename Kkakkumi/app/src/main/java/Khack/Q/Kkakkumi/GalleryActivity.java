package Khack.Q.Kkakkumi;

import android.content.Intent;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import Khack.Q.Kkakkumi.JustClass.OnSwipeTouchListener;
import Khack.Q.Kkakkumi.JustClass.ValManagement;

public class GalleryActivity extends AppCompatActivity {

    LinearLayout gestureArea;

    PlayerView tv;
    SimpleExoPlayer player;
    DataSource.Factory factory;
    TextView tvName;

    ValManagement valM;
    List<File> filepathList;
    List<String> nameList;

    Integer index = 0;
    OnSwipeTouchListener swipeListener = new OnSwipeTouchListener() {
        public void onSwipeRight() {
            //Left > right
            if (index == 0)
                Toast.makeText(GalleryActivity.this, "첫 동영상 입니다.", Toast.LENGTH_SHORT).show();
            else if (index > 0) {//이전 영상으로 변경
                index -= 1;
                changeTV();
            }
        }

        public void onSwipeLeft() {
            //Right > left
            if (index == filepathList.size() - 1) Toast.makeText(GalleryActivity.this,
                    "마지막 동영상입니다.", Toast.LENGTH_SHORT).show();
            else if (index < filepathList.size() - 1) { //이후 동영상으로 변경
                index += 1;
                changeTV();
            }
        }
    };

    ImageView imgLeft, imgRight;
    Button btnfirst, btnlast, btndelete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

        gestureArea = findViewById(R.id.gallery_gestureArea);
        gestureArea.setOnTouchListener(swipeListener);

        valM = new ValManagement(this);
        filepathList = Arrays.asList(valM.getVideoList());
        Collections.reverse(filepathList);
        nameList = new ArrayList<>();
        for (File i : this.filepathList) {
            String[] tmp = i.getName().split("_");
            this.nameList.add(tmp[2].substring(0, tmp[2].indexOf("."))+" ("+tmp[0]+")");
        }

        tv = findViewById(R.id.gallery_tv);
        tv.setOnTouchListener(swipeListener);
        tvName = findViewById(R.id.gallery_tvName);
        player= ExoPlayerFactory.newSimpleInstance(this, new DefaultTrackSelector());
        //비디오데이터 소스를 관리하는 DataSource 객체를 만들어주는 팩토리 객체 생성
        factory= new DefaultDataSourceFactory(this,"Ex89VideoAndExoPlayer");

        imgLeft = findViewById(R.id.gallery_leftvideoimg);
        imgRight = findViewById(R.id.gallery_rightvideoimg);

        btndelete = findViewById(R.id.gallery_btndelete);
        btndelete.setOnClickListener(view -> {
            if(videodeletetry(filepathList.get(index).toString())){
                if(filepathList.size() != 0) changeTV();
                else novideo();
            }
        });
        btnfirst = findViewById(R.id.gallery_btnfirst);
        btnfirst.setOnClickListener(view -> {
            if(index != 0){
                index = 0;
                changeTV();
            }else Toast.makeText(GalleryActivity.this, "첫 동영상 입니다.", Toast.LENGTH_SHORT).show();
        });
        btnlast = findViewById(R.id.gallery_btnlast);
        btnlast.setOnClickListener(view -> {
            if (index != filepathList.size() - 1) {
                index = filepathList.size()-1;
                changeTV();
            } else Toast.makeText(GalleryActivity.this, "마지막 동영상입니다.", Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        //화면 보이기 시작할 경우
        if(filepathList.size() == 0) novideo();
        else{
            index = 0;
            changeTV();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        //화면 사라지는 경우
        videostop();
    }

    public void novideo(){
        Intent intent = new Intent(getApplicationContext(), VideoInfoActivity.class);
        startActivityForResult(intent,1);
        finish();
    }

    public void videostop(){
        //플레이어뷰 및 플레이어 객체 초기화
        tv.setPlayer(null);
        player.release();
        player=null;
    }

    public boolean videodeletetry(String path){
        try{
            File file = new File(path);
            if(file.exists()){
                file.delete();
                Toast.makeText(GalleryActivity.this, "비디오 삭제 성공!", Toast.LENGTH_SHORT).show();

                File[] paths = filepathList.toArray(new File[filepathList.size()]);
                String[] names = nameList.toArray(new String[nameList.size()]);
                filepathList = new ArrayList<>();
                for(File f : paths){
                    if(!f.toString().equals(path)) filepathList.add(f);
                }
                nameList = new ArrayList<>();
                for(int i = 0 ; i < names.length ; i++){
                    if(i != index) nameList.add(names[i]);
                }
                if(filepathList.size() == index) index -= 1;
                return true;
            }
        }catch (Exception e){
            Log.d("Video",e.getMessage());
            Toast.makeText(GalleryActivity.this, "비디오 삭제 실패!", Toast.LENGTH_SHORT).show();
        }return false;
    }

    public void changeTV(){
        if(tv.getPlayer() != null) videostop();
        player= ExoPlayerFactory.newSimpleInstance(this, new DefaultTrackSelector());
        //비디오데이터를 Uri로 부터 추출해서 DataSource객체 (CD or LP판 같은 ) 생성
        ProgressiveMediaSource mediaSource= new ProgressiveMediaSource.Factory(factory).
                                    createMediaSource(Uri.parse(filepathList.get(index).toString()));
        tv.setPlayer(player);
        player.prepare(mediaSource);

        tvName.setText(String.format("%s", nameList.get(index)));

        if(filepathList.size() == 1){
            //동영상 1개 (주변 썸네일 없음)
            imgLeft.setVisibility(View.INVISIBLE);
            imgRight.setVisibility(View.INVISIBLE);
        }else{
            //동영상 2개 이상
            if(index == 0){
                imgLeft.setVisibility(View.INVISIBLE);
                imgRight.setVisibility(View.VISIBLE);
                imgRight.setImageBitmap(ThumbnailUtils.createVideoThumbnail(filepathList.get(index+1).toString(),
                                                                MediaStore.Video.Thumbnails.FULL_SCREEN_KIND));
            }else if(index == filepathList.size()-1){
                imgRight.setVisibility(View.INVISIBLE);
                imgLeft.setVisibility(View.VISIBLE);
                imgLeft.setImageBitmap(ThumbnailUtils.createVideoThumbnail(filepathList.get(index-1).toString(),
                        MediaStore.Video.Thumbnails.FULL_SCREEN_KIND));
            }else{
                imgRight.setVisibility(View.VISIBLE);
                imgLeft.setVisibility(View.VISIBLE);
                imgRight.setImageBitmap(ThumbnailUtils.createVideoThumbnail(filepathList.get(index+1).toString(),
                        MediaStore.Video.Thumbnails.FULL_SCREEN_KIND));
                imgLeft.setImageBitmap(ThumbnailUtils.createVideoThumbnail(filepathList.get(index-1).toString(),
                        MediaStore.Video.Thumbnails.FULL_SCREEN_KIND));
            }
        }
    }
}