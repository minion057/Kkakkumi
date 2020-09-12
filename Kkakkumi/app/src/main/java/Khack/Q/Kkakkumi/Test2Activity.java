package Khack.Q.Kkakkumi;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleOwner;

import com.google.android.gms.tasks.Task;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.common.FirebaseVisionImageMetadata;
import com.google.firebase.ml.vision.common.FirebaseVisionPoint;
import com.google.firebase.ml.vision.face.FirebaseVisionFace;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceContour;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetector;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetectorOptions;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Test2Activity extends AppCompatActivity {

    //<editor-fold desc="변수 선언">
    Preview preview;
    Camera camera;
    PreviewView viewFinder;

    ImageAnalysis imageAnalysis;
    ExecutorService cameraExecutor;

    private ListenableFuture<ProcessCameraProvider> cameraProviderFuture;

    TextView txt_notice;
    //</editor-fold>

    Context cont;
    RelativeLayout Rela;
    ImageView imageLE; //추가할 이미지 변수 여기에 넣기

    FaceDraw mDraw;
    //</editor-fold>

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test2);

        cont = this;
        Rela = findViewById(R.id.e_testre);
        imageLE = new ImageView(cont);
        Rela.addView(imageLE);
        /*
        * 이미지를 생성하는 방법
        * 1. 위에 변수 선언 부분에 변수명 추가
        * 2. 여기에 imageLE처럼 imageView 똑같이 따라해
        * 3. img_virus1.setImageResource(R.drawable.virus); 이런식의 구조를 써
        * (img_virus1 = imageLE 똑같이 변수명, set >> 함수니까 그대로쳐, ()안에는 R.drawable.너가 원하는 이미지명 >> res폴더 drawable에 있는친구들)
        * 4. 이미지를 안보이게 하는 경우 > img_virus1.setVisibility(View.INVISIBLE)
        * 5. 이미지를 안보이게 하다가 보이게 하는 겨우 > img_virus1.setVisibility(View.VISIBLE)
        * 6. 이미지를 화면에 추가 > Rela.addView(imageLE);
        * */
        //<editor-fold desc="변수 값 넣기">

        //<editor-fold desc="camerX">
        viewFinder = findViewById(R.id.e_viewFinder);

        cameraExecutor = Executors.newSingleThreadExecutor();
        //</editor-fold>

        txt_notice = findViewById(R.id.e_notice);
        //</editor-fold>

        //<editor-fold desc="camerX">
        startCamera();
        //</editor-fold>
    }

    //<editor-fold desc="camerX">
    private void startCamera() {
        cameraProviderFuture = ProcessCameraProvider.getInstance(this);
        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();

                // 카메라 전환시 새로운 카메라 배당
                // 이때 카메라에 연결된 bind(생명주기)가 있으면 안되므로
                // 연결된 bind 모두 해제
                cameraProvider.unbindAll();

                //<editor-fold desc="firebase ML_이미지 분석 > 1초마다 실행되는 부분">
                // .setTargetResolution(new Size(1280, 720))
                // 있어야 원활하게 실행됨 (got it 뜨는게 한번만 뜸)
                imageAnalysis =
                        new ImageAnalysis.Builder()
                                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                                .build();

                // 1. 한번만 실행
                /*
                imageAnalysis.setAnalyzer(cameraExecutor, image -> {
                    int rotationDegrees = image.getImageInfo().getRotationDegrees();
                    // insert your code here.
                    Log.d("Analy", "Average luminosity: "+rotationDegrees);

                    FirebaseVisionFaceDetectorOptions highAccuracyOpts =
                            new FirebaseVisionFaceDetectorOptions.Builder()
                                    .setPerformanceMode(FirebaseVisionFaceDetectorOptions.ACCURATE)
                                    .setLandmarkMode(FirebaseVisionFaceDetectorOptions.ALL_LANDMARKS)
                                    .setClassificationMode(FirebaseVisionFaceDetectorOptions.ALL_CLASSIFICATIONS)
                                    .build();

                });*/

                // 2. 여러번 실행 > ImageAnalysis.Analyzer class 부분을 따로 만들기
                imageAnalysis.setAnalyzer(cameraExecutor, new LuminosityAnalyzer2());

                //</editor-fold>

                preview = new Preview.Builder().build();

                CameraSelector cameraSelector = new CameraSelector.Builder()
                        .requireLensFacing(CameraSelector.LENS_FACING_FRONT)
                        .build();

                camera = cameraProvider.bindToLifecycle((LifecycleOwner)this, cameraSelector, preview, imageAnalysis);
                preview.setSurfaceProvider(viewFinder.createSurfaceProvider(camera.getCameraInfo()));
            } catch (ExecutionException | InterruptedException e) {
                Log.e("Camera", "카메라 : "+e.getMessage());
            }
        }, ContextCompat.getMainExecutor(this));
    }
    //</editor-fold>

    public class LuminosityAnalyzer2 implements ImageAnalysis.Analyzer {

        //<editor-fold desc="firebase">
        // Real-time contour detection of multiple faces
        // 실시간 여러 얼굴의 윤곽선 검출
        FirebaseVisionFaceDetectorOptions realTimeOpts;

        FirebaseVisionImage img_f = null;
        FirebaseVisionFaceDetector detector = null;
        Task<List<FirebaseVisionFace>> result = null;
        //</editor-fold>

        private int degreesToFirebaseRotation(int degrees) {
            switch (degrees) {
                case 0:
                    return FirebaseVisionImageMetadata.ROTATION_0;
                case 90:
                    return FirebaseVisionImageMetadata.ROTATION_90;
                case 180:
                    return FirebaseVisionImageMetadata.ROTATION_180;
                case 270:
                    return FirebaseVisionImageMetadata.ROTATION_270;
                default:
                    throw new IllegalArgumentException(
                            "Rotation must be 0, 90, 180, or 270.");
            }
        }

        @SuppressLint("UnsafeExperimentalUsageError")
        @Override
        public void analyze(@NonNull ImageProxy imageP) {
            // Real-time contour detection of multiple faces
            // 실시간 여러 얼굴의 윤곽선 검출
            realTimeOpts =
                    new FirebaseVisionFaceDetectorOptions.Builder()
                            .setContourMode(FirebaseVisionFaceDetectorOptions.ALL_CONTOURS)
                            .build();

            // 1.
            if (imageP == null || imageP.getImage() == null) {
                Log.e("Analy", "No Image");
                return;
            }

            try{
                img_f = FirebaseVisionImage.fromMediaImage(imageP.getImage(),
                        degreesToFirebaseRotation(imageP.getImageInfo().getRotationDegrees()));
                detector = FirebaseVision.getInstance().getVisionFaceDetector(realTimeOpts);

                Point p = new Point();
                Display display = getWindowManager().getDefaultDisplay();
                display.getSize(p);

                result = detector.detectInImage(img_f)
                        .addOnSuccessListener(
                                faces -> {
                                    Log.d("Test", "start");
                                    if(faces.size() <= 0){
                                        //drawface(null);
                                        txt_notice.setText("얼굴을 보여줘!");
                                        txt_notice.setVisibility(View.VISIBLE);

                                        imageLE.setImageResource(R.drawable.backgrond_btnstart);
                                        imageLE.setX(p.x / 2 -400);
                                        imageLE.setY(p.y /2 - 600);
                                        imageLE.setLayoutParams(new RelativeLayout.LayoutParams(800, 800));

                                        Log.d("Test","No Face");
                                    }
                                    for (FirebaseVisionFace face : faces) {
                                        //drawface(face);
                                        txt_notice.setText("잘하고 있어!");

                                        Log.d("Test","Face bounds : " + face.getBoundingBox());
                                        /*
                                        List<FirebaseVisionPoint> leftEyeContour =
                                                face.getContour(FirebaseVisionFaceContour.LEFT_EYE).getPoints();
                                        if(leftEyeContour.size() <= 0 ) return;

                                        for(FirebaseVisionPoint fp : leftEyeContour){
                                            imageLE.setImageResource(R.drawable.virus);
                                            imageLE.setX(p.x * fp.getX() / imageP.getWidth() + 100);
                                            imageLE.setY(p.y * fp.getY() / imageP.getHeight() + 90);
                                            imageLE.setLayoutParams(new RelativeLayout.LayoutParams(50, 50));

                                            break;
                                        }
                                         */

                                        //s너가 시간순대로 이미지를 띄우고 안보이게 하고 그러는 코드를 ㅕㅇ기에 짜면돼
                                        //1. 얼굴이 검출되는 순간 시간을 저장할 변수, 현재 시간을 가지고 있는 변수
                                        //2. imageLE 변수 옆에 이름 하나 더써서 만들어
                                        //3. 위 1번에 적힌 두 변수를 계산하여 5초가 지나면 straight1를 띄우고
                                        //4. 거기서 또 10초가 지나면 fold2가 나오게끔 코드를 짜보세요
                                        //5. 단, 두개의 이미지는 해당 시간에만 보입니다.
                                        //즉, 처음에는 치아배경만 보이다가 > 5초가 지나면 straight1가 띄워지고
                                        //10초가 흐르면 straight1는 사라지고 fold2가 자연스레 나옴
                                        // 이미지는 2번에 적힌 변수를 활용하여 5초가 나오면 invisible에서 visible로 되고
                                        // 처음에 set을 straight1, 두번째 fold를
                                        // fold에서 5초가 지나면 visible을 invisible로 변경

                                    }
                                })
                        .addOnFailureListener(
                                e -> Log.d("Test", "fail"));

            }catch (Exception ex){
                Log.e("Error", "Why : "+ex.getMessage());
            }

            // 4.
            imageP.close();
        }
    }


}