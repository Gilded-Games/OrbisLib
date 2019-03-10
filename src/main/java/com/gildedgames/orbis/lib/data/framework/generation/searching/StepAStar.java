package com.gildedgames.orbis.lib.data.framework.generation.searching;

import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.PriorityQueue;

public class StepAStar<T extends AStarNode>
{
	private final ISearchProblem<T> problem;

	private final PriorityQueue<T> queue = new PriorityQueue<>();

	private final HashSet<T> visitedStates = new HashSet<>();

	private final double hWeight;

	private boolean terminated;

	private T currentState;

	private int statesExpanded = 0;

	private List<T> potentialStarts;

	private int currentStartIndex = -1;

	public StepAStar(ISearchProblem<T> problem, double hWeight)
	{
		this.problem = problem;
		this.hWeight = hWeight;

		this.potentialStarts = this.problem.viableStarts();

		this.potentialStarts.sort(Comparator.comparingDouble(this.problem::heuristic));
	}

	/**
	 *
	 * @return Should terminate
	 */
	private boolean tryDifferentInitialStart()
	{
		if (this.currentStartIndex < this.potentialStarts.size() - 1)
		{
			this.statesExpanded = 0;
			this.currentStartIndex++;

			this.queue.clear();

			this.queue.add(this.potentialStarts.get(this.currentStartIndex));

			return false;
		}

		this.terminated = true;

		return true;
	}

	public void step()
	{
		if (this.terminated)
		{
			return;
		}

		if (this.queue.isEmpty() || this.statesExpanded > 5000)
		{
			this.currentState = null;

			if (this.tryDifferentInitialStart())
			{
				return;
			}
		}

		this.currentState = this.queue.poll();

		if (this.problem.isGoal(this.currentState))
		{
			this.terminated = true;
			return;
		}

		if (this.problem.shouldTerminate(this.currentState))
		{
			this.tryDifferentInitialStart();

			return;
		}

		if (this.problem.contains(this.visitedStates, this.currentState))
		{
			this.step();
			return;
		}

		this.visitedStates.add(this.currentState);
		this.statesExpanded += 1;

		for (T state : this.problem.successors(this.currentState))
		{
			state.setG(this.problem.costBetween(this.currentState, state) + this.currentState.getG());
			state.setH(this.hWeight * this.problem.heuristic(state));
			this.queue.add(state);
		}
	}

	public boolean isTerminated()
	{
		return this.terminated;
	}

	public T currentState()
	{
		if (this.currentState == null)
		{
			//OrbisLib.LOGGER.info("Current state in StepAStar is null.");
		}

		return this.currentState;
	}
}
