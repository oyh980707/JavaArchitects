package com.loveoyh.VisitorPattern.tool;

import com.loveoyh.VisitorPattern.fileobj.ExcelFile;
import com.loveoyh.VisitorPattern.fileobj.PPTFile;
import com.loveoyh.VisitorPattern.fileobj.PdfFile;
import com.loveoyh.VisitorPattern.visitor.Visitor;

/**
 * 压缩工具
 * @Created by oyh.Jerry to 2021/04/19 16:08
 */
public class Compressor implements Visitor {
	
	@Override
	public void visit(PdfFile pdfFile) {
		System.out.println("----> pdf file compress.");
	}
	
	@Override
	public void visit(PPTFile pptFile) {
		System.out.println("----> ppt file compress.");
	}
	
	@Override
	public void visit(ExcelFile excelFile) {
		System.out.println("----> excel file compress.");
	}
	
}
