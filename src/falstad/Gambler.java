/**
 * 
 */
package falstad;

/**
 * @author Jesavino
 *
 */
public class Gambler extends RobotDrivers {
	SingleRandom random;
	
	public Gambler() {
		energyConsumed = 0;
		random = SingleRandom.getRandom();
	}
	
	
	@Override
	/**
	 * uses a random number generator to move the robot. If the robot cannot go left or forward, the robot is turned around. 
	 */
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
				rand = random.nextIntWithinInterval(1, randCount);
				
				// 1 is forward, 2 is left
				if(rand == 1)
					left = false;
				else
					forward = false;
			}
			// make the move as necessary since there is only one choice
			if(forward)
				try {
					robot.move(1, true);
				} catch (HitObstacleException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();					}
			else {
				try {
					robot.rotate(90); // turn then move
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

}
