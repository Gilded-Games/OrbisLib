package com.gildedgames.orbis_api.data.framework.generation.searching;

public interface Node extends Comparable<Node>
{
	void setG(double g);

	void setH(double h);

	double getG();

	double getH();

	double getF();
}
