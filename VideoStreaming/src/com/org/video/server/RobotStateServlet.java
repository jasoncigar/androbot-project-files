package com.org.video.server;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.cellbots.CellbotProtos;
import com.cellbots.CellbotProtos.ControllerState;

@SuppressWarnings("serial")
public class RobotStateServlet extends HttpServlet {

	public String getServletInfo() {
		return "Servlet for handeling communication with phone and sensor data";
	}

	public void doGet(HttpServletRequest req, HttpServletResponse res)
			throws ServletException, IOException {

	}

	public void doPost(HttpServletRequest req, HttpServletResponse res)
			throws ServletException, IOException {
		try {
			CellbotProtos.PhoneState state = CellbotProtos.PhoneState
					.parseFrom(req.getInputStream());

			String botID = "";
			if (state.hasBotID()) {
				botID = state.getBotID();
			}

			StateHolder.getInstance(botID).setPhoneState(state);

			if (StateHolder.getInstance(botID).newControllerStateAvailble()) {
				System.out.println("writing new controller msg");
				ControllerState cs = StateHolder.getInstance(botID)
						.getControllerState();

				res.setStatus(HttpServletResponse.SC_OK);
				res.getOutputStream().write(cs.toByteArray());
			} else {
				res.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
			}

		} catch (IOException e) {
			e.printStackTrace();
			
		}

	}

}
