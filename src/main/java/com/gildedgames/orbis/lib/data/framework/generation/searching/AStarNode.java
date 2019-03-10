package com.gildedgames.orbis.lib.data.framework.generation.searching;

public interface AStarNode extends Comparable<AStarNode>
{
	/** The cost of the path from the viableStarts node to (n)
	 *
	 * @return
	 */
	double getG();

	void setG(double g);

	/**
	 * The heuristic function that estimates the cost of the cheapest path from (n) to the goal.
	 * @return
	 */
	double getH();

	void setH(double h);

	/**
	 * The cost of the shortest path.
	 * @return
	 */
	double getF();
}
