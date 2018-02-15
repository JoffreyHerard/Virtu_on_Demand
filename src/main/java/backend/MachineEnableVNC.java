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

/**
 *
 * @author root
 */
@WebServlet(name = "MachineEnableVNC", urlPatterns = {"/machine/enablevnc/*"})
public class MachineEnableVNC extends UserspaceServlet {

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
        
        boolean toR=false;
        String name = request.getParameter("name");

        User user = (User) request.getSession().getAttribute("user");
        String email = user.getEmail();
        
        ManageVirtu mv = user.getManageVirtu();
        ManageVirtu.KindVM k =null;
        switch(name.charAt(0)){

            case 'Q':
                k = ManageVirtu.KindVM.QEMU;
                break;
            case 'K':
                k = ManageVirtu.KindVM.KVM;
                break;
            default:
                k=null;
                break;
        }
        int vnc=mv.getVNC(name,k);
        String error= null;
        int retour=500;
        if(vnc==-1){error="error";retour=500;}else{error="";retour=200;}
        res.put("error",error);
        res.put("code",retour);
        res.put("vnc",vnc);
        response.getWriter().print(res.toString()); 
        System.out.println(res.toString());
    }

}
