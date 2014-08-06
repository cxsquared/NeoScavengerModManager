package com.cxsquared.nsmm;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

public class ModNode {
	private String name;
	private final Map<String, ModNode> CHILDREN;
	private String data;
	private ModNode parent = null;

	public ModNode(String name, String data) {
		this.name = name;
		this.CHILDREN = new LinkedHashMap<String, ModNode>();
		this.data = data;
	}

	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}

	public void addChild(ModNode child) {
		CHILDREN.put(child.getName(), child);
		child.setParent(this);
	}

	public void removeChild(String name) {
		CHILDREN.get(name).removeParent();
		CHILDREN.remove(name);
	}

	public ModNode getChild(String name) {
		return CHILDREN.get(name);
	}

	public Set<ModNode> getChildren() {
		return Collections.unmodifiableSet(new LinkedHashSet<ModNode>(CHILDREN.values()));
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}
	
	public void setParent(ModNode parent){
		this.parent = parent;
	}
	
	public void removeParent(){
		this.parent = null;
	}
	
	public ModNode getParent(){
		return parent;
	}

}
