package org.freshwaterlife.util;

import javax.servlet.http.HttpServlet;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.PrintWriter;
import java.io.FileInputStream;
import java.io.IOException;

public class FileDownload extends HttpServlet {

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
	doGet(request, response);
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
	String fileName = request.getParameter("fileName");
	if (fileName == null) {
	    return;
	}
	String dir;

	/*change to the directory you want to read from, you don't have to use
	the getServletConfig thing, you can just use any path on the local fs.
	 */
	//$CATALINA_HOME/webapps/$WEBAPP/WEB-INF/yourDir
	dir = getServletConfig().getServletContext().getRealPath("WEB-INF/files");
	try {
	    File file = new File(dir, fileName);
	    FileInputStream in = new FileInputStream(file);
	    ServletOutputStream out = response.getOutputStream();
	    response.setContentType("application/octet-stream");
	    response.setContentLength((int) file.length());
	    response.setHeader("Content-Disposition","attachment;filename ="+fileName);

	    for (int i = in.read(); i != -1; i = in.read()) {
		out.write(i);
	    }
	    in.close();
	    out.flush();
	    out.close();
	} catch (Exception e) {
	    response.setContentType("text/html");
	    PrintWriter writer = response.getWriter();
	    writer.print("<h2>Sorry, that file cannot be found</h2>");
	    writer.close();
	}
    }
}
