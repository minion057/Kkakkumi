package Khack.Q.Kkakkumi;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

public class EduActivity extends AppCompatActivity {
    //<editor-fold desc="변수">
    TextView test;
    Intent intent;
    int menu;
    //</editor-fold>

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edu);

        //<editor-fold desc="변수 값 입력">
        test = (TextView)findViewById(R.id.edu_txt_title);
        intent = getIntent();
        menu = intent.getIntExtra("menu", -1);
        switch (menu){
            case 0: //양치
                test.setText("양치");
                break;
            case 1: //손씻기
                test.setText("손씻기");
                break;
            case 2: //기침막기
                test.setText("기침막기");
                break;
            case 3: //마스크
                test.setText("마스크");
                break;
            default:
                test.setText("Error");
                Log.d("V_Error", "menu : "+ Integer.toString(menu));
                break;
        }
        //</editor-fold>


    }
}