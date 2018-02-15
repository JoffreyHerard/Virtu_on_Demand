/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package backend;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.JSONArray;
import org.json.JSONObject;
import virtu.ManageVirtu;

/**
 *
 * @author root
 */
@WebServlet(name = "IsoPersonnalDelete", urlPatterns = {"/iso/personnal/delete/*"})
public class IsoPersonnalDelete extends UserspaceServlet {

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response)throws ServletException, IOException {
        super.doGet(request, response);
        
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json");
        JSONObject res = new JSONObject();
        boolean toR=true;
        String name = request.getParameter("name");
        
        User user = (User) request.getSession().getAttribute("user");
        String email = user.getEmail();
        ManageVirtu mv = user.getManageVirtu();
        ManageVirtu.KindVM k =null;
        
        String path =ManageVirtu.ISOBASEPATH+"/personnal/"+email+"/"+name;
        
        try {

            /* Creating device */
            Process pr = Runtime.getRuntime().exec(new String[]{"/bin/sh", "-c", "rm "+path});
            pr.waitFor();
            
        } catch (IOException | InterruptedException ex) {

            /* Not valid anymore */
            toR = false;

        }
        String error= null;
        int retour=500;
        if(!toR){error="error";retour=500;}else{error="";retour=200;}
        
        res.put("error",error);
        res.put("code",retour);
        
        response.getWriter().print(res.toString()); 
    }
}
