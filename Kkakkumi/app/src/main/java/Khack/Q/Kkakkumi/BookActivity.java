package Khack.Q.Kkakkumi;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import Khack.Q.Kkakkumi.JustClass.ValManagement;

/**
 * 보상 스티커 확인 페이지
 * 기능 1. 수집하지 않은 스티커는 까맣게 보임
 * 기능 2. 수집한 스티커는 온전하게 볼 수 있음
 */
public class BookActivity extends AppCompatActivity {
    //<editor-fold desc="변수 선언">
    private int[] imgblackList = {R.drawable.img_book_character_0_black, R.drawable.img_book_character_1_black,
                                  R.drawable.img_book_character_2_black, R.drawable.img_book_character_3_black,
                                  R.drawable.img_book_character_4_black, R.drawable.img_book_character_5_black};

    ArrayList<ImageView> imgList;

    //</editor-fold>

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book);

        imgList = new ArrayList<>();
        imgList.add(findViewById(R.id.book_img_c0));
        imgList.add(findViewById(R.id.book_img_c1));
        imgList.add(findViewById(R.id.book_img_c2));
        imgList.add(findViewById(R.id.book_img_c3));
        imgList.add(findViewById(R.id.book_img_c4));
        imgList.add(findViewById(R.id.book_img_c5));

        ValManagement valm = new ValManagement(this);
        ArrayList<Integer> indexs = valm.getdbList();
        // 기본 이미지 - origin >> 얻지 못한 스티커만 black으로 변경
        // null - db 이상 > 0으로 모두 초기화 > 모두 black으로
        if(indexs == null){
            for(int cnt = 0 ; cnt < imgList.size() ; cnt++){
                imgList.get(cnt).setImageResource(imgblackList[cnt]);
            }
        }else if (indexs.size() == 0){//(indexs.get(0) == imgList.size()){
            // 캐릭터를 다 얻은 상태 >> 바꿀 것이 없음
        } else { // 리스트에는 얻지 못한 캐릭터 인덱스만 있으므로 해당 이미지만 black으로 변경
            for(Integer cnt : indexs){
                imgList.get(cnt).setImageResource(imgblackList[cnt]);
            }
        }
    }
}