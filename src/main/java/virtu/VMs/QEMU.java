package virtu.VMs;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;
import virtu.IVirtualization;

/**
 * Class designed to handle the creation of virtual devices using QEMU
 * @author joffreyherard
 * Modifications by Chaest (11/01/2018) => Global rework
 */
public class QEMU implements IVirtualization, Serializable {

    private static final long serialVersionUID = -1600297671968751148L;

    /* Name of the device */
    private String _name;

    /* Path to the to stocking file */
    private String _path;

    /* Image used for the device */
    private String _image;

    /* Harddisk memory */
    private int _data;

    /* RAM */
    private int _memory;

    /*vnc port*/
    private int _vnc ;
	
    /* Is the image valid */
    private boolean _valid;

    /**
     * Device's ID, 0 = debian; 1 = redhat
     *
     * @deprecated only debian should now be supported
     */
    private int _distribId;

    /* Number of instances */
    private static long _ID = 0;
	
	private static int nb_qemu=0;
	
	private boolean ON;

    /**
     * @param name : name of both the device and the image used by this one
     * @param path : path to stocking file qcow2
     * @param image : path to iso
     * @param data : harddisk memory
     * @param memory : RAM
	 * @param create
     */
    public QEMU(String name, String path, String image, int data, int memory, boolean create,int vnc) {
		System.out.println("===== QEMU ==== :Création d'une instance QEMU");
        /* Setting basic parameters */
       
        _path = path;
        _image = image;
        _data = data;
        _memory = memory;
        _vnc=nb_qemu;
        ++_ID;
		if(nb_qemu<50){
			/* Everything so far is valid */
			if(create){
                            ON = true;
				_valid = true;

				/* Creating device */
				try {
                                    _name = "QEMU_"+name + "" + _ID;
					/* Creating device */
					Process pr = Runtime.getRuntime().exec("qemu-img create -f qcow2 " + _path + "/" + _name + ".qcow2 " + _data + "G");
					pr = Runtime.getRuntime().exec(new String[]{"/bin/sh", "-c",
						"qemu-system-x86_64 -nographic -k fr --m "
						+ _memory + " -hda " + _path + "/" + _name + ".qcow2 " + "-boot d -cdrom " + _image
						+ " -net nic -net user -vnc :"+_vnc});
				} catch (IOException ex) {

					/* The creation failed, the device is unvalid */
					_valid = false;

				}

				/* The creation was a success */
				_valid = true;
			}else{
                            ON = false;
                                _name = name;
				_vnc= vnc;
				_valid = true;
			}
			nb_qemu++;
		}
    }

    /**
     * Starts up the device
     *
     * @return true if the operation was a success, false otherwise
     */
    @Override
    public boolean start() {
        try {

            /* Restarting the device */
            Process pr = Runtime.getRuntime().exec(new String[]{"/bin/sh", "-c",
                "qemu-system-x86_64 -nographic -k fr --m "
                + _memory + " -hda " + _path + "/" + _name + ".qcow2 " + "-boot d "
                + " -net nic -net user -vnc :"+_vnc});
			ON=true;

        } catch (IOException ex) {
            Logger.getLogger(QEMU.class.getName()).log(Level.SEVERE, null, ex);
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
            /* Stopping the device */
            Process pr = Runtime.getRuntime().exec(new String[]{"/bin/sh", "-c", "kill -9 $(pgrep -f " + _name + ")"});
            pr.waitFor();
            /* Destroying device */
            pr = Runtime.getRuntime().exec(new String[]{"/bin/sh", "-c", "rm " + _path + "/" + _name + ".qcow2" });
            pr.waitFor();
            afficherFlux(pr);

        } catch (IOException | InterruptedException ex) {

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
            Process pr = Runtime.getRuntime().exec(new String[]{"/bin/sh", "-c", "kill $(pgrep -f " + _name + ")"});
            pr.waitFor();
			ON=false;

        } catch (IOException | InterruptedException ex) {

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

        /* NOT IMPLEMENTED YET */
        return false;
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
     * Enables noVNC access to the device
     *
     * @return true if the operation was a success, false otherwise
     */
    @Override
    public boolean enableSSH() {

        /* NOT IMPLEMENTED YET */
        return false;

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
    public QEMU setName(String name) {
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
    public QEMU setPath(String path) {
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
    public QEMU setImage(String image) {
        _image = image;
        return this;
    }

    /**
     * Getter for the harddisk memory
     *
     * @return the harddisk memory
     */
    public int getData() {
        return _data;
    }

    /**
     * Setter for the harddisk memory
     *
     * @param data : the new harddisk memory
     * @return a reference to this object
     */
    public QEMU setImage(int data) {
        _data = data;
        return this;
    }

    /**
     * Getter for the RAM
     *
     * @return the RAM
     */
    public int getMemory() {
        return _memory;
    }

    /**
     * Setter for the RAM
     *
     * @param memory : the new RAM
     * @return a reference to this object
     */
    public QEMU setMemory(int memory) {
        _memory = memory;
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
    public QEMU setDistribId(int distribId) {
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

    public void afficherFlux(Process pr) {
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
        } catch (IOException ex) {
            Logger.getLogger(QEMU.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
	

	@Override
	public boolean isOn() {
            return ON;
	}

	public int getVnc() {
		return _vnc;
	}

	public void setVnc(int _vnc) {
		this._vnc = _vnc;
	}

	public static int getNb_qemu() {
		return nb_qemu;
	}

	public static void setNb_qemu(int nb_qemu) {
		QEMU.nb_qemu = nb_qemu;
	}
	
}
