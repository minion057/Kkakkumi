package Khack.Q.Kkakkumi;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Bundle;
import android.os.Environment;
import android.view.View;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

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
    }

}