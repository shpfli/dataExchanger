/**
 * 
 */
package com.hubery.data.web.controller;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.hubery.data.bean.DataImportParameter;
import com.hubery.data.service.DataExchangeService;
import com.hubery.data.service.impl.DataExchangeServiceImpl;

/**
 * @author Hubery
 *
 */
public class DataImportServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private DataExchangeService dataExchangeService;

	public DataImportServlet() {
		super();
		this.dataExchangeService = new DataExchangeServiceImpl();
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String importTable = req.getParameter("importTable");
		String importDir = req.getParameter("importDir");
		String separator = req.getParameter("separator");

		DataImportParameter param = new DataImportParameter(separator, importTable, importDir);		

		String msg = this.dataExchangeService.importData(param);

		System.out.println(msg);

		req.setAttribute("message", msg);
		req.getRequestDispatcher("/message.jsp").forward(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		this.doGet(req, resp);
	}

}
