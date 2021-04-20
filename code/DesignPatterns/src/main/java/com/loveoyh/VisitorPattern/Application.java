package com.loveoyh.VisitorPattern;

import com.loveoyh.IteratorPattern.ArrayList;
import com.loveoyh.IteratorPattern.List;
import com.loveoyh.VisitorPattern.fileobj.ExcelFile;
import com.loveoyh.VisitorPattern.fileobj.PPTFile;
import com.loveoyh.VisitorPattern.fileobj.PdfFile;
import com.loveoyh.VisitorPattern.fileobj.ResourceFile;
import com.loveoyh.VisitorPattern.tool.Compressor;
import com.loveoyh.VisitorPattern.tool.Extractor;

/**
 * 访问者模式
 * @Created by oyh.Jerry to 2021/04/19 16:10
 */
public class Application {
	
	public static void main(String[] args) {
		ResourceFile pdfFile = new PdfFile("test.pfd");
		ResourceFile pptFile = new PPTFile("test.ppt");
		ResourceFile excelFile = new ExcelFile("test.xslx");

		Extractor extractor = new Extractor();
		pdfFile.accept(extractor);
		excelFile.accept(extractor);
		pptFile.accept(extractor);
		
		Compressor compressor = new Compressor();
		pdfFile.accept(compressor);
		excelFile.accept(compressor);
		pptFile.accept(compressor);
	}
	
}
