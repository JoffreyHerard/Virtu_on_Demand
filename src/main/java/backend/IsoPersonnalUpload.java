/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package backend;

import java.io.File;
import java.io.IOException;
import java.util.List;
import javax.activation.MimetypesFileTypeMap;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.json.JSONObject;
import virtu.ManageVirtu;

/**
 *
 * @author kiralex
 */
@WebServlet(urlPatterns = {"/iso/personnal/upload"})
public class IsoPersonnalUpload extends UserspaceServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json");
        JSONObject res = new JSONObject();

        User user = (User) request.getSession().getAttribute("user");
        String email = user.getEmail();

        String uploadDestination = ManageVirtu.ISOBASEPATH + File.separator + email + File.separator;

        System.out.println(uploadDestination);
        
        String isoName = request.getParameter("id");
        if(isoName == null){
            res.put("code", 500);
            res.put("error", "ISO name must be passed");
            return;
        }

        //Checks if the form has 'enctype=multipart/form-data' attribute in it.
        if (!ServletFileUpload.isMultipartContent(request)) {
            res.put("code", 500);
            res.put("error", "Form tag must has 'enctype=multipart/form-data' attribute");
        } else {
            File uploadDir = new File(uploadDestination);
            if (!uploadDir.exists()) {
                uploadDir.mkdirs();
            }

            try {
                List<FileItem> multiparts = new ServletFileUpload(new DiskFileItemFactory()).parseRequest(request);
                for (FileItem item : multiparts) {
                    if (!item.isFormField()) {
                        String name = new File(item.getName()).getName();
                        
                        System.out.println("name : " + name);
                        if(name.matches("\\*.iso")){
                            item.write(new File(uploadDestination + File.separator + isoName + ".iso"));
                        }else{
                            throw new Exception("File must be an ISO");
                        }
                    }
                }

                res.put("code", 200);
                res.put("error", "File(s) uploaded successfully!");
            }
            catch (Exception e) {
                res.put("code", 500);
                res.put("error", "Error during the upload : " + e.getMessage());
            }
        }
        
        response.getWriter().println(res.toString());

    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        super.doGet(request, response);
        response.getWriter().print("youp !!");
    }

}
