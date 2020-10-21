package Khack.Q.Kkakkumi.JustClass;

import android.content.Context;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

public class ValManagement {
    //<editor-fold desc="변수 선언">
    Context cont;

    // 저장 경로
    private File outputVideo, characterTxTDB; //outputPicture,
    // 경로 밑 파일 리스트
    private File[] VideoList = null;

    String txtDBpath;

    //</editor-fold>

    //<editor-fold desc="첫 시작, 변수 값 설정">
    public ValManagement(Context context) {
        cont = context;
        //this.outputPicture = new File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "");
        this.outputVideo = new File(context.getExternalFilesDir(Environment.DIRECTORY_MOVIES), "");
        if(makefolder(outputVideo)){setFileList();}
        this.characterTxTDB = new File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), "");
        this.txtDBpath = this.characterTxTDB.toString() + "/KalCharacterDB.txt";
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

    //<editor-fold desc="캐릭터 Txt DB 쓰기">
    public void setContents(int index){
        // 캐릭터 DB 첫 내용
        String contents = "캐릭터 획득 정보를 저장하는 파일 입니다.\n삭제하지 말아주세요.\n";
        //String firstcontents = "c0:0/c1:0/c2:0/c3:0/c4:0/c5:0/";
        String[] defaultcontents = {"c0:0","c1:0","c2:0","c3:0","c4:0","c5:0"};
        // 수정할 index가 이상한 값이면 종료
        if(index < 0 || index >= defaultcontents.length) return;
        // 아니라면 새로 db를 생성하는 contents들에 새로 얻은 캐릭터 부분만 수정
        defaultcontents[index] = "c"+index+":1";

        try{
            if(makefolder(characterTxTDB)){
                File txtfile = new File(txtDBpath);
                if(txtfile.exists()==true) {//파일이 있음
                    String[] origincontents = Readfile().split("/");
                    txtfile.delete();

                    // 설정된 파일 내용이 다르다면 초기화 (index 들어온 부분만 얻은 것으로 수정)
                    // 길이가 다르면 내용이 틀리다고 판단 - 인덱스가 안맞으니까 아예 초기화
                    if(origincontents.length != defaultcontents.length) writeTextFile(contents+addallcontent(defaultcontents));
                    else{
                        // db값이 제대로 들어갔는지 확인 c다음 인덱스(0~5) / :다음 db 값 (0 or 1)
                        for(Integer cnt = 0 ; cnt < origincontents.length ; cnt++){
                            String[] tmp = origincontents[cnt].split(":");
                            //index name or c 다음 인덱스 값 틀린 경우 >> 이름과 값 0으로 초기화
                            if(!tmp[0].substring(0,1).equals("c") || !isnum(tmp[0].substring(1,tmp[0].length()), defaultcontents.length-1))
                                origincontents[cnt] = "c"+cnt+":0";
                            else if(!isnum(tmp[1], 1)){ // val 틀린 경우
                                // 첫글자가 0,1 이면 그 값으로 수정하고 아니라면 0으로 초기화
                                if(isnum(tmp[1].substring(0,1),1)) origincontents[cnt] = "c"+cnt+":"+Integer.parseInt(tmp[1].substring(0,1));
                                else origincontents[cnt] = "c"+cnt+":0";
                            }
                        }

                        // db 존재 및 이상 없음 - 기존 내용을 수정해서 다시 작성
                        origincontents[index] = "c"+index+":1";
                        writeTextFile(contents+addallcontent(origincontents));
                    }
                } else writeTextFile(contents+addallcontent(defaultcontents));
            }else{
                Toast.makeText(cont, "캐릭터 정보 파일에 이상이 있습니다!\n파일 권한을 확인해주세요.",
                                                                            Toast.LENGTH_SHORT).show();
            }
        }catch (Exception e){
            Log.e("TxtDB", e.getMessage());
        }
    }

    private String addallcontent(String[] contents){
        String content = "";
        for(String tmp : contents){
            content += tmp+"/";
        }return content;
    }

    private void writeTextFile(String contents){
        try{
            //파일 output stream 생성
            FileOutputStream fos = new FileOutputStream(txtDBpath, true);
            //파일쓰기
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(fos));
            writer.write(contents);
            writer.flush();
            writer.close();
            fos.close();
        }catch (Exception e){
            Log.e("TxtDB", e.getMessage());
        }
    }

    public String Readfile(){
        try (BufferedReader br = new BufferedReader(new FileReader(new File(txtDBpath)))) {
            String dbline;
            while ((dbline = br.readLine()) != null) {
                if(dbline.substring(0,1).equals("c")) return dbline;
            }
        } catch (Exception e){
            Log.e("TxtDB", e.getMessage());
        }
        return "";
    }

    public ArrayList<Integer> getdbList(){
        ArrayList<Integer> indexs = new ArrayList<>();
        String[] defaultcontents = {"c0:0","c1:0","c2:0","c3:0","c4:0","c5:0"};
        String[] origincontents = Readfile().split("/");
        // db 자체가 이상한 것은 완전 초기화이므로 null로 알림
        if(origincontents.length != defaultcontents.length) return null;
        for(Integer index = 0 ; index < origincontents.length ; index++){
            String[] tmp = origincontents[index].split(":");
            // index 이상있음 >> setcontent 에서 db 잘못된 부분 0으로 초기화 >> 추가
            if(!isnum(tmp[0].substring(1,tmp[0].length()), defaultcontents.length-1)
                    || !tmp[0].substring(0,1).equals("c")) indexs.add(index);
            else{
                if(!isnum(tmp[1],1)){ // val 값이 잘못된 경우
                    if(isnum(tmp[1].substring(0,1), 1)){ //첫글자가 0이라면 setcontent에서 수정하므로 list에 추가
                        if(Integer.parseInt(tmp[1].substring(0,1)) == 0) indexs.add(index);
                    } else indexs.add(index);// 첫글자가 잘못된 경우 0으로 초기화하므로 추가
                } else if(Integer.parseInt(tmp[1]) == 0) indexs.add(index); // 값이 0이면 얻지 못한 캐릭터로 해당 인덱스만 저장
            }
        }if(indexs == null){
            // 들어갈 수 없는 인덱스(사이즈)를 넣으므로 다 얻은 상태임을 알림
            indexs.add(defaultcontents.length);
            return indexs;
        }
        // 여기까지 왔다면 얻지 못한 캐릭터가 존재하고 db가 있다는 것
        return indexs;
    }

    private boolean isnum(String num, int endlen){
        try{
            int n = Integer.parseInt(num);
            if(n >= 0 && n <= endlen) return true;
            // db index를 넘어서는 경우 >> 숫자 이상
            return false;
        }catch (Exception e){
            Log.e("TxtDB_num", e.getMessage());
            //숫자로 변환할 수 없음
            return false;
        }
    }
    //</editor-fold>

    //<editor-fold desc="파일 경로 아이템(ex.사진) 리스트 Set">
    private void setFileList(){
        this.VideoList = this.outputVideo.listFiles();
    }
    //</editor-fold>

    //<editor-fold desc="변수 Getter">
    /*public File getOutputPicture() {
        return outputPicture;
    }*/

    public File getOutputVideo() {
        return outputVideo;
    }

    public File[] getVideoList() {
        return VideoList;
    }
    //</editor-fold>
}
