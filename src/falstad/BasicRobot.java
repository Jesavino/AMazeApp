/**
 * 
 */
package falstad;

/**
 * @author Jesavino and Cmedgington
 *
 */
public class BasicRobot implements Robot {
	
	public int batteryCharge;
	
	
	public Maze maze;
	private int[] currPos;
	public int currDeg;
	
	public final int costDistSensing = 1;
	public final int costRotate = 3;
	public final int costMove = 5;
	
	boolean frontSensor;
	boolean backSensor;
	boolean rightSensor;
	boolean leftSensor;
	boolean inMotion;
	
	
	/**
	 * Creates a robot off the maze with all 4 sensors up
	 */
	public BasicRobot(Maze maze) {
		batteryCharge = 2500;
		
		int[] currDir = {maze.dx, maze.dy};
		if(currDir[0] == 0 && currDir[1] == 1) {
			currDeg = 90;
		}
		if(currDir[0] == 0 && currDir[1] == -1) {
			currDeg = 270;
		}
		if(currDir[0] == 1 && currDir[1] == 0) {
			currDeg = 0;
		}
		else
			currDeg = 180;
		
		
		this.maze = maze;
		currPos = new int[2];
		currPos[0] = maze.px;
		currPos[1] = maze.py;
		inMotion = true;
		
		// set all sensors to true
		frontSensor = true;
		backSensor = true;
		rightSensor = true;
		leftSensor = true;
		
	}
	/**
	 * Sensors are placed based on boolean inputs 
	 * @param maze
	 * @param forward
	 * @param left
	 * @param back
	 * @param right
	 */
	public BasicRobot( Maze maze , boolean forward , boolean left, boolean back, boolean right ) {
		batteryCharge = 2500;
		
		int[] currDir = {maze.dx, maze.dy};
		if(currDir[0] == 0 && currDir[1] == 1) {
			currDeg = 90;
		}
		if(currDir[0] == 0 && currDir[1] == -1) {
			currDeg = 270;
		}
		if(currDir[0] == 1 && currDir[1] == 0) {
			currDeg = 0;
		}
		else
			currDeg = 180;
		
		
		this.maze = maze;
		currPos = new int[2];
		currPos[0] = maze.px;
		currPos[1] = maze.py;
		inMotion = true;
		
		// set the sensors
		frontSensor = forward;
		backSensor = back;
		leftSensor = left;
		rightSensor = right;		
	}
	/**
	 * Turns robot on the spot by either 90 or -90 degrees
	 * @param degree
	 * @throws UnsupportedArgumentException
	 */
	public void rotate(int degree) throws UnsupportedArgumentException {
		if(batteryCharge < costRotate) {
			inMotion = false;
			return;
		}
			
		
		if( (degree != 90) && (degree != -90) )
			throw new UnsupportedArgumentException();
		
		if(degree == 90)
			maze.rotate(1);
		else {
			degree = 270;
			maze.rotate(-1);
		}		
		currDeg += degree;
		currDeg = currDeg % 360;
					
		 // update battery charge
		batteryCharge -= costRotate;
	}
	
	/**
	 * Moves robot forward or backward a given number of steps. A step matches a single cell.
	 * Since a robot may only have a distance sensor in its front, driving backwards may happen blindly as distance2Obstacle may not provide values for that direction.
	 * If the robot runs out of energy somewhere on its way, it stops, which can be checked by hasStopped() and by checking the battery level. 
	 * @param distance is the number of cells to move according to the robots current direction if forward = true, opposite direction if forward = false
	 * @param forward specifies if the robot should move forward (true) or backward (false)
	 * @throws HitObstacleException if robot hits an obstacle like a wall or border, which also make the robot stop, i.e. hasStopped() = true 
	 */
	public void move(int distance, boolean forward) throws HitObstacleException {
		
		// move the robot till it cannot move anymore
		while(distance > 0) {
			if (batteryCharge < 5) {
				inMotion = false;
				break;
			}
			int[] robPos = getCurrentPosition();
			
			if(forward) {	
				if(checkMove(currDeg, robPos[0], robPos[1])) {
					throw new HitObstacleException();
				}
				else {
					maze.walk(1);
					maze.notifyViewerRedraw();
					batteryCharge -= costMove;
				}
			}
					
			else{
				if(checkMove((currDeg+180) % 360, robPos[0], robPos[1])) {
					throw new HitObstacleException();
				}
				else {
					maze.walk(-1);
					maze.notifyViewerRedraw();
					batteryCharge -= costMove;
				}
			}
			distance--;
		} 
	}

	/**
	 * Helper method for move()
	 * @param dir
	 * @return true if there is no wall in this direction
	 */
	private boolean checkMove(int dir, int px, int py) {
		int a = 0;
		switch(dir){
		case 0:
			a=0;
			break;
		case 90:
			a=1;
			break;
		case 180:
			a=2;
			break;
		case 270:
			a=3;
			break;
		}
		
		// check if cell has walls in this direction
		// returns true if there are no walls in this direction
		
		return maze.mazecells.hasMaskedBitsTrue(px, py, Constants.MASKS[a]) ;
	}
	
	/**
	 * Provides the current position as (x,y) coordinates for the maze cell as an array of length 2 with [x,y].
	 * Note that 0 <= x < width, 0 <= y < height of the maze. 
	 * @return array of length 2, x = array[0], y=array[1]
	 */
	public int[] getCurrentPosition() {
		int[] currentPos = {maze.px , maze.py};
		
		return currentPos;
	}
	
	/**
	 * Tells if current position is at the goal. Used to recognize termination of a search.
	 * Note that goal recognition is limited by the sensing functionality of robot such that isAtGoal returns false
	 * even if it is positioned directly at the exit but has no distance sensor towards the exit direction. 
	 * @return true if robot is at the goal and has a distance sensor in the direction of the goal, false otherwise
	 */
	public boolean isAtGoal() {
		int[] currentPosition = getCurrentPosition();
		return isEndPosition(currentPosition[0], currentPosition[1]);			

	}
	
	/**
	 * Provides the current direction as (dx,dy) values for the robot as an array of length 2 with [dx,dy].
	 * Note that dx,dy are elements of {-1,0,1} and as in bitmasks masks in Cells.java and dirsx,dirsy in MazeBuilder.java.
	 * 
	 * @return array of length 2, dx = array[0], dy=array[1]
	 */	
	public int[] getCurrentDirection() {
		int[] currDir = new int[2];
		
		switch(currDeg) {
		case 0: 
			currDir[0] = 1;
			currDir[1] = 0;
			break;
		case 90:
			currDir[0] = 0;
			currDir[1] = 1;
			break;
		case 180:
			currDir[0] = -1;
			currDir[1] = 0;
			break;
		case 270:
			currDir[0] = 0;
			currDir[1] = -1;
			break;
		}
		return currDir;
		
		
	}
	/**
	 * The robot has a given battery level (energy level) that it draws energy from during operations. 
	 * The particular energy consumption is device dependent such that a call for distance2Obstacle may use less energy than a move forward operation.
	 * If battery level <= 0 then robot stops to function and hasStopped() is true.
	 * @return current battery level, level is > 0 if operational. 
	 */
	public float getCurrentBatteryLevel() {
		return batteryCharge;
	}
	/**
	 * Gives the energy consumption for a full 360 degree rotation.
	 * Scaling by other degrees approximates the corresponding consumption. 
	 * @return energy for a full rotation
	 */
	public float getEnergyForFullRotation() {
		return costRotate * 4;
	}
	/**
	 * Gives the energy consumption for moving 1 step forward.
	 * For simplicity, we assume that this equals the energy necessary to move 1 step backwards and that scaling by a larger number of moves is 
	 * approximately the corresponding multiple.
	 * @return energy for a single step forward
	 */
	public float getEnergyForStepForward() {
		return costMove;
	}
	/**
	 * Tells if the robot has stopped for reasons like lack of energy, hitting an obstacle, etc.
	 * @return true if the robot has stopped, false otherwise
	 */
	public boolean hasStopped() {
		return !inMotion;
	}
	/**
	 * Tells if a sensor can identify the goal in the robot's current forward direction from the current position.
	 * @return true if the goal (here: exit of the maze) is visible in a straight line of sight
	 * @throws UnsupportedMethodException if robot has no sensor in this direction
	 */
	public boolean canSeeGoalAhead() throws UnsupportedMethodException {
		if(batteryCharge < costDistSensing){
			inMotion = false;
			return false;
		}
		batteryCharge -= costDistSensing;
		if(!frontSensor)
				throw new UnsupportedMethodException();	
			
		int[] robPos = getCurrentPosition();
		int[] robDir = getCurrentDirection();
		while(true) {
			if(isEndPosition(robPos[0], robPos[1])) {
				return true;
			}
			if(!checkMove(currDeg, robPos[0], robPos[1])) {
				robPos[0] += robDir[0];
				robPos[1] += robDir[1];
			}
			else
				return false;
			}
	}
	
	/**
	 * checks if the given position is outside the maze
	 * @param x
	 * @param y
	 * @return true if position is outside, false otherwise
	 */
	private boolean isEndPosition(int x, int y) {
		
		return x < 0 || y < 0 || x >= maze.mazew || y >= maze.mazeh;
	}
	/**
	 * Methods analogous to canSeeGoalAhead but for a the robot's current backward direction
	 * @return true if the goal (here: exit of the maze) is visible in a straight line of sight
	 * @throws UnsupportedMethodException if robot has no sensor in this direction
	 */
	public boolean canSeeGoalBehind() throws UnsupportedMethodException {
		if(batteryCharge < costDistSensing){
			inMotion = false;
			return false;
		}
		batteryCharge -= costDistSensing;
		if(!backSensor)
			throw new UnsupportedMethodException();
			
		int[] robPos = getCurrentPosition();
		int[] robDir = getCurrentDirection();
		while(true) {
			if(isEndPosition(robPos[0], robPos[1])) {
				return true;
			}
			if(!checkMove((currDeg + 180) % 360, robPos[0], robPos[1])) {
				robPos[0] -= robDir[0];
				robPos[1] -= robDir[1];
			}
			else
				return false;
			}		
	}
	/**
	 * Methods analogous to canSeeGoalAhead but for the robot's current left direction (left relative to forward)
	 * @return true if the goal (here: exit of the maze) is visible in a straight line of sight
	 * @throws UnsupportedMethodException if robot has no sensor in this direction
	 */
	public boolean canSeeGoalOnLeft() throws UnsupportedMethodException {
		if(batteryCharge < costDistSensing){
			inMotion = false;
			return false;
		}
		batteryCharge -= costDistSensing;
		if(!leftSensor)
			throw new UnsupportedMethodException();
			
		int[] robPos = getCurrentPosition();
		int[] currDir = new int[2];
		
		switch(currDeg) {
		case 0: 
			currDir[0] = 0;
			currDir[1] = 1;
			break;
		case 90:
			currDir[0] = -1;
			currDir[1] = 0;
			break;
		case 180:
			currDir[0] = 0;
			currDir[1] = -1;
			break;
		case 270:
			currDir[0] = 1;
			currDir[1] = 0;
			break;
		}
		
		while(true) {
			if(isEndPosition(robPos[0], robPos[1])) {
				return true;
			}
			if(!checkMove((currDeg + 90) % 360, robPos[0], robPos[1])) {
				robPos[0] += currDir[0];
				robPos[1] += currDir[1];
			}
			else
				return false;
			}		
	}
	/**
	 * Methods analogous to canSeeGoalAhead but for the robot's current right direction (right relative to forward)
	 * @return true if the goal (here: exit of the maze) is visible in a straight line of sight
	 * @throws UnsupportedMethodException if robot has no sensor in this direction
	 */
	public boolean canSeeGoalOnRight() throws UnsupportedMethodException {
		if(batteryCharge < costDistSensing){
			inMotion = false;
			return false;
		}
		batteryCharge -= costDistSensing;
		if(!rightSensor)
			throw new UnsupportedMethodException();
			
		int[] robPos = getCurrentPosition();
		int[] currDir = new int[2];
		
		switch(currDeg) {
		case 0: 
			currDir[0] = 0;
			currDir[1] = -1;
			break;
		case 90:
			currDir[0] = 1;
			currDir[1] = 0;
			break;
		case 180:
			currDir[0] = 0;
			currDir[1] = 1;
			break;
		case 270:
			currDir[0] = -1;
			currDir[1] = 0;
			break;
		}
		while(true) {
			if(isEndPosition(robPos[0], robPos[1])) {
				return true;
			}
			if(!checkMove((currDeg + 270) % 360, robPos[0], robPos[1])) {
				robPos[0] += currPos[0];
				robPos[1] += currPos[1];
			}
			else
				return false;
			}
	}

	/**
	 * Tells the distance to an obstacle (a wall or border) for a the robot's current forward direction.
	 * Distance is measured in the number of cells towards that obstacle, e.g. 0 if current cell has a wall in this direction
	 * @return number of steps towards obstacle if obstacle is visible in a straight line of sight, Integer.MAX_VALUE otherwise
	 * @throws UnsupportedArgumentException if not supported by robot
	 */
	public int distanceToObstacleAhead() throws UnsupportedMethodException {
		if(batteryCharge < costDistSensing){
			inMotion = false;
			return 0;
		}
		batteryCharge -= costDistSensing;
		if(!frontSensor)
			throw new UnsupportedMethodException();
		
		int counter = 0;
		int[] currPosition = getCurrentPosition();
		int[] currDir = getCurrentDirection();
		
		while(!checkMove(currDeg, currPosition[0] , currPosition[1] )) {
			if(isEndPosition(currPosition[0], currPosition[1])){
				break;
			}
			counter++;
			
			
			currPosition[0] += currDir[0];
			currPosition[1] += currDir[1];		
			
		}
		return counter;
	}
	/**
	 * Methods analogous to distanceToObstacleAhead but for the robot's current left direction (left relative to forward)
	 * @return number of steps towards obstacle if obstacle is visible in a straight line of sight, Integer.MAX_VALUE otherwise
	 * @throws UnsupportedArgumentException if not supported by robot
	 */
	public int distanceToObstacleOnLeft() throws UnsupportedMethodException {
		if(batteryCharge < costDistSensing){
			inMotion = false;
			return 0;
		}
		batteryCharge -= costDistSensing;
		if(!frontSensor)
			throw new UnsupportedMethodException();
		
		int counter = 0;
		int[] currPosition = getCurrentPosition();
		
		int[] currDir = new int[2];
		
		switch(currDeg) {
		case 0: 
			currDir[0] = 0;
			currDir[1] = 1;
			break;
		case 90:
			currDir[0] = -1;
			currDir[1] = 0;
			break;
		case 180:
			currDir[0] = 0;
			currDir[1] = -1;
			break;
		case 270:
			currDir[0] = 1;
			currDir[1] = 0;
			break;
		}
		
		while(!checkMove((currDeg + 90) % 360, currPosition[0] , currPosition[1] )) {
			if(isEndPosition(currPosition[0], currPosition[1]))
				break;
			counter++;
			
			currPosition[0] += currDir[0];
			currPosition[1] += currDir[1];		
			
		}
		return counter;
	}
	/**
	 * Methods analogous to distanceToObstacleAhead but for the robot's current right direction (right relative to forward)
	 * @return number of steps towards obstacle if obstacle is visible in a straight line of sight, Integer.MAX_VALUE otherwise
	 * @throws UnsupportedArgumentException if not supported by robot
	 */
	public int distanceToObstacleOnRight() throws UnsupportedMethodException {
		if(batteryCharge < costDistSensing){
			inMotion = false;
			return 0;
		}
		batteryCharge -= costDistSensing;
		if(!frontSensor)
			throw new UnsupportedMethodException();
		
		int counter = 0;
		int[] currPosition = getCurrentPosition();
		
		int[] currDir = new int[2];
		
		switch(currDeg) {
		case 0: 
			currDir[0] = 0;
			currDir[1] = -1;
			break;
		case 90:
			currDir[0] = 1;
			currDir[1] = 0;
			break;
		case 180:
			currDir[0] = 0;
			currDir[1] = 1;
			break;
		case 270:
			currDir[0] = -1;
			currDir[1] = 0;
			break;
		}
		
		while(!checkMove((currDeg + 270) % 360, currPosition[0] , currPosition[1] )) {
			if(isEndPosition(currPosition[0], currPosition[1]))
				break;
			counter++;
			
			currPosition[0] += currDir[0];
			currPosition[1] += currDir[1];		
			
		}
		return counter;
	}
	/**
	 * Methods analogous to distanceToObstacleAhead but for a the robot's current backward direction
	 * @return number of steps towards obstacle if obstacle is visible in a straight line of sight, Integer.MAX_VALUE otherwise
	 * @throws UnsupportedArgumentException if not supported by robot
	 */
	public int distanceToObstacleBehind() throws UnsupportedMethodException {
		if(batteryCharge < costDistSensing){
			inMotion = false;
			return 0;
		}
		batteryCharge -= costDistSensing;
		if(!backSensor)
			throw new UnsupportedMethodException();
		
		int counter = 0;
		int[] currPosition = getCurrentPosition();
		int[] currDir = getCurrentDirection();
		
		while(!checkMove((currDeg + 180) % 360, currPosition[0] , currPosition[1] )) {
			if(isEndPosition(currPosition[0], currPosition[1]))
				break;
			counter++;
			
			currPosition[0] -= currDir[0];
			currPosition[1] -= currDir[1];		
			
		}
		return counter;
		
	}
	
	/**
	 * 
	 * @return current X position
	 */
	public int getCurrX() {
		return maze.px;
	}
	/**
	 * 
	 * @return current Y position
	 */
	public int getCurrY() {
		return maze.py;
	}
	
	
	
	

}
