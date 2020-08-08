package Khack.Q.Kkakkumi;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
/**
 * 보상 스티커 확인 페이지
 * 기능 1. 수집하지 않은 스티커는 까맣게 보임
 * 기능 2. 수집한 스티커는 온전하게 볼 수 있음
 */
public class BookActivity extends AppCompatActivity {
    //<editor-fold desc="변수 선언">

    //<editor-fold desc="gif 재생">
    //gif 재생을 위해 ImageView 객체
    ImageView img_c1;
    //</editor-fold>

    //</editor-fold>

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book);

        //<editor-fold desc="gif 재생">
        // gif 재생을 위해 ImageView 요소 찾아서 넣기
        img_c1 = (ImageView)findViewById(R.id.book_img_c1);
        // gif 재생
        Glide.with(this).load(R.raw.sheep_flod).into(img_c1);
        //</editor-fold>
    }
}