package com.loveoyh.VisitorPattern.tool;

import com.loveoyh.VisitorPattern.fileobj.ExcelFile;
import com.loveoyh.VisitorPattern.fileobj.PPTFile;
import com.loveoyh.VisitorPattern.fileobj.PdfFile;
import com.loveoyh.VisitorPattern.visitor.Visitor;

/**
 * 解压器
 * @Created by oyh.Jerry to 2021/04/19 15:45
 */
public class Extractor implements Visitor {
	
	@Override
	public void visit(PdfFile pdfFile) {
		System.out.println("=====> pdf file extractor.");
	}
	
	@Override
	public void visit(PPTFile pptFile) {
		System.out.println("=====> ppt file extractor.");
	}
	
	@Override
	public void visit(ExcelFile excelFile) {
		System.out.println("=====> excel file extractor.");
	}
	
}
