package backend;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import virtu.ManageVirtu;

/**
 * Class designed to handle the connection of User in the site
 */
@ManagedBean
@SessionScoped
public class User implements Serializable {

    private String name;
    private String password;
    private String email;
    private String dbName;
    private String dbPassword;
    private transient Connection connection;
    private boolean connected;

    ManageVirtu manageVirtu;

    /**
     * Creates a new instance of User
     */
    public User() {
        try {
            this.connection = DriverManager.getConnection("jdbc:derby://localhost:1527/VALD_Maven", "vald", "vald");
            this.manageVirtu = new ManageVirtu();
        }
        catch (SQLException ex) {
            Logger.getLogger(User.class.getName()).log(Level.SEVERE, "ERREUR CONNECTION", ex);
        }
    }

    /**
     * Getters for attributes
     *
     * @return
     */
    public String getName() {
        return name;
    }

    public String getPassword() {
        return password;
    }

    public boolean isConnected() {
        return connected;
    }

    public String getEmail() {
        return email;
    }

    /**
     * Setters for attributes
     *
     * @param name
     */
    public void setName(String name) {
        this.name = name;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String login() {
        if (name == null || password == null) {
            System.out.println("Name or password not set.");
            return "error" + "?faces-redirect=true";

        }
        if (!dbData(name)) {
            connected = false;
            return "error" + "?faces-redirect=true";
        }

        if (name.equals(dbName) && password.equals(dbPassword)) {
            connected = true;
            return "index" + "?faces-redirect=true";
        } else {
            connected = false;
            return "error" + "?faces-redirect=true";
        }
    }

    /**
     * Log user out
     */
    public void logout() {

        FacesContext.getCurrentInstance().getExternalContext().invalidateSession();
        FacesContext.getCurrentInstance().getApplication().getNavigationHandler()
                .handleNavigation(FacesContext.getCurrentInstance(), null, "/index.xhtml");
    }

    /**
     * Register a new user
     *
     * @return
     */
    public String register() {
        int i = 0;
        if (name != null) {

            /* Prepareing database interaction */
            PreparedStatement ps = null;

            try {
                if (this.connection != null) {
                    String sql = "INSERT INTO \"user\" (NAME, PASSWORD, EMAIL) VALUES( ?, ?, ?)";
                    System.out.println("SQL : " + sql);
                    ps = this.connection.prepareStatement(sql);
                    ps.setString(1, name);
                    ps.setString(2, password);
                    ps.setString(3, email);
                    i = ps.executeUpdate();
                    System.out.println("Data Added Successfully");

                }
            }
            catch (SQLException e) {
                Logger.getLogger(User.class.getName()).log(Level.SEVERE, "ERREUR INSCRIPTION", e);
            }
            finally {
                try {
                    if (ps != null) {
                        ps.close();

                    }
                }
                catch (SQLException e) {
                    Logger.getLogger(User.class
                            .getName()).log(Level.SEVERE, "ERREUR FERMETURE CONNEXION", e);
                }
            }
        }
        if (i > 0) {
            return "success";
        } else {
            return "error";
        }
    }

    public boolean dbData(String uName) {
        if (uName != null) {
            PreparedStatement ps = null;
            ResultSet rs = null;

            try {
                if (this.connection != null) {
                    String sql = "select * from \"user\" where name = '" + uName + "'";
                    ps = this.connection.prepareStatement(sql);
                    rs = ps.executeQuery();
                    rs.next();
                    dbName = rs.getString("NAME");
                    dbPassword = rs.getString("PASSWORD");

                    name = rs.getString("NAME");
                    email = rs.getString("EMAIL");
                    manageVirtu.reloadVM(email);

                    return true;
                } else {
                    return false;

                }
            }
            catch (SQLException e) {
                Logger.getLogger(User.class
                        .getName()).log(Level.SEVERE, "ERREUR RECUPERATION INFORMATIONS", e);
            } finally {
                try {
                    if (ps != null) {
                        ps.close();

                    }
                }
                catch (SQLException e) {
                    Logger.getLogger(User.class
                            .getName()).log(Level.SEVERE, "ERREUR FERMETURE CONNEXION", e);
                }
            }
        }
        return false;
    }

    public ManageVirtu getManageVirtu() {
        return manageVirtu;
    }
}