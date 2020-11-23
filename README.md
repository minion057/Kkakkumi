# 나는야 깔끄미
   
'나는야 깔끄미'는 구글 firebase에서 제공하는 ML Kit 중 얼굴인식 기능을 기반으로 한 유아 위생 교육 애플리케이션입니다.    
양치, 마스크 착용, 손 씻기, 기침 가려서 하기같이 위생 습관을 캐릭터와 음성을 통해 설명합니다.    
교육은 얼굴이 인식되어야만 실행되며, 교육이 끝나면 보상 스티커를 제공합니다.    
모은 스티커는 도감을 통해 확인할 수 있으며 가려진 캐릭터들로 반복학습을 유도합니다.    
또한 교육을 하기 싫어하는 아이에게 전에 교육받으며 녹화된 자신의 모습을 영상으로 당시 즐거움을 떠올리게 해 교육을 다시 할 수 있도록 유도합니다.
   
이 어플은 (사)소프트웨어교육혁신센터에서 주관하는 제8회 K-Hackathon VR&AR 앱 개발 챌린지에서 우수상(한국콘텐츠학회장상)을 수여하였습니다.        
   
```
깔끄미 정보

폰트 : 여기어때 잘난체 OTF
나는야 : #595959
깔끄미 : #82CBC4

메뉴 글씨체 : 티머니 둥근바람 (#5D6DBE)

교육 BGM : Sand Castle - Quincas Moreia

최소 API 27 (android 8.1 - Oreo)
package - Khack.Q.Kkakkumi
```
    
     
     

## 첫 실행화면    
![00.메인화면.gif](/md_img/00.메인화면.gif)   
```
위생교육 - 양치, 손씻기, 기침가리기, 마스크끼기.
시작하기 버튼을 통해 실행.
```
  
## 교육 진행화면 
**<양치와 마스크 교육 예시>**   
![01.양치진행.gif](/md_img/01.양치진행.gif)   
![01.마스크진행.gif](/md_img/01.마스크진행.gif)    
        
**<교육 흐름>**   
![01.교육흐름.png](/md_img/01.교육흐름.png) 
```
모든 교육은 얼굴이 인식되어야지 실행.
BGM과 캐릭터 행동, 대사를 통해 끝까지 할 수 있도록 유도.
교육을 완료하면 칭찬과 보상 스티커 획득. (이 스티커는 아래 스티커 도감에서 확인 가능)
```

## 보상 스티커 도감 화면      
![02.도감.gif](/md_img/02.도감.gif)   
```
교육을 완료하고 얻은 스티커 확인.
미획득 캐릭터의 경우 교육을 하라고 알림.
```       
  
## 갤러리 화면  
**<운동을 올바르게 하고 있을 경우>**   
![03.갤러리.png](/md_img/03.갤러리.png) 
```
교육 실행 전 녹화 수락을 요청함.
이때 수락한 경우 녹화된 영상을 확인할 수 있는 공간.
파일은 앱 데이터로 있어 갤러리에서 확인 불가.
따라서 어플 자체에서 확인하고 삭제할 수 있는 기능 추가.
```       
