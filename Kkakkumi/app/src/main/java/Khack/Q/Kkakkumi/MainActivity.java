package Khack.Q.Kkakkumi;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
    //<editor-fold desc="변수 선언">

    //<editor-fold desc="뒤로가기(back key) 관리">
    //뒤로가기(back key) 제어(종료)관리 클래스 객체
    private BackPressHandler backPressHandler;
    //</editor-fold>
    //</editor-fold>

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //<editor-fold desc="변수 값 입력">

        //<editor-fold desc="뒤로가기(back key) 관리">
        //뒤로가기(back key) 제어(종료)관리할 Activity가 현재 Activity라고 값을 입력
        backPressHandler = new BackPressHandler(this);
        //</editor-fold>
        //</editor-fold>

        //<editor-fold desc="교육 진행 화면 이동">
        // 교육 시작 화면으로 넘어가기 위한 클릭리스너
        Button btnEduStart = (Button) findViewById(R.id.btnStart);
        /**
         * 교육 선택에 따른 화면 activity 선택기능 구현 필요
         */
        btnEduStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), WashHandActivity.class);
                startActivity(intent);
            }
        });
        //</editor-fold>
    }

    //<editor-fold desc="뒤로가기(back key) 관리">
    @Override
    public void onBackPressed() {
        backPressHandler.onBackPressed();
    }
    //</editor-fold>
}
