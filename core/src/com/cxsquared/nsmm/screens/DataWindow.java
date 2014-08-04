package com.cxsquared.nsmm.screens;

import java.awt.EventQueue;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

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

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;
import com.cxsquared.nsmm.tools.DataParser;
import com.cxsquared.nsmm.tools.XmlFilter;

public class DataWindow extends JFrame implements TreeSelectionListener {
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTree tree;
	private JScrollPane scrollPane;
	private DataParser dp;
	private String fileLocation;
	private DefaultMutableTreeNode prevNode, top;
	DefaultListModel<String> conditionsList, itemPropsList;

	private final String NAME = "Neo Scavenger Mod Manager";
	private final String VERSION = "0.0.6";
	private JMenuBar menuBar;
	private JMenu mnFile;
	private JMenuItem mntmLoadNeogameFile, mntmRefreshNeogame, mntmSaveNeogame;
	private GridBagLayout gbl_contentPane;

	private List<JLabel> listOfLabels = new ArrayList<JLabel>();
	private List<JComponent> listOfTextFields = new ArrayList<JComponent>();

	private JTabbedPane tabbedPane;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
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

	/**
	 * Create the frame.
	 */
	public DataWindow() {
		fileLocation = "neogame.xml";
		dp = new DataParser(Gdx.files.internal(fileLocation));

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
				JFileChooser chooser = new JFileChooser();
				chooser.setFileFilter(new XmlFilter());
				int choice = chooser.showOpenDialog(null);

				if (choice != JFileChooser.APPROVE_OPTION) return;

				File chosenFile = chooser.getSelectedFile();

				fileLocation = chosenFile.toString();

				dp = new DataParser(Gdx.files.absolute(fileLocation));

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
				if (fileLocation.equals("neogame.xml"))
					dp = new DataParser(Gdx.files.internal(fileLocation));
				else dp = new DataParser(Gdx.files.absolute(fileLocation));
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

	private void getConditions() {
		conditionsList = new DefaultListModel<String>();
		for (int i = 0; i < dp.listOfConditions.size; i++) {
			conditionsList.addElement(dp.listOfConditions.get(i));
		}
		itemPropsList = new DefaultListModel<String>();
		for (int i = 0; i < dp.listOfItemProps.size; i++) {
			itemPropsList.addElement(dp.listOfItemProps.get(i));
		}
	}

	private void createNodes(DefaultMutableTreeNode top) {
		top.removeAllChildren();
		DefaultMutableTreeNode category = null;
		DefaultMutableTreeNode table = null;

		for (String name : dp.neogameTableTypeNames) {
			category = new DefaultMutableTreeNode(name);
			top.add(category);
			Array<String> temp = dp.neogameTableData.get(name).keys().toArray();
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

			for (String name : dp.neogameTableData.get(node.getParent().toString()).get(node.toString()).keys()) {
				listOfLabels.add(new JLabel(name));
				if (name.contains("Conditions")) {
					listOfTextFields.add(createConditionsList(node, name));
				} else if (name.contains("Properties")) {
					listOfTextFields.add(createItemPropsList(node, name));
				} else {
					JTextPane temp = new JTextPane();
					temp.setText(dp.neogameTableData.get(node.getParent().toString()).get(node.toString()).get(name));
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
		if (dp.neogameTableData.get(node.getParent().toString()).get(node.toString()).get(name) != null) tempString = dp.neogameTableData.get(node.getParent().toString()).get(node.toString()).get(name).split(",");
		JSplitPane tempPane = new JSplitPane();
		DefaultListModel<String> tempList = new DefaultListModel<String>();
		for (int i = 0; i < tempString.length; i++) {
			if (!tempString[i].equals("")) tempList.addElement(tempString[i] + " (" + dp.listOfItemProps.get(Integer.parseInt(tempString[i]) - 1).split("-")[1] + ")");
		}
		tempPane.setLeftComponent(new JScrollPane(new JList<String>(tempList)));
		tempPane.setRightComponent(new JScrollPane(new JList<String>(itemPropsList)));
		return tempPane;
	}

	private JComponent createConditionsList(DefaultMutableTreeNode node, String name) {
		String[] tempString = { "" };
		if (dp.neogameTableData.get(node.getParent().toString()).get(node.toString()).get(name) != null) {
			tempString = dp.neogameTableData.get(node.getParent().toString()).get(node.toString()).get(name).split(",");
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
							tempList.addElement(tempString[i] + " (" + dp.listOfConditions.get(Integer.parseInt(tempString[i].substring(1)) - 1).split("-")[1] + ")");
						} else {
							tempList.addElement(tempString[i] + " (" + dp.listOfConditions.get(Integer.parseInt(tempString[i]) - 1).split("-")[1] + ")");
						}
					} else {
						if (i < 3) {
							if (tempString[i].contains("-")) {
								tempText.setText(dp.neogameTableData.get(node.getParent().toString()).get(node.toString()).get(name).split("]")[numberOfText] + "]" + " [-" + dp.listOfConditions.get(Integer.parseInt(tempString[0].substring(2)) - 1)
										+ "," + tempString[1] + "," + tempString[2]);
							} else {
								tempText.setText(dp.neogameTableData.get(node.getParent().toString()).get(node.toString()).get(name).split("]")[numberOfText] + "]" + " [" + dp.listOfConditions.get(Integer.parseInt(tempString[0].substring(1)) - 1)
										+ "," + tempString[1] + "," + tempString[2]);
							}
						} else {
							if (tempString[i].contains("-")) {
								tempText.setText(tempText.getText() + "\n" + dp.neogameTableData.get(node.getParent().toString()).get(node.toString()).get(name).split("]")[numberOfText].substring(1) + "]" + " [-"
										+ dp.listOfConditions.get(Integer.parseInt(tempString[i].substring(2)) - 1) + "," + tempString[i + 1] + "," + tempString[i + 2]);
							} else {
								tempText.setText(tempText.getText() + "\n" + dp.neogameTableData.get(node.getParent().toString()).get(node.toString()).get(name).split("]")[numberOfText].substring(1) + "]" + " ["
										+ dp.listOfConditions.get(Integer.parseInt(tempString[i].substring(1)) - 1) + "," + tempString[i + 1] + "," + tempString[i + 2]);
							}
						}
						numberOfText++;
						i += 2;
					}
				} else if (node.getParent().toString().equals("creatures")) {
					tempList.addElement(tempString[i] + " (" + dp.listOfConditions.get(Integer.parseInt(tempString[i].split("=")[0]) - 1).split("-")[1] + ")");
				} else if (node.getParent().toString().equals("itemtypes")) {
					if (tempString[i].contains("-")) {
						tempList.addElement(tempString[i] + " (-" + dp.listOfConditions.get(Integer.parseInt(tempString[i].split("=")[1].substring(1)) - 1).split("-")[1] + ")");
					} else {
						tempList.addElement(tempString[i] + " (" + dp.listOfConditions.get(Integer.parseInt(tempString[i].split("=")[1]) - 1).split("-")[1] + ")");
					}
				} else {
					if (tempString[i].startsWith("-")) {
						if (tempString[i].contains("x")) {
							tempList.addElement(tempString[i] + " (" + dp.listOfConditions.get(Integer.parseInt(tempString[i].substring(1, tempString[i].length() - 4)) - 1).split("-")[1] + ")");
						} else {
							tempList.addElement(tempString[i] + " (" + dp.listOfConditions.get(Integer.parseInt(tempString[i].substring(1)) - 1).split("-")[1] + ")");
						}
					} else {
						if (tempString[i].contains("x")) {
							tempList.addElement(tempString[i] + " (" + dp.listOfConditions.get(Integer.parseInt(tempString[i].substring(0, tempString[i].length() - 4)) - 1).split("-")[1] + ")");
						} else {
							tempList.addElement(tempString[i] + " (" + dp.listOfConditions.get(Integer.parseInt(tempString[i]) - 1).split("-")[1] + ")");
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
}
