package com.gildedgames.orbis.lib.data.framework_new;

import com.gildedgames.orbis.lib.data.framework.generation.FailedToGenerateException;

import javax.annotation.Nullable;

public interface IFrameworkAlgorithm
{
	boolean step() throws FailedToGenerateException;

	@Nullable
	IFramework getCompletedFramework();

	Phase getPhase();

	enum Phase
	{
		CSP, FDGD, PATHWAYS, REBUILD1, REBUILD2, REBUILD3
	}
}
