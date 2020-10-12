package Khack.Q.Kkakkumi.JustClass;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.util.List;

public class ValManagement {
    //<editor-fold desc="변수 선언">
    // 저장 경로
    private File outputPicture, outputVideo;
    // 경로 밑 파일 리스트
    private File[] VideoList;
    //</editor-fold>

    //<editor-fold desc="첫 시작, 변수 값 설정">
    public ValManagement(Context context) {
        this.outputPicture = new File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "");
        this.outputVideo = new File(context.getExternalFilesDir(Environment.DIRECTORY_MOVIES), "");
        if(makefolder(outputVideo)){setFileList();}
    }
    //</editor-fold>

    //<editor-fold desc="파일 경로 생성">
    private boolean makefolder(File dir){
        try {
            if (!dir.exists()) {
                dir.mkdirs();
                Log.d("File : ", dir.toString() + " 경로 생성 시도");
            }
        } catch (Exception e) {
            Log.e("File : ", e.getMessage());
        }

        if (dir == null || !dir.exists()) {
            Log.d("File : ", dir.toString() + "경로 생성 실패");
            return false;
        }
        Log.d("File : ", dir.toString() +"경로 생성 성공 / 존재");
        return true;
    }
    //</editor-fold>

    //<editor-fold desc="파일 경로 아이템(ex.사진) 리스트 Set">
    private void setFileList(){
        this.VideoList = this.outputVideo.listFiles();
    }
    //</editor-fold>

    //<editor-fold desc="변수 Getter">
    public File getOutputPicture() {
        return outputPicture;
    }

    public File getOutputVideo() {
        return outputVideo;
    }

    public File[] getVideoList() {
        return VideoList;
    }
    //</editor-fold>
}
