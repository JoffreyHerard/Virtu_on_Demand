/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package virtu.VMs;

import java.io.Serializable;
import virtu.IVirtualization;

/**
 *
 * @author joffreyherard
 */
public class VirtualBox implements IVirtualization, Serializable{
	
	/* Name of the LXC device */
	private String		_name;
	
	/* Path to the to stocking file */
	private String		_path;
	
	/* Image used for the LXC device */
	private String		_image;
	
	/* Harddisk memory */
	private int		_data;
	
	/* RAM */
	private int		_memory;
	
	/* Is the image valid */
	private boolean		_valid;
	
	/**
	 * LXC container's ID, 0 = debian; 1 = redhat 
	 * @deprecated only debian should now be supported
	 */
	private int			_distribId;
	
	/* Number of instances */
	private static long	_ID = 0;
	
	/**
	 * @param name : name of both the device and the image used by this one
	 * @param path : path to stocking file qcow2
	 * @param image : path to iso
	 * @param data : harddisk memory
	 * @param memory : RAM
	 */
	public VirtualBox(String name, String path, String image, int data, int memory) {
		
		/* Setting basic parameters */
		_name	=	name+""+_ID;
		_path	=	path;
		_image	=	image;
		_data	=	data;
		_memory	=	memory;
		++_ID;
		
		/* Everything so far is valid */
		_valid = true;
		
		/* Creating device */
		/* NOT IMPLEMENTED YET */
		_valid = false;
	}
	
	/**
	 * Starts up the device
	 * @return true if the operation was a success, false otherwise
	 */
	@Override
	public boolean start(){
		
		/* If unvalid, stop now */
		if(!_valid) return _valid;
		
		/* NOT IMPLEMENTED YET */
		return false;
	}
	
	/**
	 * Forces the device to stop
	 * @return true if the operation was a success, false otherwise
	 */	
	@Override   
	public boolean halt(){
		
		/* If unvalid, stop now */
		if(!_valid) return _valid;
		
		/* NOT IMPLEMENTED YET */
		return false;
	}
	
	/**
	 * Stop the device
	 * @return true if the operation was a success, false otherwise
	 */	 
	@Override  
	public boolean stop(){
		
		/* If unvalid, stop now */
		if(!_valid) return _valid;
		
		/* NOT IMPLEMENTED YET */
		return false;
	}
	
	/**
	 * Add a user to the machine
	 * @param user : name of the user
	 * @param password : user's password
	 * @return true if the operation was a success, false otherwise
	 */ 
	@Override
	public boolean addUser(String user, String password){
		
		/* NOT IMPLEMENTED YET */
		return false;
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
	 * Enables noVNC access to the device
	 * @return true if the operation was a success, false otherwise
	 */ 
	@Override
	public boolean enableSSH(){
		
		/* NOT IMPLEMENTED YET */
		return false;
	
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
	public VirtualBox setName(String name) {
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
	public VirtualBox setPath(String path) {
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
	public VirtualBox setImage(String image) {
		_image = image;
		return this;
	}

	/**
	 * Getter for the harddisk memory
	 * @return the harddisk memory
	 */
	public int getData() {
		return _data;
	}

	/**
	 * Setter for the harddisk memory
	 * @param data : the new harddisk memory
	 * @return a reference to this object
	 */
	public VirtualBox setImage(int data) {
		_data = data;
		return this;
	}

	/**
	 * Getter for the RAM
	 * @return the RAM
	 */
	public int getMemory() {
		return _memory;
	}

	/**
	 * Setter for the RAM
	 * @param memory : the new RAM
	 * @return a reference to this object
	 */
	public VirtualBox setMemory(int memory) {
		_memory = memory;
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
	public VirtualBox setDistribId(int distribId) {
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

	@Override
	public boolean isOn() {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}
	
}
