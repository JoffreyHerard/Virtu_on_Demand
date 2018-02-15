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
@WebServlet(name = "MachineResetVNC", urlPatterns = {"/machine/resetvnc/*"})
public class MachineResetVNC extends UserspaceServlet {

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

        System.out.println("MachineResetVNC : Begin exec");

        String execute = "";
        Process pr;
        // kill all process launch by old program version.
        for (int i = 0; i < 100; i++) {
            try {
                if (i < 10) {
//                    execute = "sudo lsof -i tcp:590" + i + " | awk 'NR!=1 {print $2}' | sudo xargs kill";
//                    pr = Runtime.getRuntime().exec(execute);
//                    pr.waitFor();

                    
                    
                    execute = "kill \"$(lsof -t -i:950"+i+"0)\"";
                    pr = Runtime.getRuntime().exec(execute);
                    pr.waitFor();
                } else {
//                    execute = "sudo lsof -i tcp:59" + i + " | awk 'NR!=1 {print $2}' | sudo xargs kill";
//                    pr = Runtime.getRuntime().exec(execute);
//                    pr.waitFor();

                    execute = "kill \"$(lsof -t -i:95"+i+"0)\"";
                    pr = Runtime.getRuntime().exec(execute);
                    pr.waitFor();
                }
                LXC.afficherFlux(pr);
            }
            catch (IOException | InterruptedException ex) {
                System.err.println("MachineResetVNC : exec error");
            }
        }
        
        System.out.println("MachineResetVNC : End exec");
    }

}
