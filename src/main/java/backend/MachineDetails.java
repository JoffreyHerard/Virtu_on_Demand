/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package backend;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.JSONObject;
import static org.json.JSONObject.NULL;
import virtu.IVirtualization;
import virtu.ManageVirtu;
import virtu.VMs.*;
import virtu.containers.*;

/**
 *
 * @author kiralex
 */
@WebServlet(name = "MachineDetails", urlPatterns = {"/machine/details/*"})
public class MachineDetails extends UserspaceServlet {

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
        super.doGet(request, response);
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json");
        
        String name = request.getParameter("name");

        if (name == null) {
            request.setAttribute("errorMessage", "No machine id in request parameters.");
            request.getRequestDispatcher("/error").forward(request, response);
            return;
        }//
        
        User user = (User) request.getSession().getAttribute("user");
        String email = user.getEmail();
        ManageVirtu mv = user.getManageVirtu();
        IVirtualization v = mv.getVM(name);
        
        if (v == null) {
            request.setAttribute("errorMessage", "No machine found with this id");
            request.getRequestDispatcher("/error").forward(request, response);
            return;
        }
        
        JSONObject res = new JSONObject();
        
        res.put("hddSize", 0);
        res.put("hddFile", NULL);
        res.put("iso", NULL);
        res.put("memorySize", 0);
        res.put("ports", NULL);
        res.put("startDate", NULL);
        res.put("stopDate", NULL);
        
        res.put("name", v.getName());
        res.put("state", v.isOn());
        if(v instanceof Docker){
            res.put("kind", "DOCKER");
            res.put("image", ((Docker) v).getImage());
        }else if(v instanceof LXC){
            res.put("kind", "LXC");
            res.put("image", ((LXC) v).getImage());
        }else if(v instanceof QEMU){
            res.put("kind", "QEMU");
            res.put("image", NULL);
            res.put("hddSize", ((QEMU) v).getData());
            res.put("hddFile", ((QEMU) v).getPath());
            res.put("iso", ((QEMU) v).getImage());
            res.put("memorySize", ((QEMU) v).getMemory());
        }else if(v instanceof KVM){
            res.put("kind", "KVM");
            res.put("image", NULL);
            res.put("hddSize", ((KVM) v).getData());
            res.put("hddFile", ((KVM) v).getPath());
            res.put("iso", ((KVM) v).getImage());
            res.put("memorySize", ((KVM) v).getMemory());
        }
        
        response.getWriter().print(res.toString());
    }

}
