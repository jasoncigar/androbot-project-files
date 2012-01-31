package com.org.video.server;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.cellbots.CellbotProtos;

public class VideoServlet extends HttpServlet {

	private static final long serialVersionUID = -1542374763947377440L;

	public String getServletInfo() {
		return "Servlet for handeling communication with phone";
	}

	public void doGet(HttpServletRequest req, HttpServletResponse res)
			throws ServletException, IOException {
		String botID = "";
		if (req.getParameter("BOTID") != null) {
			botID = req.getParameter("BOTID");
		}
		res.getOutputStream().write(
				StateHolder.getInstance(botID).getVideoFrame());
	}

	public void doPost(HttpServletRequest req, HttpServletResponse res)
			throws ServletException, IOException {
		res.setContentType("image/jpeg");
		try {
			CellbotProtos.AudioVideoFrame frame = CellbotProtos.AudioVideoFrame
					.parseFrom(req.getInputStream());
			String botID = "";
			if (frame.hasBotID()) {
				botID = frame.getBotID();
			}

			StateHolder.getInstance(botID).setVideoFrame(frame);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
