/**
 * 
 */
package falstad;

/**
 * @author Jesavino
 *
 */
public class CuriousGambler extends ManualDriver {
	SingleRandom random;
	int[][] visitedMap;
	
	
	public CuriousGambler() {
		energyConsumed = 0;
		random = SingleRandom.getRandom();
	}
	@Override
	/**
	 * Assigns a robot platform to the driver. Not all robot configurations may be suitable such that the method 
	 * will throw an exception if the robot does not match minimal configuration requirements, e.g. providing a sensor
	 * to measure the distance to an object in a particular direction. 
	 * @param r robot to operate
	 * @throws UnsuitableRobotException if driver cannot operate the given robot
	 */
	
	public void setRobot(BasicRobot r) throws UnsuitableRobotException {
		
		// check for at least one sensor
		if(!r.frontSensor && !r.backSensor && !r.rightSensor && !r.leftSensor)
			throw new UnsuitableRobotException();
		robot = r;
		visitedMap = new int[robot.maze.mazew][robot.maze.mazeh];
	}
	
	@Override
	public boolean drive2Exit() throws Exception {
		boolean left;
		boolean forward;
		int randCount;
		int rand = 0;
		while(true) {
			
			// check to see if we can see the goal
			if(robot.canSeeGoalAhead() ) {
				while(!robot.isAtGoal())
					robot.move(1, true);
				return true;
			}
			if(robot.canSeeGoalOnLeft()) {
				robot.rotate(90);
				while(!robot.isAtGoal())
					robot.move(1, true);
				return true;
			}
				
			left = false;
			forward = false;
			randCount = 0;
			
			// sense in the two directions
			try {
				if(robot.distanceToObstacleAhead() != 0) {
					forward = true;
					randCount++;
				}
				if(robot.distanceToObstacleOnLeft() != 0) {
					left = true;
					randCount++;
				}
			} catch (UnsupportedMethodException e) {
				 // wont get here as we know what is passed into Gambler
			}
			
			// if no moves can be made
			if(!left && !forward) {
				try {
					robot.rotate(-90);
					robot.rotate(-90);
					continue;
				} catch (UnsupportedArgumentException e) {
					// again, wont get here
				}
			}
			// only random if can move forward and left
			if(randCount == 2) {
				int[] currDir = robot.getCurrentDirection();
				int[] currPos = robot.getCurrentPosition();
				int forwardCt = visitedMap[ currDir[0] + currPos[0] ][ currDir[1] + currPos[1] ];
				int leftCt = calcVisitedLeft(currPos[0] , currPos[1]);
				
				if(forwardCt != leftCt){
					if(forwardCt < leftCt)
						left = false;
					else
						forward = false;
				}
					
				else {
					rand = random.nextIntWithinInterval(1, randCount);
				
					// 1 is forward, 2 is left
					if(rand == 1)
						left = false;
					else
						forward = false;
				}
			}
			// make the move as necessary since there is only one choice
			if(forward)
				try {
					visitedMap[robot.getCurrX()][robot.getCurrY()] += 1;
					robot.move(1, true);
				} catch (HitObstacleException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();					}
			else {
				try {
					robot.rotate(90); // turn then move
					visitedMap[robot.getCurrX()][robot.getCurrY()] += 1;
					robot.move(1, true);
					} catch (UnsupportedArgumentException | HitObstacleException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						break;
					}
				}
			if(robot.isAtGoal()) {
				return true;
			}
			if(robot.hasStopped()) {
				throw new Exception();
			}
			
			
			
			
		}
		return false;
		
	}
	/**
	 * used to calculate the count of a visited map to the left
	 */
	private int calcVisitedLeft(int currX , int currY ) {
		int[] currDir = new int[2];
		int currDeg = robot.currDeg;
		
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
		return visitedMap[ currX + currDir[0] ][currY + currDir[1]];		
	}
}

