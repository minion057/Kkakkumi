package Khack.Q.Kkakkumi;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

public class SplashActivity extends AppCompatActivity {
    //<editor-fold desc="변수 선언">
    //splash 화면에 있는 요소 - 애니메이션을 연결할 TextView
    //TextView txt_title1, txt_title2;
    //AnimationListener 안에서 다음 페이지로 넘어가기 위해 필요한 Intent 생성을 못해서 따로 변수 생성
    Intent inte;
    //애니메이션 xml 변수
    Animation ani_txt1, ani_txt2, ani_imgup, ani_imgdown;

    ImageView txt_title1, txt_title2, img1, img2, img3;
    //</editor-fold>

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        //<editor-fold desc="변수 값 입력">

        //splash 화면에 있는 요소를 찾아 입력
        txt_title1 = findViewById(R.id.splash_title1);
        txt_title2 = findViewById(R.id.splash_title2);
        img1 = findViewById(R.id.splash_img1);
        img2 = findViewById(R.id.splash_img2);
        img3 = findViewById(R.id.splash_img3);

        img1.setVisibility(View.INVISIBLE);
        img2.setVisibility(View.INVISIBLE);
        img3.setVisibility(View.INVISIBLE);

        //AnimationListener 안에서 MainActivity로 넘어가기 위해 필요한 intent 값
        inte = new Intent(this, MainActivity.class);

        //애니메이션 xml 연결
        ani_txt1 = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.anim_splash_title1);
        ani_txt2 = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.anim_splash_title2);
        ani_imgup = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.anim_splash_imgup);
        ani_imgdown = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.anim_splash_imgdown);
        //</editor-fold>

        //<editor-fold desc="애니메이션 시작하고 메인페이지로 넘어가기">
        // 애니메이션을 각 요소에 연결시켜 실행
        txt_title1.setAnimation(ani_txt1);
        txt_title2.setAnimation(ani_txt2);
        ani_txt2.setAnimationListener(new Animation.AnimationListener() {
            /*애니메이션 끝*/
            public void onAnimationEnd(Animation animation) {
                // 애니메이션 끝내고 페이지가 급하게 넘어가는 감이 있어서 1초 지연
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    public void run() {
                        startActivity(inte);
                        finish();
                    }
                }, 1500);
            }

            /*애니메이션 시작*/
            public void onAnimationStart(Animation animation) {
            }

            /*애니메이션 반복*/
            public void onAnimationRepeat(Animation animation) {
            }
        });
        new Handler().postDelayed(new Runnable() {
            public void run() {
                img1.setAnimation(ani_imgup);
                img1.setVisibility(View.VISIBLE);
                new Handler().postDelayed(new Runnable() {
                    public void run() {
                        img3.setAnimation(ani_imgdown);
                        img3.setVisibility(View.VISIBLE);
                        new Handler().postDelayed(new Runnable() {
                            public void run() {
                                img2.setAnimation(ani_imgup);
                                img2.setVisibility(View.VISIBLE);
                            }
                        }, 400);
                    }
                }, 400);
            }
        }, 400);
        //</editor-fold>
    }
}
