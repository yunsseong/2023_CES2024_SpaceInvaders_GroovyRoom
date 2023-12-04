package screen;


import engine.Cooldown;
import engine.Core;
import engine.SoundManager;

import java.awt.event.KeyEvent;

public class SelectCustomScreen extends Screen {
	private Cooldown selectionCooldown;
	private static final int SELECTION_TIME = 100;

	public SelectCustomScreen(int width, int height, int FPS) {
		super(width, height, FPS);
		this.returnCode = 0;
		this.selectionCooldown = Core.getCooldown(SELECTION_TIME);
		this.selectionCooldown.reset();
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
				this.selectionCooldown.reset();;
			}
			else if (inputManager.isKeyDown(KeyEvent.VK_DOWN)
					|| inputManager.isKeyDown(KeyEvent.VK_S)) {
				nextBox();
				this.selectionCooldown.reset();
			}
			else if (inputManager.isKeyDown(KeyEvent.VK_SPACE)){
				this.returnCode=9;
				SoundManager.playSound("SFX/S_MenuClick", "menu_select", false, false);
				this.isRunning = false;
			}
			else if (inputManager.isKeyDown(KeyEvent.VK_ESCAPE)){
				this.returnCode=1;
				this.isRunning = false;
			}
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

