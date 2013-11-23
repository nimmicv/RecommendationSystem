

package org.nimmi.jspTrials;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.thoughtworks.xstream.XStream;






/**
 * Servlet implementation class SimpleServeletJava
 */
@WebServlet("/SimpleServeletJava")
public class SimpleServeletJava extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public SimpleServeletJava() {
        super();
        // TODO Auto-generated constructor stub
    }


	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		System.out.println("Inside Get");
		Clusterer clstr = new Clusterer();
		String zipcode=request.getParameter("zipcode");
		List<hospitalList> hospitalsList = null;
		try {
			hospitalsList = clstr.findHospitals(zipcode);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		XStream xstream = new XStream();
		xstream.alias("org.nimmi.jspTrials.hospitalList", hospitalList.class);
		String hospitalXML = xstream.toXML(hospitalsList);
		System.out.println(hospitalXML);
		//xstream.alias("org.nimmi.jspTrials.hospitalList", hospitalList.class);
		//List<Marker> libraries = getLibraryDAO().findAllAsMarkers();
		//System.out.println(libraries.size());
		//String xml = xstream.toXML(libraries);
		response.setContentType("application/xml");
		response.getWriter().write(hospitalXML);

		
	}
	
	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.setContentType("text/html");
		//PrintWriter writer = response.getWriter();
		//String pincode =  request.getParameter("pinCode");
		
		//writer.println("Entered "+pincode);
		
		
		
	}

}
