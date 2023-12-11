package engine;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import screen.CustomizeScreen;

import java.awt.*;

class DrawManagerTest {
    @BeforeEach
    void setUp() {
        // DrawManager 초기화
        DrawManager drawManager = new DrawManager();

        // Frame 초기화
        Frame frame = new Frame(800, 600, 60);

        // CustomizeScreen을 테스트에 맞게 초기화
        CustomizeScreen customizeScreen = new CustomizeScreen(800, 600, 60);
        customizeScreen.setSelectedColorIndex(2);

        // initDrawing 메서드 호출
        drawManager.initDrawing(customizeScreen);
    }

    @Test
    void testDrawCustomizing() {
        // 가상의 CustomizeScreen 객체 생성
        CustomizeScreen customizeScreen = new CustomizeScreen(800, 600, 60);

        // 가상의 filledColors 배열 초기화 (예시로 일부 색상을 지정)
        Color[][] filledColors = new Color[10][10];
        filledColors[3][3] = Color.RED;
        filledColors[4][4] = Color.GREEN;

        // CustomizeScreen에 가상의 filledColors 배열 설정
        customizeScreen.filledColors = filledColors;

        // 가상의 Frame 객체 생성
        Frame frame = new Frame(800, 600, 60);

        // 가상의 DrawManager 객체 생성
        DrawManager drawManager = new DrawManager();

        // 테스트: drawCustomizing 메서드 호출 후 예상되는 결과를 확인
        drawManager.drawCustomizing(customizeScreen, 0, 0, filledColors);
    }
}
