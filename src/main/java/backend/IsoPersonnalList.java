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
@WebServlet(name = "IsoPersonnalList", urlPatterns = {"/iso/personnal/list"})
public class IsoPersonnalList extends UserspaceServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)throws ServletException, IOException {
        super.doGet(request, response);
        
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json");
        JSONArray res = new JSONArray();
        
        User user = (User) request.getSession().getAttribute("user");
        String email = user.getEmail();
        ManageVirtu mv = user.getManageVirtu();
        ManageVirtu.KindVM k =null;
        
        String path =ManageVirtu.ISOBASEPATH+"/personnal/"+email;
        File folder = new File(path);
        File[] listOfFiles = folder.listFiles();
        System.out.println(path);
        for (int i = 0; i < listOfFiles.length; i++) {
            if (listOfFiles[i].isFile()){
                JSONObject jo = new JSONObject();
                jo.put("iso",listOfFiles[i].getName());
                res.put(jo);
            }
        }
        System.out.println(res.toString());
        response.getWriter().print(res.toString()); 
    }

}
