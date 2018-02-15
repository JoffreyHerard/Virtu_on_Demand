/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package backend;

import java.io.IOException;
import java.net.URLDecoder;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.JSONObject;
import virtu.ManageVirtu;

/**
 *
 * @author kiralex
 */
@WebServlet(name = "MachineTurnOff", urlPatterns = {"/machine/turnOff/*"})
public class MachineTurnOff extends UserspaceServlet {
@Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        super.doGet(request, response);
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json");
        System.out.println("REQUEST TURNOFF : "+URLDecoder.decode(request.getQueryString(), "UTF-8"));
        JSONObject res = new JSONObject();

        String name = request.getParameter("name");

        User user = (User) request.getSession().getAttribute("user");
        String email = user.getEmail();
        ManageVirtu mv = user.getManageVirtu();
        ManageVirtu.KindVM k =null;
        String error= null;
        int retour=500;
        boolean toR=false;
        if(name!=null){

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
            toR=mv.stopVM(k,name);
            if(!toR){error="error";retour=500;}else{error="";retour=200;}

        }
        res.put("error",error);
        res.put("code",retour);
        response.getWriter().print(res.toString());
    }
    

}
