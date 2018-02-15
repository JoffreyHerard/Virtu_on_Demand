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
@WebServlet(name = "MachineAddUser", urlPatterns = {"/machine/add/*"})
public class MachineAddUser extends UserspaceServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
        super.doGet(request,response);
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json");
        
        JSONObject res = new JSONObject();
                    
        System.out.println(request.getQueryString());
        String name = request.getParameter("name");
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        
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
            case 'D':
                k = ManageVirtu.KindVM.DOCKER;
                break;
            case 'L':
                k = ManageVirtu.KindVM.LXC;
                break;
            default:
                k=null;
                break;
        }
        boolean toR=mv.addAcountUser(name, k,username,password);
        System.out.println("boolean : "+toR);
        String error= null;
        int retour=500;
        if(!toR){error="error";retour=500;}else{error="";retour=200;}
        res.put("error",error);
        res.put("code",retour);
        response.getWriter().print(res.toString()); 
    }
        

}
