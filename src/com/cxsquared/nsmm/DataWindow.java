package com.cxsquared.nsmm;

import java.awt.EventQueue;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import javax.swing.AbstractAction;
import javax.swing.DefaultListModel;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextPane;
import javax.swing.JTree;
import javax.swing.border.EmptyBorder;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeSelectionModel;

import com.cxsquared.nsmm.tools.XmlFilter;
import com.cxsquared.nsmm.tools.XmlParser;

public class DataWindow extends JFrame implements TreeSelectionListener {
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTree tree;
	private JScrollPane scrollPane;
	private XmlParser xmlParser;
	private DefaultMutableTreeNode prevNode, top;
	DefaultListModel<String> conditionsList, itemPropsList;

	private final String NAME = "Neo Scavenger Mod Manager";
	private final String VERSION = "0.1.2";
	private JMenuBar menuBar;
	private JMenu mnFile;
	private JMenuItem mntmLoadNeogameFile, mntmRefreshNeogame, mntmSaveNeogame;
	private GridBagLayout gbl_contentPane;

	private List<JLabel> listOfLabels = new ArrayList<JLabel>();
	private List<JComponent> listOfTextFields = new ArrayList<JComponent>();

	private JTabbedPane tabbedPane;

	public static Preferences prefs;

	/**
	 * Create the frame.
	 */
	public DataWindow() {
		if (prefs.get("fileLocation", "").equals("")) {
			xmlParser = new XmlParser(getClass().getResource("neogame.xml"));
		} else {
			xmlParser = new XmlParser(getClass().getResource("neogame.xml"));
			xmlParser.loadNew(prefs.get("fileLocation", ""));
		}

		getConditions();

		this.setTitle(NAME + " " + VERSION);
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.setLocationRelativeTo(null);

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 289, 393);

		menuBar = new JMenuBar();
		setJMenuBar(menuBar);

		mnFile = new JMenu("File");
		menuBar.add(mnFile);

		mntmLoadNeogameFile = new JMenuItem(new AbstractAction("Load neogame file") {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				loadXml();
				getConditions();
				resetTree();
			}
		});
		mntmLoadNeogameFile.setText("Load neogame.xml");
		mnFile.add(mntmLoadNeogameFile);

		mntmRefreshNeogame = new JMenuItem(new AbstractAction("Refresh Data") {
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (prefs.get("fileLocation", "").equals("")) {
					xmlParser = new XmlParser(getClass().getResource("neogame.xml"));
				} else {
					xmlParser = new XmlParser(getClass().getResource("neogame.xml"));
					xmlParser.loadNew(prefs.get("fileLocaiton", ""));
				}
				resetTree();
			}

		});
		mnFile.add(mntmRefreshNeogame);

		mntmSaveNeogame = new JMenuItem(new AbstractAction("Save neogame.xml") {
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent arg0) {

			}

		});
		mnFile.add(mntmSaveNeogame);

		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		gbl_contentPane = new GridBagLayout();
		gbl_contentPane.columnWidths = new int[] { 0, 0, 0 };
		gbl_contentPane.rowHeights = new int[] { 0, 0 };
		gbl_contentPane.columnWeights = new double[] { 1.0, 1.0, Double.MIN_VALUE };
		gbl_contentPane.rowWeights = new double[] { 1.0, Double.MIN_VALUE };
		contentPane.setLayout(gbl_contentPane);

		top = new DefaultMutableTreeNode("neogame.xml");
		createNodes(top);

		JSplitPane splitPane = new JSplitPane();
		GridBagConstraints gbc_splitPane = new GridBagConstraints();
		gbc_splitPane.gridwidth = 2;
		gbc_splitPane.fill = GridBagConstraints.BOTH;
		gbc_splitPane.gridx = 0;
		gbc_splitPane.gridy = 0;
		contentPane.add(splitPane, gbc_splitPane);

		scrollPane = new JScrollPane();
		splitPane.setLeftComponent(scrollPane);
		tree = new JTree(top);
		tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		tree.addTreeSelectionListener(this);
		scrollPane.setViewportView(tree);

		tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		splitPane.setRightComponent(tabbedPane);

		pack();
	}

	private void loadXml() {
		JFileChooser chooser = new JFileChooser();
		chooser.setFileFilter(new XmlFilter());
		int choice = chooser.showOpenDialog(null);

		if (choice != JFileChooser.APPROVE_OPTION) return;

		File chosenFile = chooser.getSelectedFile();

		xmlParser.loadNew(chosenFile.toString());

		prefs.put("fileLocation", chosenFile.toString());
		try {
			prefs.flush();
		} catch (BackingStoreException e) {
			e.printStackTrace();
		}
	}

	private void getConditions() {
		conditionsList = new DefaultListModel<String>();
		for (int i = 0; i < xmlParser.listOfConditions.size(); i++) {
			conditionsList.addElement(xmlParser.listOfConditions.get(i));
		}
		itemPropsList = new DefaultListModel<String>();
		for (int i = 0; i < xmlParser.listOfItemProps.size(); i++) {
			itemPropsList.addElement(xmlParser.listOfItemProps.get(i));
		}
	}

	private void createNodes(DefaultMutableTreeNode top) {
		top.removeAllChildren();
		DefaultMutableTreeNode category = null;
		DefaultMutableTreeNode table = null;

		for (String name : xmlParser.neogameTableTypeNames) {
			category = new DefaultMutableTreeNode(name);
			top.add(category);
			List<String> temp = new ArrayList<String>(xmlParser.neogameTableData.get(name).keySet());
			temp.sort(new Comparator<String>() {
				@Override
				public int compare(String arg0, String arg1) {
					return Integer.compare(Integer.parseInt(arg0.split("-")[0]), Integer.parseInt(arg1.split("-")[0]));
				}
			});
			for (String tableName : temp) {
				table = new DefaultMutableTreeNode(tableName);
				category.add(table);
			}
		}
	}

	private void resetTree() {
		scrollPane.remove(tree);

		top = new DefaultMutableTreeNode("neogame.xml");
		createNodes(top);

		tree = new JTree(top);
		tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		tree.addTreeSelectionListener(this);
		scrollPane.setViewportView(tree);
	}

	@Override
	public void valueChanged(TreeSelectionEvent arg0) {
		DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();

		if (node == null) return;
		if (node.isLeaf()) {
			if (prevNode != null) {
			}

			tabbedPane.removeAll();
			listOfLabels.clear();
			listOfTextFields.clear();

			GridBagLayout gbl_temp = new GridBagLayout();
			gbl_temp.columnWeights = new double[] { 0, 1.0, Double.MIN_VALUE };
			gbl_temp.rowWeights = new double[] { 1.0, Double.MIN_VALUE };

			GridBagConstraints textFieldConstraints = new GridBagConstraints();
			GridBagConstraints labelConstraints = new GridBagConstraints();

			textFieldConstraints.anchor = GridBagConstraints.FIRST_LINE_START;
			textFieldConstraints.fill = GridBagConstraints.HORIZONTAL;
			textFieldConstraints.insets = new Insets(5, 0, 0, 0);
			textFieldConstraints.gridx = 1;
			textFieldConstraints.gridy = 0;
			textFieldConstraints.weightx = 1.0;
			textFieldConstraints.weightx = 1.0;
			textFieldConstraints.gridwidth = 4;

			labelConstraints.insets = new Insets(5, 0, 0, 10);
			labelConstraints.anchor = GridBagConstraints.FIRST_LINE_START;
			labelConstraints.gridx = 0;
			labelConstraints.gridy = 0;
			labelConstraints.weightx = 0.0;

			for (String name : xmlParser.neogameTableData.get(node.getParent().toString()).get(node.toString()).keySet()) {
				listOfLabels.add(new JLabel(name));
				if (name.contains("Conditions")) {
					listOfTextFields.add(createConditionsList(node, name));
				} else if (name.contains("Properties")) {
					listOfTextFields.add(createItemPropsList(node, name));
				} else {
					JTextPane temp = new JTextPane();
					temp.setText(xmlParser.neogameTableData.get(node.getParent().toString()).get(node.toString()).get(name));
					temp.setEditable(true);
					listOfTextFields.add(temp);
				}
			}

			for (int i = 0; i < listOfLabels.size(); i++) {
				if (listOfTextFields.get(i).getClass().equals(JSplitPane.class)) {
					textFieldConstraints.fill = GridBagConstraints.BOTH;
				} else {
					textFieldConstraints.fill = GridBagConstraints.NONE;
				}

				JPanel tempPanel = new JPanel(gbl_temp);

				tempPanel.add(listOfLabels.get(i), labelConstraints);
				tempPanel.add(listOfTextFields.get(i), textFieldConstraints);

				tabbedPane.addTab(listOfLabels.get(i).getText(), tempPanel);
			}
			tabbedPane.updateUI();
			prevNode = node;
		}
	}

	private JComponent createItemPropsList(DefaultMutableTreeNode node, String name) {
		String[] tempString = { "" };
		if (xmlParser.neogameTableData.get(node.getParent().toString()).get(node.toString()).get(name) != null) tempString = xmlParser.neogameTableData.get(node.getParent().toString()).get(node.toString()).get(name).split(",");
		JSplitPane tempPane = new JSplitPane();
		DefaultListModel<String> tempList = new DefaultListModel<String>();
		for (int i = 0; i < tempString.length; i++) {
			if (!tempString[i].equals("")) {
				if (tempString[i].contains(":")) {
					tempList.addElement(tempString[i] + " (" + xmlParser.listOfItemProps.get(Integer.parseInt(tempString[i].split(":")[1]) - 1).split("-")[1] + ")");
				} else {
					tempList.addElement(tempString[i] + " (" + xmlParser.listOfItemProps.get(Integer.parseInt(tempString[i]) - 1).split("-")[1] + ")");
				}
			}
		}
		tempPane.setLeftComponent(new JScrollPane(new JList<String>(tempList)));
		tempPane.setRightComponent(new JScrollPane(new JList<String>(itemPropsList)));
		return tempPane;
	}

	private JComponent createConditionsList(DefaultMutableTreeNode node, String name) {
		String[] tempString = { "" };
		if (xmlParser.neogameTableData.get(node.getParent().toString()).get(node.toString()).get(name) != null) {
			tempString = xmlParser.neogameTableData.get(node.getParent().toString()).get(node.toString()).get(name).split(",");
		}
		JSplitPane tempPane = new JSplitPane();
		JTextPane tempText = new JTextPane();
		DefaultListModel<String> tempList = new DefaultListModel<String>();
		int numberOfText = 0;
		for (int i = 0; i < tempString.length; i++) {
			if (tempString[i].length() > 0) {
				if (node.getParent().toString().equals("battlemoves")) {
					if (name.contains("PreConditions")) {
						if (tempString[i].startsWith("-")) {
							tempList.addElement(tempString[i] + " (" + xmlParser.listOfConditions.get(Integer.parseInt(tempString[i].substring(1)) - 1).split("-")[1] + ")");
						} else {
							tempList.addElement(tempString[i] + " (" + xmlParser.listOfConditions.get(Integer.parseInt(tempString[i]) - 1).split("-")[1] + ")");
						}
					} else {
						if (i < 3) {
							if (tempString[i].contains("-")) {
								tempText.setText(xmlParser.neogameTableData.get(node.getParent().toString()).get(node.toString()).get(name).split("]")[numberOfText] + "]" + " [-"
										+ xmlParser.listOfConditions.get(Integer.parseInt(tempString[0].substring(2)) - 1) + "," + tempString[1] + "," + tempString[2]);
							} else {
								tempText.setText(xmlParser.neogameTableData.get(node.getParent().toString()).get(node.toString()).get(name).split("]")[numberOfText] + "]" + " ["
										+ xmlParser.listOfConditions.get(Integer.parseInt(tempString[0].substring(1)) - 1) + "," + tempString[1] + "," + tempString[2]);
							}
						} else {
							if (tempString[i].contains("-")) {
								tempText.setText(tempText.getText() + "\n" + xmlParser.neogameTableData.get(node.getParent().toString()).get(node.toString()).get(name).split("]")[numberOfText].substring(1) + "]" + " [-"
										+ xmlParser.listOfConditions.get(Integer.parseInt(tempString[i].substring(2)) - 1) + "," + tempString[i + 1] + "," + tempString[i + 2]);
							} else {
								tempText.setText(tempText.getText() + "\n" + xmlParser.neogameTableData.get(node.getParent().toString()).get(node.toString()).get(name).split("]")[numberOfText].substring(1) + "]" + " ["
										+ xmlParser.listOfConditions.get(Integer.parseInt(tempString[i].substring(1)) - 1) + "," + tempString[i + 1] + "," + tempString[i + 2]);
							}
						}
						numberOfText++;
						i += 2;
					}
				} else if (node.getParent().toString().equals("creatures")) {
					tempList.addElement(tempString[i] + " (" + xmlParser.listOfConditions.get(Integer.parseInt(tempString[i].split("=")[0]) - 1).split("-")[1] + ")");
				} else if (node.getParent().toString().equals("itemtypes")) {
					if (tempString[i].contains("-")) {
						tempList.addElement(tempString[i] + " (-" + xmlParser.listOfConditions.get(Integer.parseInt(tempString[i].split("=")[1].substring(1)) - 1).split("-")[1] + ")");
					} else {
						tempList.addElement(tempString[i] + " (" + xmlParser.listOfConditions.get(Integer.parseInt(tempString[i].split("=")[1]) - 1).split("-")[1] + ")");
					}
				} else {
					if (tempString[i].startsWith("-")) {
						if (tempString[i].contains("x")) {
							tempList.addElement(tempString[i] + " (" + xmlParser.listOfConditions.get(Integer.parseInt(tempString[i].split("x")[0].substring(1)) - 1).split("-")[1] + ")");
						} else {
							tempList.addElement(tempString[i] + " (" + xmlParser.listOfConditions.get(Integer.parseInt(tempString[i].substring(1)) - 1).split("-")[1] + ")");
						}
					} else {
						if (tempString[i].contains("x")) {
							tempList.addElement(tempString[i] + " (" + xmlParser.listOfConditions.get(Integer.parseInt(tempString[i].split("x")[0]) - 1).split("-")[1] + ")");
						} else {
							if (tempString[i].equals("0")) {
								tempList.addElement(tempString[i] + " (" + xmlParser.listOfConditions.get(Integer.parseInt(tempString[i])).split("-")[1] + ")");
							} else {
								tempList.addElement(tempString[i] + " (" + xmlParser.listOfConditions.get(Integer.parseInt(tempString[i]) - 1).split("-")[1] + ")");
							}
						}
					}
				}
			} else {
				tempList.addElement(tempString[i]);
			}
		}
		if (node.getParent().toString().equals(("battlemoves"))) {
			if (name.contains("PreConditions")) {
				tempPane.setLeftComponent(new JList<String>(tempList));
			} else {
				tempPane.setLeftComponent(tempText);
			}
		} else {
			tempPane.setLeftComponent(new JList<String>(tempList));
		}
		tempPane.setRightComponent(new JScrollPane(new JList<String>(conditionsList)));
		return tempPane;
	}

	public static void main(String[] args) {
		prefs = Preferences.userNodeForPackage(com.cxsquared.nsmm.DataWindow.class);
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					DataWindow frame = new DataWindow();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
}
