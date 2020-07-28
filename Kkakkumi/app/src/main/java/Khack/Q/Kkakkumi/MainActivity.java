package Khack.Q.Kkakkumi;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

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
    }

    //<editor-fold desc="뒤로가기(back key) 관리">
    @Override
    public void onBackPressed() {
        backPressHandler.onBackPressed();
    }
    //</editor-fold>
}
