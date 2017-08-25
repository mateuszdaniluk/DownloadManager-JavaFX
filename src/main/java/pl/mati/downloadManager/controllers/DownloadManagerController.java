package pl.mati.downloadManager.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import pl.mati.downloadManager.model.Download;

public class DownloadManagerController {

	private Download download;
	private Download selectedDownload;

	@FXML
	private Button pauseButton;
	@FXML
	private Button resumeButton;
	@FXML
	private Button cancelButton;
	@FXML
	private Button clearButton;
	@FXML
	private Button downloadButton;

	@FXML
	private TextField downloadField;

	public DownloadManagerController() {

	}

	@FXML
	// pause selected download
	private void actionPause() {
		download.pause();
		refreshButtons();
	}

	@FXML
	// resume selected download
	private void actionResume() {
		download.resume();
		refreshButtons();
	}

	@FXML
	// cancel selected download
	private void actionCancel() {
		download.cancel();
		refreshButtons();
	}

	@FXML
	// clear selected download
	private void actionClear() {
		downloadList.remove(downloadTable.getSelectionModel().getSelectedIndex());
		refreshButtons();
	}

	@FXML
	// exit from app
	private void actionExit() {
		System.exit(0);
	}

	@FXML
	// add new file
	private void actionAdd() {
		download = new Download(downloadField.getText());

		downloadList.add(download);
		downloadField.clear();
		refreshButtons();
		System.out.println("klikam download");

		System.out.println(download.toString());

	}

	@FXML
	private TableView<Download> downloadTable = new TableView<Download>();

	private final ObservableList<Download> downloadList =
			FXCollections.observableArrayList();

	@FXML
	TableColumn<Download, String> urlColumn;
	@FXML
	TableColumn<Download, Integer> sizeColumn;
	@FXML
	TableColumn<Download, Float> progressColumn;
	@FXML
	TableColumn<Download, String> statusColumn;

	@FXML
	private void tableViewMouseEvent() {

		refreshButtons();

	}

	//method to configure tableWiew in GUI
	public void configureTable() {

		urlColumn.setCellValueFactory(
				new PropertyValueFactory<Download, String>("url"));

		sizeColumn.setCellValueFactory(
				new PropertyValueFactory<Download, Integer>("size"));

		progressColumn.setCellValueFactory(
				new PropertyValueFactory<Download, Float>("progress"));

		statusColumn.setCellValueFactory(
				new PropertyValueFactory<Download, String>("status"));

		downloadTable.setItems(downloadList);
	}

	//refresh button in javaFX tableView
	private void refreshButtons() {
		int index = downloadTable.getSelectionModel().getSelectedIndex();
		 selectedDownload =
		 (Download)downloadTable.getSelectionModel().getSelectedItem();


		if (selectedDownload != null) {
			switch (downloadList.get(index).getIntStatus()) {
			case 0: // downloading
				pauseButton.setDisable(false);
				resumeButton.setDisable(true);
				clearButton.setDisable(true);
				cancelButton.setDisable(false);
				break;

			case 1: // paused
				pauseButton.setDisable(true);
				resumeButton.setDisable(false);
				clearButton.setDisable(true);
				cancelButton.setDisable(false);

				break;

			case 2: // complete
				pauseButton.setDisable(true);
				resumeButton.setDisable(true);
				clearButton.setDisable(false);
				cancelButton.setDisable(true);
				break;

			default: // cacncelled or error
				pauseButton.setDisable(true);
				resumeButton.setDisable(true);
				clearButton.setDisable(false);
				cancelButton.setDisable(true);
				break;
			}
		} else {
			// non download is selected
			pauseButton.setDisable(true);
			resumeButton.setDisable(true);
			clearButton.setDisable(true);
			cancelButton.setDisable(true);

		}
	}

}
