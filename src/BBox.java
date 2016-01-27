import java.util.*;

/**
 * A 2D bounding box.
 *
 */
public class BBox 
{
	/**
	 * The corner of the bounding box with the smaller x,y coordinates.
	 */
	public Vec2D lower; // (minX,minY)
	
	/**
	 * The corner of the bounding box with the larger x,y coordinates.
	 */
	public Vec2D upper; // (maxX,maxY)

	/**
	 * 
	 * @param box A bounding box.
	 */
	public BBox(BBox box) {
		lower = new Vec2D(box.lower);
		upper = new Vec2D(box.upper);
	}

	/**
	 * 
	 * @param lower Corner with smaller coordinates.
	 * @param upper Corner with larger coordinates.
	 */
	public BBox(Vec2D lower, Vec2D upper) {
		if (upper.x < lower.x) throw new IllegalArgumentException("invalid bbox");
		if (upper.y < lower.y) throw new IllegalArgumentException("invalid bbox");

		this.lower = lower;
		this.upper = upper;
	}

	/**
	 * Width: size along the x-dimension.
	 * 
	 * @return Width of the bounding box.
	 */
	public double getWidth() {
		return upper.x - lower.x;
	}

	/**
	 * Height: size along the y-dimension.
	 * 
	 * @return Height of the bounding box.
	 */
	public double getHeight() {
		return upper.y - lower.y;
	}

	/**
	 * 
	 * @return Returns the dimension (width or height) of maximum length.
	 */
	public double getLength() {
		
		return Math.max(getWidth(), getHeight());
	}

	/**
	 * 
	 * @return The center of this bounding box.
	 */
	public Vec2D getCenter() {
		double x = lower.x+getWidth()/2;
		double y = lower.y+getHeight()/2;
		return new Vec2D(x, y);
	}

	/**
	 * 
	 * @param d A displacement vector.
	 * @return The result of displacing this bounding box by vector d.
	 */
	public BBox displaced(Vec2D d) {
		return new BBox(Vec2D.add(this.lower, d), Vec2D.add(this.upper, d));
	}

	/**
	 * 
	 * @param p A point.
	 * @return True iff this bounding box contains point p.
	 */
	public boolean contains(Vec2D p) {
		boolean inX = lower.x <= p.x && p.x <= upper.x;
		boolean inY = lower.y <= p.y && p.y <= upper.y;
		return inX && inY;
	}

	/**
	 * 
	 * @return The area of this bounding box.
	 */
	public double getArea() {
		return getWidth()*getHeight();
	}


	/**
	 * 
	 * @param box A bounding box.
	 * @return True iff this bounding box overlaps with box.
	 */
	public boolean overlaps(BBox box) {
		if(upper.x<box.lower.x){
			return false;
		}
		if(lower.x>box.upper.x){
			return false;
		}
		if(upper.y<box.lower.y){
			return false;
		}
		if(lower.y>box.upper.y){
			return false;
		}

		return true;
	}
	
	//Solution adapted from http://gamemath.com/2011/09/detecting-whether-two-boxes-overlap/

	/**
	 * 
	 * @param iter An iterator of blocks.
	 * @return The bounding box of the blocks given by the iterator.
	 */
	public static BBox findBBox(Iterator<Block> iter) {
		// Do not modify the following "if" statement.
		if (!iter.hasNext())
			throw new IllegalArgumentException("empty iterator");
		
		Block current = iter.next();
		double minx = current.p().x-current.h;
		double maxx = current.p().x+current.h;
		double miny = current.p().y-current.h;
		double maxy = current.p().y+current.h;
		while(iter.hasNext()){
			current = iter.next();
			double x = current.p().x;
			double y = current.p().y;
			if(x-current.h<minx){
				minx = x-current.h;
			}
			if(x+current.h>maxx){
				maxx = x+current.h;
			}
			if(y-current.h<miny){
				miny = y-current.h;
			}
			if(y+current.h>maxy){
				maxy = y+current.h;
			}
		}
		return new BBox(new Vec2D(minx, miny), new Vec2D(maxx, maxy));
	}

	public String toString() {
		return lower + " -- " + upper;
	}
}
