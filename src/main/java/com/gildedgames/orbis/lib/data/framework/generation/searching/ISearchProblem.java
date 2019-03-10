package com.gildedgames.orbis.lib.data.framework.generation.searching;

import java.util.Collection;
import java.util.List;

public interface ISearchProblem<T extends AStarNode>
{
	List<T> viableStarts();

	List<T> successors(T parentState);

	boolean isGoal(T state);

	double heuristic(T state);

	double costBetween(T parent, T child);

	boolean shouldTerminate(T currentState);

	boolean contains(Collection<T> visitedStates, T currentState);
}
