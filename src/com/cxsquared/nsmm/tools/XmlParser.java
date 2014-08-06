package com.cxsquared.nsmm.tools;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.prefs.BackingStoreException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

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
	
	public XmlParser(String file){
		this(new File(file));
	}
	
	public XmlParser(File file){
		createNeoScavengerData(file);
		createConditionsList();
		createItemPropsList();
	}

	public XmlParser(URL file) {
		this(file.getFile());
	}

	public void loadNew(String file) {
		createModNode(new File(file));
	}

	private void createNeoScavengerData(File location) {
		modList = new ModNode("neogame.xml", "");
		parseXml(location);
		parseData("Neo Scavenger", location);
	}

	private void createModNode(File location) {
		parseXml(location);
		parseData(location.getParentFile().getName(), location);
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

	private void parseData(String modName, File location) {
		ModNode tempNode = new ModNode(modName, location.getPath());
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

	public void exportXML(ModNode mod, String file) {
		try {
			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

			Document doc = docBuilder.newDocument();
			Element rootElement = doc.createElement("pma_xml_export");
			rootElement.setAttribute("version", "1.0");
			doc.appendChild(rootElement);

			Element eDatabase = doc.createElement("database");
			eDatabase.setAttribute("name", "neogame");
			rootElement.appendChild(eDatabase);

			for (ModNode tables : mod.getChildren()) {
				for (ModNode table : tables.getChildren()) {
					Element eTable = doc.createElement("table");
					eTable.setAttribute("name", tables.getName());
					for (ModNode column : table.getChildren()) {
						Element eColumn = doc.createElement("column");
						eColumn.setAttribute("name", column.getName());
						eColumn.setTextContent(column.getData());
						eTable.appendChild(eColumn);
					}
					eDatabase.appendChild(eTable);
				}
			}

			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
			DOMSource source = new DOMSource(doc);
			File fFile = new File(file);
			String newLocation = fFile.getParent() + "\\neogame_nsmm.xml";
			StreamResult result = new StreamResult(new File(newLocation));
			
			transformer.transform(source, result);

		} catch (ParserConfigurationException pce) {
			pce.printStackTrace();
		} catch (TransformerException tfe) {
			tfe.printStackTrace();
		}
	}
}
