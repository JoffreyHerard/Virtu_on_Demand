package virtu.containers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import virtu.IVirtualization;

/**
 * Class used to create virtual devices thanks to LXC
 *
 * @author joffreyherard Modifications by Chaest (11/01/2018) => Global rework
 */
public class LXC implements IVirtualization, Serializable {

    private static final long serialVersionUID = -356687860730753609L;

    /* Name of the LXC device */
    private String _name;

    /* Path to the to stocking file */
    private String _path;

    /* Image used for the LXC device */
    private String _image;

    /* Is the image valid */
    private boolean _valid;

    /**
     * LXC container's ID, 0 = debian; 1 = redhat
     *
     * @deprecated only debian should now be supported
     */
    private int _distribId;

    /* Number of instances */
    private static long _ID = 0;

    private boolean ON;

    /**
     * @param name : name of both the device and the image used by this one
     * @param path : path to stocking file qcow2
     * @param image : path to iso
     * @param create
     */
    public LXC(String name, String path, String image, boolean create) {

        /* Setting basic parameters */
        _name = "LXC_"+name + "" + _ID;
        _path = path;
        _image = image;
        ++_ID;

        /* Everything so far is valid */
        _valid = true;
        if (create) {
            /* Creating device */
            try {
                _name = "LXC_"+name + "" + _ID;
                /* Creating device */
                Process pr = Runtime.getRuntime().exec(new String[]{"/bin/sh", "-c", "lxc-create -n " + _name + " --template=" + _image});
                //Process pr = Runtime.getRuntime().exec("lxc-create -n " + _name + " --template=" + _image);
                pr.waitFor();
               
                /* Adding configuration files for this device */
//                pr = Runtime.getRuntime().exec(new String[]{"sed", "-i.bak10", "/lxc.network/d", "/var/lib/lxc/" + _name + "/config"});
//                pr.waitFor();
//
//                //afficherFlux(pr);
//                pr = Runtime.getRuntime().exec(new String[]{"/bin/sh", "-c", " echo 'lxc.network.type = veth' >> /var/lib/lxc/" + _name + "/config"});
//                pr.waitFor();
//                pr = Runtime.getRuntime().exec(new String[]{"/bin/sh", "-c", " echo 'lxc.network.flags = up' >> /var/lib/lxc/" + _name + "/config"});
//                pr.waitFor();
//
//                /* Careful here : vmbr0 must be set */
//                pr = Runtime.getRuntime().exec(new String[]{"/bin/sh", "-c", " echo 'lxc.network.link = vmbr0' >> /var/lib/lxc/" + _name + "/config"});
//                pr.waitFor();
//                pr = Runtime.getRuntime().exec("echo 'lxc.network.veth.pair = br-" + _name + "' >> /var/lib/lxc/" + _name + "/config");
//                pr.waitFor();

            }
            catch (IOException | InterruptedException ex) {

                /* The creation failed, the device is unvalid */
                _valid = false;

            }

            /* The creation was a success */
            _valid = true;
        }
        else{
            _name = name;
        }

    }

    /**
     * Starts up the device
     *
     * @return true if the operation was a success, false otherwise
     */
    @Override
    public boolean start() {

        /* If unvalid, stop now */
        if (!_valid) {
            return _valid;
        }

        try {

            /* Restarting the device */
            //Process pr = Runtime.getRuntime().exec(new String[]{"/bin/sh", "-c", "lxc-start", "-n "+ _name});
            Process pr = Runtime.getRuntime().exec("lxc-start -n "+ _name);
            pr.waitFor();
            //LXC.afficherFlux(pr);
            ON = true;
        }
        catch (IOException | InterruptedException ex) {

            /* Process failed */
            return false;

        }

        /* Process succeeded */
        return true;
    }

    /**
     * Forces the device to stop
     *
     * @return true if the operation was a success, false otherwise
     */
    @Override
    public boolean halt() {

        /* If unvalid, stop now */
        if (!_valid) {
            return _valid;
        }

        try {

            /* Halting the device */
            Process pr = Runtime.getRuntime().exec("lxc-stop -n " + _name);
            pr.waitFor();

            /* Destroying instance */
            pr = Runtime.getRuntime().exec("lxc-destroy -n " + _name);
            pr.waitFor();

        }
        catch (IOException | InterruptedException ex) {

            /* The operation failed */
            return false;

        }

        /* The operation was a success */
        return true;
    }

    /**
     * Stop the device
     *
     * @return true if the operation was a success, false otherwise
     */
    @Override
    public boolean stop() {

        /* If unvalid, stop now */
        if (!_valid) {
            return _valid;
        }

        try {

            /* Stopping the device */
            Process pr = Runtime.getRuntime().exec("lxc-stop -n " + _name);
            pr.waitFor();
            ON = false;

        }
        catch (IOException | InterruptedException ex) {

            /* The operation failed */
            return false;

        }

        /* The operation was a success */
        return true;
    }

    /**
     * Add a user to the machine
     *
     * @param user : name of the user
     * @param password : user's password
     * @return true if the operation was a success, false otherwise
     */
    @Override
    public boolean addUser(String user, String password) {
        try {
	    System.out.println("Ajout de l'utilisateur"); 
	    Process pr = Runtime.getRuntime().exec(new String[]{"lxc-attach","--clear-env","-n",_name,"--","adduser","--quiet","--disabled-password", "--shell","/bin/bash", "--home", "/home/"+user,"--gecos","User",user});
	    pr.waitFor();
	    //afficherFlux(pr);

	    System.out.println("Apt-get update");
	    pr = Runtime.getRuntime().exec(new String[]{"lxc-attach","--clear-env","-n", _name,"--","apt-get" ,"-y","update"});
	    pr.waitFor();
	    //afficherFlux(pr);

	    /* Setting user's password */
	    System.out.println("Set password");
	    String[] toto; //=new String[]{"lxc-attach","--clear-env","-n", _name,"echo \""+user+":"+password+"\" | chpasswd "};
	    //pr = Runtime.getRuntime().exec(new String[]{"lxc-attach","--clear-env","-n", _name,"--","/bin/sh -c","echo \""+user+":"+password+"\" | chpasswd "});
	    pr = Runtime.getRuntime().exec(toto=new String[]{"lxc-attach", "--clear-env","-n", _name,"--", "bash","-c", "echo '" + user + ":" + password + "' | chpasswd "});
	    //pr = Runtime.getRuntime().exec(toto=new String[]{"/bin/bash","-c","lxc-attach --clear-env -n "+ _name+" --", "bash","-c", "echo '" + user + ":" + password + "' ","|", "chpasswd "});
	    System.out.println("toto: "+Arrays.toString(toto));
	    pr.waitFor();
	    afficherFlux(pr);

	    /* Installing sudo */
	    System.out.println("Installation de sudo ");
	    pr = Runtime.getRuntime().exec(new String[]{"lxc-attach","--clear-env" ,"-n", _name,"--","apt-get","install", "-y", "sudo"});
	    pr.waitFor();
	    //afficherFlux(pr);

	    /* Adding sudo user */
	    System.out.println("Adding user to group sudo ");
	    pr = Runtime.getRuntime().exec(new String[]{"lxc-attach","--clear-env","-n",_name,"adduser", user,"sudo"});
	    pr.waitFor();

        }
        catch (IOException | InterruptedException ex) {
            Logger.getLogger(LXC.class.getName()).log(Level.SEVERE, null, ex);
        }

        /* The operation was a success */
        return true;
    }

    /**
     * Enables noVNC access to the device
     *
     * @return true if the operation was a success, false otherwise
     */
    @Override
    public boolean enableNoVNC() {

        /* NOT IMPLEMENTED YET */
        return false;

    }

    /**
     * Enables SSH on the device
     *
     * @return true if the operation was a success, false otherwise
     */
    @Override
    public boolean enableSSH() {

        /* If unvalid, stop now */
        if (!_valid) {
            return _valid;
        }

        try {

            /* Updating */
            Process pr = Runtime.getRuntime().exec(new String[]{"lxc-attach", "-n", "--clear-env", _name, "apt-get -y update"});
            pr.waitFor();

            /* Installing ssh server */
            pr = Runtime.getRuntime().exec(new String[]{"lxc-attach", "-n", "--clear-env", _name, "apt-get -y install openssh-server"});
            pr.waitFor();

            /* Starting ssh service */
            pr = Runtime.getRuntime().exec(new String[]{"lxc-attach", "-n", _name, "--clear-env", "/etc/init.d/ssh start"});
            pr.waitFor();

        }
        catch (IOException | InterruptedException ex) {

            /* The operation failed */
            return false;

        }

        /* The operation was a success */
        return true;
    }

    /**
     * Getter for the name
     *
     * @return the name of the device
     */
    public String getName() {
        return _name;
    }

    /**
     * Setter for the name
     *
     * @param name the name of the device
     * @return a reference to this object
     */
    public LXC setName(String name) {
        _name = name;
        return this;
    }

    /**
     * Getter for the path to stocking file
     *
     * @return the path to stocking file
     */
    public String getPath() {
        return _path;
    }

    /**
     * Setter for the path to stocking file
     *
     * @param path : the new path to stocking file
     * @return a reference to this object
     */
    public LXC setPath(String path) {
        _path = path;
        return this;
    }

    /**
     * Getter for the image
     *
     * @return the image
     */
    public String getImage() {
        return _image;
    }

    /**
     * Setter for the image
     *
     * @param image : the new image
     * @return a reference to this object
     */
    public LXC setImage(String image) {
        _image = image;
        return this;
    }

    /**
     * Getter for the distrubuction ID
     *
     * @return the distribution ID
     * @deprecated should not be used as distrution ID is deprecated
     */
    public int getDistribId() {
        return _distribId;
    }

    /**
     * Setter for the distribution ID
     *
     * @param distribId : the new distribution ID
     * @return a reference to this object
     * @deprecated should not be used as distribution ID is deprecated
     */
    public LXC setDistribId(int distribId) {
        _distribId = distribId;
        return this;
    }

    /**
     * Getter for the number of instances
     *
     * @return the number of instances
     */
    public static long getNbInstances() {
        return _ID;
    }

    /**
     * Tells if this device had a valid construction
     *
     * @return true if this device is valid
     */
    public boolean isValid() {
        return _valid;
    }

    public static void afficherFlux(Process pr) {
        BufferedReader stdInput = new BufferedReader(new InputStreamReader(pr.getInputStream()));

        BufferedReader stdError = new BufferedReader(new InputStreamReader(pr.getErrorStream()));

        // read the output from the command
        System.out.println("Here is the standard output of the command:\n");
        String s = null;
        try {
            while ((s = stdInput.readLine()) != null) {
                System.out.println(s);
            }

            // read any errors from the attempted command
            System.out.println("Here is the standard error of the command (if any):\n");
            while ((s = stdError.readLine()) != null) {
                System.out.println(s);
            }
        }
        catch (IOException ex) {
            Logger.getLogger(LXC.class.getName()).log(Level.SEVERE, null, ex);
        }
    }


    public void setON(boolean ON) {
        this.ON = ON;
    }

    @Override
    public boolean isOn() {
        boolean tor = false;
        /* Updating */
        Process pr;
	
	try {
	    
            //pr = Runtime.getRuntime().exec(new String[]{"lxc-info", "-n", _name, "|", "grep", "RUNNING"});
            //pr = Runtime.getRuntime().exec(new String[]{"lxc-attach", "-n", _name, "--clear-env",
              //  " --  ls"});
              pr = Runtime.getRuntime().exec("lxc-attach -n " + _name + " --clear-env --  ls");
	   
	   
            int retour = pr.waitFor();
            //LXC.afficherFlux(pr);
            if (retour == 0) {
                tor = true;
            }
        }
        catch (IOException ex) {
            Logger.getLogger(Docker.class.getName()).log(Level.SEVERE, null, ex);
        }
        catch (InterruptedException ex) {
            Logger.getLogger(Docker.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.out.println("ISON LXC : " + tor);
        return tor;
    }
    public String getIP(){
        if(this.isOn()){
            boolean tor = false;
            /* Updating */
            Process pr;
            String s=null;
            try {

                pr = Runtime.getRuntime().exec(new String[]{"lxc-info","-iH","-n",this._name});
                BufferedReader stdInput = new BufferedReader(new InputStreamReader(pr.getInputStream()));
                s = stdInput.readLine();
                System.out.println(s);
                int retour = pr.waitFor();
                if (retour == 0) {
                    tor = true;
                }
            }
            catch (IOException ex) {
                Logger.getLogger(LXC.class.getName()).log(Level.SEVERE, null, ex);
            }
            catch (InterruptedException ex) {
                Logger.getLogger(LXC.class.getName()).log(Level.SEVERE, null, ex);
            }
            return s;
        }else{
            return "failed";
        }
    }
}
