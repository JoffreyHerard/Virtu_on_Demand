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
import virtu.ManageVirtu;

/**
 *
 * @author kiralex
 */
@WebServlet(name = "MachineCreate", urlPatterns = {"/machine/create/*"})
public class MachineCreate extends UserspaceServlet {

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		super.doGet(request, response);

		response.setCharacterEncoding("UTF-8");
		response.setContentType("application/json");
		JSONObject res = new JSONObject();

		boolean toR = false;
		String name = request.getParameter("name");
		String kind = request.getParameter("kind");
		String path = null;
		String image = request.getParameter("img");
		String data = request.getParameter("hddSize");
		String memory = request.getParameter("memorySize");

		User user = (User) request.getSession().getAttribute("user");
		String email = user.getEmail();
		ManageVirtu mv = user.getManageVirtu();
		ManageVirtu.KindVM k = null;
		String nom;
		switch (kind.charAt(0)) {

			case 'Q':
			case 'q':
				k = ManageVirtu.KindVM.QEMU;
				break;
			case 'K':
			case 'k':
				k = ManageVirtu.KindVM.KVM;
				break;
			case 'D':
			case 'd':
				k = ManageVirtu.KindVM.DOCKER;
				break;
			case 'L':
			case 'l':
				k = ManageVirtu.KindVM.LXC;
				break;
			default:
				k = null;
				break;
		}
		if (k == ManageVirtu.KindVM.LXC || k == ManageVirtu.KindVM.DOCKER) {
			//Containers
			System.out.println("Création d'un conteneur ");

			String error = null;
			int retour = 500;
			error = "error";
			retour = 500;
			res.put("error", error);
			res.put("code", retour);
			
			response.getWriter().print(res.toString());
			nom = mv.createVM(email, k, name, "nothingtoput", image, 0, 0);
			if (nom != null) {
				toR = mv.startVM(k, nom);
				if (toR) {
					toR = mv.addAcount(nom, k, email);
				} else {
					/*String */error = null;
					/*int */retour = 500;
					error = "error";
					retour = 500;
					res.put("error", error);
					res.put("code", retour);
					response.getWriter().print(res.toString());
				}
			} else {
				/*String */error = null;
				/*int */retour = 500;
				error = "error";
				retour = 500;
				res.put("error", error);
				res.put("code", retour);
				response.getWriter().print(res.toString());
			}
		} else {
			//hypervisors
			System.out.println("Création d'une VM ");
			if (image.equals("debian") || image.equals("ubuntu")) {
				//COMMON
				path = ManageVirtu.ISOBASEPATH + "/generic";
				image = ManageVirtu.ISOBASEPATH + "/generic/" + image + ".iso";
				nom = mv.createVM(email, k, name, path, image, Integer.parseInt(data), Integer.parseInt(memory));
			} else {
				//PERSONNAL
				path = ManageVirtu.ISOBASEPATH + "/personal/" + email + "/" + image + ".iso";
				nom = mv.createVM(email, k, name, path, image, Integer.parseInt(data), Integer.parseInt(memory));
			}
		}

		String error = null;
		int retour = 500;
		if (nom != null) {
			error = "error";
			retour = 200;
		} else {
			error = "";
			retour = 500;
		}
		res.put("error", error);
		res.put("code", retour);
		response.getWriter().print(res.toString());
	}

}
