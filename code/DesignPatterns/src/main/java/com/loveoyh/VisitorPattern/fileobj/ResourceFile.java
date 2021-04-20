package com.loveoyh.VisitorPattern.fileobj;

import com.loveoyh.VisitorPattern.visitor.Visitor;

/**
 * 资源抽象
 * @Created by oyh.Jerry to 2021/04/19 15:46
 */
public abstract class ResourceFile {
	protected String filePath;
	
	public ResourceFile(String filePath) {
		this.filePath = filePath;
	}
	
	abstract public void accept(Visitor vistor);
	
}
