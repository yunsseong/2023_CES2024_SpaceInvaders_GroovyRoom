package screen;


import engine.Cooldown;
import engine.Core;
import engine.FileManager;
import engine.SoundManager;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Map;

import static screen.TitleScreen.menuSelection;

public class SelectCustomScreen extends Screen {
	private Cooldown selectionCooldown;
	private static final int SELECTION_TIME = 100;

	public SelectCustomScreen(int width, int height, int FPS) {
		super(width, height, FPS);
		this.selectionCooldown = Core.getCooldown(SELECTION_TIME);
		this.selectionCooldown.reset();
		ArrayList<Map.Entry<boolean[][], Color>> customShips = FileManager.loadSkinList();

	}
	public final int run() {
		super.run();
		System.out.println("Returned code: " + this.returnCode);
		return this.returnCode;
	}

	protected final void update() {
		super.update();
		draw();
		if (this.selectionCooldown.checkFinished()
				&& this.inputDelay.checkFinished()) {
			if (inputManager.isKeyDown(KeyEvent.VK_UP)
					|| inputManager.isKeyDown(KeyEvent.VK_W)) {
				previousBox();
				this.selectionCooldown.reset();
				;
			} else if (inputManager.isKeyDown(KeyEvent.VK_DOWN)
					|| inputManager.isKeyDown(KeyEvent.VK_S)) {
				nextBox();
				this.selectionCooldown.reset();
			} else if (inputManager.isKeyDown(KeyEvent.VK_SPACE)) {
				SoundManager.playSound("SFX/S_MenuClick", "menu_select", false, false);
				if ("customization".equals(menuSelection)) {
					// '커스터마이징' 메뉴를 선택한 경우의 처리
					this.returnCode = 9;
					this.isRunning = false;
					System.out.println("넘기는 값 Returned code: " + this.returnCode);
				} else if ("gameplay".equals(menuSelection) ) { // 게임 메뉴 선택시
						this.returnCode = 7;
						this.isRunning = false;
						System.out.println("넘기는 값 Returned code: " + this.returnCode);
					}

				}
			} else if (inputManager.isKeyDown(KeyEvent.VK_ESCAPE)) {
				this.returnCode = 1;
				this.isRunning = false;
			}
		}

	/**
	 * Shifts the focus to the next menu item.
	 */
	private void nextBox() {
		if (this.returnCode == 5)
			this.returnCode = 0;
		else
			this.returnCode++;
	}

	private void previousBox() {
		if (this.returnCode == 0)
			this.returnCode = 5;
		else
			this.returnCode--;
	}
	public void draw(){
		drawManager.initDrawing(this);
		drawManager.drawSelectCustom(this, returnCode);
		drawManager.completeDrawing(this);
	}

}

