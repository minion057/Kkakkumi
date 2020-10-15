package Khack.Q.Kkakkumi;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;

import Khack.Q.Kkakkumi.JustClass.BackPressHandler;

public class MainActivity extends AppCompatActivity {
    //<editor-fold desc="변수 선언">

    //<editor-fold desc="뒤로가기(back key) 관리">
    //뒤로가기(back key) 제어(종료)관리 클래스 객체
    private BackPressHandler backPressHandler;
    //</editor-fold>

    //<editor-fold desc="페이지 넘기기">
    //뒤로가기(back key) 제어(종료)관리 클래스 객체
    Button btnEduStart, btnBookStart, btnGalleryStart, btncopyright;
    // 페이지 넘기기 객체
    Intent intent;
    //</editor-fold>

    //<editor-fold desc="권한">
    //snackbar 사용을 위한 view
    View p_view;
    //</editor-fold>

    //<editor-fold desc="교육 메뉴">
    // 메뉴 넘기기 버튼을 누르면 글자를 변경 >> 배열에서 글자 꺼내오기
    String[] menus = {"양치", "손씻기", "기침막기", "마스크"};
    // 다음 버튼 +1 / 이전 버튼 -1 로 순서를 제한하여 어느 메뉴를 꺼내야하는지 정함
    int menu_num = 0;
    // 메뉴 글자를 보여줄 요소
    TextView txtMenu;
    // 버튼 요소
    Button btnMenu_pre, btnMenu_af;
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

        //<editor-fold desc="페이지 넘기기">
        //<editor-fold desc="교육 진행 화면 이동">
        // 교육 시작 화면으로 넘어가기 위한 클릭리스너
        btnEduStart = findViewById(R.id.main_btn_Start);
        /**
         * 교육 선택에 따른 화면 activity 선택기능 구현
         */
        btnEduStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (menu_num){
                    case 0: //양치
                        intent = new Intent(getApplicationContext(), Test3Activity.class);
                        break;
                    case 1: //손씻기
                        intent = new Intent(getApplicationContext(), Test4Activity.class);
                        break;
                    case 2: //기침막기
                        intent = new Intent(getApplicationContext(), VideoActivity.class);
                        break;
                    case 3: //마스크
                        intent = new Intent(getApplicationContext(), recordActivity.class);
                        break;
                    default:
                        Log.d("V_Error", "menu : "+ Integer.toString(menu_num));
                        return;
                }
                intent.putExtra("menu", menu_num);
                startActivity(intent);
            }
        });
        //</editor-fold>
        //<editor-fold desc="도감 화면 이동">
        // 도감 화면으로 넘어가기 위한 클릭리스너
        btnBookStart = findViewById(R.id.main_btn_book);
        btnBookStart.setOnClickListener(view -> {
            intent = new Intent(getApplicationContext(), BookActivity.class);
            startActivity(intent);
        });
        //</editor-fold>
        //<editor-fold desc="갤러리 화면 이동">
        // 갤러리 화면으로 넘어가기 위한 클릭리스너
        btnGalleryStart = findViewById(R.id.main_btn_gallery);
        btnGalleryStart.setOnClickListener(view -> {
            intent = new Intent(getApplicationContext(), GalleryActivity.class);
            startActivity(intent);
        });
        //</editor-fold>
        //<editor-fold desc="저작권 화면 이동">
        // 저작권 화면으로 넘어가기 위한 클릭리스너
        btncopyright = findViewById(R.id.main_btn_copyright);
        btncopyright.setOnClickListener(view -> {
            intent = new Intent(getApplicationContext(), InfoActivity.class);
            startActivityForResult(intent,1);
        });
        //</editor-fold>
        //</editor-fold>

        //<editor-fold desc="권한">
        //권한 요청할 때 snackbar를 위해 현재 layout을 view에 저장
        p_view = findViewById(R.id.layout_main);
        //</editor-fold>

        //<editor-fold desc="교육 메뉴">
        // 메뉴 글자를 보여줄 요소
        txtMenu = findViewById(R.id.main_txt_Menu);
        // 메뉴를 조절할 버튼
        btnMenu_pre = findViewById(R.id.main_btn_menu1);
        btnMenu_pre.setOnClickListener(view -> {
            menu_num -= 1;
            if(0 > menu_num) menu_num = 3;
            txtMenu.setText(menus[menu_num]);
        });
        btnMenu_af = (Button)findViewById(R.id.main_btn_menu2);
        btnMenu_af.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                menu_num += 1;
                if(menus.length <= menu_num) menu_num = 0;
                txtMenu.setText(menus[menu_num]);
            }
        });
        //</editor-fold>
        //</editor-fold>

        //<editor-fold desc="권한">
        // SDK의 버전을 확인하여 23 이전이라면 절차를 진행할 필요 x
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.M) finish();
            // 아니라면 권한 체크 실행
        else checkSelfPermission();
        //</editor-fold>
    }

    //<editor-fold desc="뒤로가기(back key) 관리">
    @Override
    public void onBackPressed() {
        backPressHandler.onBackPressed();
    }
    //</editor-fold>

    //<editor-fold desc="권한">
    //<editor-fold desc="권한 요청">
    /**
     * 허락되지 않은 권한만 구분해서 요청하는 메소드
     */
    public void checkSelfPermission() {
        // 요청이 필요한 권한만 추가해서 저장할 변수
        String temp = "";

        if(Build.VERSION.SDK_INT < 29){
            //29 >> 안드로이드 10보다 낮으면 파일 읽기 쓰기 권한 필요
            // 파일 읽기 권한 확인
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                temp += Manifest.permission.READ_EXTERNAL_STORAGE + " ";
            }

            // 파일 쓰기 권한 확인
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                temp += Manifest.permission.WRITE_EXTERNAL_STORAGE + " ";
            }
        }

        // 카메라 권환 확인
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            temp += Manifest.permission.CAMERA + " ";
        }

        // 화면 녹화를 위한 오디오 녹음 권한 확인
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            temp += Manifest.permission.RECORD_AUDIO + " ";
        }

        if (TextUtils.isEmpty(temp) == false) {
            // 권한 요청
            Toast.makeText(this, "앱을 실행하기 위해 파일, 카메라 권한이 필요합니다.", Toast.LENGTH_SHORT).show();
            ActivityCompat.requestPermissions(this, temp.trim().split(" "),1);
        } else {
            // 모두 허용 상태
            //Toast.makeText(this, "오늘도 화이팅!", Toast.LENGTH_SHORT).show();
        }
    }
    //</editor-fold>

    //<editor-fold desc="요청 답장 오면 실행">
    /**
     * 사용자에게 권한 요청 답장이 오면 실행되는 메소드
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        //권한을 요청에 응답했을 경우
        if(requestCode == 1){
            int length = permissions.length;
            for (int i = 0; i < length; i++) {
                if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                    // 동의
                    Log.d("Permission","권한 허용 : " + permissions[i]);
                }else{
                    if(ActivityCompat.shouldShowRequestPermissionRationale(this, permissions[i])){
                        Snackbar.make(p_view, "퍼미션이 거부되었습니다. 앱을 다시 실행하여 퍼미션을 허용해주세요. ",
                                Snackbar.LENGTH_INDEFINITE).setAction("확인", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                finish();
                            }
                        }).show();
                    }else {
                        Snackbar.make(p_view, "퍼미션이 거부되었습니다. 설정(앱 정보)에서 퍼미션을 허용해야 합니다. ",
                                Snackbar.LENGTH_INDEFINITE).setAction("확인", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                finish();
                            }
                        }).show();
                    }
                }
            }
        }
    }
    //</editor-fold>
    //</editor-fold>
}
