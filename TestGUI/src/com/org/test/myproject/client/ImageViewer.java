package com.org.test.myproject.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;

public class ImageViewer implements EntryPoint {

	public void onModuleLoad() {
		RootPanel rootPanel = RootPanel.get();
		
		HorizontalPanel horizontalPanel = new HorizontalPanel();
		rootPanel.add(horizontalPanel, 10, 10);
		
		VerticalPanel verticalPanel = new VerticalPanel();
		horizontalPanel.add(verticalPanel);
		verticalPanel.setSize("426px", "266px");
		
		Label lblWelcomeToGui = new Label("Welcome to GUI Test");
		verticalPanel.add(lblWelcomeToGui);
		verticalPanel.setCellHorizontalAlignment(lblWelcomeToGui, HasHorizontalAlignment.ALIGN_CENTER);
		
		login login_ = new login();
		verticalPanel.add(login_);
		verticalPanel.setCellHorizontalAlignment(login_, HasHorizontalAlignment.ALIGN_CENTER);
		verticalPanel.setCellVerticalAlignment(login_, HasVerticalAlignment.ALIGN_MIDDLE);
		
	}
}
