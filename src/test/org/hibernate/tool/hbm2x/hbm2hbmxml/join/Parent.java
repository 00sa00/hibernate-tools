//$Id$
package org.hibernate.tool.hbm2x.hbm2hbmxml.join;

import java.util.Collection;
import java.util.HashSet;

/**
 * @author gavin
 */
public class Parent {
	private Long id;
	private String name;
	private Collection<Child> children = new HashSet<Child>();
	/**
	 * @return Returns the children.
	 */
	public Collection<Child> getChildren() {
		return children;
	}
	/**
	 * @param children The children to set.
	 */
	public void setChildren(Collection<Child> children) {
		this.children = children;
	}
	/**
	 * @return Returns the id.
	 */
	public Long getId() {
		return id;
	}
	/**
	 * @param id The id to set.
	 */
	public void setId(Long id) {
		this.id = id;
	}
	/**
	 * @return Returns the name.
	 */
	public String getName() {
		return name;
	}
	/**
	 * @param name The name to set.
	 */
	public void setName(String name) {
		this.name = name;
	}
}
