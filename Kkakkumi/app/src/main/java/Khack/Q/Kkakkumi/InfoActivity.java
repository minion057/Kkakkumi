package Khack.Q.Kkakkumi;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

public class InfoActivity extends AppCompatActivity {

    //<editor-fold desc="변수">
    // 클릭리스너를 위한 버튼 객체
    Button btnClose;
    //</editor-fold>

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        //<editor-fold desc="팝업 페이지 닫기">
        // 교육 시작 화면으로 넘어가기 위한 클릭리스너
        btnClose = (Button) findViewById(R.id.pop_btn_Close);
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