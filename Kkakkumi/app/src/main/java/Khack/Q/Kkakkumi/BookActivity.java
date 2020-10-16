package Khack.Q.Kkakkumi;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

/**
 * 보상 스티커 확인 페이지
 * 기능 1. 수집하지 않은 스티커는 까맣게 보임
 * 기능 2. 수집한 스티커는 온전하게 볼 수 있음
 */
public class BookActivity extends AppCompatActivity {
    //<editor-fold desc="변수 선언">

    //<editor-fold desc="gif 재생">
    //gif 재생을 위해 ImageView 객체
    //ImageView img_c1;
    //</editor-fold>

    List<ImageView> imgCList;

    //</editor-fold>

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book);

        //<editor-fold desc="gif 재생">
        // gif 재생을 위해 ImageView 요소 찾아서 넣기
        //img_c1 = (ImageView)findViewById(R.id.book_img_c1);
        // gif 재생
        //Glide.with(this).load(R.raw.sheep_flod).into(img_c1);
        //</editor-fold>

        imgCList = new ArrayList<>();
        imgCList.add(findViewById(R.id.book_img_c0));
        imgCList.add(findViewById(R.id.book_img_c1));
        imgCList.add(findViewById(R.id.book_img_c2));
        imgCList.add(findViewById(R.id.book_img_c3));
        imgCList.add(findViewById(R.id.book_img_c4));
        imgCList.add(findViewById(R.id.book_img_c5));
        /*
        *
6-1. 도감에서 1인 데이터만 그림 변경 - 생동감있는 친구로 (바꾸면 imgCList에서 지우기)
6-2. imgCList 돌려서 클릭 이벤트 생성 ( 교육을 진행해 얻으라고 팝업 띄우기 )
        * */
    }
}