package com.gildedgames.orbis_api.data.framework.generation.searching;

public interface Node extends Comparable<Node>
{
	double getG();

	void setG(double g);

	double getH();

	void setH(double h);

	double getF();
}
