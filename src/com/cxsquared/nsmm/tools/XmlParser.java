package com.cxsquared.nsmm.tools;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.prefs.BackingStoreException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.cxsquared.nsmm.DataWindow;
import com.cxsquared.nsmm.ModNode;

public class XmlParser {

	public NodeList neogameDatabase;
	public ModNode modList;
	public ArrayList<String> listOfConditions = new ArrayList<String>();
	public ArrayList<String> listOfItemProps = new ArrayList<String>();

	public XmlParser(URL file) {
		createNeoScavengerData(new File(file.getFile()));
		createConditionsList();
		createItemPropsList();
	}

	public void loadNew(String file) {
		createModNode(new File(file));
	}

	private void createNeoScavengerData(File location) {
		modList = new ModNode("neogame.xml", "");
		parseXml(location);
		parseData("Neo Scavenger");
	}

	private void createModNode(File location) {
		parseXml(location);
		parseData(location.getParentFile().getName());
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

	private void parseData(String modName) {
		ModNode tempNode = new ModNode(modName, "");
		modList.addChild(tempNode);
		createTableNodes(tempNode);
		parseDatabase(tempNode);
	}

	private void createTableNodes(ModNode modNode) {
		for (int i = 0; i < neogameDatabase.getLength(); i++) {
			Node nNode = neogameDatabase.item(i);
			if (nNode.getNodeType() == Node.ELEMENT_NODE) {
				Element eElement = (Element) nNode;
				if (modNode.getChild(eElement.getAttribute("name")) == null) {
					ModNode tableNode = new ModNode(eElement.getAttribute("name"), "");
					modNode.addChild(tableNode);
				}
			}
		}
	}

	private void parseDatabase(ModNode modNode) {
		for (int i = 0; i < neogameDatabase.getLength(); i++) {
			Node nNode = neogameDatabase.item(i);
			if (nNode.getNodeType() == Node.ELEMENT_NODE) {
				Element eElement = (Element) nNode;
				modNode.getChild(eElement.getAttribute("name")).addChild(createChildNode(eElement));
			}
		}
	}

	private ModNode createChildNode(Element eTable) {
		ModNode tableNode = new ModNode("", "");
		String tempName = "";
		String eName = "";
		NodeList nNodeList = eTable.getChildNodes();
		for (int j = 0; j < nNodeList.getLength(); j++) {
			Node nNode = nNodeList.item(j);
			if (nNode.getNodeType() == Node.ELEMENT_NODE) {
				Element eColumn = (Element) nNode;
				// Setting table name
				if (eColumn.getAttribute("name").equals("strName")) {
					tempName = eColumn.getTextContent();
				} else if (eColumn.getAttribute("name").equals("stDesc") && eTable.getAttribute("name").equals("camptypes")) {
					tempName = eColumn.getTextContent();
				} else if (eColumn.getAttribute("name").equals("strPropertyName")) {
					tempName = eColumn.getTextContent();
				} else if (eColumn.getAttribute("name").equals("strHeadline")) {
					tempName = eColumn.getTextContent().substring(0, 25);
				}
				if (eColumn.getAttribute("name").equals("id") || eColumn.getAttribute("name").equals("nID")) {
					eName = eColumn.getTextContent();
				}
				// Putting column data
				ModNode columnNode = new ModNode(eColumn.getAttribute("name"), eColumn.getTextContent());
				tableNode.addChild(columnNode);
			}
		}
		tableNode.setName(eName + "-" + tempName);
		return tableNode;
	}

	private void createConditionsList() {
		for (ModNode condition : modList.getChild("Neo Scavenger").getChild("conditions").getChildren()) {
			listOfConditions.add(condition.getChild("id").getData() + "-" + condition.getChild("strName").getData());
		}
	}

	private void createItemPropsList() {
		for (ModNode itemProp : modList.getChild("Neo Scavenger").getChild("itemprops").getChildren()) {
			listOfItemProps.add(itemProp.getChild("nID").getData() + "-" + itemProp.getChild("strPropertyName").getData());
		}
	}
}
