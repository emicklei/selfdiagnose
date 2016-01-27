/*
    Copyright 2006-2010 Ernest Micklei @ PhilemonWorks.com

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
   
*/
package com.philemonworks.selfdiagnose;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.philemonworks.selfdiagnose.output.*;

/**
 * SelfDiagnoseServlet is a server component that can run and report on all registered diagnostic tasks.<br/>
 * Queries:
 * <ul>
 * <li>reload, trigger reloading the configuration [selfdiagnose.xml] found on the classpath.</li>
 * </ul>
 * Request parameters:
 * <ul>
 * <li>html, if true then the report is presented by a HTML table. Otherwise the plain log text is returned.</li>
 * <li>xml, if true then the report is available in XML format for processing by other applications (such as dashboards)</li>
 * </ul>
 * @author emicklei
 */
public class SelfDiagnoseServlet extends HttpServlet {
	private static final long serialVersionUID = 3256721762736877881L;
	private static ThreadLocal requestHolder = new ThreadLocal();

	/**
	 * @return HttpSession the current session
	 */
	public static HttpSession getCurrentSession() {
	    if (getCurrentRequest() == null) return null;
		return getCurrentRequest().getSession();
	}

	/**
	 * Store the current request in a threadlocal variable.
	 * @param request HttpServletRequest
	 */
	public static void setCurrentRequest(HttpServletRequest request) {
		requestHolder.set(request);
	}

	/**
	 * @return HttpServletRequest the current request
	 */
	public static HttpServletRequest getCurrentRequest() {
		return (HttpServletRequest) requestHolder.get();
	}

	
	public static String getWebApplicationName() {
		if (getCurrentRequest()==null) return "Not applicable";
		final StringBuffer sb = new StringBuffer();
		sb.append("http://");
		sb.append(getCurrentRequest().getServerName());
		sb.append(':');
		sb.append(getCurrentRequest().getServerPort());
		sb.append('/');
		sb.append(getCurrentRequest().getContextPath());
		return sb.toString();
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest,
	 *      javax.servlet.http.HttpServletResponse)
	 */
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// make it public such that any jsp can see it.
		this.run(req, resp);
	}

	/**
	 * Run all diagnostic tasks
	 * @param req : HttpServletRequest
	 * @param resp : HttpServletResponse
	 * @throws ServletException
	 * @throws IOException
	 */
	public void run(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		if ("true".equals(req.getParameter("reload"))) {
			SelfDiagnose.reloadConfiguration();
		}
		String alternateConfig = req.getParameter("config");
		if (alternateConfig != null) {
		    SelfDiagnose.configure(new URL(alternateConfig));
		}
		String format = req.getParameter("format");
		if (format == null)
			format = "html";
		DiagnoseRunReporter reporter = getReporter(format);
		String content = "?";
		try {
			// bring request available to thread
			SelfDiagnoseServlet.setCurrentRequest(req);
			ExecutionContext ctx = new ExecutionContext();
			try { ctx.setValue("servletcontext", this.getServletContext()); } catch (Exception ex) { } // bummer
			DiagnoseRun run = SelfDiagnose.runTasks(reporter,ctx);
			resp.setHeader("X-SelfDiagnose-OK", Boolean.toString(run.isOK()));
			content = reporter.getContent();			
		} finally {
			// remove request from thread
			SelfDiagnoseServlet.setCurrentRequest(null);
		}
		resp.setContentType(reporter.getContentType());
		// write the buffer
		PrintWriter out = resp.getWriter();
		resp.setContentLength(content.length());
		out.write(content);
		out.flush();
		out.close();
	}

	public DiagnoseRunReporter getReporter(String format) {
		if ("html".equals(format))
			return new HTMLReporter();
		if ("xml".equals(format))
			return new XMLReporter();
		if ("json".equals(format))
			return new JSONReporter();
		throw new IllegalArgumentException("Unkown format for reporting:" + format);
	}

	/**
	 * Accept the configuration that is POSTed by the request.
	 */
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        SelfDiagnose.flush();
        try {
            SelfDiagnose.configure(req.getInputStream());
            resp.sendRedirect(req.getRequestURL().toString());
        } catch (Exception e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
        }
    }
}
