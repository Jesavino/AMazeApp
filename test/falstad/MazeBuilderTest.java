/**
 * 
 */
package falstad;

import static org.junit.Assert.*;

import org.junit.Test;


/**
 * @author Jesavino and cmedgington
 *
 */
public class MazeBuilderTest {
	
	/**
	 * @return A new maze object
	 */
	private Maze createMaze() {
		
		return new MockMaze();
	}

	@Test
	/**
	 * Creates a mockMaze object, builds the mock maze object, and makes sure that object created has the correct output. 
	 */
	public void constructorTest() {
		final int width = 5;
		final int height = 5;
		final int roomCt = 1;
		final int pc = 1;
		
		Maze testMaze = createMaze();
		MazeBuilder mazeBuilder = new MazeBuilder();
		
		mazeBuilder.build(testMaze, width, height, roomCt, pc);
		
		try{ mazeBuilder.buildThread.join();
		
		} catch (Exception e) {System.out.println("DARN");
			
		}
		String cells =  testMaze.mazecells.toString();
		
		assertFalse("".equalsIgnoreCase(cells));
		
	}
	
	
	
	@Test
	public void constructorArgTest() {
		final int width = 5;
		final int height = 5;
		final int roomCt = 1;
		final int pc = 1;
		
		Maze testMaze = createMaze();
		MazeBuilder mazeBuilder = new MazeBuilder(false);
		
		mazeBuilder.build(testMaze, width, height, roomCt, pc);
		
		try{ mazeBuilder.buildThread.join();
		
		} catch (Exception e) {System.out.println("DARN");
			
		}
		String cells =  testMaze.mazecells.toString();
		
		assertFalse("".equalsIgnoreCase(cells));
		
	}
	
	@Test
	public void testWidthAndHeight() {
		final int width = 5;
		final int height = 5;
		final int roomCt = 1;
		final int pc = 1;
		
		Maze testMaze = createMaze();
		MazeBuilder mazeBuilder = new MazeBuilder();
		mazeBuilder.build(testMaze, width, height, roomCt, pc);
		
		try{ mazeBuilder.buildThread.join();
		
			} catch (Exception e) {System.out.println("DARN");	
		}
		
		assertTrue(mazeBuilder.width == width);
		assertTrue(mazeBuilder.height == height);
		assertTrue(mazeBuilder.expectedPartiters == pc);
		
		
	}
	
	@Test
	/**
	 * Makes a maze with one room, looks to see if that room is created
	 */
	public void testRooms() {
		final int width = 100;
		final int height = 100;
		final int roomCt = 1;
		final int pc = 60;
		
		Maze testMaze = createMaze();
		MazeBuilder mazeBuilder = new MazeBuilder();
		
		mazeBuilder.build(testMaze, width, height, roomCt, pc);
		try{ mazeBuilder.buildThread.join();
		
		} 	catch (Exception e) {System.out.println("DARN");	
			}
		
		assertTrue(containsRoom(width, height, mazeBuilder.cells));
		
		
	}
	@Test
	/**
	 * Compares the given solution to the max distance that are possible for exit. 
	 */
	public void testSolutionExists() {
		final int height = 10;
		final int width = 10;
		final int roomCt = 1;
		final int pc = 10;
		int dist;
		int right = 0;
		int forward = 0;
		int left = 0;
		int back = 0;
		
		Maze testMaze = createMaze();
		MazeBuilder mazeBuilder = new MazeBuilder();
		
		mazeBuilder.build(testMaze, width, height, roomCt, pc);
		try{ mazeBuilder.buildThread.join();
		
		} 	catch (Exception e) {System.out.println("DARN");	
			}
		
		dist = mazeBuilder.dists.getMaxDistance();
		assertTrue(dist <= (height * width));
		
		BasicRobot robot = new BasicRobot(testMaze);
		
		try { robot.rotate(-90);
		} catch (UnsupportedArgumentException e) {System.out.println("Bad Dir!");
		}
		
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
		
		int[] robotArray = robot.getCurrentPosition();
		
		System.out.printf("x: %d  y: %d  deg: %d \n", robotArray[0], robotArray[1], robot.currDeg);
		System.out.println(testMaze.mazecells.toString());
		System.out.println(forward);
			
	}
	
	@Test
	/**
	 * Simple test for getSign() by checking a few numbers
	 */
	public void testGetSign() {
		final int smallPos = 1;
		final int smallNeg = -1;
		final int largePos = 100;
		final int largeNeg = -99;
		final int zero = 0;
		
		
		assertTrue(MazeBuilder.getSign(smallPos) == 1);
		assertTrue(MazeBuilder.getSign(smallNeg) == -1);
		assertTrue(MazeBuilder.getSign(largePos) == 1);
		assertTrue(MazeBuilder.getSign(largeNeg) == -1);
		assertTrue(MazeBuilder.getSign(zero) == 0);
				
		
	}
	
	/**
	 * method for checking if a room has been created. Called by testRooms()
	 * @param width
	 * @param height
	 * @param c the cells grid
	 * @return boolean
	 */
	private boolean containsRoom(int width, int height, Cells c) {
		int x,y;
		
		for(x = 0; x < width; x++) {
			for(y=0; y < height; y++) {
				if(c.isInRoom(x,y)) 
					return true;
				
			}
		}
		return false;
	}
}
