package screen;

import engine.*;

import java.awt.*;
import java.awt.List;
import java.awt.event.KeyEvent;
import java.util.*;

import static engine.DrawManager.selectedCustom;

public class CustomizeScreen extends Screen {
	int selectedColorIndex = 0; // Default is 0 (Red)
	public Color selectedColor;

	public Color[][] filledColors = new Color[10][10]; // 각 위치의 색상 정보를 저장하는 배열


	protected ArrayList<Map.Entry<boolean[][], Color>> getSkinList() {
		return this.skinList;
	}
	ArrayList<Map.Entry<boolean[][], Color>> skinList;
	boolean [][] grid;
	private Color[] colors;
	private Cooldown selectionCooldown;
	private static final int SELECTION_TIME = 200; // 각 위치가 채워졌는지 추적하는 배열


	private int x_position;
	private int y_position;
	private int index;


	public CustomizeScreen(int width, int height, int fps) {

		super(width, height, fps);
		this.x_position =3;
		this.y_position =3;
		this.selectionCooldown = Core.getCooldown(SELECTION_TIME);
		this.selectionCooldown.reset();
		// Initialize the colors array with user provided colors

		skinList = FileManager.loadSkinList();
		grid = skinList.get(selectedCustom).getKey();
		for(int i=0; i<10; i++){
			for(int j=0; j<10; j++){
				boolean isCenter = i >= 3 && i < 7 && j >= 3 && j < 7;
				if(isCenter) filledColors[i][j] = skinList.get(selectedCustom).getValue();
				if(filledColors[i][j]!=null) grid[i][j] = true;
			}
		}
		Color navy = new Color(0, 0, 128); // 네이비
		Color purple = new Color(70, 38, 121); // 보라
		Color green = new Color(34, 143, 34); // 그린

		colors = new Color[]{
				Color.RED,    // 1
				Color.ORANGE, // 2
				Color.YELLOW, // 3
				green, //4
				Color.BLUE,
				navy,
				purple,
				Color.GRAY,
				Color.WHITE
		};

		// Initialize the selected color
		selectedColor = colors[0]; // Default is RED


	}
	public final int run() {
		super.run();
		return this.returnCode;
	}

	protected final void update() {
		super.update();

		draw();

		if(FileManager.loadSkinList()!=null) skinList = FileManager.loadSkinList();

		if (this.selectionCooldown.checkFinished() && this.inputDelay.checkFinished()) {
			if (inputManager.isKeyDown(KeyEvent.VK_1)) { // 1번 클릭 빨간색
				setSelectedColorIndex(0);
				this.selectionCooldown.reset();
			} else if (inputManager.isKeyDown(KeyEvent.VK_2)) { // 2번 클릭 주황색
				setSelectedColorIndex(1);
				this.selectionCooldown.reset();
			} else if (inputManager.isKeyDown(KeyEvent.VK_3)) { // 3번 클릭 노란색
				setSelectedColorIndex(2);
				this.selectionCooldown.reset();
			} else if (inputManager.isKeyDown(KeyEvent.VK_4)) { // 4번 클릭 그린
				setSelectedColorIndex(3);
				this.selectionCooldown.reset();
			} else if (inputManager.isKeyDown(KeyEvent.VK_5)) { // 5번 클릭 파란색
				setSelectedColorIndex(4);
				this.selectionCooldown.reset();
			} else if (inputManager.isKeyDown(KeyEvent.VK_6)) { // 6번 클릭 네이비
				setSelectedColorIndex(5);
				this.selectionCooldown.reset();
			} else if (inputManager.isKeyDown(KeyEvent.VK_7)) { // 7번 클릭 보라색
				setSelectedColorIndex(6);
				this.selectionCooldown.reset();
			} else if (inputManager.isKeyDown(KeyEvent.VK_8)) { // 8번 클릭 회색
				setSelectedColorIndex(7);
				this.selectionCooldown.reset();
			} else if (inputManager.isKeyDown(KeyEvent.VK_9)) { // 9번 클릭 흰색
				setSelectedColorIndex(8);
				this.selectionCooldown.reset();
			}
		}
		if (this.selectionCooldown.checkFinished()
				&& this.inputDelay.checkFinished()) {
			if (inputManager.isKeyDown(KeyEvent.VK_UP)
					|| inputManager.isKeyDown(KeyEvent.VK_W)) {
				up();
				this.selectionCooldown.reset();
			}
			else if (inputManager.isKeyDown(KeyEvent.VK_DOWN)
					|| inputManager.isKeyDown(KeyEvent.VK_S)) {
				down();
				this.selectionCooldown.reset();
			}
			else if (inputManager.isKeyDown(KeyEvent.VK_RIGHT)
					|| inputManager.isKeyDown(KeyEvent.VK_D)) {
				right();
				this.selectionCooldown.reset();
			}
			else if (inputManager.isKeyDown(KeyEvent.VK_LEFT)
					|| inputManager.isKeyDown(KeyEvent.VK_A)) {
				left();
				this.selectionCooldown.reset();
			}
			else if(inputManager.isKeyDown(KeyEvent.VK_ESCAPE)){
				this.returnCode = 10;
				this.isRunning = false;
			}
			else if (inputManager.isKeyDown(KeyEvent.VK_SPACE)) {
				/**
				 boolean adjacentFilled = // 인접한지 확인
				 (x_position > 0 && filledSpaces[x_position - 1][y_position]) || // 왼쪽 확인
				 (x_position < 9 && filledSpaces[x_position + 1][y_position]) || // 오른쪽 확인
				 (y_position > 0 && filledSpaces[x_position][y_position - 1]) || // 위쪽 확인
				 (y_position < 9 && filledSpaces[x_position][y_position + 1]);   // 아래쪽 확인

				 if (adjacentFilled) {
				 **/
				SoundManager.playSound("SFX/S_MenuClick", "menu_select", false, false);
				filledColors[x_position][y_position] = selectedColor;
				grid[x_position][y_position] = true;
			}
			if (inputManager.isKeyDown(KeyEvent.VK_DELETE)) {
				SoundManager.playSound("SFX/S_MenuClick", "menu_select", false, false);
				filledColors[x_position][y_position] = null;
				grid[x_position][y_position] = false; // 색상을 삭제하면 grid의 해당 위치도 0으로 설정

			}
			if (inputManager.isKeyDown(KeyEvent.VK_ENTER)) {
				skinList.set(selectedCustom, new AbstractMap.SimpleEntry<>(grid, colors[selectedColorIndex]));
				FileManager.saveSkinList(skinList);
				this.returnCode = 1;
				this.isRunning = false;
			}
		}
	}

	/**
	 * Shifts the focus to the next menu item.
	 */

	private void up() {
		if (this.y_position == 0)
			this.y_position = 9;
		else
			this.y_position--;
	}

	private void down() {
		if (this.y_position == 9)
			this.y_position = 0;
		else
			this.y_position++;
	}

	private void right() {
		if (this.x_position == 9)
			this.x_position = 0;
		else
			this.x_position++;
	}

	private void left() {
		if (this.x_position == 0)
			this.x_position = 9;
		else
			this.x_position--;
	}

	public void draw() {
		drawManager.initDrawing(this);
		drawManager.drawCustomizing(this, x_position, y_position, filledColors); // filledColors를 그리는 메소드에 전달
		drawManager.completeDrawing(this);
	}
	public void setSelectedColorIndex(int index) {
		if (index >= 0 && index < colors.length) {
			this.selectedColorIndex = index;
			this.selectedColor = colors[index];
		} else {
			throw new IllegalArgumentException("Invalid index: " + index);
		}
	}

	public int getSelectedColorIndex() {
		return this.selectedColorIndex;
	}
	public int getXPosition() {
		return x_position;
	}

	public int getYPosition() {
		return y_position;
	}
	public Color[][] getFilledColors() {
		return filledColors;
	}

}
