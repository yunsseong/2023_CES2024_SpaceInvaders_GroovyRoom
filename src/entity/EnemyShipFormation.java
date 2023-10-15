package entity;

import java.util.*;
import java.util.logging.Logger;

import engine.*;
import screen.Screen;
import engine.DrawManager.SpriteType;

/**
 * Groups enemy ships into a formation that moves together.
 * 
 * @author <a href="mailto:RobertoIA1987@gmail.com">Roberto Izquierdo Amo</a>
 * 
 */
public class EnemyShipFormation implements Iterable<EnemyShip> {

	/** Initial position in the x-axis. */
	private static final int INIT_POS_X = 20;
	/** Initial position in the y-axis. */
	private static final int INIT_POS_Y = 100;
	/** Distance between ships. */
	private static final int SEPARATION_DISTANCE = 40;
	/** Proportion of C-type ships. */
	private static final double PROPORTION_C = 0.2;
	/** Proportion of B-type ships. */
	private static final double PROPORTION_B = 0.4;
	/** Lateral speed of the formation. */
	private static final int X_SPEED = 8;
	/** Downwards speed of the formation. */
	private static final int Y_SPEED = 4;
	/** Speed of the bullets shot by the members. */
	//private static final int BULLET_SPEED = 4;
	/** Proportion of differences between shooting times. */
	private static final double SHOOTING_VARIANCE = .2;
	/** Margin on the sides of the screen. */
	private static final int SIDE_MARGIN = 20;
	/** Margin on the bottom of the screen. */
	private static final int BOTTOM_MARGIN = 80;
	/** Distance to go down each pass. */
	private static final int DESCENT_DISTANCE = 20;
	/** Minimum speed allowed. */
	private static final int MINIMUM_SPEED = 10;
	/** 적의 타입에 따라 총 쿨타임이 줄어듬 */
	private static final double[] BULLETCOOLDOWN = {0.05,0.1,0.2};

	/** DrawManager instance. */
	private DrawManager drawManager;
	/** Application logger. */
	private Logger logger;
	/** Screen to draw ships on. */
	private Screen screen;
	/** current gamestate*/
	private GameState gameState;
	/** List of enemy ships forming the formation. */
	private List<List<EnemyShip>> enemyShips;
	/** Minimum time between shots. */
	private Cooldown shootingCooldown;
	/** Number of ships in the formation - horizontally. */
	private int nShipsWide;
	/** Number of ships in the formation - vertically. */
	private int nShipsHigh;
	/** Time between shots. */
	private int shootingInterval;
	/** Variance in the time between shots. */
	private int shootingVariance;
	/** Initial ship speed. */
	private int baseSpeed;
	/** Speed of the ships. */
	private int movementSpeed;
	/** Current direction the formation is moving on. */
	private Direction currentDirection;
	/** Direction the formation was moving previously. */
	private Direction previousDirection;
	/** Interval between movements, in frames. */
	private int movementInterval;
	/** Total width of the formation. */
	private int width;
	/** Total height of the formation. */
	private int height;
	/** Position in the x-axis of the upper left corner of the formation. */
	private int positionX;
	/** Position in the y-axis of the upper left corner of the formation. */
	private int positionY;
	/** Width of one ship. */
	private int shipWidth;
	/** Height of one ship. */
	private int shipHeight;
	/** List of ships that are able to shoot. */
	private List<EnemyShip> shooters;
	/** Number of not destroyed ships. */
	private int shipCount;
	/** Directions the formation can move. */
	private enum Direction {
		/** Movement to the right side of the screen. */
		RIGHT,
		/** Movement to the left side of the screen. */
		LEFT,
		/** Movement to the bottom of the screen. */
		DOWN
	};

	/**
	 * Constructor, sets the initial conditions.
	 * 
	 * @param gameSettings
	 *            Current game settings.
	 */
	public EnemyShipFormation(final GameSettings gameSettings, final GameState gameState) {
		this.gameState = gameState;
		this.drawManager = Core.getDrawManager();
		this.logger = Core.getLogger();
		this.enemyShips = new ArrayList<List<EnemyShip>>();
		this.currentDirection = Direction.RIGHT;
		this.movementInterval = 0;
		this.nShipsWide = gameSettings.getFormationWidth();
		this.nShipsHigh = gameSettings.getFormationHeight();
		this.shootingInterval = gameSettings.getShootingFrecuency();
		this.shootingVariance = (int) (gameSettings.getShootingFrecuency()
				* SHOOTING_VARIANCE);
		this.baseSpeed = gameSettings.getBaseSpeed();
		this.movementSpeed = this.baseSpeed;
		this.positionX = INIT_POS_X;
		this.positionY = INIT_POS_Y;
		this.shooters = new ArrayList<EnemyShip>();
		SpriteType spriteType;

		this.logger.info("Initializing " + nShipsWide + "x" + nShipsHigh
				+ " ship formation in (" + positionX + "," + positionY + ")");

		// Each sub-list is a column on the formation.
		for (int i = 0; i < this.nShipsWide; i++)
			this.enemyShips.add(new ArrayList<EnemyShip>());

		for (List<EnemyShip> column : this.enemyShips) {
			for (int i = 0; i < this.nShipsHigh; i++) {
				if (i / (float) this.nShipsHigh < PROPORTION_C)
					spriteType = SpriteType.EnemyShipC1;
				else if (i / (float) this.nShipsHigh < PROPORTION_B
						+ PROPORTION_C)
					spriteType = SpriteType.EnemyShipB1;
				else
					spriteType = SpriteType.EnemyShipA1;

				EnemyShip enemyShip = null;

				switch (spriteType)
				{
					case EnemyShipA1:
						enemyShip = new EnemyShipA((SEPARATION_DISTANCE
								* this.enemyShips.indexOf(column))
								+ positionX, (SEPARATION_DISTANCE * i)
								+ positionY, spriteType,gameState);
						break;
					case EnemyShipB1:
						enemyShip = new EnemyShipB((SEPARATION_DISTANCE
								* this.enemyShips.indexOf(column))
								+ positionX, (SEPARATION_DISTANCE * i)
								+ positionY, spriteType,gameState);
						break;
					case EnemyShipC1:
						enemyShip = new EnemyShipC((SEPARATION_DISTANCE
								* this.enemyShips.indexOf(column))
								+ positionX, (SEPARATION_DISTANCE * i)
								+ positionY, spriteType,gameState);
						break;
					default:
						enemyShip = new EnemyShip((SEPARATION_DISTANCE
								* this.enemyShips.indexOf(column))
								+ positionX, (SEPARATION_DISTANCE * i)
								+ positionY, spriteType,gameState);
				}
				column.add(enemyShip);
				this.shipCount++;
			}
		}

		this.shipWidth = this.enemyShips.get(0).get(0).getWidth();
		this.shipHeight = this.enemyShips.get(0).get(0).getHeight();

		this.width = (this.nShipsWide - 1) * SEPARATION_DISTANCE
				+ this.shipWidth;
		this.height = (this.nShipsHigh - 1) * SEPARATION_DISTANCE
				+ this.shipHeight;

		for (List<EnemyShip> column : this.enemyShips)
			this.shooters.add(column.get(column.size() - 1));
	}

	/**
	 * Associates the formation to a given screen.
	 * 
	 * @param newScreen
	 *            Screen to attach.
	 */
	public final void attach(final Screen newScreen) {
		screen = newScreen;
	}

	/**
	 * Draws every individual component of the formation.
	 */
	public final void draw() {
		for (List<EnemyShip> column : this.enemyShips)
			for (EnemyShip enemyShip : column)
				drawManager.drawEntity(enemyShip, enemyShip.getPositionX(),
						enemyShip.getPositionY());
	}

	/**
	 * Updates the position of the ships.
	 */
	public final void update() {
		if(this.shootingCooldown == null) {
			this.shootingCooldown = Core.getVariableCooldown(shootingInterval,
					shootingVariance);
			this.shootingCooldown.reset();
		}
		
		cleanUp();

		int movementX = 0;
		int movementY = 0;
		double remainingProportion = (double) this.shipCount
				/ (this.nShipsHigh * this.nShipsWide);
		this.movementSpeed = (int) (Math.pow(remainingProportion, 2)
				* this.baseSpeed);
		this.movementSpeed += MINIMUM_SPEED;
		
		movementInterval++;
		if (movementInterval >= this.movementSpeed) {
			movementInterval = 0;

			boolean isAtBottom = positionY
					+ this.height > screen.getHeight() - BOTTOM_MARGIN;
			boolean isAtRightSide = positionX
					+ this.width >= screen.getWidth() - SIDE_MARGIN;
			boolean isAtLeftSide = positionX <= SIDE_MARGIN;
			boolean isAtHorizontalAltitude = positionY % DESCENT_DISTANCE == 0;

			if (currentDirection == Direction.DOWN) {
				if (isAtHorizontalAltitude)
					if (previousDirection == Direction.RIGHT) {
						currentDirection = Direction.LEFT;
						this.logger.info("Formation now moving left 1");
					} else {
						currentDirection = Direction.RIGHT;
						this.logger.info("Formation now moving right 2");
					}
			} else if (currentDirection == Direction.LEFT) {
				if (isAtLeftSide)
					if (!isAtBottom) {
						previousDirection = currentDirection;
						currentDirection = Direction.DOWN;
						this.logger.info("Formation now moving down 3");
					} else {
						currentDirection = Direction.RIGHT;
						this.logger.info("Formation now moving right 4");
					}
			} else {
				if (isAtRightSide)
					if (!isAtBottom) {
						previousDirection = currentDirection;
						currentDirection = Direction.DOWN;
						this.logger.info("Formation now moving down 5");
					} else {
						currentDirection = Direction.LEFT;
						this.logger.info("Formation now moving left 6");
					}
			}

			if (currentDirection == Direction.RIGHT)
				movementX = X_SPEED;
			else if (currentDirection == Direction.LEFT)
				movementX = -X_SPEED;
			else
				movementY = Y_SPEED;

			positionX += movementX;
			positionY += movementY;

			// Cleans explosions.
			List<EnemyShip> destroyed;
			for (List<EnemyShip> column : this.enemyShips) {
				destroyed = new ArrayList<EnemyShip>();
				for (EnemyShip ship : column) {
					if (ship != null && ship.isDestroyed()) {
						destroyed.add(ship);
						this.logger.info("Removed enemy "
								+ column.indexOf(ship) + " from column "
								+ this.enemyShips.indexOf(column));
					}
				}
				column.removeAll(destroyed);
			}

			for (List<EnemyShip> column : this.enemyShips)
				for (EnemyShip enemyShip : column) {
					enemyShip.move(movementX, movementY);
					enemyShip.update();
				}
		}
	}

	/**
	 * Cleans empty columns, adjusts the width and height of the formation.
	 */
	private void cleanUp() {
		Set<Integer> emptyColumns = new HashSet<Integer>();
		int maxColumn = 0;
		int minPositionY = Integer.MAX_VALUE;
		for (List<EnemyShip> column : this.enemyShips) {
			if (!column.isEmpty()) {
				// Height of this column
				int columnSize = column.get(column.size() - 1).positionY
						- this.positionY + this.shipHeight;
				maxColumn = Math.max(maxColumn, columnSize);
				minPositionY = Math.min(minPositionY, column.get(0)
						.getPositionY());
			} else {
				// Empty column, we remove it.
				emptyColumns.add(this.enemyShips.indexOf(column));
			}
		}
		for (int index : emptyColumns) {
			this.enemyShips.remove(index);
			logger.info("Removed column " + index);
		}

		int leftMostPoint = 0;
		int rightMostPoint = 0;
		
		for (List<EnemyShip> column : this.enemyShips) {
			if (!column.isEmpty()) {
				if (leftMostPoint == 0)
					leftMostPoint = column.get(0).getPositionX();
				rightMostPoint = column.get(0).getPositionX();
			}
		}

		this.width = rightMostPoint - leftMostPoint + this.shipWidth;
		this.height = maxColumn;

		this.positionX = leftMostPoint;
		this.positionY = minPositionY;
	}

	/**
	 * Shoots a bullet downwards.
	 * 
	 * @param bullets
	 *            Bullets set to add the bullet being shot.
	 */
	public final void shoot(final Set<Bullet> bullets) {
		// For now, only ships in the bottom row are able to shoot.

		if (this.shootingCooldown.checkFinished()) {
			this.shootingCooldown.reset();
			ArrayList<Boolean> shot = new ArrayList<>();// 적이 한번만 발사
			for (int i=0;i<this.shooters.size();i++) shot.add(false);
			for (int i = 0; i < gameState.getLevel(); i++) {
				int index = (int) (Math.random() * (this.shooters.size()-1));
				if (shot.get(index))continue;
				shot.set(index, true);
				EnemyShip shooter = this.shooters.get(index);
				shooter.shoot(bullets);
				switch (shooter.spriteType)
				{
					case EnemyShipA1:
					case EnemyShipA2:
						this.shootingCooldown.timedown(BULLETCOOLDOWN[0]);
						break;
					case EnemyShipB1:
					case EnemyShipB2:
						this.shootingCooldown.timedown(BULLETCOOLDOWN[1]);
						break;
					case EnemyShipC1:
					case EnemyShipC2:
						this.shootingCooldown.timedown(BULLETCOOLDOWN[2]);
						break;
					default:
						break;
				}
			}
		}
	}

	/**
	 * Destroys a ship.
	 * 
	 * @param destroyedShip
	 *            Ship to be destroyed.
	 */
	public final void destroy(final EnemyShip destroyedShip) {
		for (List<EnemyShip> column : this.enemyShips)
			for (int i = 0; i < column.size(); i++)
				if (column.get(i).equals(destroyedShip)) {
					column.get(i).destroy();
					this.logger.info("Destroyed ship in ("
							+ this.enemyShips.indexOf(column) + "," + i + ")");
				}

		// Updates the list of ships that can shoot the player.
		if (this.shooters.contains(destroyedShip)) {
			int destroyedShipIndex = this.shooters.indexOf(destroyedShip);
			int destroyedShipColumnIndex = -1;

			for (List<EnemyShip> column : this.enemyShips)
				if (column.contains(destroyedShip)) {
					destroyedShipColumnIndex = this.enemyShips.indexOf(column);
					break;
				}

			EnemyShip nextShooter = getNextShooter(this.enemyShips
					.get(destroyedShipColumnIndex));

			if (nextShooter != null)
				this.shooters.set(destroyedShipIndex, nextShooter);
			else {
				this.shooters.remove(destroyedShipIndex);
				this.logger.info("Shooters list reduced to "
						+ this.shooters.size() + " members.");
			}
		}
		if (destroyedShip.isDestroyed())
			this.shipCount--;
	}

	/**
	 * Destroys a ship by bomb.
	 *
	 * @param destroyedShip
	 *            Ship to be destroyed first.
	 *
	 * @return destroyedByBombEnemyShips
	 * 			  List of Enemy ships destroyed by bomb.
	 */
	public final List<EnemyShip> destroyByBomb(final EnemyShip destroyedShip) {
		List<EnemyShip> destroyedByBombEnemyShips = new ArrayList<>();
		int howManyEnemyIsDead = 0;
		int[] dx = {0, 0, 1, 1, 1, -1, -1, -1};
		int[] dy = {-1, 1, -1, 0, 1, -1, 0, 1};
		for (int i = 0; i < this.enemyShips.size(); i++){
			for (int j = 0; j < this.enemyShips.get(i).size(); j++) {
				if (this.enemyShips.get(i).get(j) == destroyedShip && true) {
					destroyedByBombEnemyShips.add(this.enemyShips.get(i).get(j));
					this.enemyShips.get(i).get(j).destroy();
					this.logger.info("Destroyed ship in ("
							+ i + "," + j + ")");
					if (this.enemyShips.get(i).get(j).isDestroyed())
						howManyEnemyIsDead++;
					for(int n = 0; n < 8; n++){
						int nx = i + dx[n]; int ny = j + dy[n];
						if(!(nx >= 0 && nx <= this.enemyShips.size() - 1 && ny >= 0 && ny <= this.enemyShips.get(nx).size() - 1)) continue;
						if(this.enemyShips.get(nx).get(ny).isDestroyed()) continue;
						destroyedByBombEnemyShips.add(this.enemyShips.get(nx).get(ny));
						for (int repeat=0;repeat<3;repeat++)
							this.enemyShips.get(nx).get(ny).destroy();
						this.enemyShips.get(nx).get(ny).destroy();
						this.logger.info("Destroyed ship in ("
								+ nx + "," + ny + ")");
						if (this.enemyShips.get(nx).get(ny).isDestroyed())
							howManyEnemyIsDead++;
					}
				}
			}
		}


		for(EnemyShip enemyShip : destroyedByBombEnemyShips)
			// Updates the list of ships that can shoot the player.
			if (this.shooters.contains(enemyShip)) {
				int destroyedShipIndex = this.shooters.indexOf(enemyShip);
				int destroyedShipColumnIndex = -1;

				for (List<EnemyShip> column : this.enemyShips)
					if (column.contains(enemyShip)) {
						destroyedShipColumnIndex = this.enemyShips.indexOf(column);
						break;
					}

				EnemyShip nextShooter = getNextShooter(this.enemyShips
						.get(destroyedShipColumnIndex));

				if (nextShooter != null)
					this.shooters.set(destroyedShipIndex, nextShooter);
				else {
					this.shooters.remove(destroyedShipIndex);
					this.logger.info("Shooters list reduced to "
							+ this.shooters.size() + " members.");
				}
			}


		this.shipCount -= howManyEnemyIsDead;
		return destroyedByBombEnemyShips;

	}

	/**
	 * Destroys a ship by bomb.
	 *
	 * @param destroyedShip
	 *            Ship to be destroyed first.
	 *
	 * @return destroyedByBombEnemyShips
	 * 			  List of Enemy ships destroyed by bomb.
	 */
	public final List<EnemyShip> destroyByBomb(final EnemyShip destroyedShip) {
		List<EnemyShip> destroyedByBombEnemyShips = new ArrayList<>();
		int howManyEnemyIsDead = 0;
		int[] dx = {0, 0, 1, 1, 1, -1, -1, -1};
		int[] dy = {-1, 1, -1, 0, 1, -1, 0, 1};
		for (int i = 0; i < this.enemyShips.size(); i++){
			for (int j = 0; j < this.enemyShips.get(i).size(); j++) {
				if (this.enemyShips.get(i).get(j) == destroyedShip && true) {
					destroyedByBombEnemyShips.add(this.enemyShips.get(i).get(j));
					this.enemyShips.get(i).get(j).destroy();
					this.logger.info("Destroyed ship in ("
							+ i + "," + j + ")");
					howManyEnemyIsDead++;
					for(int n = 0; n < 8; n++){
						int nx = i + dx[n]; int ny = j + dy[n];
						if(!(nx >= 0 && nx <= this.enemyShips.size() - 1 && ny >= 0 && ny <= this.enemyShips.get(nx).size() - 1)) continue;
						if(this.enemyShips.get(nx).get(ny).isDestroyed()) continue;
						destroyedByBombEnemyShips.add(this.enemyShips.get(nx).get(ny));
						this.enemyShips.get(nx).get(ny).destroy();
						this.logger.info("Destroyed ship in ("
								+ nx + "," + ny + ")");
						howManyEnemyIsDead++;
					}
				}
			}
		}


		for(EnemyShip enemyShip : destroyedByBombEnemyShips)
			// Updates the list of ships that can shoot the player.
			if (this.shooters.contains(enemyShip)) {
				int destroyedShipIndex = this.shooters.indexOf(enemyShip);
				int destroyedShipColumnIndex = -1;

				for (List<EnemyShip> column : this.enemyShips)
					if (column.contains(enemyShip)) {
						destroyedShipColumnIndex = this.enemyShips.indexOf(column);
						break;
					}

				EnemyShip nextShooter = getNextShooter(this.enemyShips
						.get(destroyedShipColumnIndex));

				if (nextShooter != null)
					this.shooters.set(destroyedShipIndex, nextShooter);
				else {
					this.shooters.remove(destroyedShipIndex);
					this.logger.info("Shooters list reduced to "
							+ this.shooters.size() + " members.");
				}
			}


		this.shipCount -= howManyEnemyIsDead;
		return destroyedByBombEnemyShips;

	}

	/**
	 * Gets the ship on a given column that will be in charge of shooting.
	 * 
	 * @param column
	 *            Column to search.
	 * @return New shooter ship.
	 */
	public final EnemyShip getNextShooter(final List<EnemyShip> column) {
		Iterator<EnemyShip> iterator = column.iterator();
		EnemyShip nextShooter = null;
		while (iterator.hasNext()) {
			EnemyShip checkShip = iterator.next();
			if (checkShip != null && !checkShip.isDestroyed())
				nextShooter = checkShip;
		}

		return nextShooter;
	}

	/**
	 * Returns an iterator over the ships in the formation.
	 * 
	 * @return Iterator over the enemy ships.
	 */
	@Override
	public final Iterator<EnemyShip> iterator() {
		Set<EnemyShip> enemyShipsList = new HashSet<EnemyShip>();

		for (List<EnemyShip> column : this.enemyShips)
			for (EnemyShip enemyShip : column)
				enemyShipsList.add(enemyShip);

		return enemyShipsList.iterator();
	}

	/**
	 * Checks if there are any ships remaining.
	 * 
	 * @return True when all ships have been destroyed.
	 */
	public final boolean isEmpty() {
		return this.shipCount <= 0;
	}
}
