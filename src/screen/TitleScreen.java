package screen;

import java.awt.event.KeyEvent;
import java.util.logging.Logger;

import engine.Cooldown;
import engine.Core;
import engine.InputManager;
import engine.SoundManager;

/**
 * Implements the title screen.
 *
 * @author <a href="mailto:RobertoIA1987@gmail.com">Roberto Izquierdo Amo</a>
 *
 */
public class TitleScreen extends Screen {

	/** Milliseconds between changes in user selection. */
	private static final int SELECTION_TIME = 200;
	public static String menuSelection;

	/** Time between changes in user selection. */
	private Cooldown selectionCooldown;

	/**
	 * Constructor, establishes the properties of the screen.
	 *
	 * @param width
	 *            Screen width.
	 * @param height
	 *            Screen height.
	 * @param fps
	 *            Frames per second, frame rate at which the game is run.
	 */
	public TitleScreen(final int width, final int height, final int fps) {
		super(width, height, fps);

		// Defaults to play.
		this.returnCode = 2;
		this.selectionCooldown = Core.getCooldown(SELECTION_TIME);
		this.selectionCooldown.reset();
	}

	/**
	 * Starts the action.
	 *
	 * @return Next screen code.
	 */
	public final int run() {
		super.run();
		System.out.println("Returned code: " + this.returnCode);
		return this.returnCode;
	}

	/**
	 * Updates the elements on screen and checks for events.
	 */
	protected final void update() {
		super.update();
		draw();
		if (this.selectionCooldown.checkFinished()
				&& this.inputDelay.checkFinished()) {
			if (inputManager.isKeyDown(KeyEvent.VK_UP)
					|| inputManager.isKeyDown(KeyEvent.VK_W)) {
				previousMenuItem();
				this.selectionCooldown.reset();
			}
			if (inputManager.isKeyDown(KeyEvent.VK_DOWN)
					|| inputManager.isKeyDown(KeyEvent.VK_S)) {
				nextMenuItem();
				this.selectionCooldown.reset();
			}
			if (inputManager.isKeyDown(KeyEvent.VK_SPACE)){
				SoundManager.playSound("SFX/S_MenuClick", "menu_select", false, false);
				this.isRunning = false;
				switch (this.returnCode) {
					case 2:  // '게임 시작' 메뉴를 선택한 경우
						menuSelection = "gameplay";
						break;
					case 7: // '커스터 마이징 메뉴 선택한 경우 '
						this.returnCode=10;
						menuSelection = "customization";

				}
			}
		}
	}

	/**
	 * Shifts the focus to the next menu item.
	 */

	private void nextMenuItem() {
		if (this.returnCode == 7)  // 마지막 메뉴 아이템 번호
			this.returnCode = 0;  // 첫 번째 메뉴 아이템으로
		else
			this.returnCode++;
	}

	private void previousMenuItem() {
		if (this.returnCode == 0)  // 첫 번째 메뉴 아이템 번호
			this.returnCode = 7;  // 마지막 메뉴 아이템으로
		else
			this.returnCode--;
	}



	/**
	 * Shifts the focus to the previous menu item.
	 */

	/**
	 * Draws the elements associated with the screen.
	 */
	private void draw() {
		drawManager.initDrawing(this);
		drawManager.drawTitle(this);
		drawManager.drawMenu(this, this.returnCode);
		drawManager.completeDrawing(this);
	}
}
