package falstad;

import java.awt.Color;

/**
 * Uses the distance matrix to correctly move the robot. 
 * we move the robot to the place with the shorter distance of the 4 paths. 
 * @author Jesavino
 *
 */
public class Wizard extends ManualDriver {
	
	private int[][] distance;
	
	
	/**
	 * Initialize the distance object which the algorithm will follow 
	 * @param dists
	 */
	public Wizard(Distance dists) {
		distance = dists.getDists();
	}
	
	@Override
	public boolean drive2Exit() throws Exception {
		boolean left;
		boolean forward;
		int randCount;
		int rand = 0;
		boolean right;
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
			right = false;
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
				if(robot.distanceToObstacleOnRight() != 0) {
					right = true;
					randCount++;
				}
			} catch (UnsupportedMethodException e) {
				 // wont get here as we know what is passed into Gambler
			}
			
			// if no moves can be made
			if(!left && !forward && !right) {
				try {
					robot.rotate(-90);
					robot.rotate(-90);
					continue;
				} catch (UnsupportedArgumentException e) {
					// again, wont get here
				}
			}
			// only random if can move forward and left
			if(randCount == 3) {
				int[] currDir = robot.getCurrentDirection();
				int[] currPos = robot.getCurrentPosition();
				int forwardCt = distance[ currDir[0] + currPos[0] ][ currDir[1] + currPos[1] ];
				int leftCt = calcDistanceLeft(currPos[0] , currPos[1]);
				int rightCt = calcDistanceRight(currPos[0], currPos[1]);
				
				int min = Math.min(forwardCt, Math.min(rightCt, leftCt));
				
				if(min == rightCt) {
					left = forward = false;
				}
				else if(min == leftCt)
					right = forward = false;
				else
					left = right = false;					
			}
			if(randCount == 2) {
				if(forward && left){
					int[] currDir = robot.getCurrentDirection();
					int[] currPos = robot.getCurrentPosition();
					int forwardCt = distance[ currDir[0] + currPos[0] ][ currDir[1] + currPos[1] ];
					int leftCt = calcDistanceLeft(currPos[0] , currPos[1]);
					
					int min = Math.min(forwardCt, leftCt);
					if(min == leftCt)
						forward = false;
					else
						left = false;
				}
				else if(forward && right) {
					int[] currDir = robot.getCurrentDirection();
					int[] currPos = robot.getCurrentPosition();
					int forwardCt = distance[ currDir[0] + currPos[0] ][ currDir[1] + currPos[1] ];
					int rightCt = calcDistanceRight(currPos[0] , currPos[1]);
					
					int min = Math.min(forwardCt, rightCt);
					if(min == rightCt)
						forward = false;
					else
						right = false;
				}
				else {
					int[] currDir = robot.getCurrentDirection();
					int[] currPos = robot.getCurrentPosition();
					int leftCt = calcDistanceLeft(currPos[0] , currPos[1]);
					int rightCt = calcDistanceRight(currPos[0] , currPos[1]);
					
					int min = Math.min(leftCt, rightCt);
					if(min == rightCt)
						left = false;
					else
						right = false;
					
				}
				
				
			}
			
			
			// make the move as necessary since there is only one choice
			if(forward)
				try {
					
					robot.move(1, true);
				} catch (HitObstacleException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();					}
			else if(right) {
				try {
					robot.rotate(-90); // turn then move
					
					robot.move(1, true);
					} catch (UnsupportedArgumentException | HitObstacleException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						break;
					}
			}
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
	/**
	 * used to calculate the count of a visited map to the left
	 */
	private int calcDistanceLeft(int currX , int currY ) {
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
		return distance[ currX + currDir[0] ][currY + currDir[1]];		
	}
	/**
	 * used to calculate the count of a visited map to the left
	 */
	private int calcDistanceRight(int currX , int currY ) {
		int[] currDir = new int[2];
		int currDeg = robot.currDeg;
		
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
			currDir[0] = -1;
			currDir[1] = 0;
			break;
		case 270:
			currDir[0] = -1;
			currDir[1] = 0;
			break;
		}
		return distance[ currX + currDir[0] ][currY + currDir[1]];		
	}
}
