package Khack.Q.Kkakkumi;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleOwner;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetectorOptions;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

/**
 * 교육 실행 화면
 * 기능 1. cameraX로 카메라 화면을 보여줌
 * 기능 2. 하단에 캐릭터 + 세균 나옴
 * 기능 3. 음성으로 알려줌
 */
public class EduActivity extends AppCompatActivity {
    //<editor-fold desc="변수">
    //<editor-fold desc="camerX">
    // 화면 요소
    Preview preview;
    Camera camera;
    PreviewView viewFinder;
    Button btn_capture, btn_change;

    // 캡처 및 분석
    ImageCapture imageCapture;
    ImageAnalysis imageAnalysis;

    // 카메라를 전환할지 결정하는 변수 (0 : 전면 / 1 : 후면 / Defalut : 0)
    int select_c = 0;

    // 사진 저장시 날짜 및 시간으로 절대 겹칠 수 없게 파일명 지정
    String FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS";

    // 사진을 저장할 경로
    File outputDirectory;

    // 카메라 켜기
    private ListenableFuture<ProcessCameraProvider> cameraProviderFuture;
    //</editor-fold>

    //<editor-fold desc="넘겨받은 변수">
    Intent intent;
    int menu;
    //</editor-fold>

    //<editor-fold desc="firebase">
    FirebaseVisionFaceDetectorOptions highAccuracyOpts, realTimeOpts;
    //</editor-fold>
    //</editor-fold>

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edu);

        //<editor-fold desc="변수 값 입력">
        //<editor-fold desc="camerX">
        // 찍기 위한 클릭리스너
        btn_capture = findViewById(R.id.camera_btn_capture);
        btn_capture.setOnClickListener(view ->  takePhoto());
        // 카메라 전환을 위한 클릭리스너
        btn_change = findViewById(R.id.camera_btn_change);
        btn_change.setOnClickListener(view ->  {
            select_c = select_c >= 1 ? 0 : 1;
            startCamera();
        });
        viewFinder = findViewById(R.id.viewFinder);
        outputDirectory = getOutputDirectory();
        //</editor-fold>

        //<editor-fold desc="넘겨받은 변수">
        intent = getIntent();
        menu = intent.getIntExtra("menu", -1);
        switch (menu){
            case 0: //양치
                Log.d("Menu : ", "양치");
                break;
            case 1: //손씻기
                Log.d("Menu : ", "손씻기");
                break;
            case 2: //기침막기
                Log.d("Menu : ", "기침막기");
                break;
            case 3: //마스크
                Log.d("Menu : ", "마스크");
                break;
            default:
                Log.d("V_Error", "Menu : "+ Integer.toString(menu));
                break;
        }
        //</editor-fold>
        //</editor-fold>

        //<editor-fold desc="camerX">
        startCamera();
        //</editor-fold>
    }

    //<editor-fold desc="camerX">
    //<editor-fold desc="camerX 실행 및 전환">
    /**
     * CameraX를 실행하는 메소드
     * 기능 1. 기본값인 전면카메라가 찍는 화면을 실시간으로 보여줌
     * 기능 2. change 버튼 클릭시 실행 > 전면 or 후면 카메라로 변경해서 다시 실행
     */
    private void startCamera() {
        cameraProviderFuture = ProcessCameraProvider.getInstance(this);
        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                // 카메라 전환시 새로운 카메라 배당
                // 이때 카메라에 연결된 bind(생명주기)가 있으면 안되므로
                // 연결된 bind 모두 해제
                cameraProvider.unbindAll();
                preview = new Preview.Builder().build();
                CameraSelector cameraSelector = new CameraSelector.Builder()
                        .requireLensFacing(CameraSelector.LENS_FACING_FRONT)
                        .build();
                if(select_c == 1){
                    cameraSelector = new CameraSelector.Builder()
                            .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                            .build();
                }
                imageCapture = new ImageCapture.Builder()
                        .setTargetRotation(getWindowManager().getDefaultDisplay().getRotation())
                        .build();
                camera = cameraProvider.bindToLifecycle((LifecycleOwner)this, cameraSelector, preview, imageCapture);
                preview.setSurfaceProvider(viewFinder.createSurfaceProvider(camera.getCameraInfo()));
            } catch (ExecutionException | InterruptedException e) {
                Log.e("Camera", "카메라 : "+e.getMessage());
            }
        }, ContextCompat.getMainExecutor(this));
    }
    //</editor-fold>

    //<editor-fold desc="사진 경로">
    /**
     * 사진 저장할 경로를 만드는 메소드
     * 기능 1. 사진을 저장할 경로가 있는지 확인
     * 기능 2. 사진 경로가 없다면 경로대로 폴더 생성
     * */
    private File getOutputDirectory() {
        File dir = new File(Environment.getExternalStorageDirectory().toString()+"/kkal");
        try{
            if(!dir.exists()){
                dir.mkdir();
                Log.d("File : ", dir.toString()+" 경로 생성");
            }
            if(dir.exists()) Log.d("File : ", dir.toString()+" 경로 생성 succes");
        }catch (Exception e){
            Log.e("File : ", e.getMessage());
        }
        return dir;
    }
    //</editor-fold>

    //<editor-fold desc="사진 찍기">
    /**
     * 지정해둔 경로에 현재 카메라가 보고 있는 화면을 사진 파일로 생성
     * 기능 1. 현재 화면에 보이는 장면을 jpg로 지정된 경로에 저장
     */
    private void takePhoto() {
        File photoFile = new File(outputDirectory, new SimpleDateFormat(FILENAME_FORMAT, Locale.US).format(System.currentTimeMillis()) + ".jpg");
        ImageCapture.OutputFileOptions outputFileOptions =
                new ImageCapture.OutputFileOptions.Builder(photoFile).build();
        imageCapture.takePicture(outputFileOptions, ContextCompat.getMainExecutor(this),
                new ImageCapture.OnImageSavedCallback(){
                    @Override
                    public void onImageSaved(@NonNull ImageCapture.OutputFileResults outputFileResults) {
                        Uri saveUri = Uri.fromFile(photoFile);
                        String msg = "Photo capture succeeded: "+saveUri;
                        Log.d("Camera Save", msg);
                    }

                    @Override
                    public void onError(@NonNull ImageCaptureException exc) {
                        Log.e("Camera", "Photo capture failed: "+ exc.getMessage());
                    }
                });
    }
    //</editor-fold>
    //</editor-fold>
}