package falstad;

public class ManualDriver extends RobotDrivers {
	float energyConsumed;
	public BasicRobot robot;
	int pathLength;
	
	public ManualDriver() {
		energyConsumed = 0;
		
		
	}
	
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
		
		
	}

	/**
	 * Drives the robot towards the exit given it exists and given the robot's energy supply lasts long enough. 
	 * @return true if driver successfully reaches the exit, false otherwise
	 * @throws exception if robot stopped due to an accident
	 */
	public boolean drive2Exit() throws Exception {
		
		if(robot.hasStopped()) {
			throw new Exception();
		}
		
		if(robot.isAtGoal()) {
			return true;
		}
		return false;

	}
	
	/**
	 * Returns the total energy consumption of the journey
	 */
	public float getEnergyConsumption() {
		
		return energyConsumed;
	}
	
	/**
	 * Returns the total length of the journey in number of cells traversed. The initial position counts as 0. 
	 */
	public int getPathLength() {
		return pathLength;
		
	}
	

	@Override
	public void setRobot(Robot r) throws UnsuitableRobotException {
		// TODO Auto-generated method stub
		
	}
	
	/**
	 * move the robot forward one step
	 */
	public void stepForward() throws HitObstacleException {	
		try {robot.move(1, true);
		} catch (HitObstacleException o ) {
			throw new HitObstacleException();
		}
	}
	
	/**
	 * move the robot one step backward
	 */
	public void stepBackward() throws HitObstacleException {
		
		try {robot.move(1, false);
		} catch (HitObstacleException o) {
			throw new HitObstacleException();
		}
	}
	
	/**
	 * turn left
	 */
	public void turnLeft() {
		try{ robot.rotate(90);
		} catch (UnsupportedArgumentException u) {
			// do nothing as it wont happen
		}
	}
	/**
	 * turn right
	 */
	public void turnRight() {
		try{ robot.rotate(-90);
		} catch (UnsupportedArgumentException u) {
			// do nothing as it wont happen
		}
	}
	
	
	
	
}