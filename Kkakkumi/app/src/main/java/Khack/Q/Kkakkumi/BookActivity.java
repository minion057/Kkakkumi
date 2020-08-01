package Khack.Q.Kkakkumi;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

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