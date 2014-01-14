package falstad;

import static org.junit.Assert.*;

import org.junit.Test;

public class BasicRobotTest {

	private Maze createMaze() {
		
		return new MockMaze();
	}
	
	@Test
	/**
	 * Test the rotate method
	 */
	public void RotateTest() {
		final int height = 10;
		final int width = 10;
		final int roomCt = 1;
		final int pc = 10;
		final int[] faceRight = {1, 0};
		final int[] faceLeft = {-1 , 0};
		final int[] faceNorth = {0, 1};
		final int[] faceSouth = {0, -1};
		
		
		// Maze testMaze = createMaze();
		MazeApplication newMaze = new MazeApplication();
		newMaze.maze.build(1);
		BasicRobot robot = new BasicRobot(newMaze.maze);
		
		/** 
		MazeBuilder mazeBuilder = new MazeBuilder();
		
		mazeBuilder.build(newMaze.maze, width, height, roomCt, pc);
		try{ mazeBuilder.buildThread.join();
		} 	catch (Exception e) {System.out.println("DARN");	
			}
		*/
		
		try { robot.rotate(-90);
		} 	catch (UnsupportedArgumentException e) {System.out.println("Bad Dir!");
			}
		
		assertTrue(robot.getCurrentDirection()[0] == faceRight[0]);
		assertTrue(robot.getCurrentDirection()[1] == faceRight[1]);
		
		try { robot.rotate(90);
		} 	catch (UnsupportedArgumentException e) {System.out.println("Bad Dir!");
			}
		
		
		assertTrue(robot.getCurrentDirection()[0] == faceNorth[0]);
		assertTrue(robot.getCurrentDirection()[1] == faceNorth[1]);
		
		try { robot.rotate(90);
		} 	catch (UnsupportedArgumentException e) {System.out.println("Bad Dir!");
			}
		
		assertTrue(robot.getCurrentDirection()[0] == faceLeft[0]);
		assertTrue(robot.getCurrentDirection()[1] == faceLeft[1]);
		
		try { robot.rotate(90);
		} 	catch (UnsupportedArgumentException e) {System.out.println("Bad Dir!");
			}
		
		assertTrue(robot.getCurrentDirection()[0] == faceSouth[0]);
		assertTrue(robot.getCurrentDirection()[1] == faceSouth[1]);
	
	}
	
	@Test
	/**
	 * Test the move method
	 * 
	 */
	public void MoveForwardTest() {
		final int height = 10;
		final int width = 10;
		final int roomCt = 1;
		final int pc = 10;
		boolean flag = false;
		
		MazeApplication newMaze = new MazeApplication();
		MazeBuilder mazeBuilder = new MazeBuilder();
		
		mazeBuilder.build(newMaze.maze, width, height, roomCt, pc);
		try{ mazeBuilder.buildThread.join();
		
		} 	catch (Exception e) {System.out.println("DARN");	
			}
		
		BasicRobot robot = new BasicRobot(newMaze.maze);
		
		try {
			int x = robot.distanceToObstacleAhead();
			robot.move(x, true);			
		}
		catch (HitObstacleException e) {
			fail("Unable to calc dists correctly");
		} catch (UnsupportedMethodException e) {
			System.out.println("This will never be called");
		}
		
		try {
			robot.move(1, true);
		}
		catch (HitObstacleException e) {
			flag = true;
		}
		assertTrue(flag);
		
		
	}
	
	
	@Test
	/**
	 * Tests the distance methods based on a correct test of the forward sensor. 
	 */
	public void TestSensors() {
		final int height = 10;
		final int width = 10;
		final int roomCt = 1;
		final int pc = 10;
		
		int right = 0;
		int forward = 0;
		int left = 0;
		int back = 0;
		
		MazeApplication newMaze = new MazeApplication();
		MazeBuilder mazeBuilder = new MazeBuilder();
		
		mazeBuilder.build(newMaze.maze, width, height, roomCt, pc);
		try{ mazeBuilder.buildThread.join();
		
		} 	catch (Exception e) {System.out.println("DARN");	
			}
		
		BasicRobot robot = new BasicRobot(newMaze.maze);
		
		
		
		
		try {
			forward = robot.distanceToObstacleAhead();			
		} catch (UnsupportedMethodException e) {
			System.out.println("This will never be called");
		}
		
		try { robot.rotate(90);
		} catch (UnsupportedArgumentException e) {System.out.println("Bad Dir!");
		}
		try {
			right = robot.distanceToObstacleOnRight();			
		} catch (UnsupportedMethodException e) {
			System.out.println("This will never be called");
		}
		assertTrue(right == forward);
		
		try { robot.rotate(90);
		} catch (UnsupportedArgumentException e) {System.out.println("Bad Dir!");
		}
		try {
			back = robot.distanceToObstacleBehind();			
		} catch (UnsupportedMethodException e) {
			System.out.println("This will never be called");
		}
		assertTrue(back == forward);
		
		try { robot.rotate(90);
		} catch (UnsupportedArgumentException e) {System.out.println("Bad Dir!");
		}
		try {
			left = robot.distanceToObstacleOnLeft();			
		} catch (UnsupportedMethodException e) {
			System.out.println("This will never be called");
		}
		assertTrue(left == forward);
	}
}
