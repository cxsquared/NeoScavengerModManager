package com.cxsquared.nsmm.tools;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.prefs.BackingStoreException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.cxsquared.nsmm.DataWindow;

public class XmlParser {

	public NodeList neogameDatabase;
	public HashMap<String, ArrayList<Element>> neogameTableTypes;
	public ArrayList<String> neogameTableTypeNames, listOfConditions, listOfItemProps;
	public HashMap<String, HashMap<String, HashMap<String, String>>> neogameTableData;

	private File neogameLocation;
	private boolean getList = true;

	public XmlParser(URL file) {
		getList = true;
		neogameLocation = new File(file.getFile());
		parseXml(neogameLocation);
		createTableArray(neogameDatabase);
		parseDatabase(neogameDatabase);
		createTableData(neogameTableTypes);
	}

	public XmlParser(String file) {
		getList = false;
		neogameLocation = new File(file);
		parseXml(neogameLocation);
		createTableArray(neogameDatabase);
		parseDatabase(neogameDatabase);
		createTableData(neogameTableTypes);
	}

	public void loadNew(String file) {
		getList = false;
		neogameLocation = new File(file);
		parseXml(neogameLocation);
		createTableArray(neogameDatabase);
		parseDatabase(neogameDatabase);
		createTableData(neogameTableTypes);
	}

	private void parseXml(File fXmlFile) {
		try {
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder;
			dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(fXmlFile);

			doc.getDocumentElement().normalize();

			neogameDatabase = doc.getElementsByTagName("table");
		} catch (Exception e) {
			e.printStackTrace();
			try {
				DataWindow.prefs.flush();
			} catch (BackingStoreException e1) {
				e1.printStackTrace();
			}
		}
	}

	private void createTableArray(NodeList database) {
		neogameTableTypeNames = new ArrayList<String>();
		for (int temp = 0; temp < database.getLength(); temp++) {
			Node nNode = database.item(temp);
			if (nNode.getNodeType() == Node.ELEMENT_NODE) {
				Element eElement = (Element) nNode;
				if (!neogameTableTypeNames.contains(eElement.getAttribute("name"))) neogameTableTypeNames.add(eElement.getAttribute("name"));
			}
		}
		neogameTableTypes = new HashMap<String, ArrayList<Element>>();
		for (int i = 0; i < neogameTableTypeNames.size(); i++) {
			neogameTableTypes.put(neogameTableTypeNames.get(i), new ArrayList<Element>());
		}
	}

	private void parseDatabase(NodeList database) {
		for (int temp = 0; temp < database.getLength(); temp++) {
			Node nNode = database.item(temp);
			if (nNode.getNodeType() == Node.ELEMENT_NODE) {
				Element eElement = (Element) nNode;
				neogameTableTypes.get(eElement.getAttribute("name")).add(eElement);
			}
		}
	}

	private void createTableData(HashMap<String, ArrayList<Element>> tableList) {
		neogameTableData = new HashMap<String, HashMap<String, HashMap<String, String>>>();
		if (getList) {
			System.out.println("Making new conditison list");
			listOfConditions = new ArrayList<String>();
			listOfItemProps = new ArrayList<String>();
		}
		for (String tableName : tableList.keySet()) {
			HashMap<String, HashMap<String, String>> tableTemp = new HashMap<String, HashMap<String, String>>();
			for (int i = 0; i < tableList.get(tableName).size(); i++) {
				HashMap<String, String> temp = new HashMap<String, String>();
				String tempName = "";
				String eName = "";
				NodeList nNodeList = tableList.get(tableName).get(i).getChildNodes();
				for (int j = 0; j < nNodeList.getLength(); j++) {
					Node nNode = nNodeList.item(j);
					if (nNode.getNodeType() == Node.ELEMENT_NODE) {
						Element eElement = (Element) nNode;
						// Setting table name
						if (eElement.getAttribute("name").equals("strName")) {
							tempName = eElement.getTextContent();
						} else if (eElement.getAttribute("name").equals("stDesc") && tableName.equals("camptypes")) {
							tempName = eElement.getTextContent();
						} else if (eElement.getAttribute("name").equals("strPropertyName")) {
							tempName = eElement.getTextContent();
						} else if (eElement.getAttribute("name").equals("strHeadline")) {
							tempName = eElement.getTextContent().substring(0, 25);
						}
						if (eElement.getAttribute("name").equals("id") || eElement.getAttribute("name").equals("nID")) {
							eName = eElement.getTextContent();
						}
						// Putting column data
						temp.put(eElement.getAttribute("name"), eElement.getTextContent());
					}
				}
				// Putting Table with column data
				tableTemp.put(eName + "-" + tempName, temp);
				if (getList) {
					if (tableList.get(tableName).get(i).getAttribute("name").equals("conditions")) {
						listOfConditions.add(eName + "-" + tempName);
					} else if (tableList.get(tableName).get(i).getAttribute("name").equals("itemprops")) {
						listOfItemProps.add(eName + "-" + tempName);
					}
				}

			}
			neogameTableData.put(tableName, tableTemp);
		}
	}

	public void save(String fileName) {
		try {
			FileOutputStream fos = new FileOutputStream(fileName + ".ser");
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(neogameTableData);
			oos.close();
			fos.close();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}

	@SuppressWarnings("unchecked")
	public void load(String fileName) {
		try {
			FileInputStream fis = new FileInputStream(fileName + ".ser");
			ObjectInputStream ois = new ObjectInputStream(fis);
			if (ois.readObject() instanceof HashMap) {
				if (neogameTableData != null) {
					neogameTableData = (HashMap<String, HashMap<String, HashMap<String, String>>>) ois.readObject();
				} else {
					System.out.println("Can only open files saved with NSMM");
				}
				ois.close();
				fis.close();
			}
		} catch (IOException ioe) {
			ioe.printStackTrace();
			return;
		} catch (ClassNotFoundException c) {
			System.out.println("Class not found");
			c.printStackTrace();
			return;
		}
	}
}
