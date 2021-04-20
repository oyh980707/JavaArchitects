package com.loveoyh.VisitorPattern.fileobj;

import com.loveoyh.VisitorPattern.visitor.Visitor;

/**
 * @Created by oyh.Jerry to 2021/04/19 15:49
 */
public class ExcelFile extends ResourceFile {
	
	public ExcelFile(String filePath) {
		super(filePath);
	}
	
	@Override
	public void accept(Visitor vistor) {
		vistor.visit(this);
	}
	
}
