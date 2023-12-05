package screen;

import engine.InputManager;
import org.junit.jupiter.api.Test;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class CustomizeScreenTest {
    private InputManager inputManager = InputManager.getInstance();

    @Test
    public void testMoveWithArrowKeys() { // 방향 키 에 따른 좌표 변동 확인 , 키를 누를 때 CustomizeScreen 클래스의 좌표가 예상대로 이동하는지를 확인
        CustomizeScreen customizeScreen = new CustomizeScreen(800, 600, 60);

        // 초기 위치
        int initialX = customizeScreen.getXPosition();
        int initialY = customizeScreen.getYPosition();

        // 방향 키 누르기
        if (inputManager.isKeyDown(KeyEvent.VK_UP)) {
            int movedUpY = customizeScreen.getYPosition();
            assertEquals((initialY - 1 + 10) % 10, movedUpY);
        }

        if (inputManager.isKeyDown(KeyEvent.VK_DOWN)) {
            int movedDownY = customizeScreen.getYPosition();
            assertEquals((initialY + 1) % 10, movedDownY);
        }
        if (inputManager.isKeyDown(KeyEvent.VK_RIGHT)) {
            int movedRightX = customizeScreen.getXPosition();
            assertEquals((initialX + 1) % 10, movedRightX);
        }
        if (inputManager.isKeyDown(KeyEvent.VK_LEFT)) {
            int movedLeftX = customizeScreen.getXPosition();
            assertEquals((initialX - 1 + 10) % 10, movedLeftX);
        }
    }
    @Test
    public void testSelectColorFromPalette() { // 1.3.2 색상 팔레트 선택 로직 테스트 //

        CustomizeScreen customizeScreen = new CustomizeScreen(800, 600, 60);

        if (inputManager.isKeyDown(KeyEvent.VK_2)) // 2번 키 입력 받을 시
            assertEquals(1, customizeScreen.getSelectedColorIndex()); // 1번 색상의 인덱스와 비교

        else if (inputManager.isKeyDown(KeyEvent.VK_5)) { // 5번 키 입력 받을 시
            assertEquals(4, customizeScreen.getSelectedColorIndex()); // 4번 색상의 인덱스와 비교
        } else if (inputManager.isKeyDown(KeyEvent.VK_X)) {    // X 키 입력 받을 시
            // 색상이 변경되지 않아야 함
            assertEquals(4, customizeScreen.getSelectedColorIndex());
        }
    }
    @Test
    public void testColoringWithSpaceBar() { //스페이스바 시 현재 위치에 선택한 색상이 칠해지는지 확인하는 코드

        //현재 선택된 색상의 인덱스를 가져와서, 이 값이 현재 위치의 색상 인덱스와 일치하는지를 테스트
        CustomizeScreen customizeScreen = new CustomizeScreen(800, 600, 60);


        if(inputManager.isKeyDown(KeyEvent.VK_SPACE)) {
            int selectedColorIndex = customizeScreen.getSelectedColorIndex(); // 선택한 색상 정보
            int currentColorIndex = getColorIndexAtPosition(customizeScreen, customizeScreen.getXPosition(), customizeScreen.getYPosition()); // 현재 색상 정보
            assertEquals(selectedColorIndex, currentColorIndex);
        }

    }

    private int getColorIndexAtPosition(CustomizeScreen customizeScreen, int x, int y) { //현재 화면의 특정 위치 (x, y)에서의 색상 인덱스를 가져오는 함수
        Color[][] filledColors = customizeScreen.filledColors;
        return filledColors[x][y] != null ? findColorIndex(customizeScreen, filledColors[x][y]) : -1;
        // 해당 위치의 색상을 가져와서 null이 아닌 경우 findColorIndex 메소드를 호출하여 색상 인덱스 반환
    }

    private int findColorIndex(CustomizeScreen customizeScreen, Color color) { // 특정 색상의 인덱스를 찾아 반환
        Color[] colors = customizeScreen.filledColors[customizeScreen.selectedColorIndex];
        for (int i = 0; i < colors.length; i++) { //입력으로 받은 color와 동일한 색상을 찾기 위해 반복문을 사용합니다. 찾으면 해당 색상의 인덱스를 반환
            if (colors[i].equals(color)) {
                return i;
            }
        }
        return -1;
    }
    @Test
    public void testSaveToFile() { // 파일 저장 (Enter 키) 입력 시 2차원 배열 파일 있는지 확인인

        CustomizeScreen customizeScreen = new CustomizeScreen(800, 600, 60);

        // Initial state
        ArrayList<Map.Entry<boolean[][], Color>> initialSkinList = customizeScreen.getSkinList();
        // Press Enter to save the current file information
        if (inputManager.isKeyDown(KeyEvent.VK_ENTER)) {
            // Check if the skinList was updated
            ArrayList<Map.Entry<boolean[][], Color>> updatedSkinList = customizeScreen.getSkinList();
            assertNotEquals(initialSkinList, updatedSkinList);
        }
    }



}
