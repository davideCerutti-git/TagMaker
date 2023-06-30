/**
 * Sample Skeleton for 'MainViewSiemens.fxml' Controller Class
 */

package controller;

import java.io.*;
import java.nio.channels.*;
import org.controlsfx.control.*;
import org.controlsfx.validation.*;
import javafx.stage.*;
import java.net.URL;
import java.nio.file.*;
import java.util.*;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.*;
import javafx.geometry.Insets;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.layout.*;
import javafx.stage.FileChooser.ExtensionFilter;
import model.siemens.ModelSiemens;
import settings.Settings;

public class MainViewControllerSiemens extends ViewController implements Initializable {
	private ModelSiemens model;
	private ValidationSupport validationSupportDb = new ValidationSupport();
	private ValidationSupport validationSupportXlsx = new ValidationSupport();
	private static List<File> selectedDbFiles;
	private static List<File> selectedXlsxFiles;
	private Settings properties;
	private MaskerPane masker = new MaskerPane();
	@FXML // ResourceBundle that was given to the FXMLLoader
	private ResourceBundle resources;
	@FXML // URL location of the FXML file that was given to the FXMLLoader
	private URL location;
	@FXML // fx:id="buttonBack"
	private Button buttonBack; // Value injected by FXMLLoader
	@FXML // fx:id="buttonChooseDb"
	private Button buttonChooseDb; // Value injected by FXMLLoader
	@FXML // fx:id="buttonChooseXlsx"
	private Button buttonChooseXlsx; // Value injected by FXMLLoader
	@FXML // fx:id="buttonDbToXls"
	private Button buttonDbToXls; // Value injected by FXMLLoader
	@FXML // fx:id="buttonXlsxToCsv"
	private Button buttonXlsxToCsv; // Value injected by FXMLLoader
	@FXML // fx:id="imageViewButtonDbToXls"
	private ImageView imageViewButtonDbToXls; // Value injected by FXMLLoader
	@FXML // fx:id="imageViewButtonFileChooserDb"
	private ImageView imageViewButtonFileChooserDb; // Value injected by FXMLLoader
	@FXML // fx:id="imageViewButtonFileChooserXls"
	private ImageView imageViewButtonFileChooserXls; // Value injected by FXMLLoader
	@FXML // fx:id="imageViewButtonXlsToCsv"
	private ImageView imageViewButtonXlsToCsv; // Value injected by FXMLLoader
	@FXML // fx:id="imageViewMenuItemAbout"
	private ImageView imageViewMenuItemAbout; // Value injected by FXMLLoader
	@FXML // fx:id="imageViewMenuItemClose"
	private ImageView imageViewMenuItemClose; // Value injected by FXMLLoader
	@FXML // fx:id="imageViewMenuItemSettings"
	private ImageView imageViewMenuItemSettings; // Value injected by FXMLLoader
	@FXML // fx:id="labelLocationDb"
	private Label labelLocationDb; // Value injected by FXMLLoader
	@FXML // fx:id="labelLocationXls"
	private Label labelLocationXls; // Value injected by FXMLLoader
	@FXML // fx:id="labelPrefixDriver"
	private Label labelPrefixDriver; // Value injected by FXMLLoader
	@FXML // fx:id="mainPane"
	private BorderPane mainPane; // Value injected by FXMLLoader
	@FXML // fx:id="mainStackPane"
	private StackPane mainStackPane; // Value injected by FXMLLoader
	@FXML // fx:id="mbPrefixDriver"
	private MenuButton mbPrefixDriver; // Value injected by FXMLLoader
	@FXML // fx:id="menuBar"
	private MenuBar menuBar; // Value injected by FXMLLoader
	@FXML // fx:id="menuItemSettingsSiemens"
	private MenuItem menuItemSettingsSiemens; // Value injected by FXMLLoader
	@FXML // fx:id="textFieldChooseDb"
	private TextField textFieldChooseDb; // Value injected by FXMLLoader
	@FXML // fx:id="textFieldChooseXlsx"
	private TextField textFieldChooseXlsx; // Value injected by FXMLLoader
	@FXML // fx:id="txtFieldPrefixDriver"
	private TextField txtFieldPrefixDriver; // Value injected by FXMLLoader
	@FXML // fx:id="rbAbsolute"
	private RadioButton rbAbsolute; // Value injected by FXMLLoader
	@FXML // fx:id="rbSymbolic"
	private RadioButton rbSymbolic; // Value injected by FXMLLoader

	public MainViewControllerSiemens() {
		model = new ModelSiemens(null);
		properties = model.getProp();
	}

	@FXML
	void openSettingsSiemens(ActionEvent event) throws IOException {
		Stage stage = new Stage();
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(getClass().getResource("/fxml/SettingsSiemens.fxml"));
		Parent root = loader.load();
		SettingsControllerSiemens settingsSiemensController = (SettingsControllerSiemens) loader.getController();
		settingsSiemensController.setModel(model);
		Image icon = new Image(getClass().getResourceAsStream("/images/tagIcon48.png"));
		stage.getIcons().add(icon);
		stage.setTitle("settings Siemens importer");
		stage.setScene(new Scene(root));
		stage.show();
	}

	public void setModel(ModelSiemens modelSiemens) {
		model = modelSiemens;
		properties = model.getProp();
	}

	/**
	 * ######################## DB files
	 * ##############################################
	 **/
	@FXML
	void openChooseFileDb(ActionEvent event) {
		ValidationSupport.setRequired(textFieldChooseDb, false);
		String filePath = "";
		model.readProperties();
		FileChooser fc = new FileChooser();
		fc.setTitle("Open db File");
		fc.getExtensionFilters().addAll(new ExtensionFilter("db Files", "*.db"));
		if (Files.exists(Paths.get(properties.getProperty("filePath"))) && !properties.getProperty("filePath").isBlank()
				&& !properties.getProperty("filePath").isEmpty() && !properties.getProperty("filePath").isBlank()) {
			fc.setInitialDirectory(new File(properties.getProperty("filePath")));
		} else {
			fc.setInitialDirectory(new File(System.getProperty("user.home") + "\\Desktop\\"));
		}
		selectedDbFiles = fc.showOpenMultipleDialog(buttonDbToXls.getParent().getScene().getWindow());
		String s = "";
		if (selectedDbFiles == null) {
			return;
		}
		for (File f : selectedDbFiles) {
			if (selectedDbFiles.size() > 1) {
				s = s + f.getName() + " ; ";
			} else {
				s = s + f.getName();
			}
			filePath = f.getParentFile().getAbsolutePath();
		}
		properties.setProperty("filePath", filePath);
		storePropertiesOnFile();
		textFieldChooseDb.setText(s);
	}

	private void storePropertiesOnFile() {
		try {
			properties.store(new FileOutputStream("properties/siemensImporterSettings.cfg"), null);
		} catch (IOException e) {
			ModelSiemens.logSiem.error("IOException");
		}
	}

	@FXML
	void dbToXls(ActionEvent event) throws IOException, CloneNotSupportedException {
		if (selectedDbFiles == null) {
			ValidationSupport.setRequired(textFieldChooseDb, true);
		} else {
			ConvertDb task = new ConvertDb(event);
			setDisable(true);
			new Thread(task).start();
		}
	}

	public class ConvertDb extends Task<Void> {

		public ConvertDb(ActionEvent event) {
		}

		@Override
		protected Void call() throws Exception {
			if (selectedDbFiles != null) {
				File file = null;
				for (File f : selectedDbFiles) {
					if (!ModelSiemens.readDBFile(f)) {
						ModelSiemens.logSiem.error("Errore lettura file " + f.getName());
						this.cancel();
						return null;
					}
					file = f;
				}
				model.generateXlsx(file.getAbsolutePath().replace(".db", ".xlsx"));
			}
			return null;
		}

		@Override
		protected void succeeded() {
			selectedDbFiles = null;
			textFieldChooseDb.setText("select a file...");
			model.clearDbList();
			setDisable(false);
		}

		@Override
		protected void cancelled() {
			selectedDbFiles = null;
			textFieldChooseDb.setText("select a file...");
			model.clearDbList();
			setDisable(false);
			new Alert(Alert.AlertType.ERROR, "Errore converisone file .db ").showAndWait();
		}
	}

	/**
	 * ########################################################################################
	 **/

	/**
	 * ######################## XLS files
	 * ##############################################
	 **/
	@FXML
	void openChooseFileXlsx(ActionEvent event) {
		ValidationSupport.setRequired(textFieldChooseXlsx, false);
		ValidationSupport.setRequired(txtFieldPrefixDriver, false);
		String filePath = "";
		model.readProperties();
		FileChooser fc = new FileChooser();
		fc.setTitle("Open xlsx File");
		fc.getExtensionFilters().addAll(new ExtensionFilter("xlsx Files", "*.xlsx"));
		if (Files.exists(Paths.get(properties.getProperty("filePath"))) && !properties.getProperty("filePath").isBlank()
				&& !properties.getProperty("filePath").isEmpty() && !properties.getProperty("filePath").isBlank()) {
			fc.setInitialDirectory(new File(properties.getProperty("filePath")));
		} else {
			fc.setInitialDirectory(new File(System.getProperty("user.home") + "\\Desktop\\"));
		}
		selectedXlsxFiles = fc.showOpenMultipleDialog(buttonXlsxToCsv.getParent().getScene().getWindow());
		String s = "";
		if (selectedXlsxFiles == null) {
			return;
		}
		for (File f : selectedXlsxFiles) {
			if (selectedXlsxFiles.size() > 1) {
				s = s + f.getName() + " ; ";
			} else {
				s = s + f.getName();
			}
			filePath = f.getParentFile().getAbsolutePath();
		}
		properties.setProperty("filePath", filePath);
		try {
			properties.store(new FileOutputStream("properties/siemensImporterSettings.cfg"), null);
		} catch (IOException e) {
			ModelSiemens.logSiem.error("IOException");
		}
		textFieldChooseXlsx.setText(s);
	}

	@FXML
	void xlsxToCsv(ActionEvent event) throws IOException, CloneNotSupportedException {
		boolean error = false;
		if (selectedXlsxFiles == null) {
			ValidationSupport.setRequired(textFieldChooseXlsx, true);
			error = true;
		}
		if (txtFieldPrefixDriver.getText().isEmpty()) {
			ValidationSupport.setRequired(txtFieldPrefixDriver, true);
			error = true;
		}
		if (!error) {
			ConvertXlsx task = new ConvertXlsx(event);
			setDisable(true);
			new Thread(task).start();
		}
	}

	public class ConvertXlsx extends Task<Void> {

		public ConvertXlsx(ActionEvent event) {
		}

		@Override
		protected Void call() throws Exception {
			if (selectedXlsxFiles != null) {
				File file = null;
				for (File f : selectedXlsxFiles) {
					if (!model.readXlsxFile(f)) {
						ModelSiemens.logSiem.error("Errore lettura file " + f.getName());
						this.cancel();
						return null;
					}
					file = f;
				}
				model.getCsvGenerator().generateCSV(file.getAbsolutePath().replace(".xlsx", ".csv"),
						rbSymbolic.isSelected(), false);
				model.clearListEntry();
			}
			return null;
		}

		@Override
		protected void succeeded() {
			selectedXlsxFiles = null;
			textFieldChooseXlsx.setText("select a file...");
			txtFieldPrefixDriver.setText("");
			model.clearDbList();
			setDisable(false);
		}

		@Override
		protected void cancelled() {
			selectedXlsxFiles = null;
			textFieldChooseXlsx.setText("select a file...");
			txtFieldPrefixDriver.setText("");
			model.clearDbList();
			setDisable(false);
			new Alert(Alert.AlertType.ERROR, "Errore converisone file .xlsx ").showAndWait();
		}
	}

	/**
	 * ########################################################################################
	 **/

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

	@FXML // This method is called by the FXMLLoader when initialization is complete
	void initialize() {
		assert buttonBack != null
				: "fx:id=\"buttonBack\" was not injected: check your FXML file 'MainViewSiemens.fxml'.";
		assert buttonChooseDb != null
				: "fx:id=\"buttonChooseDb\" was not injected: check your FXML file 'MainViewSiemens.fxml'.";
		assert buttonChooseXlsx != null
				: "fx:id=\"buttonChooseXlsx\" was not injected: check your FXML file 'MainViewSiemens.fxml'.";
		assert buttonDbToXls != null
				: "fx:id=\"buttonDbToXls\" was not injected: check your FXML file 'MainViewSiemens.fxml'.";
		assert buttonXlsxToCsv != null
				: "fx:id=\"buttonXlsxToCsv\" was not injected: check your FXML file 'MainViewSiemens.fxml'.";
		assert imageViewButtonDbToXls != null
				: "fx:id=\"imageViewButtonDbToXls\" was not injected: check your FXML file 'MainViewSiemens.fxml'.";
		assert imageViewButtonFileChooserDb != null
				: "fx:id=\"imageViewButtonFileChooserDb\" was not injected: check your FXML file 'MainViewSiemens.fxml'.";
		assert imageViewButtonFileChooserXls != null
				: "fx:id=\"imageViewButtonFileChooserXls\" was not injected: check your FXML file 'MainViewSiemens.fxml'.";
		assert imageViewButtonXlsToCsv != null
				: "fx:id=\"imageViewButtonXlsToCsv\" was not injected: check your FXML file 'MainViewSiemens.fxml'.";
		assert imageViewMenuItemAbout != null
				: "fx:id=\"imageViewMenuItemAbout\" was not injected: check your FXML file 'MainViewSiemens.fxml'.";
		assert imageViewMenuItemClose != null
				: "fx:id=\"imageViewMenuItemClose\" was not injected: check your FXML file 'MainViewSiemens.fxml'.";
		assert imageViewMenuItemSettings != null
				: "fx:id=\"imageViewMenuItemSettings\" was not injected: check your FXML file 'MainViewSiemens.fxml'.";
		assert labelLocationDb != null
				: "fx:id=\"labelLocationDb\" was not injected: check your FXML file 'MainViewSiemens.fxml'.";
		assert labelLocationXls != null
				: "fx:id=\"labelLocationXls\" was not injected: check your FXML file 'MainViewSiemens.fxml'.";
		assert labelPrefixDriver != null
				: "fx:id=\"labelPrefixDriver\" was not injected: check your FXML file 'MainViewSiemens.fxml'.";
		assert mainPane != null : "fx:id=\"mainPane\" was not injected: check your FXML file 'MainViewSiemens.fxml'.";
		assert mainStackPane != null
				: "fx:id=\"mainStackPane\" was not injected: check your FXML file 'MainViewSiemens.fxml'.";
		assert mbPrefixDriver != null
				: "fx:id=\"mbPrefixDriver\" was not injected: check your FXML file 'MainViewSiemens.fxml'.";
		assert menuBar != null : "fx:id=\"menuBar\" was not injected: check your FXML file 'MainViewSiemens.fxml'.";
		assert menuItemSettingsSiemens != null
				: "fx:id=\"menuItemSettingsSiemens\" was not injected: check your FXML file 'MainViewSiemens.fxml'.";
		assert textFieldChooseDb != null
				: "fx:id=\"textFieldChooseDb\" was not injected: check your FXML file 'MainViewSiemens.fxml'.";
		assert textFieldChooseXlsx != null
				: "fx:id=\"textFieldChooseXlsx\" was not injected: check your FXML file 'MainViewSiemens.fxml'.";
		assert txtFieldPrefixDriver != null
				: "fx:id=\"txtFieldPrefixDriver\" was not injected: check your FXML file 'MainViewSiemens.fxml'.";
		assert rbAbsolute != null
				: "fx:id=\"rbAbsolute\" was not injected: check your FXML file 'MainViewSiemens.fxml'.";
		assert rbSymbolic != null
				: "fx:id=\"rbSymbolic\" was not injected: check your FXML file 'MainViewSiemens.fxml'.";
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		String[] menuItemStringsList = properties.getProperty("listOfPrefixes").split(";");
		for (String s : menuItemStringsList) {
			s = s.trim();
		}
		validationSupportDb.registerValidator(textFieldChooseDb, Validator.createEmptyValidator("Field required"));
		ValidationSupport.setRequired(textFieldChooseDb, false);
		validationSupportXlsx.registerValidator(textFieldChooseXlsx, Validator.createEmptyValidator("Field required"));
		ValidationSupport.setRequired(textFieldChooseXlsx, false);
		validationSupportXlsx.registerValidator(txtFieldPrefixDriver, Validator.createEmptyValidator("Field required"));
		ValidationSupport.setRequired(txtFieldPrefixDriver, false);
		makePopOverInfo(buttonDbToXls, "Converte un file db (creato con TIA) in file xlsx (formato Bottero)\n"
				+ "- I file db non hanno informazione del numero DB, quindi è necessario\n"
				+ "mettere nella cartella in cui si trova i/il file .db anche il file indexDBs.cfg\n"
				+ "questo è un file di testo con il seguente contenuto (esempio):\n"
				+ "					DB_Fix write bit=185\n" + "					DB_FixManComConv=189\n"
				+ "					DB_FixReadBit=181\n" + "					DB_FixReadDint=183\n"
				+ "					DB_FixReadInt=182\n" + "					DB_FixReadReal=184\n"
				+ "					DB_FixWrite_Real=188\n" + "					DB_FixWriteDint=187\n"
				+ "					DB_FixWriteInt=186\n" + "					FixReadCap=330\n"
				+ "					FixReadCap_copy=331\n"
				+ "in questo file viene indicato a sinistra dell'uguale il nome simbolico della db e a destra il numero della db");
		makePopOverInfo(buttonXlsxToCsv,
				"Converte un file xlsx (formato Bottero) in \nfile csv (formato importabile in iFix)\nPer il momento solo in assoluto!");
		ToggleGroup tg = new ToggleGroup();
		rbAbsolute.setSelected(Boolean.parseBoolean(properties.getProperty("absoluteAddressSelected")));
		rbSymbolic.setSelected(!Boolean.parseBoolean(properties.getProperty("absoluteAddressSelected")));
		rbAbsolute.setToggleGroup(tg);
		rbSymbolic.setToggleGroup(tg);
		rbAbsolute.selectedProperty().addListener((observable, oldValue, newValue) -> {
			properties.setProperty("absoluteAddressSelected", "" + rbAbsolute.isSelected());
			storePropertiesOnFile();
		});
	}

	private void makePopOverInfo(Button button, String string) {
		PopOver popOver = new PopOver(createPopOverinfo(string));
		button.setOnMouseEntered(mouseEvent -> {
			// Show PopOver when mouse enters label
			popOver.show(button);
		});
		button.setOnMouseExited(mouseEvent -> {
			// Show PopOver when mouse enters label
			popOver.hide();
		});
	}

	private VBox createPopOverinfo(String str) {
		Label lblName = new Label(str);
		VBox vBox = new VBox(lblName);
		VBox.setMargin(lblName, new Insets(10.0, 10.0, 10.0, 10.0));
		return vBox;
	}

	@FXML
	void closeProgram(ActionEvent event) {
		System.exit(0);
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
			ModelSiemens.logSiem.warn("File gia' aperto in un altro processo!!!!");
		}
		return false;
	}

	public String getPrefixSelected() {
		return txtFieldPrefixDriver.getText();
	}

	public ModelSiemens getModel() {
		return this.model;
	}

}
