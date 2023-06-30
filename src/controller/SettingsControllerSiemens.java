/**
 * Sample Skeleton for 'SettingsRockwell.fxml' Controller Class
 */

package controller;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.*;
import javafx.scene.control.*;
import javafx.stage.Stage;
import model.siemens.ModelSiemens;
import settings.Settings;
import java.io.*;

public class SettingsControllerSiemens implements Initializable {
	private ModelSiemens model;
	private Settings properties;
	@FXML // ResourceBundle that was given to the FXMLLoader
	private ResourceBundle resources;
	@FXML // URL location of the FXML file that was given to the FXMLLoader
	private URL location;
	@FXML // fx:id="btnSave"
	private Button btnSave; // Value injected by FXMLLoader
	@FXML // fx:id="btnCancel"
	private Button btnCancel; // Value injected by FXMLLoader
	@FXML // fx:id="txtFieldDefaultFolder"
	private TextField txtFieldDefaultFolder; // Value injected by FXMLLoader
	private ContextMenu contextMenuALARMs;
	private ContextMenu contextMenuREADs;
	private ContextMenu contextMenuWRITEs;
	@FXML // fx:id="cbxOpenXml"
	private CheckBox cbxOpenXml; // Value injected by FXMLLoader
	@FXML // fx:id="cbximportAllsTags"
	private CheckBox cbximportAllsTags; // Value injected by FXMLLoader
	@FXML // fx:id="cbxImportOnlyUsedTags"
	private CheckBox cbxImportOnlyUsedTags; // Value injected by FXMLLoader
	@FXML // fx:id="listViewSuffixesALARMs"
	private ListView<String> listViewSuffixesALARMs; // Value injected by FXMLLoader
	@FXML // fx:id="listViewSuffixesREADs"
	private ListView<String> listViewSuffixesREADs; // Value injected by FXMLLoader
	@FXML // fx:id="listViewSuffixesWRITEs"
	private ListView<String> listViewSuffixesWRITEs; // Value injected by FXMLLoader

	@FXML
	void setCbxOpenXml(ActionEvent event) {
		// TODO da rimuovere?
//		if(cbxOpenXml.isSelected())
//			properties.setProperty("openXlsAfterImport", "false");
//		else 
//			properties.setProperty("openXlsAfterImport", "true");
	}

	@FXML
	void setImportAllsTags(ActionEvent event) {
		// TODO da rimuovere?
	}

	@FXML
	void cancelSettings(ActionEvent event) {
//		ModelSiemens.logSiem.debug("Siemens settings changes canceled ");
		Stage stage = (Stage) btnSave.getScene().getWindow();
		stage.close();
	}

	@FXML
	void saveSettings(ActionEvent event) throws FileNotFoundException, IOException {
		StringBuffer suffixeStringBuffer = new StringBuffer();
		for (int i = 0; i < listViewSuffixesALARMs.getItems().size(); i++) {
			if (i > 0)
				suffixeStringBuffer.append(" ; ");
			suffixeStringBuffer.append(listViewSuffixesALARMs.getItems().get(i));
		}
		properties.setProperty("suffixList_ALARMs", suffixeStringBuffer.toString());
		suffixeStringBuffer = null;
		suffixeStringBuffer = new StringBuffer();
		for (int i = 0; i < listViewSuffixesREADs.getItems().size(); i++) {
			if (i > 0)
				suffixeStringBuffer.append(" ; ");
			suffixeStringBuffer.append(listViewSuffixesREADs.getItems().get(i));
		}
		properties.setProperty("suffixList_READs", suffixeStringBuffer.toString());
		suffixeStringBuffer = null;
		suffixeStringBuffer = new StringBuffer();
		for (int i = 0; i < listViewSuffixesWRITEs.getItems().size(); i++) {
			if (i > 0)
				suffixeStringBuffer.append(" ; ");
			suffixeStringBuffer.append(listViewSuffixesWRITEs.getItems().get(i));
		}
		properties.setProperty("suffixList_WRITEs", suffixeStringBuffer.toString());
		properties.setProperty("filePath", txtFieldDefaultFolder.getText());
		if (cbxOpenXml.isSelected()) {
			properties.setProperty("openXlsAfterImport", "true");
		} else {
			properties.setProperty("openXlsAfterImport", "false");
		}
		properties.store(new FileOutputStream("properties/siemensImporterSettings.cfg"), null);
		Stage stage = (Stage) btnSave.getScene().getWindow();
		stage.close();
	}

	@FXML // This method is called by the FXMLLoader when initialization is complete
	void initialize() {
		assert btnSave != null : "fx:id=\"btnSave\" was not injected: check your FXML file 'SettingsRockwell.fxml'.";
		assert btnCancel != null
				: "fx:id=\"btnCancel\" was not injected: check your FXML file 'SettingsRockwell.fxml'.";
		assert txtFieldDefaultFolder != null
				: "fx:id=\"txtFieldDefaultFolder\" was not injected: check your FXML file 'SettingsRockwell.fxml'.";
		assert cbxOpenXml != null
				: "fx:id=\"cbxOpenXml\" was not injected: check your FXML file 'SettingsRockwell.fxml'.";
		assert listViewSuffixesALARMs != null
				: "fx:id=\"listViewSuffixesALARMs\" was not injected: check your FXML file 'SettingsRockwell.fxml'.";
		assert listViewSuffixesREADs != null
				: "fx:id=\"listViewSuffixesREADs\" was not injected: check your FXML file 'SettingsRockwell.fxml'.";
		assert listViewSuffixesWRITEs != null
				: "fx:id=\"listViewSuffixesWRITEs\" was not injected: check your FXML file 'SettingsRockwell.fxml'.";
	}

	public void setModel(ModelSiemens _model) {
		this.model = _model;
	}

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		properties = new Settings();
		try {
			properties.load(new File("properties/siemensImporterSettings.cfg"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		txtFieldDefaultFolder.setText(properties.getProperty("filePath").trim());

		if (properties.getProperty("openXlsAfterImport").equals("true")) {
			cbxOpenXml.setSelected(true);
		} else {
			cbxOpenXml.setSelected(false);
		}
		initializeListSuffixesALARMs();
		initializeListSuffixesREADs();
		initializeListSuffixesWRITEs();
	}

	private void initializeListSuffixesALARMs() {
		// populate the listView...
		String[] stringsArray = properties.getProperty("suffixList_ALARMs").trim().split(";");
		for (String suffix : stringsArray) {
			listViewSuffixesALARMs.getItems().add(suffix.trim());
		}
		listViewSuffixesALARMs.setContextMenu(makeContextMenuALARMs());
	}

	private void initializeListSuffixesREADs() {
		// populate the listView...
		String[] stringsArray = properties.getProperty("suffixList_READs").trim().split(";");
		for (String suffix : stringsArray) {
			listViewSuffixesREADs.getItems().add(suffix.trim());
		}
		listViewSuffixesREADs.setContextMenu(makeContextMenuREADs());
	}

	private void initializeListSuffixesWRITEs() {
		// populate the listView...
		String[] stringsArray = properties.getProperty("suffixList_WRITEs").trim().split(";");
		for (String suffix : stringsArray) {
			listViewSuffixesWRITEs.getItems().add(suffix.trim());
		}
		listViewSuffixesWRITEs.setContextMenu(makeContextMenuWRITEs());
	}

	private ContextMenu makeContextMenuALARMs() {
		contextMenuALARMs = new ContextMenu();
		TextInputDialog textInputDialog = new TextInputDialog();
		textInputDialog.setTitle("Enter a new string");
		textInputDialog.setContentText("String(s)");
		textInputDialog.setHeaderText("Type new string ( ; to separate string)");
		MenuItem addItem = new MenuItem("Add new string");
		addItem.setOnAction(e -> {
			textInputDialog.showAndWait();
			String textEnteredString = textInputDialog.getEditor().getText();
			String[] listStrings = textEnteredString.trim().split(";");
			for (String s : listStrings) {
				if (s.trim().length() != 0) {
					listViewSuffixesALARMs.getItems().add(s.trim());
				}
			}
		});
		MenuItem cancItem = new MenuItem("Canc selected string");
		cancItem.setOnAction(e -> {
			listViewSuffixesALARMs.getItems().removeAll(listViewSuffixesALARMs.getSelectionModel().getSelectedItem());
		});
		contextMenuALARMs.getItems().addAll(addItem, new SeparatorMenuItem(), cancItem);
		return contextMenuALARMs;
	}

	private ContextMenu makeContextMenuREADs() {
		contextMenuREADs = new ContextMenu();
		TextInputDialog textInputDialog = new TextInputDialog();
		textInputDialog.setTitle("Enter a new string");
		textInputDialog.setContentText("String(s)");
		textInputDialog.setHeaderText("Type new string ( ; to separate strings)");
		MenuItem addItem = new MenuItem("Add new string");
		addItem.setOnAction(e -> {
			textInputDialog.showAndWait();
			String textEnteredString = textInputDialog.getEditor().getText();
			String[] listStrings = textEnteredString.trim().split(";");
			for (String s : listStrings) {
				if (s.trim().length() != 0)
					listViewSuffixesREADs.getItems().add(s.trim());
			}
		});
		MenuItem cancItem = new MenuItem("Canc selected string");
		cancItem.setOnAction(e -> {
			listViewSuffixesREADs.getItems().removeAll(listViewSuffixesREADs.getSelectionModel().getSelectedItem());
		});
		contextMenuREADs.getItems().addAll(addItem, new SeparatorMenuItem(), cancItem);
		return contextMenuREADs;
	}

	private ContextMenu makeContextMenuWRITEs() {
		contextMenuWRITEs = new ContextMenu();
		TextInputDialog textInputDialog = new TextInputDialog();
		textInputDialog.setTitle("Enter a new string");
		textInputDialog.setContentText("String(s)");
		textInputDialog.setHeaderText("Type new string ( ; to separate strings)");
		MenuItem addItem = new MenuItem("Add new string");
		addItem.setOnAction(e -> {
			textInputDialog.showAndWait();
			String textEnteredString = textInputDialog.getEditor().getText();
			String[] listStrings = textEnteredString.trim().split(";");
			for (String s : listStrings) {
				if (s.trim().length() != 0) {
					listViewSuffixesWRITEs.getItems().add(s.trim());
				}
			}
		});
		MenuItem cancItem = new MenuItem("Canc selected string");
		cancItem.setOnAction(e -> {
			listViewSuffixesWRITEs.getItems().removeAll(listViewSuffixesWRITEs.getSelectionModel().getSelectedItem());
		});
		contextMenuWRITEs.getItems().addAll(addItem, new SeparatorMenuItem(), cancItem);
		return contextMenuWRITEs;
	}

	void closeWindow() {
		ModelSiemens.readProperties();
	}

}
