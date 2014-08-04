package com.cxsquared.nsmm.tools;

import java.io.File;

import javax.swing.filechooser.FileFilter;

public class XmlFilter extends FileFilter {

	private String fileFormat = "XML";
	private char dotIndex = '.';

	@Override
	public boolean accept(File arg0) {
		if (arg0.isDirectory()) {
			return true;
		}
		if (extension(arg0).equalsIgnoreCase(fileFormat)) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public String getDescription() {
		return "XML";
	}

	public String extension(File f) {
		String fileName = f.getName();
		int indexFile = fileName.lastIndexOf(dotIndex);
		if (indexFile > 0 && indexFile < fileName.length() - 1) {
			return fileName.substring(indexFile + 1);
		} else {
			return "";
		}
	}

}
