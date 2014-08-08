package com.cxsquared.nsmm;

import java.awt.EventQueue;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.DefaultListModel;
import javax.swing.DropMode;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextPane;
import javax.swing.JTree;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;
import javax.swing.border.EmptyBorder;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeSelectionModel;

import com.cxsquared.nsmm.tools.EditListAction;
import com.cxsquared.nsmm.tools.ListAction;
import com.cxsquared.nsmm.tools.ListTransferHandler;
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
	public static final String NSLOACTION = "nsLocation";

	private final String NAME = "Neo Scavenger Mod Manager";
	private final String VERSION = "0.1.3";
	private JMenuBar menuBar;
	private JMenu mnFile;
	private JMenuItem mntmAddNeogameFile, mntmRefreshNeogame, mntmExportNeogame, mntmSetLocationNeogame;
	private GridBagLayout gbl_contentPane;

	private HashMap<JLabel, JComponent> listOfColumns = new HashMap<JLabel, JComponent>();

	private JTabbedPane tabbedPane;

	public static Preferences prefs;

	/**
	 * Create the frame.
	 */
	public DataWindow() {
		if (prefs.get(NSLOACTION, "").equals("")) {
			setNeoScavengerFolder();
		}
		getMods();
		getConditions();
		createFrame();
		createMenu();

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

	private void getMods() {
		if (!prefs.get(NSLOACTION, "").equals("")) {
			File neoScavengerLocation = new File(prefs.get(NSLOACTION, "") + ".");
			List<File> folderList = new ArrayList<File>();
			File[] children = neoScavengerLocation.listFiles();
			if (children != null) {
				for (File child : children) {
					if (child.getName().equals("neogame.xml")) {
						xmlParser = new XmlParser(child);
					}
					if (child.isDirectory()) {
						folderList.add(child);
					}
				}
			}
			for (File file : folderList) {
				for (int i = 0; i < file.listFiles().length; i++) {
					if (file.listFiles()[i].toString().contains("neogame.xml")) {
						xmlParser.loadNew(file.listFiles()[i].toString());

					}
				}
			}
		} else {
			xmlParser = new XmlParser(getClass().getResource("neogame.xml"));
		}
	}

	private void createFrame() {
		this.setTitle(NAME + " " + VERSION);
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.setLocationRelativeTo(null);
		this.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				// xmlParser.save("neogame");
			}
		});

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 800, 600);
	}

	private void createMenu() {
		menuBar = new JMenuBar();
		setJMenuBar(menuBar);

		mnFile = new JMenu("File");
		menuBar.add(mnFile);

		mntmAddNeogameFile = new JMenuItem(new AbstractAction("Add new Mod") {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				loadXml();
			}
		});
		mnFile.add(mntmAddNeogameFile);

		mntmRefreshNeogame = new JMenuItem(new AbstractAction("Refresh Data") {
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent arg0) {
				refreshData();
			}

		});
		mnFile.add(mntmRefreshNeogame);

		mntmSetLocationNeogame = new JMenuItem(new AbstractAction("Set NeoScavenger Folder") {
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent arg0) {
				setNeoScavengerFolder();
				refreshData();
			}
		});
		mnFile.add(mntmSetLocationNeogame);

		mntmExportNeogame = new JMenuItem(new AbstractAction("Export neogame.xml") {
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent arg0) {
				DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
				if (node.isLeaf()) {
					ModNode mod = xmlParser.modList.getChild(node.getParent().getParent().toString());
					xmlParser.exportXML(mod, mod.getData());
					infoBox("Xml export to mod location as neogame_nsmm.xml.\nRename the neogame_nsmm.xml to neogame.xml and replace the old mod neogame.xml to activate mod", "Xml Export Successful!");
				} else {
					infoBox("Xml export failed. Select a node inside the mod you want to export\nand try again.", "Xml Export Failed.");
				}
			}
		});
		mnFile.add(mntmExportNeogame);
	}

	private void setNeoScavengerFolder() {
		JFileChooser chooser = new JFileChooser();
		chooser.setDialogTitle("Select Neo Scavenger Game Folder");
		chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		chooser.setAcceptAllFileFilterUsed(false);

		File chosenFile;
		if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
			chosenFile = chooser.getSelectedFile();

			prefs.put(NSLOACTION, chosenFile.toString());
			try {
				prefs.flush();
			} catch (BackingStoreException e) {
				e.printStackTrace();
			}
		} else {
			System.out.println("No Selection");
		}
	}

	private void loadXml() {
		JFileChooser chooser = new JFileChooser();
		chooser.setDialogTitle("Select neogame.xml to load");
		chooser.setFileFilter(new XmlFilter());
		int choice = chooser.showOpenDialog(null);

		if (choice != JFileChooser.APPROVE_OPTION) return;

		File chosenFile = chooser.getSelectedFile();

		xmlParser.loadNew(chosenFile.toString());

		resetTree();
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
		DefaultMutableTreeNode mod = null;
		DefaultMutableTreeNode category = null;
		DefaultMutableTreeNode table = null;

		for (ModNode modNode : xmlParser.modList.getChildren()) {
			mod = new DefaultMutableTreeNode(modNode.getName());
			top.add(mod);
			for (ModNode tableCategory : modNode.getChildren()) {
				category = new DefaultMutableTreeNode(tableCategory.getName());
				mod.add(category);
				for (ModNode tableNode : tableCategory.getChildren()) {
					table = new DefaultMutableTreeNode(tableNode.getName());
					category.add(table);
				}
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
				detectChange();
			}

			tabbedPane.removeAll();
			listOfColumns.clear();

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

			for (ModNode columnNode : xmlParser.modList.getChild(node.getParent().getParent().toString()).getChild(node.getParent().toString()).getChild(node.toString()).getChildren()) {
				if (columnNode.getName().contains("Conditions")) {
					listOfColumns.put(new JLabel(columnNode.getName()), createConditionsList(columnNode));
				} else if (columnNode.getName().contains("Properties")) {
					listOfColumns.put(new JLabel(columnNode.getName()), createItemPropsList(columnNode));
				} else {
					JTextPane columnData = new JTextPane();
					columnData.setText(columnNode.getData());
					columnData.setEditable(true);
					listOfColumns.put(new JLabel(columnNode.getName()), columnData);
				}
			}

			for (JLabel label : listOfColumns.keySet()) {
				if (listOfColumns.get(label).getClass().equals(JSplitPane.class)) {
					textFieldConstraints.fill = GridBagConstraints.BOTH;
				} else {
					textFieldConstraints.fill = GridBagConstraints.NONE;
				}

				JPanel tempPanel = new JPanel(gbl_temp);

				tempPanel.add(label, labelConstraints);
				tempPanel.add(listOfColumns.get(label), textFieldConstraints);

				tabbedPane.addTab(label.getText(), tempPanel);
			}
			tabbedPane.updateUI();
			prevNode = node;
		}
	}

	private JComponent createItemPropsList(ModNode column) {
		String[] itemStrings = {};
		if (column.getData() != null) itemStrings = column.getData().split(",");
		JSplitPane columnData = new JSplitPane();
		DefaultListModel<String> itemList = new DefaultListModel<String>();
		for (int i = 0; i < itemStrings.length; i++) {
			if (!itemStrings[i].equals("")) {
				if (itemStrings[i].contains(":")) {
					itemList.addElement(itemStrings[i] + " (" + xmlParser.listOfItemProps.get(Integer.parseInt(itemStrings[i].split(":")[1]) - 1).split("-")[1] + ")");
				} else {
					itemList.addElement(itemStrings[i] + " (" + xmlParser.listOfItemProps.get(Integer.parseInt(itemStrings[i]) - 1).split("-")[1] + ")");
				}
			}
		}
		columnData.setLeftComponent(new JScrollPane(new JList<String>(itemList)));
		columnData.setRightComponent(new JScrollPane(new JList<String>(itemPropsList)));
		return columnData;
	}

	private JComponent createConditionsList(ModNode column) {
		String[] conditionStrings = {};
		if (column.getData() != null) conditionStrings = column.getData().split(",");
		JSplitPane tempPane = new JSplitPane();
		DefaultListModel<String> tempList = new DefaultListModel<String>();
		int numberOfText = 0;
		for (int i = 0; i < conditionStrings.length; i++) {
			if (conditionStrings[i].length() > 0 && !conditionStrings[i].equals("\n")) {
				if (column.getParent().getParent().getName().equals("battlemoves")) {
					if (column.getName().contains("PreConditions")) {
						if (conditionStrings[i].startsWith("-")) {
							tempList.addElement(conditionStrings[i] + " (" + xmlParser.listOfConditions.get(Integer.parseInt(conditionStrings[i].substring(1).replace(" ", "")) - 1).split("-")[1] + ")");
						} else {
							tempList.addElement(conditionStrings[i] + " (" + xmlParser.listOfConditions.get(Integer.parseInt(conditionStrings[i].replace(" ", "")) - 1).split("-")[1] + ")");
						}
					} else {
						if (i < 3) {
							if (conditionStrings[i].contains("-")) {
								tempList.addElement(column.getData().split("]")[numberOfText] + "]" + " [-" + xmlParser.listOfConditions.get(Integer.parseInt(conditionStrings[0].substring(2)) - 1) + "," + conditionStrings[1] + ","
										+ conditionStrings[2]);
							} else {
								tempList.addElement(column.getData().split("]")[numberOfText] + "]" + " [" + xmlParser.listOfConditions.get(Integer.parseInt(conditionStrings[0].substring(1)) - 1) + "," + conditionStrings[1] + ","
										+ conditionStrings[2]);
							}
						} else {
							if (conditionStrings[i].contains("-")) {
								tempList.addElement(column.getData().split("]")[numberOfText].substring(1) + "]" + " [-" + xmlParser.listOfConditions.get(Integer.parseInt(conditionStrings[i].substring(2)) - 1) + "," + conditionStrings[i + 1] + ","
										+ conditionStrings[i + 2]);
							} else {
								tempList.addElement(column.getData().split("]")[numberOfText].substring(1) + "]" + " [" + xmlParser.listOfConditions.get(Integer.parseInt(conditionStrings[i].substring(1)) - 1) + "," + conditionStrings[i + 1] + ","
										+ conditionStrings[i + 2]);
							}
						}
						numberOfText++;
						i += 2;
					}
				} else if (column.getParent().getParent().getName().equals("creatures")) {
					tempList.addElement(conditionStrings[i] + " (" + xmlParser.listOfConditions.get(Integer.parseInt(conditionStrings[i].split("=")[0]) - 1).split("-")[1] + ")");
				} else if (column.getParent().getParent().getName().equals("itemtypes")) {
					if (conditionStrings[i].contains("-")) {
						tempList.addElement(conditionStrings[i] + " (-" + xmlParser.listOfConditions.get(Integer.parseInt(conditionStrings[i].split("=")[1].substring(1)) - 1).split("-")[1] + ")");
					} else {
						tempList.addElement(conditionStrings[i] + " (" + xmlParser.listOfConditions.get(Integer.parseInt(conditionStrings[i].split("=")[1]) - 1).split("-")[1] + ")");
					}
				} else {
					if (conditionStrings[i].startsWith("-")) {
						if (conditionStrings[i].contains("x")) {
							tempList.addElement(conditionStrings[i] + " (" + xmlParser.listOfConditions.get(Integer.parseInt(conditionStrings[i].split("x")[0].substring(1)) - 1).split("-")[1] + ")");
						} else {
							tempList.addElement(conditionStrings[i] + " (" + xmlParser.listOfConditions.get(Integer.parseInt(conditionStrings[i].substring(1)) - 1).split("-")[1] + ")");
						}
					} else {
						if (conditionStrings[i].contains("x")) {
							tempList.addElement(conditionStrings[i] + " (" + xmlParser.listOfConditions.get(Integer.parseInt(conditionStrings[i].split("x")[0]) - 1).split("-")[1] + ")");
						} else {
							if (conditionStrings[i].equals("0")) {
								tempList.addElement(conditionStrings[i] + " (?)");
							} else {
								tempList.addElement(conditionStrings[i] + " (" + xmlParser.listOfConditions.get(Integer.parseInt(conditionStrings[i]) - 1).split("-")[1] + ")");
							}
						}
					}
				}
			}
		}
		tempPane.setLeftComponent(createList(tempList, column.getName()));
		tempPane.setRightComponent(new JScrollPane(createList(conditionsList, "conditionsList")));
		return tempPane;
	}

	private JComponent createList(DefaultListModel<String> list, String name) {
		JList<String> textList = new JList<String>(list);
		textList.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		textList.setDragEnabled(true);
		textList.setTransferHandler(new ListTransferHandler());
		textList.setName(name);
		textList.setDropMode(DropMode.INSERT);
		if (!name.equals("conditionsList")) {
			Action edit = new EditListAction();
			@SuppressWarnings("unused")
			ListAction la = new ListAction(textList, edit);
		}
		return textList;
	}

	private void detectChange() {
		for (JLabel columnName : listOfColumns.keySet()) {
			ModNode columnNode = xmlParser.modList.getChild(prevNode.getParent().getParent().toString()).getChild(prevNode.getParent().toString()).getChild(prevNode.toString()).getChild(columnName.getText());
			if (listOfColumns.get(columnName).getClass().equals(JTextPane.class)) {
				changeTextPane(columnNode, columnName);
			} else if (listOfColumns.get(columnName).getClass().equals(JSplitPane.class)) {
				changeSplitPane(columnNode, columnName);
			}
		}
	}

	private void changeTextPane(ModNode columnNode, JLabel columnName) {
		JTextPane columnText = (JTextPane) listOfColumns.get(columnName);
		if (!columnText.getText().equals(columnNode.getData())) {
			columnNode.setData(columnText.getText());
		}
	}

	private void changeSplitPane(ModNode columnNode, JLabel columnName) {
		JSplitPane columnPane = (JSplitPane) listOfColumns.get(columnName);
		@SuppressWarnings("unchecked")
		JList<String> columnList = (JList<String>) columnPane.getLeftComponent();
		ListModel<String> columnModel = columnList.getModel();
		if (columnModel.getSize() == 0) {
			columnNode.setData("");
		} else if (columnNode.getData().equals("")) {
			if (columnName.getText().equals("vUsConditions") || columnName.getText().equals("vThemConditions") || columnName.getText().equals("vPairConditions") || columnName.getText().equals("vUsFailConditions")
					|| columnName.getText().equals("vThemFailConditions") || columnName.getText().equals("vPairFailConditions")) {
				changeBattlemoves(columnNode, columnModel);
				return;
			} else {
				changeConditions(columnNode, columnModel);
				return;
			}
		} else {
			for (int i = 0; i < columnModel.getSize(); i++) {
				if (columnName.getText().equals("vUsConditions") || columnName.getText().equals("vThemConditions") || columnName.getText().equals("vPairConditions") || columnName.getText().equals("vUsFailConditions")
						|| columnName.getText().equals("vThemFailConditions") || columnName.getText().equals("vPairFailConditions")) {
					if (!columnModel.getElementAt(i).split("]")[0].equals(columnNode.getData().split("]")[i])) {
						changeBattlemoves(columnNode, columnModel);
					}
				} else if (!columnModel.getElementAt(i).split("\\(")[0].equals(columnNode.getData().split(",")[i])) {
					changeConditions(columnNode, columnModel);
					return;
				}
			}
		}
	}

	private void changeConditions(ModNode columnNode, ListModel<String> columnModel) {
		for (int j = 0; j < columnModel.getSize(); j++) {
			if (j == 0) {
				columnNode.setData(columnModel.getElementAt(j).split("\\(")[0]);
			} else {
				columnNode.setData(columnNode.getData() + "," + columnModel.getElementAt(j).split("\\(")[0]);
			}
		}
	}

	private void changeBattlemoves(ModNode columnNode, ListModel<String> columnModel) {
		for (int j = 0; j < columnModel.getSize(); j++) {
			if (j == 0) {
				columnNode.setData(columnModel.getElementAt(j).split("]")[0] + "]");
			} else {
				columnNode.setData(columnNode.getData() + "," + columnModel.getElementAt(j).split("]")[0] + "]");
			}
		}
	}

	private void refreshData() {
		getMods();
		getConditions();
		resetTree();
	}

	public static void infoBox(String infoMessage, String title) {
		JOptionPane.showMessageDialog(null, infoMessage, title, JOptionPane.INFORMATION_MESSAGE);
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
