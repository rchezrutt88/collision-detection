import java.util.*;

/**
 *
 * A non-empty collection of points organized in a hierarchical binary tree structure.
 *
 */
public class BlockTree 
{
	/**
	 * The bounding box of the blocks contained in this tree. 
	 */
	private BBox box;

	/**
	 * Number of blocks contained in this tree.
	 */
	private int numBlocks;

	/**
	 * Left child (subtree):
	 * (left  == null) iff (this is a leaf node)
	 */
	private BlockTree left;

	/**
	 * Right child (subtree):
	 * (right == null) iff (this is a leaf node)
	 */
	private BlockTree right;

	/**
	 * Block (of a leaf node):
	 * (block == null) iff (this is an intermediate node)
	 */
	private Block block; 

	// REMARK:
	// Leaf node: left, right == null && block != null
	// Intermediate node: left, right != null && block == null

	/**
	 * Construct a binary tree containing blocks.
	 * The tree has no be non-empty, i.e., it must contain at least one block.
	 * 
	 * @param vertices
	 */
	public BlockTree(ArrayList<Block> blocks) 
	{	// Leave the following two "if" statements as they are.
		if (blocks == null)
			throw new IllegalArgumentException("blocks null");
		if (blocks.size() == 0)
			throw new IllegalArgumentException("no blocks");

		numBlocks = blocks.size();
		box = BBox.findBBox(blocks.listIterator());

		//base case
		if(blocks.size()==1){
			block = blocks.get(0);
			return;
		}


		BBox topLeft;
		BBox bottomRight;
		double halfWidth = box.getLength()/2;

		if(box.getWidth()>box.getHeight()){
			topLeft = new BBox(new Vec2D(box.lower), box.upper.minus(new Vec2D(halfWidth, 0)));
			bottomRight = new BBox(new Vec2D(box.lower.add(new Vec2D(halfWidth, 0))), new Vec2D(box.upper));
		}
		else{
			topLeft = new BBox(new Vec2D(box.lower), box.upper.minus(new Vec2D(0, halfWidth)));
			bottomRight = new BBox(new Vec2D(box.lower.add(new Vec2D(0, halfWidth))), new Vec2D(box.upper));
		}

		ArrayList<Block> topLeftAL = new ArrayList<Block>();
		ArrayList<Block> bottomRightAL = new ArrayList<Block>();

		for(Block b:blocks){
			if(topLeft.contains(new Vec2D(b.p()))){
				topLeftAL.add(b);
			}
			if(bottomRight.contains(new Vec2D(b.p()))){
				bottomRightAL.add(b);
			}
		}

		left = new BlockTree(topLeftAL);
		right = new BlockTree(bottomRightAL);	


	}


	/**
	 * 
	 * @return The bounding box of this collection of blocks.
	 */
	public BBox getBox() { return box; }

	/**
	 * 
	 * @return True iff this is a leaf node.
	 */
	public boolean isLeaf() {
		return (block != null);
	}

	/**
	 * 
	 * @return True iff this is an intermediate node.
	 */
	public boolean isIntermediate() {
		return !isLeaf();
	}

	/**
	 * 
	 * @return Number of blocks contained in tree.
	 */
	public int getNumBlocks() {
		return numBlocks;
	}

	/**
	 * 
	 * @param p A point.
	 * @return True iff this collection of blocks contains the point p.
	 */
	public boolean contains(Vec2D p) {

		if( this.block !=null ){
			return this.block.contains(p);
		}

		return this.left.contains(p) || this.right.contains(p);

	}

	/**
	 * 
	 * @param thisD Displacement of this tree.
	 * @param t A tree of blocks.
	 * @param d Displacement of tree t.
	 * @return True iff this tree and tree t overlap (account for displacements).
	 */
	public boolean overlaps(Vec2D thisD, BlockTree t, Vec2D d) {

		if( this.isLeaf() && t.isLeaf() ){
			return Block.overlaps(this.block, thisD, t.block, d);
		}
		
		BBox thisDisplacedBBox = null;
		BBox tDisplacedBBox = null;
		boolean overLap = false;
		if(this.box != null && t.box != null && thisD != null && d != null){
			thisDisplacedBBox = this.box.displaced(thisD);
			tDisplacedBBox = t.box.displaced(d);
			overLap = thisDisplacedBBox.overlaps(tDisplacedBBox);
		}
		

		

		if(overLap){
			//Check to see whether left and right trees are not null
			boolean leftLeftOL = false;
			boolean leftRightOL = false;
			boolean rightLeftOL = false;
			boolean rightRightOL = false;
			if(this.left != null){
				if(t.left != null){
					leftLeftOL = this.left.overlaps(thisD, t.left, d);
				}
				if(t.right != null){
					leftRightOL = this.left.overlaps(thisD, t.right, d);
				}
				
				
			}
			
			if(this.right != null){
				if(t.left != null){
					rightLeftOL = this.right.overlaps(thisD, t.left, d);	
				}
				if(t.right != null){
					rightRightOL = this.right.overlaps(thisD, t.right, d);
				}			
			}
			

			return leftLeftOL || leftRightOL || rightLeftOL || rightRightOL;
		}

		return false;
	}


	public String toString() {
		return toString(new Vec2D(0,0));
	}

	/**
	 * 
	 * @param d Displacement vector.
	 * @return String representation of this tree (displaced by d).
	 */
	public String toString(Vec2D d) {
		return toStringAux(d,"");
	}

	/**
	 * Useful for creating appropriate indentation for the toString method.
	 */
	private static final String indentation = "   ";
	/**
	 * 
	 * @param d Displacement vector.
	 * @param indent Indentation.
	 * @return String representation of this tree (displaced by d).
	 */
	private String toStringAux(Vec2D d, String indent) 
	{
		String str = indent + "Box: ";
		str += "(" + (box.lower.x + d.x) + "," + (box.lower.y + d.y) + ")";
		str += " -- ";
		str += "(" + (box.upper.x + d.x) + "," + (box.upper.y + d.y) + ")";
		str += "\n";

		if (isLeaf()) {
			String vStr = "(" + (block.p.x + d.x) + "," + (block.p.y + d.y) + ")" + block.h; 
			str += indent + "Leaf: " + vStr + "\n";
		}
		else {
			String newIndent = indent + indentation;
			str += left.toStringAux(d,newIndent);
			str += right.toStringAux(d,newIndent);
		}

		return str;
	}

}
