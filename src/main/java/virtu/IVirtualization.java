/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package virtu;

/**
 * Interface implemented by classes used to create virtuel devices
 * @author joffreyherard
 * Mofications made by Chaest (11/01/2018) => global rework
 */
public interface IVirtualization  {
    
	/**
	 * Starts up the device
	 * @return true if the operation was a success, false otherwise
	 */
	public boolean start();
	
	/**
	 * Forces the device to stop
	 * @return true if the operation was a success, false otherwise
	 */	
	public boolean halt();
	
	/**
	 * Stop the device
	 * @return true if the operation was a success, false otherwise
	 */	
	public boolean stop();
	
	/**
	 * Add a user to the machine
	 * @param user : name of the user
	 * @param password : user's password
	 * @return true if the operation was a success, false otherwise
	 */ 
	public boolean addUser(String user, String password);
	
	/**
	 * Enables noVNC access to the device
	 * @return true if the operation was a success, false otherwise
	 */ 
	public boolean enableNoVNC();
	
	/**
	 * Enables SSH on the device
	 * @return true if the operation was a success, false otherwise
	 */ 
	public boolean enableSSH();
	
	public boolean isOn();
        
        public String getName();
        
}
