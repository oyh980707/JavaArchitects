package com.loveoyh.VisitorPattern.fileobj;

import com.loveoyh.VisitorPattern.visitor.Visitor;

/**
 * pdf文件封装类
 * @Created by oyh.Jerry to 2021/04/19 15:45
 */
public class PdfFile extends ResourceFile {
	
	public PdfFile(String filePath) {
		super(filePath);
	}
	
	@Override
	public void accept(Visitor vistor) {
		vistor.visit(this);
	}
	
}
