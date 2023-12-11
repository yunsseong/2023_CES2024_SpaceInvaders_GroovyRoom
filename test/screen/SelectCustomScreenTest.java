package screen;

import static org.junit.Assert.assertEquals;
import java.lang.reflect.Method;
import org.junit.Before;
import org.junit.Test;

public class SelectCustomScreenTest { //1.2 커스터 마이징 스킨 선택 로직

    private SelectCustomScreen selectCustomScreen;

    @Before
    public void setUp() {
        // SelectCustomScreen 객체 초기화
        selectCustomScreen = new SelectCustomScreen(800, 600, 60);
    }

    @Test
    public void testNextBox() {
        // 현재 returnCode가 2일 때 nextBox 호출 시 3으로 증가하는지 확인

        // Given
        selectCustomScreen.returnCode = 2;

        // When
        invokePrivateMethod(selectCustomScreen, "nextBox");

        // Then
        assertEquals(3, selectCustomScreen.returnCode);
    }

    @Test
    public void testNextBoxWrapping() { // 현재 returnCode가 5일 때 nextBox 호출 시 0으로 wrapping 되는지 확인

        // Given
        selectCustomScreen.returnCode = 5;

        // When
        invokePrivateMethod(selectCustomScreen, "nextBox");

        // Then
        assertEquals(0, selectCustomScreen.returnCode);
    }

    @Test
    public void testPreviousBox() {
        // 현재 returnCode가 4일 때 previousBox 호출 시 3으로 감소하는지 확인

        // Given
        selectCustomScreen.returnCode = 4;

        // When
        invokePrivateMethod(selectCustomScreen, "previousBox");

        // Then
        assertEquals(3, selectCustomScreen.returnCode);
    }

    @Test
    public void testPreviousBoxWrapping() {// 현재 returnCode가 0일 때 previousBox 호출 시 5으로 wrapping 되는지 확인

        // Given
        selectCustomScreen.returnCode = 0;

        // When
        invokePrivateMethod(selectCustomScreen, "previousBox");

        // Then
        assertEquals(5, selectCustomScreen.returnCode);
    }

    // Reflection을 사용하여 private 메서드에 접근하는 유틸리티 메서드
    private void invokePrivateMethod(Object object, String methodName) {
        try {
            Method method = object.getClass().getDeclaredMethod(methodName);
            method.setAccessible(true);
            method.invoke(object);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
