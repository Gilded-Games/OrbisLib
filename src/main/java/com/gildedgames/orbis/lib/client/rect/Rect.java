package com.gildedgames.orbis.lib.client.rect;

public interface Rect
{

	RectBuilder rebuild();

	float degrees();

	float originX();

	float originY();

	float scale();

	float maxX();

	float maxY();

	float centerX();

	float centerY();

	Pos2D center();

	Pos2D min();

	float x();

	float y();

	float width();

	float height();

	boolean isCenteredX();

	boolean isCenteredY();

	boolean intersects(float x, float y);

	boolean intersects(Rect dim);

	boolean snapToIntegers();

}
