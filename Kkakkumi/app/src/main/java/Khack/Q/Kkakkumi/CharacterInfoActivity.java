package Khack.Q.Kkakkumi;

import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Random;

import Khack.Q.Kkakkumi.JustClass.ValManagement;

public class CharacterInfoActivity extends AppCompatActivity {

    //<editor-fold desc="변수">
    // 클릭리스너를 위한 버튼 객체
    Button btnClose;

    TextView imgname;
    ImageView img;
    private int[] imgdrawList = {R.drawable.img_book_character_0, R.drawable.img_book_character_1,
                         R.drawable.img_book_character_2, R.drawable.img_book_character_3,
                         R.drawable.img_book_character_4, R.drawable.img_book_character_5};
    private String[] imgnameList = { "꼬꼬", "슈슈", "타타", "토토", "댕댕", "끼끼"};

    Integer imgnum = 0;

    ValManagement valm;
    //</editor-fold>

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_characterinfo);

        imgname = findViewById(R.id.characterinfo_name);
        img = findViewById(R.id.characterinfo_img);

        valm = new ValManagement(this);

        ArrayList<Integer> indexs = valm.getdbList();
        if(indexs == null) imgnum = new Random().nextInt(imgdrawList.length);
        else if (indexs.size() == 0){//(indexs.get(0) == imgdrawList.length) {
            // 모든 캐릭터를 다 얻은 상태 >> 모든 캐릭터 중 랜덤으로
            imgnum = new Random().nextInt(imgdrawList.length);
            TextView notice = findViewById(R.id.character_txt_notice);
            notice.setText("모든 스티커 획득!");
        }
        else imgnum = indexs.get(new Random().nextInt(indexs.size()));
        // 얻지 못한 캐릭터 인덱스 중 랜덤으로 뽑아 지정

        valm.setContents(imgnum); // dbTXT 수정
        imgname.setText(imgnameList[imgnum]);
        img.setImageResource(imgdrawList[imgnum]);

        //<editor-fold desc="팝업 페이지 닫기">
        btnClose = (Button) findViewById(R.id.character_pop_btn_Close);
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        //</editor-fold>
    }

    //<editor-fold desc="팝업 페이지 범위 밖 터치 제어">
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(event.getAction() == MotionEvent.ACTION_OUTSIDE) return false;
        return true;
    }
}