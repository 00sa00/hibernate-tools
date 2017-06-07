package org.hibernate.tool.hbm2x.hbm2hbmxml.AbstractTest;

import java.io.ObjectStreamClass;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Paco Hern�ndez
 */
public class Car implements java.io.Serializable {

	private static final long serialVersionUID = 
			ObjectStreamClass.lookup(Car.class).getSerialVersionUID();
		
	private long id;
	private String model;
	private Set<CarPart> carParts = new HashSet<CarPart>();
	
	/**
	 * @return Returns the id.
	 */
	public long getId() {
		return id;
	}
	/**
	 * @param id The id to set.
	 */
	public void setId(long id) {
		this.id = id;
	}
	/**
	 * @return Returns the model.
	 */
	public String getModel() {
		return model;
	}
	/**
	 * @param model The model to set.
	 */
	public void setModel(String model) {
		this.model = model;
	}
	public Set<CarPart> getCarParts() {
		return carParts;
	}
	public void setCarParts(Set<CarPart> carParts) {
		this.carParts = carParts;
	}
}
