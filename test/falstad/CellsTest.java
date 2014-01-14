/**
 * 
 */
package falstad;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * @author Jesavino and Cmedgington
 *
 */
public class CellsTest {
	/**
	 * Tests the constructor call by inputing a simple board. Also tests getCells to check the construction
	 */
	@Test
	public final void testInit() {
		int[][] target = new int[][] {
				{0 , 0 , 1, 1}, { 0, 1, 1, 0}, {1, 0 , 0 , 0} , { 0 , 1, 0, 1}
		};
		
		Cells c = new Cells(target);
		
		assertTrue(c.getCells(0,0) == target[0][0]);
		assertTrue(c.getCells(3,2) == target[3][2]);
		
	}
	
	/**
	 * Test's the canGo method on the simple 2v2 created maze. Also tests setting bits to unvisitied 
	 */
	@Test
	public final void testMotion() {
		int[][] twoByTwo = new int[][] {
				{53, 25},
				{28, 22}
		};
		Cells testGrid = new Cells(twoByTwo);
		
		
		assertTrue(testGrid.canGo(1, 0, -1, 0));
		assertTrue(testGrid.canGo(1,0,0,1));
		assertFalse(testGrid.canGo(0, 0, 0,-1));
		assertTrue(testGrid.canGo(0,0, 1,0));
		assertFalse(testGrid.canGo(0, 0, 1, 1));
	}
	
	/**
	 * Tests getMaks() call. Trivial. 
	 *
	 */
	@Test
	public final void getMasks() {
		
		int[] masks = Constants.MASKS;
		
		assertTrue(Cells.getMasks() == masks);		
	}
	/**
	 * Tests the deleteWall method
	 */
	@Test
	public final void deleteWall() {
		int[][] target = new int[][] {
				{0, 04}, {0, 0}
		};
		Cells c = new Cells(target);
		c.initialize();
		
		c.deleteWall(0,1,0,-1);
		assertTrue(c.hasNoWallOnBottom(0, 0));
		assertTrue(c.hasNoWallOnTop(0, 1));
	}
	/**
	 * Test's hasMaskedBits()
	 */
	@Test
	public final void testMaskedBits() {
		int[][] grid = {
				{53, 25} , {28 , 22}				
		};
		Cells c = new Cells(grid);
		final int bitRight = 256;
		final int bitUp = 32;
			
		
		assertFalse(c.hasMaskedBitsTrue(0,0,bitRight));
		assertTrue(c.hasMaskedBitsTrue(0, 0, bitUp));
		
	}
	
	/**
	 * Tests the set bit  methods
	 */
	@Test
	public final void testBitSet() {
		Cells c = new Cells(3,3);
		c.initialize();
		final int topLeftInit = 191;
		
		c.setBitToZero(0,0,1); // 191 -> 190
		assertTrue(c.getCells(0,0) == (topLeftInit - 1));
		c.setAllToZero(1, 1);
		assertTrue(c.getCells(1, 1)  == 16);
		c.setVisitedFlagToZero(0, 0);
		assertTrue((c.getCells(0,0) & 16) == 0);
		c.setWallToZero(0, 0, 1, 0);
		assertTrue((c.getCells(0,0) & 8) == 0);
		c.setBoundToZero(0,0,0,-1);
		assertTrue((c.getCells(0,0) & 32) == 0);
		c.setBoundAndWallToOne(0,0,0,-1);
		assertTrue((c.getCells(0, 0) & 33) == 33);
		c.setInRoomToOne(0,0);
		assertTrue((c.getCells(0, 0) & 512) == 512);
		c.setTopToOne(0, 0);
		assertTrue((c.getCells(0,0) & 1) == 1);
		
	}

	/**
	 * Tests the overlapps with room function by setting (0,0) to be a room
	 */
	@Test
	public final void testRoomOverlap() {
		Cells c = new Cells(5,5);
		c.initialize();
		
		c.setInRoomToOne(0, 0);
		assertTrue(c.areaOverlapsWithRoom(1,1,2,2));
		assertFalse(c.areaOverlapsWithRoom(2, 2, 3, 3));
		
	}
	
	/**
	 * Tests the Mark Area as room method
	 */
	@Test
	public final void testMarkAreaAsRoom() {
		Cells c = new Cells(5,5);
		c.initialize();
		
		c.markAreaAsRoom(3, 3, 1, 1, 3, 3);
		assertTrue(areaIsRoom(1,1,3,3,c));
		
	}
	
	/**
	 * Test hasMaskedbits
	 */
	@Test
	public final void testHasMaskedBits() {
		Cells c = new Cells(1,1);
		c.initialize();
		
		assertTrue(c.hasWallOnRight(0,0));
		assertTrue(c.hasWallOnLeft(0,0));
		assertTrue(c.hasWallOnTop(0,0));
		assertTrue(c.hasWallOnBottom(0,0));
		assertFalse(c.hasNoWallOnBottom(0, 0));
		assertFalse(c.hasNoWallOnTop(0, 0));
		assertFalse(c.hasNoWallOnLeft(0, 0));
		assertFalse(c.hasNoWallOnRight(0, 0));
		assertFalse(c.hasMaskedBitsFalse(0, 0, 32));
		assertTrue(c.hasMaskedBitsGTZero(0, 0, 32));
		
		c.setAllToZero(0, 0);
		assertTrue(c.hasNoWallOnBottom(0, 0));
		assertTrue(c.hasNoWallOnTop(0, 0));
		assertTrue(c.hasNoWallOnLeft(0, 0));
		assertTrue(c.hasNoWallOnRight(0, 0));
		c.setBitToZero(0, 0, 32);
		assertTrue(c.hasMaskedBitsFalse(0, 0, 32));
		assertFalse(c.hasMaskedBitsGTZero(0, 0, 32));
		
	}
	
	/**
	 * Tests the toString method on a 1v1 board and a 2v2 board
	 */
	@Test
	public final void testToString()	{
		Cells c1 = new Cells(1,1);
		int[][] twoByTwo = new int[][] {
				{53, 25},
				{28, 22}
		};
		Cells c2 = new Cells(twoByTwo);
		
		String base = c1.toString();
		String two = c2.toString();
		
		assertFalse("".equalsIgnoreCase(base));
		assertFalse("".equalsIgnoreCase(two));
		
	}
	
	/**
	 * Checks the board to see if all the defined area is a room. Used in testMarkAreaAsRoom()
	 * @param rx Starting x position
	 * @param ry Starting y position
	 * @param x  Bottom Right x position
	 * @param y  bottom right y position
	 * @param c  the Cells array
	 * @return
	 */
	private boolean areaIsRoom (int rx, int ry, int x, int y, Cells c) {
		
		int i, j;
		for( i = rx; i < x ; i++ ){
			for( j = ry; j < y; j++) {
				
				if((c.getCells(i, j) & 512) != 512) return false;
			}
		}
		return true;
	}
	
	
}

