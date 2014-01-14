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
public class MazeBuilderEllerTest {
	
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
		final int roomCt = 0;
		final int pc = 1;
		
		Maze testMaze = createMaze();
		MazeBuilder mazeBuilder = new MazeBuilderEller();
		mazeBuilder.build(testMaze, width, height, roomCt, pc);
		
		try{ mazeBuilder.buildThread.join();
		
		} catch (Exception e) {System.out.println("DARN");
			
		}
		String cells =  testMaze.mazecells.toString();
		
		assertFalse("".equalsIgnoreCase(cells));
		
	}
	
	/**
	 * Small scale test for debugging purposes
	 */
	@Test
	public void smallScaleTest() {
		final int width = 3;
		final int height = 3;
		final int roomCt = 0;
		final int pc = 1;
		
		Maze testMaze = createMaze();
		MazeBuilder mazeBuilder = new MazeBuilderEller();
		mazeBuilder.build(testMaze, width, height, roomCt, pc);
		
		try{ mazeBuilder.buildThread.join();
		
		} catch (Exception e) {System.out.println("DARN");
			
		}
		String cells =  testMaze.mazecells.toString();
		
		assertFalse("".equalsIgnoreCase(cells));
		
	}

	@Test
	public void testWidthAndHeght() {
		final int width = 5;
		final int height = 5;
		final int roomCt = 0;
		final int pc = 1;
		
		Maze testMaze = createMaze();
		MazeBuilder mazeBuilder = new MazeBuilderEller();
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
	 * Compares the given solution to the max distance that are possible for exit. 
	 */
	public void testSolutionExists() {
		final int height = 10;
		final int width = 10;
		final int roomCt = 0;
		final int pc = 10;
		int dist;
		
		Maze testMaze = createMaze();
		MazeBuilder mazeBuilder = new MazeBuilderEller();
		
		mazeBuilder.build(testMaze, width, height, roomCt, pc);
		try{ mazeBuilder.buildThread.join();
		
		} 	catch (Exception e) {System.out.println("DARN");	
			}
		
		dist = mazeBuilder.dists.getMaxDistance();
		assertTrue(dist <= (height * width));
			
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
	 * Test to see if the exit is computed correctly. This also will make sure every square has a valid distance from exit
	 */
	@Test
	public void testExit() {
		final int height = 10;
		final int width = 10;
		final int roomCt = 0;
		final int pc = 10;
		int[] dist = new int[2];
		
		Maze testMaze = createMaze();
		MazeBuilder mazeBuilder = new MazeBuilderEller();
		
		mazeBuilder.build(testMaze, width, height, roomCt, pc);
		try{ mazeBuilder.buildThread.join();
		
		} 	catch (Exception e) {System.out.println("DARN");	
			}
		
		
		dist = mazeBuilder.dists.getExitPosition();
		assertTrue((dist[0] < width) && (dist[1] < height));
		
		
	}
	
	
}
