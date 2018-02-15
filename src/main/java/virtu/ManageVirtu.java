/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package virtu;

import virtu.VMs.KVM;
import virtu.VMs.QEMU;
import virtu.containers.Docker;
import virtu.containers.LXC;

import backend.User;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author joffreyherard
 */
public class ManageVirtu implements Serializable {

	private static final long serialVersionUID = -6524229616823009699L;

	protected transient Connection connection;
	private String dbName;
	private String dbPassword;

	public static int nbVM = 0;
	public static final String ISOBASEPATH = System.getProperty("user.dir") + "/ISO";

	private QEMU qemu[];
	private KVM kvm[];
	private Docker docker[];
	private LXC lxc[];
	private int indexes[];

	public enum KindVM {
		QEMU(0),
		KVM(1),
		DOCKER(2),
		LXC(3);
		private int value;

		private KindVM(int value) {
			this.value = value;
		}

		public int getValue() {
			return value;
		}
	;

	}

	/**
	 * Creates a new instance of ManageVirtu
	 */
    public ManageVirtu() {
		qemu = new QEMU[25];
		kvm = new KVM[25];
		docker = new Docker[25];
		lxc = new LXC[25];
		indexes = new int[4];
		indexes[0] = indexes[1] = indexes[2] = indexes[3] = 0;
		System.out.println("Constructeur Manage Virtu");
		try {
			this.connection = DriverManager.getConnection("jdbc:derby://localhost:1527/VALD_Maven", "vald", "vald");
		} catch (SQLException ex) {
			Logger.getLogger(ManageVirtu.class.getName()).log(Level.SEVERE, "ERREUR CONNECTION VIRTU MANAGER ", ex);
		}

	}

	public IVirtualization getVM(String name) {

		System.out.println("Manage Virtu : getVM");
		int id;
		if ((id = findinArray(name, KindVM.KVM)) != -1) {
			return kvm[id];
		} else if ((id = findinArray(name, KindVM.QEMU)) != -1) {
			return qemu[id];
		} else if ((id = findinArray(name, KindVM.DOCKER)) != -1) {
			System.out.println(docker[id].getName());
			return docker[id];
		} else if ((id = findinArray(name, KindVM.LXC)) != -1) {
			return lxc[id];
		} else {
			return null;
		}

	}

	public int findinArray(String name, KindVM kind) {

		System.out.println("Manage Virtu : findInArray");
		int i = 0;
		int id = -1;
		System.out.println("MV: Find  ->> etat des variables: kind :" + kind + " Nom recherche :" + name + " index du kind " + indexes[kind.getValue()]);
		while (i < indexes[kind.getValue()] && id == -1) {
			switch (kind) {
				case QEMU:
					if (qemu[i] != null && qemu[i].getName().equals(name)) {
						id = i;
					}
					i++;
					break;
				case KVM:
					if (kvm[i] != null && kvm[i].getName().equals(name)) {
						id = i;
					}
					i++;
					break;
				case DOCKER:
					if (docker[i] != null && docker[i].getName().equals(name)) {
						id = i;
						System.out.println("trouve !  ");
					} else {
						System.out.println("Different : " + docker[i].getName() + " avec " + name);
					}

					i++;
					break;
				case LXC:
					if (lxc[i] != null && lxc[i].getName().equals(name)) {
						id = i;
                                            System.out.println("trouve !  ");
					} else {
						System.out.println("Different : " + lxc[i].getName() + " avec " + name);
					}
					i++;
					break;
			}
		}
		return id;

	}
	// lister (avec ou sans type, avec ou sans utilisateur)

	public JSONArray list(String email, KindVM kind) {

		System.out.println("Manage Virtu : list");
		JSONArray arrayJ = new JSONArray();
		if (email != null) {
			PreparedStatement ps = null;
			ResultSet rs = null;

			try {
				if (this.connection != null) {
					if (kind != null) {
						String sql = "SELECT * FROM \"virtual_machine\" WHERE KIND_VM = ? AND EMAIL = ?";
						ps = this.connection.prepareStatement(sql);
						ps.setInt(1, kind.getValue());
						ps.setString(2, email);
					} else {
						String sql = "SELECT * FROM \"virtual_machine\" WHERE EMAIL = ?";
						ps = this.connection.prepareStatement(sql);
						ps.setString(1, email);
					}

					rs = ps.executeQuery();

					while (rs.next()) {
						JSONObject json = new JSONObject()
								.put("NAME", rs.getString("NAME"))
								.put("KIND_VM", rs.getInt("KIND_VM"));
						//json.put("IMAGE", rs.getString("IMAGE"));
						//json.put("EMAIl", rs.getString("EMAIl"));
						//json.put("DATA", rs.getInt("DATA"));
						//json.put("MEMORY", rs.getInt("MEMORY"));
						//json.put("VNC", rs.getInt("VNC"));
						arrayJ.put(json);
						System.out.println("json :" + json);
					}
					System.out.println("arrayJ :" + arrayJ);
					return arrayJ;
				}
			} catch (SQLException e) {
				Logger.getLogger(User.class.getName()).log(Level.SEVERE, "ERREUR RECUPERATION INFORMATIONS", e);
			} finally {
				try {
					if (ps != null) {
						ps.close();
					}
				} catch (SQLException e) {
					Logger.getLogger(User.class.getName()).log(Level.SEVERE, "ERREUR FERMETURE CONNEXION", e);
				}
			}
		}
		return arrayJ;
	}

	// créer 
	public String createVM(String email, KindVM kind, String name, String path, String image, int data, int memory) {
		int i = 0;
		String name_created = null;
		System.out.println("Manage Virtu : creationVM");
		if ((email != null) && (kind != null) && (name != null) && (path != null) && (image != null) && (data != -1) && (memory != -1)) {
			PreparedStatement ps = null;
			System.out.println("Manage Virtu => creationVM : debut try catch ");
			try {

				/* Creation de la vm */
				System.out.println("Manage Virtu => creationVM : switch case kind");

				switch (kind) {
					case QEMU:
						qemu[indexes[kind.getValue()]] = new QEMU(name, path, image, data, memory, true, -1);
						name_created = qemu[indexes[kind.getValue()]].getName();
						break;
					case KVM:
						kvm[indexes[kind.getValue()]] = new KVM(name, path, image, data, memory, true, -1);
						name_created = kvm[indexes[kind.getValue()]].getName();

						break;
					case DOCKER:
						docker[indexes[kind.getValue()]] = new Docker(name, path, image, true);
						name_created = docker[indexes[kind.getValue()]].getName();
						break;
					case LXC:
						lxc[indexes[kind.getValue()]] = new LXC(name, path, image, true);
						name_created = lxc[indexes[kind.getValue()]].getName();
						break;
				}

				System.out.println("Manage Virtu => creationVM : apres swith case");
				/* Ajout de la vm dans la bdd*/
				if (this.connection != null) {
					String sql = "INSERT INTO \"virtual_machine\" (NAME, PATH, KIND_VM, IMAGE, EMAIL,DATA,MEMORY, VNC) VALUES( ?, ?, ?, ?, ?, ?, ?,?)";
					ps = this.connection.prepareStatement(sql);
					switch (kind) {
						case QEMU:
							ps.setString(1, qemu[indexes[kind.getValue()]].getName());
							break;
						case KVM:
							ps.setString(1, kvm[indexes[kind.getValue()]].getName());
							break;
						case DOCKER:
							ps.setString(1, docker[indexes[kind.getValue()]].getName());
							break;
						case LXC:
							ps.setString(1, lxc[indexes[kind.getValue()]].getName());
							break;
					}

					ps.setString(2, path);
					ps.setInt(3, kind.getValue());
					ps.setString(4, image);
					ps.setString(5, email);
					ps.setInt(6, data);
					ps.setInt(7, memory);
					int vnc = -1;
					switch (kind) {
						case QEMU:
							vnc = qemu[indexes[kind.getValue()]].getVnc();
							break;
						case KVM:
							vnc = kvm[indexes[kind.getValue()]].getVnc();
							break;

					}

					ps.setInt(8, vnc);
					i = ps.executeUpdate();
					/*TO DO */

				}
			} catch (SQLException e) {
				Logger.getLogger(User.class.getName()).log(Level.SEVERE, "ERREUR INSCRIPTION", e);
			} finally {
				try {
					if (ps != null) {
						ps.close();
					}
				} catch (SQLException e) {
					Logger.getLogger(User.class.getName()).log(Level.SEVERE, "ERREUR FERMETURE CONNEXION", e);
				}
			}
		}
		nbVM++;
		indexes[kind.getValue()]++;
		if (i > 0) {

			System.out.println("Manage Virtu => creationVM : retourne vrai ");
			return name_created;
		} else {
			System.out.println("Manage Virtu => creationVM : retourne faux ");
			return null;
		}

	}

	//Start une VM
	public boolean startVM(KindVM kind, String nom) {
		/* Creation de la vm */

		System.out.println("Manage Virtu : startVM");
		boolean tor = false;
		int id = -1;
		int i = 0;
		switch (kind) {
			case QEMU:
				if ((id = findinArray(nom, kind)) != -1) {
					tor = qemu[id].start();
				} else {
					tor = false;
				}
				break;
			case KVM:
				if ((id = findinArray(nom, kind)) != -1) {
					tor = kvm[id].start();
				} else {
					tor = false;
				}
				break;

			case DOCKER:
				if ((id = findinArray(nom, kind)) != -1) {

					tor = docker[id].start();
				} else {
					tor = false;
				}
				break;

			case LXC:
				if ((id = findinArray(nom, kind)) != -1) {
					tor = lxc[id].start();
                                        System.out.println("toR dans startVM manage : " + tor);
				} else {
					tor = false;
				}
				break;

		}
                
		return tor;

	}

	//Stop une VM
	public boolean stopVM(KindVM kind, String nom) {
		/* Creation de la vm */
		System.out.println("Manage Virtu : stopVM");
		boolean tor = false;
		int id = -1;
		int i = 0;
		switch (kind) {
			case QEMU:
				if ((id = findinArray(nom, kind)) != -1) {
					tor = qemu[id].stop();
				} else {
					tor = false;
				}
				break;
			case KVM:
				if ((id = findinArray(nom, kind)) != -1) {
					tor = kvm[id].stop();
				} else {
					tor = false;
				}
				break;

			case DOCKER:
				if ((id = findinArray(nom, kind)) != -1) {
					tor = docker[id].stop();
				} else {
					tor = false;
				}
				break;

			case LXC:
				if ((id = findinArray(nom, kind)) != -1) {
					tor = lxc[id].stop();
				} else {
					tor = false;
				}
				break;

		}
		return tor;

	}

	public String deleteVM(KindVM kind, String email, String nom) {

		System.out.println("Manage Virtu : deleteVM");
		int i = 0;
		int id = -1;
		boolean tor = false;
		PreparedStatement ps = null;
		ResultSet rs = null;
		String name = null;
		System.out.println("Manage Virtu : deleteVM -> switch case about the name");
		switch (kind) {
			case QEMU:
				if ((id = findinArray(nom, kind)) != -1) {
					name = qemu[id].getName();
				} else {
					return "ko";
				}
				break;
			case KVM:
				if ((id = findinArray(nom, kind)) != -1) {
					name = kvm[id].getName();
				} else {
					return "ko";
				}
				break;
			case DOCKER:
				if ((id = findinArray(nom, kind)) != -1) {
					name = docker[id].getName();
				} else {
					return "ko";
				}
				break;
			case LXC:
				if ((id = findinArray(nom, kind)) != -1) {
					name = lxc[id].getName();
				} else {
					return "ko";
				}
				break;
		}
		System.out.println("Manage Virtu : deleteVM-->Suppression de la bd");
		try {
			if (this.connection != null) {
				String sql = "DELETE FROM \"virtual_machine\" WHERE NAME= ? AND EMAIL = ? AND KIND_VM = ?";
				ps = this.connection.prepareStatement(sql);

				ps.setString(1, name);
				ps.setString(2, email);
				ps.setInt(3, kind.getValue());
				ps.executeUpdate();

			}
		} catch (SQLException e) {
			Logger.getLogger(User.class.getName()).log(Level.SEVERE, "ERREUR RECUPERATION INFORMATIONS", e);
		} finally {
			try {
				if (ps != null) {
					ps.close();
				}
			} catch (SQLException e) {
				Logger.getLogger(User.class.getName()).log(Level.SEVERE, "ERREUR FERMETURE CONNEXION", e);
			}
		}
		System.out.println("Manage Virtu : deleteVM -> Suppression du systeme");
		
		switch (kind) {
			case QEMU:

				tor = qemu[id].halt();
				if (tor) {
					qemu[id] = null;
                                        if(id!=indexes[kind.getValue()]){
                                           System.out.println("Manage Virtu : deleteVM -> QEMU -> manipulation id :"+id+" avec longueur du tableau qemu.length "+qemu.length);
                                           int k;
                                           for(k=id;k<qemu.length-1;k++){
                                               qemu[k]=qemu[k+1];
                                           }
                                        }
                                        
                                        indexes[kind.getValue()]--;
					return "succes";
				} else {
					return "ko";
				}
			case KVM:
				tor = kvm[id].halt();
				if (tor) {
					kvm[id] = null;
                                        
                                        if(id!=indexes[kind.getValue()]){
                                           System.out.println("Manage Virtu : deleteVM -> kvm -> manipulation id :"+id+" avec longueur du tableau qemu.length "+kvm.length);
                                        
                                           int k;
                                           for(k=id;k<kvm.length-1;k++){
                                               kvm[k]=kvm[k+1];
                                           }
                                        }
                                        indexes[kind.getValue()]--;
					return "succes";
				} else {
					return "ko";
				}
			case DOCKER:
				tor = docker[id].halt();
				if (tor) {
					docker[id] = null;
					System.out.println("LOG DELETEVM ==> DOCKER DELETED");
                                        
                                        System.out.println("LOG DELETEVM ==> DOCKER DELETED le bon if "+(id!=indexes[kind.getValue()]));
                                        if(id!=indexes[kind.getValue()]){
                                           System.out.println("INSIDE IF Manage Virtu : deleteVM -> docker -> manipulation id :"+id+" c egal ???? "+indexes[kind.getValue()]+" avec longueur du tableau docker.length "+docker.length);
                                            int k;
                                            for(k=id;k<docker.length-1;k++){
                                               docker[k]=docker[k+1];
                                            }
                                        }
					System.out.println("OUTSIDE IFManage Virtu : deleteVM -> docker -> manipulation id :"+id+" c egal ???? "+indexes[kind.getValue()]+" avec longueur du tableau docker.length "+docker.length);
                                           
                                        indexes[kind.getValue()]--;
					return "succes";
				} else {
					System.out.println("LOG DELETEVM ==> DOCKER NOT DELETED");
					return "ko";
				}
			case LXC:
				tor = lxc[id].halt();
				if (tor) {
					lxc[id] = null;
                                        
                                        if(id!=indexes[kind.getValue()]){
                                           System.out.println("Manage Virtu : deleteVM -> lxc -> manipulation id :"+id+" avec longueur du tableau qemu.length "+lxc.length);
                                            int k;
                                            for(k=id;k<lxc.length-1;k++){
                                               lxc[k]=lxc[k+1];
                                            }
                                        }
                                        indexes[kind.getValue()]--;
					return "succes";
				} else {
					return "ko";
				}
		}
                
		return "ko";
	}

	// reload instances
	public String reloadVM(String email) {

		System.out.println("Manage Virtu : reloadVM");
		if (email != null) {
			PreparedStatement ps = null;
			ResultSet rs = null;

			indexes[0] = indexes[1] = indexes[2] = indexes[3] = 0;
			try {
				if (this.connection != null) {
					String sql = "SELECT * FROM \"virtual_machine\" WHERE EMAIL = ?";
					ps = this.connection.prepareStatement(sql);
					ps.setString(1, email);
					rs = ps.executeQuery();

					while (rs.next()) {
						switch (rs.getInt("KIND_VM")) {
							case 0:
								qemu[indexes[rs.getInt("KIND_VM")]] = new QEMU(rs.getString("NAME"), rs.getString("PATH"), rs.getString("IMAGE"), rs.getInt("DATA"), rs.getInt("MEMORY"), false, rs.getInt("VNC"));
								break;
							case 1:
								kvm[indexes[rs.getInt("KIND_VM")]] = new KVM(rs.getString("NAME"), rs.getString("PATH"), rs.getString("IMAGE"), rs.getInt("DATA"), rs.getInt("MEMORY"), false, rs.getInt("VNC"));
								break;
							case 2:
								docker[indexes[rs.getInt("KIND_VM")]] = new Docker(rs.getString("NAME"), rs.getString("PATH"), rs.getString("IMAGE"), false);
								break;
							case 3:
								lxc[indexes[rs.getInt("KIND_VM")]] = new LXC(rs.getString("NAME"), rs.getString("PATH"), rs.getString("IMAGE"), false);
								break;
						}
						indexes[rs.getInt("KIND_VM")]++;
						System.out.println("Ajout de la VM/conteneurs : " + rs.getString("NAME"));
					}
					return "success";
				} else {
					return "failed";

				}
			} catch (SQLException e) {
				Logger.getLogger(User.class.getName()).log(Level.SEVERE, "ERREUR RECUPERATION INFORMATIONS", e);
			} finally {
				try {
					if (ps != null) {
						ps.close();
					}
				} catch (SQLException e) {
					Logger.getLogger(User.class.getName()).log(Level.SEVERE, "ERREUR FERMETURE CONNEXION", e);
				}
			}
		}
		return "failed";
	}

	public boolean isOn(String nom) {
		boolean tor = false;

		System.out.println("Manage Virtu : ISON");
		switch (nom.charAt(0)) {
			case 'Q':
			case 'q':
				tor = qemu[(findinArray(nom, KindVM.QEMU))].isOn();
				break;
			case 'K':
			case 'k':
				tor = kvm[(findinArray(nom, KindVM.KVM))].isOn();
				break;
			case 'D':
			case 'd':
				tor = docker[(findinArray(nom, KindVM.DOCKER))].isOn();
				break;
			case 'L':
			case 'l':
				tor = lxc[(findinArray(nom, KindVM.LXC))].isOn();
				break;
		}
		return tor;
	}

	public boolean addAcount(String nom, KindVM kind, String email) {

		System.out.println("Manage Virtu : ADDACOUNT");
		boolean tor = false;
		String username = "";
		String password = "";
		System.out.println("LOG Manage Virtu ==> ADDACOUNT : ");
		if (email != null && kind != null) {
			PreparedStatement ps = null;
			ResultSet rs = null;

			System.out.println("LOG Manage Virtu ==> ADDACOUNT : try catch ");
			try {
				if (this.connection != null) {
					String sql = "SELECT VALD.\"user\".\"NAME\", VALD.\"user\".\"PASSWORD\" FROM VALD.\"virtual_machine\",VALD.\"user\" WHERE VALD.\"virtual_machine\".EMAIL = VALD.\"user\".EMAIL AND VALD.\"virtual_machine\".NAME = ? AND VALD.\"virtual_machine\".KIND_VM = ? ";
					ps = this.connection.prepareStatement(sql);
					ps.setString(1, nom);
					ps.setInt(2, kind.getValue());
					rs = ps.executeQuery();
					rs.next();
					username = rs.getString("NAME");
					password = rs.getString("PASSWORD");
				}
			} catch (SQLException e) {
				Logger.getLogger(User.class.getName()).log(Level.SEVERE, "ERREUR RECUPERATION INFORMATIONS", e);
				return false;
			} finally {
				try {
					if (ps != null) {
						ps.close();
					}
				} catch (SQLException e) {
					Logger.getLogger(User.class.getName()).log(Level.SEVERE, "ERREUR FERMETURE CONNEXION", e);
				}
			}
			System.out.println("LOG Manage Virtu ==> ADDACOUNT : switch case sur kind");
			switch (kind) {
				case DOCKER:

					tor = docker[(findinArray(nom, kind))].isOn();
					if (tor) {
						docker[(findinArray(nom, kind))].addUser(username, password);
					} else {
						System.out.println("LOG Manage Virtu ==> ADDACOUNT : retourne faux ");
						return false;
					}
					break;
				case LXC:
					tor = lxc[(findinArray(nom, kind))].isOn();
					if (tor) {
						lxc[(findinArray(nom, kind))].addUser(username, password);
					} else {
						System.out.println("LOG Manage Virtu ==> ADDACOUNT : retourne faux ");
						return false;
					}
					break;
			}
		}
		System.out.println("LOG Manage Virtu ==> ADDACOUNT : retourne vrai ");
		return true;
	}

	public boolean addAcountUser(String nom, KindVM kind, String username, String password) {

		System.out.println("Manage Virtu : addAcountUser");
		boolean tor = false;
		switch (kind) {
			case DOCKER:

				tor = docker[(findinArray(nom, kind))].isOn();
				if (tor) {
					docker[(findinArray(nom, kind))].addUser(username, password);
				} else {
					return false;
				}
				break;
			case LXC:
				tor = lxc[(findinArray(nom, kind))].isOn();
				if (tor) {
					lxc[(findinArray(nom, kind))].addUser(username, password);
				} else {
					return false;
				}
				break;
		}

		return true;
	}

	public boolean activateSSH(String nom, KindVM kind) {

		System.out.println("Manage Virtu : activateSSH");
		boolean tor;
		switch (kind) {
			case DOCKER:
				tor = docker[(findinArray(nom, kind))].isOn();
				if (tor) {
					docker[(findinArray(nom, kind))].enableSSH();
				} else {
					return false;
				}
				break;
			case LXC:
				tor = lxc[(findinArray(nom, kind))].isOn();
				if (tor) {
					lxc[(findinArray(nom, kind))].enableSSH();
				} else {
					return false;
				}
				break;
		}
		return true;
	}

	public int getVNC(String nom, KindVM kind) {

		System.out.println("Manage Virtu : getVNC");
		boolean back = false;
		int toR = -1;
		switch (kind) {
			case QEMU:
				
				
				toR = qemu[(findinArray(nom, kind))].getVnc();
				
				break;
			case KVM:
				
				toR = kvm[(findinArray(nom, kind))].getVnc();
				
				break;
		}
		return toR;
	}

	public String getIP(String name) {
		String res = null;
		boolean toR = false, back = false;
		switch (name.charAt(0)) {

			case 'D':
				back = docker[(findinArray(name, KindVM.DOCKER))].isOn();
				if (back) {
					res = docker[(findinArray(name, KindVM.DOCKER))].getIP();
				} else {
					return "OFF";
				}
				break;
			case 'L':
				back = lxc[(findinArray(name, KindVM.LXC))].isOn();
				if (back) {
					res = lxc[(findinArray(name, KindVM.LXC))].getIP();
				} else {
					return "OFF";
				}
				break;
		}
		return res;
	}

	public void setDbName(String dbName) {
		this.dbName = dbName;
	}

	public void setDbPassword(String dbPassword) {
		this.dbPassword = dbPassword;
	}

	public Connection getConnection() {
		return connection;
	}

	public String getDbName() {
		return dbName;
	}

	public String getDbPassword() {
		return dbPassword;
	}

}
