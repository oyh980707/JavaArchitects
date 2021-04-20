package com.loveoyh.VisitorPattern.fileobj;

import com.loveoyh.VisitorPattern.visitor.Visitor;

/**
 * @Created by oyh.Jerry to 2021/04/19 15:55
 */
public class PPTFile extends ResourceFile {
	
	public PPTFile(String filePath) {
		super(filePath);
	}
	
	@Override
	public void accept(Visitor vistor) {
		vistor.visit(this);
	}
	
}
