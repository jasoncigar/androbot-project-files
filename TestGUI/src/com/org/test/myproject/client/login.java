package com.org.test.myproject.client;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.org.test.myproject.client.TestForServer;
import com.org.test.myproject.client.TestForServerAsync;

;

public class login extends Composite {

	private final TestForServerAsync test = GWT.create(TestForServer.class);

	public login() {

		VerticalPanel verticalPanel = new VerticalPanel();
		initWidget(verticalPanel);
		verticalPanel.setSize("340px", "186px");

		Label lblEnterUsernameAnd = new Label(
				"Enter username and Password to sign in ");
		verticalPanel.add(lblEnterUsernameAnd);
		lblEnterUsernameAnd.setSize("241px", "17px");

		FlexTable flexTable = new FlexTable();
		flexTable.setTitle("Remember Me");
		verticalPanel.add(flexTable);
		verticalPanel.setCellVerticalAlignment(flexTable,
				HasVerticalAlignment.ALIGN_MIDDLE);
		flexTable.setSize("331px", "127px");

		Label lblNewLabel = new Label("UserName  :");
		flexTable.setWidget(0, 0, lblNewLabel);
		lblNewLabel.setSize("83px", "18px");
		flexTable.getCellFormatter().setHorizontalAlignment(0, 0,
				HasHorizontalAlignment.ALIGN_LEFT);

		final TextBox textBox = new TextBox();
		flexTable.setWidget(0, 1, textBox);

		Label lblNewLabel_1 = new Label("Password  :");
		flexTable.setWidget(1, 0, lblNewLabel_1);

		final TextBox textBox_1 = new TextBox();
		flexTable.setWidget(1, 1, textBox_1);
		flexTable.getCellFormatter().setVerticalAlignment(0, 0,
				HasVerticalAlignment.ALIGN_MIDDLE);
		flexTable.getCellFormatter().setVerticalAlignment(1, 1,
				HasVerticalAlignment.ALIGN_MIDDLE);

		final Label lblNewLabel_3 = new Label("Test Button Event");
		flexTable.setWidget(3, 0, lblNewLabel_3);

		Button btnSignIn = new Button("Sign in");
		btnSignIn.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				test.checkUser(textBox.getText(), textBox_1.getText(),
						new AsyncCallback<Boolean>() {

							@Override
							public void onSuccess(Boolean result) {
								if (result == true) {
									lblNewLabel_3.setText("login successfull");
								} else {
									lblNewLabel_3.setText("login failed");
								}
							}

							@Override
							public void onFailure(Throwable caught) {
								lblNewLabel_3.setText("Network Error");
							}
						});
			}
		});

		flexTable.setWidget(2, 1, btnSignIn);

	}

}
