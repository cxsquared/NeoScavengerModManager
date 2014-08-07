package com.cxsquared.nsmm.tools;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;

import javax.swing.DefaultListModel;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.TransferHandler;

public class ListTransferHandler extends TransferHandler {
	private static final long serialVersionUID = 1L;

	private int[] indices = null;
	private int addIndex = -1; // Location where items were added
	private int addCount = 0; // Number of items added;

	// Only support strings
	public boolean canImport(TransferHandler.TransferSupport info) {
		// Check for String flavor
		if (!info.isDataFlavorSupported(DataFlavor.stringFlavor)) {
			return false;
		}
		return true;
	}

	/**
	 * Bundle up the selected items in a single list for export. Each line is
	 * separated by a newline.
	 */

	protected Transferable createTransferable(JComponent c) {
		JList<?> list = (JList<?>) c;
		indices = list.getSelectedIndices();
		Object[] values = (Object[]) list.getSelectedValuesList().toArray();

		StringBuffer buff = new StringBuffer();

		for (int i = 0; i < values.length; i++) {
			Object val = values[i];
			buff.append(val == null ? "" : val.toString());
			if (i != values.length - 1) {
				buff.append("\n");
			}
		}
		return new StringSelection(buff.toString());
	}

	/**
	 * We support both copy and move actions.
	 */
	public int getSourceActions(JComponent c) {
		return TransferHandler.COPY_OR_MOVE;
	}

	/**
	 * Perform the actual import.
	 */
	public boolean importData(TransferHandler.TransferSupport info) {
		if (!info.isDrop()) {
			return false;
		}

		@SuppressWarnings("unchecked")
		JList<String> list = (JList<String>) info.getComponent();
		DefaultListModel<String> listModel = (DefaultListModel<String>) list.getModel();
		JList.DropLocation dl = (JList.DropLocation) info.getDropLocation();
		int index = dl.getIndex();
		boolean insert = dl.isInsert();

		// Get the string that is being dropped
		Transferable t = info.getTransferable();
		String data;
		try {
			data = (String) t.getTransferData(DataFlavor.stringFlavor);
		} catch (Exception e) {
			return false;
		}

		// Wherever there is a newline in the incoming data,
		// break it into a separate item in the list.
		String[] values = data.split("\n");

		addIndex = index;
		addCount = values.length;

		if (!list.getName().equals("conditionsList")) {
			// Perform the actual import
			for (int i = 0; i < values.length; i++) {
				String dataText = values[i];
				if (list.getName().equals("vAttackerConditions")) {
					dataText = dataText.split("-")[0] + "x1.0 (" + dataText.split("-")[1] + ")";
				}
				if (insert) {
					listModel.add(index++, dataText);
				} else {
					// If the items go beyond the end of the current
					// list add, them in.
					if (index < listModel.getSize()) {
						listModel.set(index++, dataText);
					} else {
						listModel.add(index++, dataText);
					}
				}
			}
		}
		return true;
	}

	/**
	 * Remove the items moved from the list.
	 */
	protected void exportDone(JComponent c, Transferable data, int action) {
		JList<?> source = (JList<?>) c;
		if (!source.getName().equals("conditionsList")) {
			DefaultListModel<?> listModel = (DefaultListModel<?>) source.getModel();

			if (action == TransferHandler.MOVE) {
				for (int i = indices.length - 1; i >= 0; i--) {
					listModel.remove(indices[i]);
				}
			}
		}
		indices = null;
		addCount = 0;
		addIndex = -1;
	}
}
