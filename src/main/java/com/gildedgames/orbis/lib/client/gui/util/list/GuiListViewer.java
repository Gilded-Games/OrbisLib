package com.gildedgames.orbis.lib.client.gui.util.list;

import com.gildedgames.orbis.lib.client.gui.data.list.IListNavigator;
import com.gildedgames.orbis.lib.client.gui.data.list.IListNavigatorListener;
import com.gildedgames.orbis.lib.client.gui.util.GuiAbstractButton;
import com.gildedgames.orbis.lib.client.gui.util.GuiFactoryGeneric;
import com.gildedgames.orbis.lib.client.gui.util.gui_library.GuiElement;
import com.gildedgames.orbis.lib.client.gui.util.gui_library.IGuiElement;
import com.gildedgames.orbis.lib.client.rect.Pos2D;
import com.gildedgames.orbis.lib.client.rect.Rect;
import com.google.common.collect.Lists;

import java.util.List;
import java.util.function.Function;

public class GuiListViewer<NODE, NODE_GUI extends GuiElement> extends GuiElement
		implements IListNavigatorListener<NODE>
{
	private final IListNavigator<NODE> navigator;

	private final List<NODE_GUI> visibleGuiNodes = Lists.newArrayList();

	private final List<NODE> visibleNodes = Lists.newArrayList();

	private final List<GuiAbstractButton> visibleDeletes = Lists.newArrayList();

	private final NodeFactory<NODE, NODE_GUI> guiFactory;

	private final Function<Integer, NODE> nodeFactory;

	private GuiAbstractButton addButton;

	private int currentScroll, maxScroll, nodeHeight;

	private List<IListViewerListener> listeners = Lists.newArrayList();

	private Function<IListNavigator<NODE>, Integer> newNodeIndex;

	private boolean allowModifications = true;

	public GuiListViewer(final Rect dim, Function<IListNavigator<NODE>, Integer> newNodeIndex, final IListNavigator<NODE> navigator,
			final NodeFactory<NODE, NODE_GUI> guiFactory, final Function<Integer, NODE> nodeFactory, int nodeHeight)
	{
		super(dim, true);

		this.navigator = navigator;
		this.navigator.addListener(this);

		this.newNodeIndex = newNodeIndex;
		this.guiFactory = guiFactory;
		this.nodeFactory = nodeFactory;
		this.nodeHeight = nodeHeight;
	}

	public GuiListViewer<NODE, NODE_GUI> allowModifications(boolean flag)
	{
		this.allowModifications = flag;

		return this;
	}

	public void listen(IListViewerListener listener)
	{
		if (!this.listeners.contains(listener))
		{
			this.listeners.add(listener);
		}
	}

	public boolean unlisten(IListViewerListener listener)
	{
		return this.listeners.remove(listener);
	}

	public NODE_GUI getNodeGui(final int index)
	{
		return this.visibleGuiNodes.get(index - this.currentScroll);
	}

	public IListNavigator<NODE> getNavigator()
	{
		return this.navigator;
	}

	public int findPosition(NODE node)
	{
		return this.navigator.getNodes().inverse().get(node);
	}

	public List<NODE> getVisibleNodes()
	{
		return this.visibleNodes;
	}

	private List<NODE_GUI> listVisibleNodes(final List<NODE> nodes, final int scrollCount,
			final int height, final int width)
	{
		final List<NODE_GUI> guis = Lists.newArrayList();

		final int nodeWidth = width - (this.allowModifications ? 20 : 0);
		final int nodeHeight = this.nodeHeight;

		final int possibleNumberOfRows = height / nodeHeight;

		if (nodes.size() < possibleNumberOfRows)
		{
			this.currentScroll = 0;
			this.maxScroll = 0;
		}
		else
		{
			this.maxScroll = nodes.size() - possibleNumberOfRows + 1;
		}

		final int frontNodeIndex = Math.max(0, Math.min(scrollCount, this.maxScroll));
		final int backNodeIndex = Math.min(nodes.size(), frontNodeIndex + possibleNumberOfRows);

		for (int i = frontNodeIndex; i < backNodeIndex; i++)
		{
			final NODE node = nodes.get(i);

			if (node == null)
			{
				continue;
			}

			final int row = i - frontNodeIndex;

			final Pos2D pos = Pos2D.flush(0, (row * nodeHeight));

			final NODE_GUI guiNode = this.guiFactory.create(pos, node, i);

			guiNode.dim().mod().width(nodeWidth).height(nodeHeight).scale(1.0F).flush();

			guis.add(guiNode);

			this.visibleNodes.add(node);
		}

		if (this.allowModifications)
		{
			if (this.currentScroll == this.maxScroll)
			{
				this.addButton = GuiFactoryGeneric.createAddButton();

				final Pos2D pos = Pos2D.flush(nodeWidth, ((backNodeIndex - frontNodeIndex) * nodeHeight));

				this.addButton.dim().mod().x(nodeWidth).pos(pos).scale(1.0F).flush();
			}
			else
			{
				this.addButton = null;
			}
		}

		return guis;
	}

	@Override
	public void onMouseClicked(IGuiElement element, final double mouseX, final double mouseY, final int mouseButton)
	{
		if (this.state().isEnabled() && mouseButton == 0)
		{
			if (this.addButton.state().isHoveredAndTopElement() && this.addButton.state().isEnabled())
			{
				int index = this.newNodeIndex.apply(this.getNavigator());

				this.getNavigator().put(this.nodeFactory.apply(index), index, true);
				return;
			}

			for (int i = 0; i < this.visibleGuiNodes.size(); i++)
			{
				final GuiAbstractButton button = this.allowModifications ? this.visibleDeletes.get(i) : null;
				final GuiElement nodeGui = this.visibleGuiNodes.get(i);

				final NODE node = this.visibleNodes.get(i);

				if (button.state().isHoveredAndTopElement() && button.state().isEnabled())
				{
					this.getNavigator().remove(node, this.navigator.getNodes().inverse().get(node));

					return;
				}
				else if (nodeGui.state().isHoveredAndTopElement() && nodeGui.state().isEnabled())
				{
					this.getNavigator().click(node, this.navigator.getNodes().inverse().get(node));
				}
			}
		}
	}

	private void refreshNodes()
	{
		if (this.addButton != null)
		{
			this.context().removeChild(this.addButton);
		}

		this.visibleNodes.clear();

		final List<NODE_GUI> guiNodes = this
				.listVisibleNodes(Lists.newArrayList(this.navigator.getNodes().values()), this.currentScroll, (int) this.dim().height(),
						(int) (this.dim().width()));

		this.visibleDeletes.forEach(this.context()::removeChild);
		this.visibleGuiNodes.forEach(this.context()::removeChild);

		this.visibleGuiNodes.clear();
		this.visibleDeletes.clear();

		this.visibleGuiNodes.addAll(guiNodes);
		this.visibleGuiNodes.forEach(this.context()::addChildren);

		if (this.allowModifications)
		{
			this.visibleGuiNodes.forEach(g -> {
				final GuiAbstractButton deleteButton = GuiFactoryGeneric.createDeleteButton();

				deleteButton.dim().mod().pos(Pos2D.flush(g.dim().originalState().x(), g.dim().originalState().y())).addX(g.dim().width()).flush();

				this.visibleDeletes.add(deleteButton);
				this.context().addChildren(deleteButton);
			});
		}

		if (this.addButton != null)
		{
			this.context().addChildren(this.addButton);
		}

		this.addButton.state().setCanBeTopHoverElement(true);
	}

	@Override
	public void onMouseWheel(IGuiElement element, final double state)
	{
		int oldScroll = this.currentScroll;

		this.currentScroll = Math.max(0, Math.min(this.maxScroll, this.currentScroll - (state / 120)));

		this.refreshNodes();

		this.listeners.forEach(l -> l.onScroll(oldScroll, this.currentScroll));
	}

	@Override
	public void build()
	{
		this.refreshNodes();
	}

	@Override
	public void onRemoveNode(final NODE node, final int index)
	{
		this.refreshNodes();
	}

	@Override
	public void onAddNode(final NODE node, final int index, boolean newNode)
	{
		this.refreshNodes();
	}

	@Override
	public void onNodeClicked(final NODE node, final int index)
	{

	}

	public interface NodeFactory<N, G>
	{

		G create(Pos2D pos, N node, int index);

	}
}
