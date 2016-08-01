/*
 * PostgreSQL Studio
 */
package com.openscg.pgstudio.client.panels;

import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.DecoratedTabPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;
import com.openscg.pgstudio.client.PgStudio;
import com.openscg.pgstudio.client.PgStudio.ITEM_TYPE;
import com.openscg.pgstudio.client.models.ModelInfo;
import com.openscg.pgstudio.client.utils.TextFormat;

public class DetailsTabPanel {

	private ModelInfo selectedItem;
	private int selectedTab;
	private ITEM_TYPE type;

	private final DecoratedTabPanel panel = new DecoratedTabPanel();

	private final PgStudio main;

	private ColumnPanel columnPanel;
	private IndexPanel indexPanel;
	private ConstraintPanel constPanel;
	private TriggerPanel triggerPanel;
	private RulePanel rulePanel;
	private ItemDataPanel dataPanel;
	private StatsPanel statsPanel;
	private ScriptPanel scriptPanel;
	private FunctionsScriptPanel functionsScriptPanel;
	private SecurityPanel secPanel;
	private PolicyPanel polPanel;
	
	private Widget columnTabWidget = new HTML(TextFormat.getHeaderString("Columns", PgStudio.Images.column()));
	private Widget indexTabWidget = new HTML(TextFormat.getHeaderString("Indexes", PgStudio.Images.index()));
	private Widget constTabWidget = new HTML(TextFormat.getHeaderString("Constraints", PgStudio.Images.constraint()));
	private Widget triggerTabWidget = new HTML(TextFormat.getHeaderString("Triggers", PgStudio.Images.triggers()));
	private Widget ruleTabWidget = new HTML(TextFormat.getHeaderString("Rules", PgStudio.Images.rules()));
	private Widget dataTabWidget = new HTML(TextFormat.getHeaderString("Data", PgStudio.Images.data()));
	private Widget statsTabWidget = new HTML(TextFormat.getHeaderString("Stats", PgStudio.Images.stats()));
	private Widget scriptTabWidget = new HTML(TextFormat.getHeaderString("Script", PgStudio.Images.script()));
	private Widget securityTabWidget = new HTML(TextFormat.getHeaderString("Security", PgStudio.Images.security()));
	private Widget policyTabWidget = new HTML(TextFormat.getHeaderString("Policies", PgStudio.Images.policy()));

	private Widget indexWidget;
	private Widget constWidget;
	private Widget triggerWidget;
	private Widget ruleWidget;
	private Widget securityWidget;
	private Widget policyWidget;

	public DetailsTabPanel(final PgStudio main) {
		this.main = main;
		
		columnPanel = new ColumnPanel();
		indexPanel = new IndexPanel(this.main);
		constPanel = new ConstraintPanel();
		triggerPanel = new TriggerPanel();
		rulePanel = new RulePanel();
		dataPanel = new ItemDataPanel();
		statsPanel = new StatsPanel();
		scriptPanel = new ScriptPanel();
		functionsScriptPanel = new FunctionsScriptPanel();
		secPanel = new SecurityPanel();
		polPanel = new PolicyPanel();
	}

	public void setSelectedItem(ModelInfo selected) {		
		this.selectedItem = selected;
		
		if (selectedItem.getItemType() != type) {
			this.type = selectedItem.getItemType();
			
			switch(type) {
				case TABLE :
					setupTablePanels();
					break;
				case MATERIALIZED_VIEW:
				case VIEW :
					setupViewPanels();
					break;
				case FOREIGN_TABLE :
					setupForeignTablePanels();
					break;
				case FUNCTION :
					setupFunctionPanels();
					break;
				case SEQUENCE :
					setupSequencePanels();
					break;
				case TYPE :
					setupTypePanels();
					break;
			default:
				break;

			}
		}

		if (panel.getTabBar().getSelectedTab() < 0) {
			panel.selectTab(0);
		}
		
		DetailsPanel p = (DetailsPanel) panel.getWidget(panel.getTabBar().getSelectedTab());
		p.setItem(selectedItem);	
		
		
		DOM.setStyleAttribute(RootPanel.get().getElement(), "cursor", "default");

	}
	
	public Widget asWidget() {

		indexWidget = indexPanel.asWidget();
		constWidget = constPanel.asWidget();
		triggerWidget = triggerPanel.asWidget();
		ruleWidget = rulePanel.asWidget();
		securityWidget = secPanel.asWidget();
		policyWidget = polPanel.asWidget();

		panel.setHeight("100%");
		panel.setWidth("100%");
		panel.setStyleName("studio-DecoratedTabBar");
		
		panel.add(columnPanel, columnTabWidget);
		panel.add(indexWidget, indexTabWidget);
		panel.add(constWidget, constTabWidget);
		panel.add(triggerWidget, triggerTabWidget);
		panel.add(ruleWidget, ruleTabWidget);
		panel.add(dataPanel, dataTabWidget);
		panel.add(statsPanel, statsTabWidget);
		panel.add(scriptPanel, scriptTabWidget);
		panel.add(securityWidget, securityTabWidget);
		panel.add(polPanel, policyTabWidget);

		panel.addSelectionHandler(new SelectionHandler<Integer>() {
			@Override
			public void onSelection(SelectionEvent<Integer> event) {
					selectedTab = (Integer) event.getSelectedItem();
				/* I am sometimes HTML cast to DetailsPanel errors, so ignore this */
				try {
					DetailsPanel p = (DetailsPanel) panel.getWidget(selectedTab);
					p.setItem(selectedItem);
				} catch (Exception e) {}
			}
			
		});
		
	    return panel.asWidget();
	}
	
	private void setupTablePanels() {
		int panels = 9;
		
		if (main.getDatabaseVersion() >= 90500) {
			panel.insert(policyWidget, policyTabWidget, 0);
			panels = 10;
		}
		panel.insert(securityWidget, securityTabWidget, 0);
		panel.insert(scriptPanel, scriptTabWidget, 0);
		panel.insert(statsPanel, statsTabWidget, 0);
		panel.insert(dataPanel, dataTabWidget, 0);
		panel.insert(ruleWidget, ruleTabWidget, 0);
		panel.insert(triggerWidget, triggerTabWidget, 0);
		panel.insert(constWidget, constTabWidget, 0);
		panel.insert(indexWidget, indexTabWidget, 0);
		panel.insert(columnPanel, columnTabWidget, 0);

		removeExtraPanels(panels);
}

	private void setupViewPanels() {		

		panel.insert(securityWidget, securityTabWidget, 0);
		panel.insert(scriptPanel, scriptTabWidget, 0);
		panel.insert(dataPanel, dataTabWidget, 0);
		panel.insert(ruleWidget, ruleTabWidget, 0);
		panel.insert(columnPanel, columnTabWidget, 0);

		removeExtraPanels(5);
}

	private void setupForeignTablePanels() {		

		panel.insert(securityWidget, securityTabWidget, 0);
		panel.insert(scriptPanel, scriptTabWidget, 0);
		panel.insert(dataPanel, dataTabWidget, 0);
		panel.insert(columnPanel, columnTabWidget, 0);

		removeExtraPanels(4);
}

	private void setupFunctionPanels() {		

		panel.insert(securityWidget, securityTabWidget, 0);
		panel.insert(functionsScriptPanel, scriptTabWidget, 0);

		removeExtraPanels(2);
}

	private void setupSequencePanels() {		

		panel.insert(securityWidget, securityTabWidget, 0);
		panel.insert(scriptPanel, scriptTabWidget, 0);
		panel.insert(statsPanel, statsTabWidget, 0);

		removeExtraPanels(3);
	}

	private void setupTypePanels() {		

		panel.insert(securityWidget, securityTabWidget, 0);
		panel.insert(scriptPanel, scriptTabWidget, 0);
		
		removeExtraPanels(2);
	}

	private void removeExtraPanels(int numPanelsToKeep) {
		for (int i = panel.getWidgetCount(); i > numPanelsToKeep; i--) {
			panel.remove(i-1);
		}
	}

	public void refreshCurrent()	{
		DetailsPanel p = (DetailsPanel) panel.getWidget(selectedTab);
		p.setItem(selectedItem);
	}
	
	public void clearTabs() {
		columnPanel.clearColumnData();
	}
}
