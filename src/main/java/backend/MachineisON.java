/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package backend;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLDecoder;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import virtu.ManageVirtu;

/**
 *
 * @author root
 */
@WebServlet(name = "MachineisON", urlPatterns = {"/machine/isOn"})
public class MachineisON extends UserspaceServlet {

    
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
       super.doGet(request,response);
       
            System.out.println("REQUEST ISON : "+URLDecoder.decode(request.getQueryString(), "UTF-8"));
            String name = request.getParameter("name");
            User user = (User) request.getSession().getAttribute("user");
            String email = user.getEmail();
            ManageVirtu mv = user.getManageVirtu();
            boolean toR=false;
            toR=mv.isOn(name);
            if(toR)
                response.getWriter().print("on");
            else
                response.getWriter().print("off");
                
           
    }

}
