package persistence_test;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity 
public class Dogs implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1785374661881098920L;
	@Id
	@Column(name="id")
	@GeneratedValue
	private long id;
	@Column(name="name")
	private String name;
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	
}
