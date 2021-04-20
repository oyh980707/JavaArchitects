package com.loveoyh.VisitorPattern.visitor;

import com.loveoyh.VisitorPattern.fileobj.ExcelFile;
import com.loveoyh.VisitorPattern.fileobj.PPTFile;
import com.loveoyh.VisitorPattern.fileobj.PdfFile;

/**
 * @Created by oyh.Jerry to 2021/04/19 15:43
 */
public interface Visitor {
	
	void visit(PdfFile pdfFile);
	
	void visit(PPTFile pptFile);
	
	void visit(ExcelFile excelFile);
	
}
