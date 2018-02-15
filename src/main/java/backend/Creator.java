package backend;

import java.io.Serializable;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

/**
 * Bean handling VM creation
 * @author Chaest
 */
@ManagedBean
@SessionScoped
public class Creator implements Serializable {

	private String name;
	private String email;
	private short typeChoice;
	private short hddChoice;
	private short RAMChoice;
	private short constructorChoice;

	/**
	 * Creates a new instance of User
	 */
	public Creator() {
		
	}

	/**
	 * Getters for attributes
	 *
	 * @return
	 */
	public String getName() {
		return name;
	}

	public short getTypeChoice() {
		return typeChoice;
	}

	public String getEmail() {
		return email;
	}
	
	public short getHddChoice(){
		return hddChoice;
	}
	
	public short getRAMChoice(){
		return RAMChoice;
	}
	
	public short getConstructorChoice(){
		return constructorChoice;
	}

	/**
	 * Setters for attributes
	 *
	 */
	public void setName(String name) {
		this.name = name;
	}

	public void setTypeChoice(short typeChoice) {
		this.typeChoice = typeChoice;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public void setHddChoice(short hddChoice) {
		this.hddChoice = hddChoice;
	}

	public void setRAMChoice(short RAMChoice) {
		this.RAMChoice = RAMChoice;
	}

	public void setConstructorChoice(short constructorChoice) {
		this.constructorChoice = constructorChoice;
	}
	
	public void create(){}

}