package com.gildedgames.orbis.lib.data.framework.generation.searching;

import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.PriorityQueue;

public class Searching
{

	public static <T extends AStarNode> T aStar(ISearchProblem<T> problem)
	{
		return weightedAStar(problem, 1.0);
	}

	public static <T extends AStarNode> T weightedAStar(ISearchProblem<T> problem, double weight)
	{
		List<T> potentialStarts = problem.viableStarts();

		potentialStarts.sort(Comparator.comparingDouble(problem::heuristic).reversed());

		initialStates:
		for (T initialState : potentialStarts)
		{
			PriorityQueue<T> priorityQueue = new PriorityQueue<>();
			HashSet<T> visitedStates = new HashSet<>();

			priorityQueue.add(initialState);

			while (!priorityQueue.isEmpty())
			{
				T state = priorityQueue.poll();

				if (problem.isGoal(state))
				{
					return state;
				}

				if (problem.shouldTerminate(state))
				{
					continue initialStates;
				}

				if (problem.contains(visitedStates, state))
				{
					continue;
				}

				visitedStates.add(state);

				for (T newState : problem.successors(state))
				{
					newState.setG(problem.costBetween(state, newState) + state.getG());
					newState.setH(weight * problem.heuristic(newState));

					priorityQueue.add(newState);
				}
			}
		}

		return null;
	}

}
