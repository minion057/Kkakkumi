package Khack.Q.Kkakkumi.JustClass;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;
import android.view.View;

import com.google.firebase.ml.vision.common.FirebaseVisionPoint;
import com.google.firebase.ml.vision.face.FirebaseVisionFace;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceContour;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceLandmark;

import java.util.List;

public class FaceDraw extends View {

    FirebaseVisionFace face;
    Rect bounds;

    public FaceDraw(Context context, FirebaseVisionFace f) {
        super(context);
        face = f;
        if(face != null) facecal();
    }
    public void facecal(){
        Log.d("Test", "Face bounds : " + face.getBoundingBox());
        Log.d("Test", "Face ID : " + face.getTrackingId());

        bounds = face.getBoundingBox();

        float rotY = face.getHeadEulerAngleY();  // Head is rotated to the right rotY degrees
        float rotZ = face.getHeadEulerAngleZ();  // Head is tilted sideways rotZ degrees

        // If landmark detection was enabled (mouth, ears, eyes, cheeks, and
        // nose available):
        FirebaseVisionFaceLandmark leftEar = face.getLandmark(FirebaseVisionFaceLandmark.LEFT_EAR);
        if (leftEar != null) {
            FirebaseVisionPoint leftEarPos = leftEar.getPosition();
        }

        // If contour detection was enabled:
        List<FirebaseVisionPoint> leftEyeContour =
                face.getContour(FirebaseVisionFaceContour.LEFT_EYE).getPoints();
        List<FirebaseVisionPoint> upperLipBottomContour =
                face.getContour(FirebaseVisionFaceContour.UPPER_LIP_BOTTOM).getPoints();

        // If classification was enabled:
        if (face.getSmilingProbability() != FirebaseVisionFace.UNCOMPUTED_PROBABILITY) {
            float smileProb = face.getSmilingProbability();
        }
        if (face.getRightEyeOpenProbability() != FirebaseVisionFace.UNCOMPUTED_PROBABILITY) {
            float rightEyeOpenProb = face.getRightEyeOpenProbability();
        }

        // If face tracking was enabled:
        if (face.getTrackingId() != FirebaseVisionFace.INVALID_ID) {
            int id = face.getTrackingId();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // TODO Auto-generated method stub
        //  canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
        if(face == null) {
            super.onDraw(canvas);
            return;
        }
        Paint paint = new Paint();

        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(2);
        paint.setColor(Color.RED);

        canvas.drawRect(bounds, paint);
        //canvas.drawRect(100, 100, 200, 200, paint);

    }
}
