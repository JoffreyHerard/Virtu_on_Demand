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
import org.json.JSONArray;
import org.json.JSONObject;
import virtu.ManageVirtu;
import virtu.ManageVirtu.KindVM;

/**
 *
 * @author kiralex
 */

@WebServlet(name = "MachineList", urlPatterns = {"/machine/list/*"})
public class MachineList extends UserspaceServlet {
@Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        super.doGet(request, response);
        
        
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json");
        
	System.out.println("ENTERS");
        String kind = request.getParameter("kind");
        User user = (User) request.getSession().getAttribute("user");
        String email = user.getEmail();
       ManageVirtu mv = user.getManageVirtu();
        KindVM k =null;
	if(kind!=null){
	    
	    switch(kind.charAt(0)){

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
	}
        JSONArray res =mv.list(email,k);
        	System.out.println("EXITS WITH " + new JSONObject().put("list",res).toString());
        response.getWriter().print(new JSONObject().put("list",res).toString());
        
    }

}
