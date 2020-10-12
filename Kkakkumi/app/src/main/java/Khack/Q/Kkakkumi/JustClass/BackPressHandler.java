package Khack.Q.Kkakkumi.JustClass;

import android.app.Activity;
import android.widget.Toast;

/**
 * back 입력을 관리하는 클래스
 * 기능 1. back key를 처음 입력 시 한 번 더 누를 경우 앱이 종료된다고 Toast 메시지로 힌트를 줌
 * 기능 2. 2초 이내 back key를 두 번 입력하면 Activity가 종료
 */
public class BackPressHandler {
    //<editor-fold desc="변수 선언">
    // 마지막으로 back key를 눌렀던 시간 저장
    private long backKeyPressedTime = 0;
    // 첫 번째 back key를 누를 때 표시
    private Toast toast;
    // 종료할 Activity
    private Activity activity;
    //</editor-fold>

    //<editor-fold desc="back key를 제어할 Activity를 지정">
    /**
     * 뒤로가기를 제어할 Activity를 지정하는 메소드
     * @Param activity 뒤로가기를 제어할 Activity
     */
    public BackPressHandler(Activity activity) {
        this.activity = activity;
    }
    //</editor-fold>

    //<editor-fold desc="back key 제어">
    /*
     * 뒤로가기를 실제로 제어하는 메소드
     * 기능 1. 2초 이내 back key 첫 입력이면, 힌트를 줄 Toast 메시지 메소드를 호출
     * 기능 2. 2초 이내 back key를 두 번 입력해야지 Activity가 종료됨
     */
    public void onBackPressed() {
        /*기능 1
         * backKeyPressedTime(마지막으로 back key를 눌렀던 시간)+2초가 현재 시간보다 작다면
         * 즉, 마지막 입력 시간보다 2초가 흘렀다면 2초 이내 back key 입력이 없었으므로
         * 힌트를 줄 Toast 메시지 메소드 호출 후 종료
         */
        if (System.currentTimeMillis() > backKeyPressedTime + 2000) {
            backKeyPressedTime = System.currentTimeMillis();
            showGuide();
            return;
        }
        /*기능 2
         * backKeyPressedTime(마지막으로 back key를 눌렀던 시간)+2초가 현재 시간보다 크거나 같다면
         * 즉, 마지막 입력 시간이 2초도 흐르지 않았는데 한 번 더 입력이 왔으므로
         * 힌트를 줄 토스트 메시지는 이미 보였으므로 종료하고
         * 관리하고 있는 Activity 또한 종료
         */
        if (System.currentTimeMillis() <= backKeyPressedTime + 2000) {
            activity.finish();
            toast.cancel();
        }
    }
    //</editor-fold>

    //<editor-fold desc="toast 메시지로 힌트주기">
    /**
     * 힌트를 줄 Toast 메시지를 띄우는 메소드
     */
    public void showGuide() {
        toast = Toast.makeText(activity, "\'뒤로\' 버튼을 한 번 더 누르시면 종료됩니다.",
                Toast.LENGTH_SHORT); toast.show();
    }
    //</editor-fold>
}
