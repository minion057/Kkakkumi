package Khack.Q.Kkakkumi;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.*;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleOwner;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Point;
import android.graphics.Rect;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.util.Size;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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
import com.google.firebase.ml.vision.face.FirebaseVisionFaceLandmark;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TestActivity extends AppCompatActivity {

    //<editor-fold desc="변수 선언">
    Preview preview;
    Camera camera;
    PreviewView viewFinder;

    ImageCapture imageCapture;
    ImageAnalysis imageAnalysis;
    ExecutorService cameraExecutor;

    int select_c = 0;
    String FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS";

    File outputDirectory;
    Button btn_capture, btn_change;

    private ListenableFuture<ProcessCameraProvider> cameraProviderFuture;

    TextView txt_notice;

    //</editor-fold>

    Context cont;
    RelativeLayout Rela;
    ImageView imageLE, img_virus1, img_virus2, img_virus3, img_virus4, img_virus5, img_virus6;

    FaceDraw mDraw;


    public void drawface(FirebaseVisionFace face){

        if(mDraw == null){
            mDraw = new FaceDraw(cont, face);
            addContentView(mDraw, new ViewGroup.LayoutParams
                    (ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            return;
        }
        if (mDraw.getParent() != null) ((ViewGroup) mDraw.getParent()).removeView(mDraw);
        mDraw = new FaceDraw(cont, face);
        addContentView(mDraw, new ViewGroup.LayoutParams
                (ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));



        //mGrap = new FaceGraphic(over, face, 0);
    }
    //</editor-fold>

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        cont = this;
        Rela = findViewById(R.id.testre);
        imageLE = new ImageView(cont);
        img_virus1 = new ImageView(cont);
        img_virus2 = new ImageView(cont);
        img_virus3 = new ImageView(cont);
        img_virus4 = new ImageView(cont);
        img_virus5 = new ImageView(cont);
        img_virus6 = new ImageView(cont);
        img_virus1.setImageResource(R.drawable.virus);
        img_virus2.setImageResource(R.drawable.virus);
        img_virus3.setImageResource(R.drawable.virus);
        img_virus4.setImageResource(R.drawable.virus);
        img_virus5.setImageResource(R.drawable.virus);
        img_virus6.setImageResource(R.drawable.virus);
        img_virus1.setVisibility(View.INVISIBLE);
        img_virus2.setVisibility(View.INVISIBLE);
        img_virus3.setVisibility(View.INVISIBLE);
        img_virus4.setVisibility(View.INVISIBLE);
        img_virus5.setVisibility(View.INVISIBLE);
        img_virus6.setVisibility(View.INVISIBLE);
        Rela.addView(imageLE);
        Rela.addView(img_virus1);
        Rela.addView(img_virus2);
        Rela.addView(img_virus3);
        Rela.addView(img_virus4);
        Rela.addView(img_virus5);
        Rela.addView(img_virus6);

        //<editor-fold desc="변수 값 넣기">

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
        Log.d("test : ",outputDirectory.toString());

        cameraExecutor = Executors.newSingleThreadExecutor();
        //</editor-fold>

        txt_notice = findViewById(R.id.edu_notice);
        //</editor-fold>

        //<editor-fold desc="camerX">
        startCamera();
        //</editor-fold>
    }

    //<editor-fold desc="camerX">
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

    private void startCamera() {
        cameraProviderFuture = ProcessCameraProvider.getInstance(this);
        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();

                // 카메라 전환시 새로운 카메라 배당
                // 이때 카메라에 연결된 bind(생명주기)가 있으면 안되므로
                // 연결된 bind 모두 해제
                cameraProvider.unbindAll();

                //<editor-fold desc="firebase ML_캡처">
                imageCapture = new ImageCapture.Builder()
                        .setTargetRotation(getWindowManager().getDefaultDisplay().getRotation())
                        .build();
                //</editor-fold>

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

                if(select_c == 1){
                    cameraSelector = new CameraSelector.Builder()
                            .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                            .build();
                }

                camera = cameraProvider.bindToLifecycle((LifecycleOwner)this, cameraSelector, preview, imageCapture, imageAnalysis);
                preview.setSurfaceProvider(viewFinder.createSurfaceProvider(camera.getCameraInfo()));
            } catch (ExecutionException | InterruptedException e) {
                Log.e("Camera", "카메라 : "+e.getMessage());
            }
        }, ContextCompat.getMainExecutor(this));
    }

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

                                        img_virus1.setVisibility(View.INVISIBLE);
                                        img_virus2.setVisibility(View.INVISIBLE);
                                        img_virus3.setVisibility(View.INVISIBLE);
                                        img_virus4.setVisibility(View.INVISIBLE);
                                        img_virus5.setVisibility(View.INVISIBLE);
                                        img_virus6.setVisibility(View.INVISIBLE);

                                        Log.d("Test","No Face");
                                    }
                                    for (FirebaseVisionFace face : faces) {
                                        //drawface(face);
                                        txt_notice.setText("잘하고 있어!");

                                        Log.d("Test","Face bounds : " + face.getBoundingBox());
                                        List<FirebaseVisionPoint> leftEyeContour =
                                                face.getContour(FirebaseVisionFaceContour.LEFT_EYE).getPoints();
                                        if(leftEyeContour.size() <= 0 ) return;

                                        for(FirebaseVisionPoint fp : leftEyeContour){
                                            imageLE.setImageResource(R.drawable.virus);
                                            imageLE.setX(p.x * fp.getX() / imageP.getWidth() + 100);
                                            imageLE.setY(p.y * fp.getY() / imageP.getHeight() + 90);
                                            imageLE.setLayoutParams(new RelativeLayout.LayoutParams(50, 50));

                                            img_virus1.setVisibility(View.VISIBLE);
                                            img_virus1.setX(p.x * fp.getX() / imageP.getWidth() + 150);
                                            img_virus1.setY(p.y * fp.getY() / imageP.getHeight() + 200);
                                            img_virus1.setLayoutParams(new RelativeLayout.LayoutParams(50, 50));

                                            img_virus2.setVisibility(View.VISIBLE);
                                            img_virus2.setX(p.x * fp.getX() / imageP.getWidth() + 200);
                                            img_virus2.setY(p.y * fp.getY() / imageP.getHeight() + 140);
                                            img_virus2.setLayoutParams(new RelativeLayout.LayoutParams(50, 50));

                                            img_virus3.setVisibility(View.VISIBLE);
                                            img_virus3.setX(p.x * fp.getX() / imageP.getWidth() + 250);
                                            img_virus3.setY(p.y * fp.getY() / imageP.getHeight() + 210);
                                            img_virus3.setLayoutParams(new RelativeLayout.LayoutParams(50, 50));

                                            img_virus4.setVisibility(View.VISIBLE);
                                            img_virus4.setX(p.x * fp.getX() / imageP.getWidth() + 300);
                                            img_virus4.setY(p.y * fp.getY() / imageP.getHeight() + 160);
                                            img_virus4.setLayoutParams(new RelativeLayout.LayoutParams(50, 50));

                                            img_virus5.setVisibility(View.VISIBLE);
                                            img_virus5.setX(p.x * fp.getX() / imageP.getWidth() + 350);
                                            img_virus5.setY(p.y * fp.getY() / imageP.getHeight() + 200);
                                            img_virus5.setLayoutParams(new RelativeLayout.LayoutParams(50, 50));

                                            img_virus6.setVisibility(View.VISIBLE);
                                            img_virus6.setX(p.x * fp.getX() / imageP.getWidth() + 400);
                                            img_virus6.setY(p.y * fp.getY() / imageP.getHeight() + 80);
                                            img_virus6.setLayoutParams(new RelativeLayout.LayoutParams(50, 50));

                                            break;
                                        }
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