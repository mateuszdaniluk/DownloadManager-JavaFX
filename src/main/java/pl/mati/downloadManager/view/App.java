package pl.mati.downloadManager.view;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import pl.mati.downloadManager.controllers.DownloadManagerController;

public class App extends Application {

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage stage) throws IOException, InvocationTargetException {

		FXMLLoader loader = new FXMLLoader();
		GridPane gridPane = null;

		URL path;
		path = getClass().getResource("/fxml/DownloadManagerWindow2.fxml");
		try {
			loader.setLocation(path);
			gridPane = loader.load();
		}
		 catch (IOException e) {
			System.out.println("Not found: " + path);
			e.printStackTrace();

		}
		catch (Exception e) {

		    // Answer:
		    e.getCause().printStackTrace();

		}

		DownloadManagerController controller = loader.getController();
		Scene scene = new Scene(gridPane);

		stage.setScene(scene);

		stage.setTitle("first JavaFX app");
		stage.show();

		controller.configureTable();

	}

}
