/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package backend;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import virtu.*;
import virtu.containers.*;
/**
 *
 * @author joffreyherard
 */
@WebServlet("/Test")
public class Test extends HttpServlet {

	/**
	 * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
	 * methods.
	 *
	 * @param request servlet request
	 * @param response servlet response
	 * @throws ServletException if a servlet-specific error occurs
	 * @throws IOException if an I/O error occurs
	 */
	protected void processRequest(HttpServletRequest request, HttpServletResponse response)
	throws ServletException, IOException {
		response.setContentType("text/html;charset=UTF-8");
		try (PrintWriter out = response.getWriter()) {
			/* TODO output your page here. You may use following sample code. */
			out.println("<!DOCTYPE html>");
			out.println("<html>");
			out.println("<head>");
			out.println("<title>Servlet Test</title>");			
			out.println("</head>");
			out.println("<body>");
			out.println("<h1>Servlet Test at " + request.getContextPath() + "</h1>");
			out.println("</body>");
			out.println("</html>");
			
			User user = (User) request.getSession().getAttribute("user");
			String email = user.getEmail();
			ManageVirtu m = user.getManageVirtu();
			ManageVirtu.KindVM k =null;
			/*m.createVM("joffrey@test.fr", ManageVirtu.KindVM.DOCKER, "test0", "toto", "ubuntu", 0, 0);
			m.createVM("joffrey@test.fr", ManageVirtu.KindVM.DOCKER, "docker_test", "toto", "ubuntu", 0, 0);
			m.createVM("joffrey@test.fr", ManageVirtu.KindVM.DOCKER, "docker_test", "toto", "ubuntu", 0, 0);
			m.createVM("joffrey@test.fr", ManageVirtu.KindVM.DOCKER, "docker_test", "toto", "ubuntu", 0, 0);
			
			JSONArray list = m.list("joffrey@test.fr", ManageVirtu.KindVM.DOCKER);
			System.out.println(list.toString());
			System.out.println("Liste des VMs load ");*/
			m.reloadVM("joffrey@test.fr");
			/*if(m.startVM(ManageVirtu.KindVM.DOCKER, "Docker_test00")){
				System.out.println("redemarrage vm ");
			}
			else{
				System.out.println("Non redemarrage vm ");
			}*/
			boolean tor = false;
			/* Updating */
			Process pr =null;
			String s=null;
			
			try {
			
			    pr = Runtime.getRuntime().exec(new String[]{"docker", "inspect", "-f", "{{range .NetworkSettings.Networks}}{{.IPAddress}}{{end}}", "Docker_test00"});
			    BufferedReader stdInput = new BufferedReader(new InputStreamReader(pr.getInputStream()));
			    s = stdInput.readLine();
			    System.out.println(s);
			    int retour = pr.waitFor();
			    if (retour == 0) {
				tor = true;
			    }
			}
			catch (IOException ex) {
			    Logger.getLogger(Docker.class.getName()).log(Level.SEVERE, null, ex);
			}
			catch (InterruptedException ex) {
			    Logger.getLogger(Docker.class.getName()).log(Level.SEVERE, null, ex);
			}				    

		}


	}

	// <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
	/**
	 * Handles the HTTP <code>GET</code> method.
	 *
	 * @param request servlet request
	 * @param response servlet response
	 * @throws ServletException if a servlet-specific error occurs
	 * @throws IOException if an I/O error occurs
	 */
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
	throws ServletException, IOException {
		processRequest(request, response);
	}

	/**
	 * Handles the HTTP <code>POST</code> method.
	 *
	 * @param request servlet request
	 * @param response servlet response
	 * @throws ServletException if a servlet-specific error occurs
	 * @throws IOException if an I/O error occurs
	 */
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
	throws ServletException, IOException {
		processRequest(request, response);
	}

	/**
	 * Returns a short description of the servlet.
	 *
	 * @return a String containing servlet description
	 */
	@Override
	public String getServletInfo() {
		return "Short description";
	}// </editor-fold>

}
