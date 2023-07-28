package controller;

import java.io.*;
import java.net.URL;
import java.nio.channels.*;
import java.nio.file.*;
import java.util.*;
import org.apache.commons.io.FilenameUtils;
import org.controlsfx.control.MaskerPane;
import org.controlsfx.validation.*;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.layout.*;
import javafx.stage.*;
import javafx.stage.FileChooser.ExtensionFilter;
import model.rockwell.ModelRockwell;
import settings.Settings;

public class MainViewControllerRockwell extends ViewController implements Initializable {
	private ValidationSupport validationSupportXml = new ValidationSupport();
	private ValidationSupport validationSupportXls = new ValidationSupport();
	private static ModelRockwell model;
	private Settings properties;
	private static List<File> selectedFiles_L5X;
	private static List<File> selectedFiles_Xlsx;
	@FXML // ResourceBundle that was given to the FXMLLoader
	private ResourceBundle resources;
	@FXML // URL location of the FXML file that was given to the FXMLLoader
	private URL location;
	@FXML // fx:id="mainPane"
	private StackPane mainStackPane; // Value injected by FXMLLoader
	private VBox box;
	@FXML // fx:id="buttonBack"
	private Button buttonBack; // Value injected by FXMLLoader
	@FXML // fx:id="labelLocationXml"
	private Label labelLocationXml; // Value injected by FXMLLoader
	@FXML // fx:id="textFieldChooseXml"
	private TextField textFieldChooseXml; // Value injected by FXMLLoader
	@FXML // fx:id="buttonChooseXml"
	private Button buttonChooseXml; // Value injected by FXMLLoader
	@FXML // fx:id="imageViewButtonFileChooserXml"
	private ImageView imageViewButtonFileChooserXml; // Value injected by FXMLLoader
	@FXML // fx:id="buttonXmlToXls"
	private Button buttonXmlToXls; // Value injected by FXMLLoader
	@FXML // fx:id="imageViewButtonXmlToXls"
	private ImageView imageViewButtonXmlToXls; // Value injected by FXMLLoader
	@FXML // fx:id="labelLocationXls"
	private Label labelLocationXls; // Value injected by FXMLLoader
	@FXML // fx:id="textFieldChooseXls"
	private TextField textFieldChooseXls; // Value injected by FXMLLoader
	@FXML // fx:id="buttonChooseXls"
	private Button buttonChooseXls; // Value injected by FXMLLoader
	@FXML // fx:id="imageViewButtonFileChooserXls"
	private ImageView imageViewButtonFileChooserXls; // Value injected by FXMLLoader
	@FXML // fx:id="buttonXlsToCsv"
	private Button buttonXlsToCsv; // Value injected by FXMLLoader
	@FXML // fx:id="imageViewButtonXlsToCsv"
	private ImageView imageViewButtonXlsToCsv; // Value injected by FXMLLoader
	@FXML // fx:id="labelPrefixDriver"
	private Label labelPrefixDriver; // Value injected by FXMLLoader
	@FXML // fx:id="txtFieldPrefixDriver"
	private TextField txtFieldPrefixDriver; // Value injected by FXMLLoader
	@FXML // fx:id="mbPrefixDriver"
	private MenuButton mbPrefixDriver; // Value injected by FXMLLoader
	@FXML // fx:id="menuBar"
	private MenuBar menuBar; // Value injected by FXMLLoader
	@FXML // fx:id="imageViewMenuItemClose"
	private ImageView imageViewMenuItemClose; // Value injected by FXMLLoader
	@FXML // fx:id="menuItemSettingsRockwell"
	private MenuItem menuItemSettingsRockwell; // Value injected by FXMLLoader
	@FXML // fx:id="imageViewMenuItemSettings"
	private ImageView imageViewMenuItemSettings; // Value injected by FXMLLoader
	@FXML // fx:id="imageViewMenuItemAbout"
	private ImageView imageViewMenuItemAbout; // Value injected by FXMLLoader
	private MaskerPane masker = new MaskerPane();

	public MainViewControllerRockwell() throws FileNotFoundException, IOException {
		model = new ModelRockwell(null);
		properties = new Settings();
		properties.load(new File("properties/rockwellImporterSettings.cfg"));
	}

	@FXML
	void openSettingsRockwell(ActionEvent event) throws IOException {
		Stage stage = new Stage();
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(getClass().getResource("/fxml/SettingsRockwell.fxml"));
		Parent root = loader.load();
		SettingsControllerRockwell settingsRockwellController = (SettingsControllerRockwell) loader.getController();
		settingsRockwellController.setModel(model);
		Image icon = new Image(getClass().getResourceAsStream("/images/tagIcon48.png"));
		stage.getIcons().add(icon);
		stage.setTitle("settings Rockwell importer");
		stage.setScene(new Scene(root));
		stage.show();
	}

	@FXML
	void closeProgram(ActionEvent event) {
		System.exit(0);
	}

	@FXML
	void xlsToCsv(ActionEvent event) throws IOException {
		if (selectedFiles_Xlsx == null) {
			ValidationSupport.setRequired(textFieldChooseXls, true);
		} else {
			Convert_Xlsx_to_Csv task = new Convert_Xlsx_to_Csv(event);
			new Thread(task).start();
			setDisable(true);
		}
	}

	@FXML
	void xmlToXls(ActionEvent event) throws IOException, InterruptedException {
		if (selectedFiles_L5X == null) {
			ValidationSupport.setRequired(textFieldChooseXml, true);
		} else {
			Convert_L5X_To_Xlsx task = new Convert_L5X_To_Xlsx(event);
			setDisable(true);
			new Thread(task).start();
		}
	}

	private void setDisable(boolean b) {
		for (Node n : mainStackPane.getChildren()) {
			n.setDisable(b);
		}
		if (b) {
			masker.setVisible(b);
			masker.setProgress(ProgressBar.INDETERMINATE_PROGRESS);
			masker.setText("Please wait...");
			mainStackPane.getChildren().add(masker);
		} else
			mainStackPane.getChildren().remove(masker);
	}

	@FXML
	void openChooseFileXls(ActionEvent event) {
		ValidationSupport.setRequired(textFieldChooseXls, false);
		String filePath = "";
		model.readProperties();
		FileChooser fc = new FileChooser();
		fc.setTitle("Open XLS File");
		fc.getExtensionFilters().addAll(new ExtensionFilter("XLSX Files", "*.xlsx"));
		if (Files.exists(Paths.get(properties.getProperty("filePath")))) {
			fc.setInitialDirectory(new File(properties.getProperty("filePath")));
		} else {
			fc.setInitialDirectory(new File(System.getProperty("user.home") + "\\Desktop\\"));
		}
		selectedFiles_Xlsx = fc.showOpenMultipleDialog(buttonXlsToCsv.getParent().getScene().getWindow());
		String s = "";
		if (selectedFiles_Xlsx == null) {
			return;
		}
		for (File f : selectedFiles_Xlsx) {
			if (selectedFiles_Xlsx.size() > 1) {
				s = s + f.getName() + " ; ";
			} else {
				s = s + f.getName();
			}
			filePath = f.getParentFile().getAbsolutePath();
		}
		properties.setProperty("filePath", filePath);
		try {
			properties.store(new FileOutputStream("properties/rockwellImporterSettings.cfg"), null);
		} catch (IOException e) {
			ModelRockwell.logRock.error("IOException");
		}
		textFieldChooseXls.setText(s);
	}

	@FXML
	void openChooseFileL5X(ActionEvent event) {
		ModelRockwell.logRock.info("openChooseFileL5X");
		ValidationSupport.setRequired(textFieldChooseXml, false);
		String filePath = "";
		ModelRockwell.readProperties();
		FileChooser fc = new FileChooser();
		fc.setTitle("Open XML File");
		fc.getExtensionFilters().addAll(new ExtensionFilter("L5X Files", "*.L5X"));
		if (Files.exists(Paths.get(properties.getProperty("filePath"))) && !properties.getProperty("filePath").isBlank()
				&& !properties.getProperty("filePath").isEmpty() && !properties.getProperty("filePath").isBlank()) {
			fc.setInitialDirectory(new File(properties.getProperty("filePath")));
		} else {
			fc.setInitialDirectory(new File(System.getProperty("user.home") + "\\Desktop\\"));
		}
		selectedFiles_L5X = fc.showOpenMultipleDialog(buttonXmlToXls.getParent().getScene().getWindow());
		
		String s = "";
		if (selectedFiles_L5X == null) {
			ModelRockwell.logRock.info("selectedFiles_L5X == null");
			return;
		}
		
		for (File f : selectedFiles_L5X) {
			if (selectedFiles_L5X.size() > 1) {
				s = s + f.getName() + " ; ";
			} else {
				s = s + f.getName();
			}
			filePath = f.getParentFile().getAbsolutePath();
		}
		properties.setProperty("filePath", filePath);
		try {
			properties.store(new FileOutputStream("properties/rockwellImporterSettings.cfg"), null);
		} catch (IOException e) {
			ModelRockwell.logRock.error("IOException");
		}
		textFieldChooseXml.setText(s);
	}

	@FXML // This method is called by the FXMLLoader when initialization is complete
	void initialize() {
		assert buttonBack != null
				: "fx:id=\"buttonBack\" was not injected: check your FXML file 'MainViewRockwell.fxml'.";
		assert labelLocationXml != null
				: "fx:id=\"labelLocationXml\" was not injected: check your FXML file 'MainViewRockwell.fxml'.";
		assert textFieldChooseXml != null
				: "fx:id=\"textFieldChooseXml\" was not injected: check your FXML file 'MainViewRockwell.fxml'.";
		assert buttonChooseXml != null
				: "fx:id=\"buttonChooseXml\" was not injected: check your FXML file 'MainViewRockwell.fxml'.";
		assert imageViewButtonFileChooserXml != null
				: "fx:id=\"imageViewButtonFileChooserXml\" was not injected: check your FXML file 'MainViewRockwell.fxml'.";
		assert buttonXmlToXls != null
				: "fx:id=\"buttonXmlToXls\" was not injected: check your FXML file 'MainViewRockwell.fxml'.";
		assert imageViewButtonXmlToXls != null
				: "fx:id=\"imageViewButtonXmlToXls\" was not injected: check your FXML file 'MainViewRockwell.fxml'.";
		assert labelLocationXls != null
				: "fx:id=\"labelLocationXls\" was not injected: check your FXML file 'MainViewRockwell.fxml'.";
		assert textFieldChooseXls != null
				: "fx:id=\"textFieldChooseXls\" was not injected: check your FXML file 'MainViewRockwell.fxml'.";
		assert buttonChooseXls != null
				: "fx:id=\"buttonChooseXls\" was not injected: check your FXML file 'MainViewRockwell.fxml'.";
		assert imageViewButtonFileChooserXls != null
				: "fx:id=\"imageViewButtonFileChooserXls\" was not injected: check your FXML file 'MainViewRockwell.fxml'.";
		assert buttonXlsToCsv != null
				: "fx:id=\"buttonXlsToCsv\" was not injected: check your FXML file 'MainViewRockwell.fxml'.";
		assert imageViewButtonXlsToCsv != null
				: "fx:id=\"imageViewButtonXlsToCsv\" was not injected: check your FXML file 'MainViewRockwell.fxml'.";
		assert labelPrefixDriver != null
				: "fx:id=\"labelPrefixDriver\" was not injected: check your FXML file 'MainViewRockwell.fxml'.";
		assert txtFieldPrefixDriver != null
				: "fx:id=\"txtFieldPrefixDriver\" was not injected: check your FXML file 'MainViewRockwell.fxml'.";
		assert mbPrefixDriver != null
				: "fx:id=\"mbPrefixDriver\" was not injected: check your FXML file 'MainViewRockwell.fxml'.";
		assert menuBar != null : "fx:id=\"menuBar\" was not injected: check your FXML file 'MainViewRockwell.fxml'.";
		assert imageViewMenuItemClose != null
				: "fx:id=\"imageViewMenuItemClose\" was not injected: check your FXML file 'MainViewRockwell.fxml'.";
		assert menuItemSettingsRockwell != null
				: "fx:id=\"menuItemSettingsRockwell\" was not injected: check your FXML file 'MainViewRockwell.fxml'.";
		assert imageViewMenuItemSettings != null
				: "fx:id=\"imageViewMenuItemSettings\" was not injected: check your FXML file 'MainViewRockwell.fxml'.";
		assert imageViewMenuItemAbout != null
				: "fx:id=\"imageViewMenuItemAbout\" was not injected: check your FXML file 'MainViewRockwell.fxml'.";
	}

	public void setModel(ModelRockwell modelRockwell) {
		model = modelRockwell;
	}

	public String getPrefixSelected() {
		return txtFieldPrefixDriver.getText();
	}

	@FXML
	void backToMainView(ActionEvent event) throws IOException {
		Node source = (Node) event.getSource();
		Stage stage = (Stage) source.getScene().getWindow();
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(getClass().getResource("/fxml/MainView.fxml"));
		Parent root = loader.load();
		Scene scene = new Scene(root);
		stage.setScene(scene);
		stage.setTitle("TagMaker");
		stage.show();
	}

	public class Convert_L5X_To_Xlsx extends Task<Void> {

		public Convert_L5X_To_Xlsx(ActionEvent event) {
		}

		@Override
		protected Void call() throws Exception {
			model.readProperties();
			boolean rawExport = Boolean.parseBoolean(properties.getProperty("rawExport"));
			if (selectedFiles_L5X != null) {
				if (!rawExport) {
					for (File f : selectedFiles_L5X) {
						model.clearLists();
						model.readL5X(f.getAbsolutePath());
						model.generateXlsx(f.getName().replace("L5X", "xlsx"));
					}
				} else {
					for (File f : selectedFiles_L5X) {
						model.clearLists();
						model.readXmlRaw(f.getAbsolutePath());
					}
				}
			}
			return null;
		}

		@Override
		protected void succeeded() {
			selectedFiles_L5X = null;
			textFieldChooseXml.setText("select a file...");
			setDisable(false);
		}

		@Override
		protected void cancelled() {
			setDisable(false);
		}
	}

	public class Convert_Xlsx_to_Csv extends Task<Void> {

		public Convert_Xlsx_to_Csv(ActionEvent event) {
		}

		@Override
		protected Void call() throws Exception {
			if (selectedFiles_Xlsx != null) {
				for (File f : selectedFiles_Xlsx) {
					if (isFileClosed(f)) {
						model.readXsl(f.getAbsolutePath());
						if (Files.exists(Paths.get(properties.getProperty("filePath")))) {
							model.getCsvGenerator().generateCSV(properties.getProperty("filePath") + "\\"
									+ FilenameUtils.removeExtension(f.getName()) + ".csv", true, true);
						} else {
							model.getCsvGenerator().generateCSV(System.getProperty("user.home") + "\\"
									+ FilenameUtils.removeExtension(f.getName()) + ".csv", true, true);
						}
					} else {
						this.cancel();
					}
				}
			}
			return null;
		}

		@Override
		protected void succeeded() {
			selectedFiles_Xlsx = null;
			textFieldChooseXls.setText("select a file...");
			setDisable(false);
		}

		@Override
		protected void cancelled() {
			selectedFiles_Xlsx = null;
			textFieldChooseXls.setText("select a file...");
			setDisable(false);
			new Alert(Alert.AlertType.WARNING, "The file is open in another process, coundn't perform conversion!")
					.showAndWait();
		}
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		String[] menuItemStringsList = properties.getProperty("listOfPrefixes").split(";");
		for (String s : menuItemStringsList) {
			s = s.trim();
		}
		validationSupportXml.registerValidator(textFieldChooseXml, Validator.createEmptyValidator("Field required"));
		validationSupportXls.registerValidator(textFieldChooseXls, Validator.createEmptyValidator("Field required"));
		ValidationSupport.setRequired(textFieldChooseXml, false);
		ValidationSupport.setRequired(textFieldChooseXls, false);
	}

	private boolean isFileClosed(File file) {
		try {
			FileChannel channel = new RandomAccessFile(file, "rw").getChannel();
			FileLock lock = channel.tryLock();
			if (lock == null) {
				return false;
			} else {
				lock.release();
				return true;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}

}
