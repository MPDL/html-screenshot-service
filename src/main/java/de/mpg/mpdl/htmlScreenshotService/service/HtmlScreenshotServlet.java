/*
 *
 * CDDL HEADER START
 *
 * The contents of this file are subject to the terms of the
 * Common Development and Distribution License, Version 1.0 only
 * (the "License"). You may not use this file except in compliance
 * with the License.
 *
 * You can obtain a copy of the license at license/ESCIDOC.LICENSE
 * or http://www.escidoc.de/license.
 * See the License for the specific language governing permissions
 * and limitations under the License.
 *
 * When distributing Covered Code, include this CDDL HEADER in each
 * file and include the License file at license/ESCIDOC.LICENSE.
 * If applicable, add the following below this CDDL HEADER, with the
 * fields enclosed by brackets "[]" replaced with your own identifying
 * information: Portions Copyright [yyyy] [name of copyright owner]
 *
 * CDDL HEADER END
 */
/*
 * Copyright 2006-2007 Fachinformationszentrum Karlsruhe Gesellschaft
 * für wissenschaftlich-technische Information mbH and Max-Planck-
 * Gesellschaft zur Förderung der Wissenschaft e.V.
 * All rights reserved. Use is subject to license terms.
 */
package de.mpg.mpdl.htmlScreenshotService.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;

/**
 * TODO Description
 * 
 * @author saquet (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 */
public class HtmlScreenshotServlet extends HttpServlet {
	private static final long serialVersionUID = -3073642728935619196L;
	private HtmlScreenshotService screenshotService;

	private File file;
	// private File resizeImageFile;
	private int browserWidth;
	private int browserHeight;

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.GenericServlet#init()
	 */
	@Override
	public void init() throws ServletException {
		super.init();
		screenshotService = new HtmlScreenshotService();

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest
	 * , javax.servlet.http.HttpServletResponse)
	 */
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		try {
			String url = req.getParameter("url");

			Boolean fullSize = Boolean.parseBoolean(req
					.getParameter("fullSize"));

			if (fullSize == true) {
				file = screenshotService.takeScreenshot(url, fullSize);
			} else {
				if (req.getParameter("browserWidth") != null
						&& req.getParameter("browserHeight") != null) {
					browserWidth = Integer.parseInt(req
							.getParameter("browserWidth"));
					browserHeight = Integer.parseInt(req
							.getParameter("browserHeight"));
					file = screenshotService.takeScreenshot(url, browserWidth,
							browserHeight);
				} else if (req.getParameter("browserWidth") != null
						&& req.getParameter("browserHeight") == null) {
					browserWidth = Integer.parseInt(req
							.getParameter("browserWidth"));
					file = screenshotService.takeScreenshot(url, browserWidth,
							0);
				} else if (req.getParameter("browserWidth") == null
						&& req.getParameter("browserHeight") != null) {
					browserHeight = Integer.parseInt(req
							.getParameter("browserHeight"));
					file = screenshotService.takeScreenshot(url, 0,
							browserHeight);
				} else {
					file = screenshotService.takeScreenshot(url);
				}

			}

			IOUtils.copy(new FileInputStream(file), resp.getOutputStream());

		} catch (Exception e) {
			resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		}
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		try {
			File html = File.createTempFile("htmlTemp", ".html");
			IOUtils.copy(req.getInputStream(), new FileOutputStream(html));
			String path = "file:///" + html.getAbsolutePath();
			System.out.println(path);
			file = screenshotService.takeScreenshot(path);
			IOUtils.copy(new FileInputStream(file), resp.getOutputStream());
		} catch (Exception e) {
			resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		}

	}
}
