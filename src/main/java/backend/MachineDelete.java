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
import virtu.IVirtualization;
import virtu.ManageVirtu;
import virtu.VMs.KVM;
import virtu.VMs.QEMU;
import virtu.containers.Docker;
import virtu.containers.LXC;

/**
 *
 * @author kiralex
 */
@WebServlet(urlPatterns = {"/machine/delete/*"})
public class MachineDelete extends UserspaceServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        super.doGet(request, response);
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json");
        JSONObject res = new JSONObject();
        String name = request.getParameter("name");

        if (name == null) {
            request.setAttribute("errorMessage", "No machine name in request parameters.");
            request.getRequestDispatcher("/error").forward(request, response);
            return;
        }
        System.out.println("SERVLET DELETING VM received: ");
        User user = (User) request.getSession().getAttribute("user");
        ManageVirtu mv = user.getManageVirtu();

        ManageVirtu.KindVM k = null;

        switch (name.charAt(0)) {
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
                k = null;
                break;
        }

        String toR = mv.deleteVM(k, user.getEmail(), name);
        String error = null;
        int retour = 500;
        if (!toR.equals("succes")) {
            error = "error";
            retour = 500;
        } else {
            error = "";
            retour = 200;
        }
        res.put("error", error);
        res.put("code", retour);
        response.getWriter().print(res.toString());
    }

}
