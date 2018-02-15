package virtu.containers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;
import virtu.IVirtualization;
/**
 * Class used to create virtual devices using Docker
 * @author joffreyherard
 * Modifications made by : Kiralex (08/01/2018) => correction and testing
 * Modifications made by : Chaest (11/01/2018) => global rework
 */
public class Docker implements IVirtualization, Serializable{

    private static final long serialVersionUID = 7516998646343323247L;
	
	/* Name of the docker device */
	private String		_name;
	
	/* Path to the to stocking file */
	private String		_path;
	
	/* Image used for the docker device */
	private String		_image;
	
	/* Is the image valid */
	private boolean		_valid;
	
	/**
	 * Docker container's ID, 0 = debian; 1 = redhat 
	 * @deprecated only debian should now be supported
	 */
	private int			_distribId;
	
	/* Number of instances */
	private static long	_ID = 0;
	
	
	private boolean ON; 
	/**
	 * @param name : name of both the device and the image used by this one
	 * @param path : path to stocking file qcow2
	 * @param image : path to iso
	 * @param create
	 */
	public Docker(String name, String path, String image, boolean create) {
		
		/* Setting basic parameters */
		
		_path	=	path;
		_image	=	image;
		++_ID;
		
		/* Everything so far is valid */
		_valid = true;
		if(create){
			/* Creating device */
			try {

				/* Creating device */
                                _name	=	"Docker_"+name+""+_ID;
				Process pr = Runtime.getRuntime().exec(new String[]{"/bin/sh", "-c", "docker run --name "+_name+ " -h "+_name+" -dit "+image});
				pr.waitFor();
                                /* Updating and installing sudo */
                                pr = Runtime.getRuntime().exec(new String[]				{"docker", "exec", _name, "/bin/sh", "-c","apt-get update && apt-get -y install sudo "});
                                pr.waitFor();
				/* As the device is launched by default, it is stopped right away */
				pr = Runtime.getRuntime().exec(new String[]{"/bin/sh", "-c","docker stop "+_name});
				pr.waitFor();
			} catch (IOException | InterruptedException ex) {

				/* Not valid anymore */
				_valid = false;

			}
		}
		else{
			_valid = true;
                        _name	= name;
		}
	}
	
	/**
	 * Starts up the device
	 * @return true if the operation was a success, false otherwise
	 */
	@Override
	public boolean start(){
		System.out.println("Starting the machine: "+_name );
		/* If unvalid, stop now */
		if(!_valid) return _valid;
		
		try {
			
			/* Restarting the device */
			
			
			Process pr = Runtime.getRuntime().exec(new String[]{"/bin/sh", "-c","docker restart "+ _name});
			pr.waitFor();
			ON=true;
		
		} catch (IOException | InterruptedException ex) {
			
			/* Process failed */
			return false;
		
		}
		
		/* Process succeeded */
		return true;
	}
	
	/**
	 * Forces the device to stop
	 * @return true if the operation was a success, false otherwise
	 */	
	@Override   
	public boolean halt(){
		System.out.println("Deleting the machine:"+_name );
		/* If unvalid, stop now */
		if(!_valid) return _valid;
		
		try {
			
			/* Halting the device */
                        this.stop();
			Process pr = Runtime.getRuntime().exec(new String[]{"/bin/sh", "-c","docker rm -f "+ _name});
			pr.waitFor();
			
		} catch (IOException | InterruptedException ex) {
			
			/* The operation failed */
			return false;
			
		}
		
		/* The operation was a success */
		return true;
	}
	
	/**
	 * Stop the device
	 * @return true if the operation was a success, false otherwise
	 */	 
	@Override  
	public boolean stop(){
		System.out.println("Stopping the machine: "+_name );
		/* If unvalid, stop now */
		try {
			
			/* Stopping the device */
			Process pr = Runtime.getRuntime().exec(new String[]{"/bin/sh", "-c","docker stop "+ _name});
			pr.waitFor();
			
		} catch (IOException | InterruptedException ex) {
			
			/* The operation failed */
			return false;
			
		}
		
		/* The operation was a success */
		return true;
	}
	
	/**
	 * Add a user to the machine
	 * @param user : name of the user
	 * @param password : user's password
	 * @return true if the operation was a success, false otherwise
	 */ 
	@Override
	public boolean addUser(String user, String password){
		
		/* If unvalid, stop now */
		if(!_valid) return _valid;
		
		try {
			
			/* Adding user */
			Process pr = Runtime.getRuntime().exec(new String[]		{"docker", "exec", _name, "/bin/sh", "-c",
																	"adduser --quiet --disabled-password --shell /bin/bash --home /home/"+user+" --gecos \"User\" "+user});
			pr.waitFor();

			/* Setting user's password */
			pr = Runtime.getRuntime().exec(new String[]				{"docker", "exec", _name, "/bin/sh", "-c", 
																	"echo \""+user+":"+password+"\" | chpasswd"});
			pr.waitFor();

			/* Adding sudo user */
			pr = Runtime.getRuntime().exec(new String[]				{"docker", "exec", _name, "/bin/sh", "-c", 
																	"adduser "+user+" sudo"});
			pr.waitFor();
			
		} catch (IOException | InterruptedException ex) {
			
			/* The operation failed */
			return false;
			
		}
		
		/* The operation was a success */
		return true;
	}
	
	/**
	 * Enables noVNC access to the device
	 * @return true if the operation was a success, false otherwise
	 */ 
	@Override
	public boolean enableNoVNC(){
		
		/* NOT IMPLEMENTED YET */
		return false;
	
	}
	
	
	/**
	 * Enables SSH on the device
	 * @return true if the operation was a success, false otherwise
	 */ 
	@Override
	public boolean enableSSH(){
		System.out.println("ENABLING SSH ON DOCKER: "+_name);
		/* If unvalid, stop now */
		if(!_valid) return _valid;
		
		try {
			
			/* Updating */				
			Process pr = Runtime.getRuntime().exec(new String[]		{"docker", "exec", _name, "/bin/sh", "-c", "apt-get -y update"});
			pr.waitFor();
			
			/* Installing ssh server */
			pr = Runtime.getRuntime().exec(new String[]				{"docker", "exec", _name, "/bin/sh", "-c", "apt-get -y install openssh-server"});
			pr.waitFor();
			
			/* Starting ssh service */
			pr = Runtime.getRuntime().exec(new String[]				{"docker", "exec", _name, "/bin/sh", "-c", "/etc/init.d/ssh start"});
			pr.waitFor();
			
		} catch (IOException | InterruptedException ex) {
			
			/* The operation failed */
			return false;
			
		}
		
		/* The operation was a success */
		return true;
	}
	
	/**
	 * Getter for the name
	 * @return the name of the device
	 */
	public String getName() {
		return _name;
	}

	/**
	 * Setter for the name
	 * @param name the name of the device
	 * @return a reference to this object
	 */
	public Docker setName(String name) {
		_name = name;
		return this;
	}

	/**
	 * Getter for the path to stocking file
	 * @return the path to stocking file
	 */
	public String getPath() {
		return _path;
	}

	/** 
	 * Setter for the path to stocking file
	 * @param path : the new path to stocking file
	 * @return a reference to this object
	 */
	public Docker setPath(String path) {
		_path = path;
		return this;
	}

	/**
	 * Getter for the image
	 * @return the image
	 */
	public String getImage() {
		return _image;
	}

	/**
	 * Setter for the image
	 * @param image : the new image
	 * @return a reference to this object
	 */
	public Docker setImage(String image) {
		_image = image;
		return this;
	}

	/**
	 * Getter for the distrubuction ID
	 * @return the distribution ID
	 * @deprecated should not be used as distrution ID is deprecated
	 */
	public int getDistribId() {
		return _distribId;
	}

	/**
	 * Setter for the distribution ID
	 * @param distribId : the new distribution ID
	 * @return a reference to this object
	 * @deprecated should not be used as distribution ID is deprecated
	 */
	public Docker setDistribId(int distribId) {
		_distribId = distribId;
		return this;
	}

	/**
	 * Getter for the number of instances 
	 * @return the number of instances
	 */
	public static long getNbInstances() {
		return _ID;
	}
	
	/**
	 * Tells if this device had a valid construction
	 * @return true if this device is valid
	 */
	public boolean isValid(){
		return _valid;
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
                        // pr = Runtime.getRuntime().exec(new String[]	{"/bin/sh", "-c", "docker exec", _name, "ps"});
                        pr = Runtime.getRuntime().exec(new String[]				{"docker", "exec", _name, "/bin/sh", "-c","ls "});

			int retour = pr.waitFor();
                        LXC.afficherFlux(pr);
			if(retour== 0){tor=true;}
		} catch (IOException | InterruptedException ex) {
			Logger.getLogger(Docker.class.getName()).log(Level.SEVERE, null, ex);
		}
                System.out.println("ISON docker : " + tor);
		return tor;
	}
        public String getIP(){
            if(this.isOn()){
                boolean tor = false;
                /* Updating */
                Process pr =null;
                String s=null;

                try {

                    pr = Runtime.getRuntime().exec(new String[]{"docker", "inspect", "-f", "{{range .NetworkSettings.Networks}}{{.IPAddress}}{{end}}", _name});
                    BufferedReader stdInput = new BufferedReader(new InputStreamReader(pr.getInputStream()));
                    s = stdInput.readLine();
                    System.out.println(s);
                    int retour = pr.waitFor();
                    if (retour == 0) {
                        tor = true;
                        return s;
                    }
                    
                }
                catch (IOException ex) {
                    Logger.getLogger(Docker.class.getName()).log(Level.SEVERE, null, ex);
                }
                catch (InterruptedException ex) {
                    Logger.getLogger(Docker.class.getName()).log(Level.SEVERE, null, ex);
                }	
                
            }else{
                
            }    
            return "failed";
        }
}
