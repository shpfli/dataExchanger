/**
 * 
 */
package com.hubery.data.web.controller;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.hubery.data.bean.DataExportParameter;
import com.hubery.data.service.DataExchangeService;
import com.hubery.data.service.impl.DataExchangeServiceImpl;

/**
 * @author Hubery
 *
 */
public class DataExportServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private DataExchangeService dataExchangeService;

	public DataExportServlet() {
		super();
		this.dataExchangeService = new DataExchangeServiceImpl();
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String exportTable = req.getParameter("exportTable");
		String exportDir = req.getParameter("exportDir");
		String separator = req.getParameter("separator");
		String maxSize = req.getParameter("maxSize");
		String exportTableColumns = req.getParameter("exportTableColumns");
		Boolean doCompress = Boolean.valueOf(req.getParameter("doCompress"));

		DataExportParameter param = new DataExportParameter();
		param.setExportDir(exportDir);
		param.setExportTable(exportTable);
		param.setMaxSize(Integer.valueOf(maxSize));
		param.setSeparator(separator);
		param.setExportTableColumns(exportTableColumns);
		param.setDoCompress(doCompress);

		String msg = this.dataExchangeService.exportData(param);

		System.out.println(msg);

		req.setAttribute("message", msg);
		req.getRequestDispatcher("/message.jsp").forward(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		this.doGet(req, resp);
	}

}
