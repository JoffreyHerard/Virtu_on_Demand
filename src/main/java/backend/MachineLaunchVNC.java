/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package backend;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.JSONObject;
import virtu.ManageVirtu;
import virtu.containers.LXC;

/**
 *
 * @author root
 */
@WebServlet(name = "MachineLaunchVNC", urlPatterns = {"/machine/launchvnc/*"})
public class MachineLaunchVNC extends UserspaceServlet {

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
        super.doGet(request, response);

        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json");
        JSONObject res = new JSONObject();

        String vncPortParam = request.getParameter("vncPort");        
     
        // port of VM
        int showPort = Integer.parseInt("95"+vncPortParam.substring(vncPortParam.length() - 2));
        int vncPort = Integer.parseInt(vncPortParam);
        System.out.println("launcVNCServlet : vncPort - " + vncPort);
        System.out.println("launcVNCServlet : showPort - " + showPort);
        
        System.out.println("launcVNCServlet : Begin exec");
        String execute = "/home/stephane/noVNC/utils/websockify/websockify.py --web /home/stephane/noVNC " + showPort + " localhost:"+vncPort;
        Process pr = Runtime.getRuntime().exec(execute);
        
        LXC.afficherFlux(pr);
        
        res.put("showPort", showPort);
        res.put("vncPort", vncPort);
        response.getWriter().print(res.toString());
        System.out.println(res.toString());
        System.out.println("launchVNC : OK");        
    }

}
