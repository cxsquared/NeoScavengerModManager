package com.cxsquared.nsmm.tools;

import java.io.IOException;
import java.util.HashMap;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.XmlReader;
import com.badlogic.gdx.utils.XmlReader.Element;

public class DataParser {

	public Element neogameDatabase;
	public HashMap<String, Array<Element>> neogameTableTypes;
	public Array<String> neogameTableTypeNames, listOfConditions, listOfItemProps;
	public HashMap<String, HashMap<String, HashMap<String, String>>> neogameTableData;

	private FileHandle neogameLocation;

	public DataParser() {
		neogameLocation = Gdx.files.internal("neogame.xml");
		parseXML(neogameLocation);
		createTableArray(neogameDatabase);
		parseDatabase(neogameDatabase);
		createTableData(neogameTableTypes);
	}

	public DataParser(String file) {
		neogameLocation = Gdx.files.absolute(file);
		parseXML(neogameLocation);
		createTableArray(neogameDatabase);
		parseDatabase(neogameDatabase);
		createTableData(neogameTableTypes);
	}
	
	private void parseXML(FileHandle file) {
		XmlReader reader = new XmlReader();
		try {
			Element root = reader.parse(file);
			neogameDatabase = root.getChildByName("database");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void createTableArray(Element database) {
		neogameTableTypeNames = new Array<String>(database.getChildCount());
		for (Element table : database.getChildrenByName("table")) {
			if (!neogameTableTypeNames.contains(table.getAttribute("name"), false)) neogameTableTypeNames.add(table.getAttribute("name"));
		}
		neogameTableTypes = new HashMap<String, Array<Element>>(neogameTableTypeNames.size);
		for (int i = 0; i < neogameTableTypeNames.size; i++) {
			neogameTableTypes.put(neogameTableTypeNames.get(i), new Array<Element>());
		}
	}

	private void parseDatabase(Element database) {
		for (Element table : database.getChildrenByName("table")) {
			neogameTableTypes.get(table.getAttribute("name")).add(table);
		}
	}

	private void createTableData(HashMap<String, Array<Element>> tableList) {
		neogameTableData = new HashMap<String, HashMap<String, HashMap<String, String>>>();
		listOfConditions = new Array<String>();
		listOfItemProps = new Array<String>();
		for (String tableName : tableList.keySet()) {
			HashMap<String, HashMap<String, String>> tableTemp = new HashMap<String, HashMap<String, String>>();
			for (int i = 0; i < tableList.get(tableName).size; i++) {
				HashMap<String, String> temp = new HashMap<String, String>();
				String tempName = "";
				for (int j = 0; j < tableList.get(tableName).get(i).getChildCount(); j++) {
					// Setting name
					if (tableList.get(tableName).get(i).getChild(j).getAttribute("name").equals("strName")) {
						tempName = tableList.get(tableName).get(i).getChild(j).getText();
					} else if (tableList.get(tableName).get(i).getChild(j).getAttribute("name").equals("strDesc") && tableName.equals("camptypes")) {
						tempName = tableList.get(tableName).get(i).getChild(j).getText();
					} else if (tableList.get(tableName).get(i).getChild(j).getAttribute("name").equals("strPropertyName")) {
						tempName = tableList.get(tableName).get(i).getChild(j).getText();
					} else if (tableList.get(tableName).get(i).getChild(j).getAttribute("name").equals("strHeadline")) {
						tempName = tableList.get(tableName).get(i).getChild(j).getText().substring(0, 25);
					}
					// Putting column data
					temp.put(tableList.get(tableName).get(i).getChild(j).getAttribute("name"), tableList.get(tableName).get(i).getChild(j).getText());
				}
				// Putting Table with column data
				tableTemp.put(tableList.get(tableName).get(i).getChild(0).getText() + "-" + tempName, temp);
				// Finding conditions
				if (tableList.get(tableName).get(i).getAttribute("name").equals("conditions")) {
					listOfConditions.add(tableList.get(tableName).get(i).getChild(0).getText() + "-" + tableList.get(tableName).get(i).getChild(1).getText());
				}
				if (tableList.get(tableName).get(i).getAttribute("name").equals("itemprops")) {
					listOfItemProps.add(tableList.get(tableName).get(i).getChild(0).getText() + "-" + tableList.get(tableName).get(i).getChild(1).getText());
				}
			}
			neogameTableData.put(tableName, tableTemp);
		}
	}

	public void save() {

	}
}
