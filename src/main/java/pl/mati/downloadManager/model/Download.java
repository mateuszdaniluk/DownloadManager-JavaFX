package pl.mati.downloadManager.model;

import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Observable;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

/*
@author = Mati D
*/
public class Download extends Observable implements Runnable {

	// max rozmiar bufora pobierania
	private static final int MAX_BUFFER_SIZE = 2048;

	// statuses names
	public static final String STATUSES[] =
		{ "Downloading", "Paused", "Complete", "Cancelled", "Error" };

	// codes of statuses
	public static final int DOWNLOADING = 0;
	public static final int PAUSED = 1;
	public static final int COMPLETE = 2;
	public static final int CANCELLED = 3;
	public static final int ERROR = 4;

	private SimpleStringProperty url;
	private SimpleIntegerProperty size;
	private SimpleIntegerProperty downloaded;
	private SimpleIntegerProperty status;

	// constructor
	public Download(String url) {

		this.url = new SimpleStringProperty(url);
		size = new SimpleIntegerProperty(-1);
		downloaded = new SimpleIntegerProperty(0);
		status = new SimpleIntegerProperty(DOWNLOADING);

		// we need to start download at making an object
		download();
	}

	public Download(String url, int size, int status) {
		this.url = new SimpleStringProperty(url);
		this.size = new SimpleIntegerProperty(size);
		downloaded = new SimpleIntegerProperty(0);
		this.status = new SimpleIntegerProperty(status);

	}

	// getURL method
	public String getUrl() {
		return url.get();
	}

	// getSize method
	public String getSize() {
		return Integer.toString(size.get()/1024)+"KB";
	}

	// getProgress method
	public String getProgress() {
		return Float.toString((downloaded.get() / size.get()) * 100)+"%";
	}

	// getStatus method - return String value
	public String getStatus() {
		switch (status.get()) {
		case 0:
			return "Downloading";
		case 1:
			return "Paused";
		case 2:
			return "Complete";
		case 3:
			return "Cancelled";
		case 4:
			return "Error";


		default:
			return "Error";

		}

	}



	// getStatus method - return an Integer
	public int getIntStatus() {
		return status.get();
	}


	// pause method
	public void pause() {
		status = new SimpleIntegerProperty(PAUSED);
	}

	// resume method
	public void resume() {
		status = new SimpleIntegerProperty(DOWNLOADING);
		download();
	}

	/*
	 *  cancel method
	 */
	public void cancel() {
		status = new SimpleIntegerProperty(CANCELLED);
	}

	// "download has an errror" method
	private void error() {
		status = new SimpleIntegerProperty(ERROR);
	}

	// start downloading method
	private void download() {
		Thread thread = new Thread(this);
		thread.start();
	}

	// getFileName method
	private String getFileName(String url) {
		String fileName = url;

		return fileName.substring(fileName.lastIndexOf('/') + 1);
	}

	@Override
	// download a file method
	public void run() {
		RandomAccessFile file = null;
		InputStream stream = null;

		try {

			// open URL connection
			HttpURLConnection connection = (HttpURLConnection) new URL(url.get()).openConnection();

			connection.setRequestProperty("Range", "bytes=" + downloaded + "-");

			connection.connect();

			/*
			 * make sure that ResponseCode is in range of 200s 200-299 range
			 * means that there is no errors
			 */
			if (connection.getResponseCode() / 100 != 2) {
				error();
			}

			// make sure that length is proper
			int contentLength = connection.getContentLength();

			if (contentLength < 1) {
				error();
			}

			// make sure that the size is set
			if (size.get() == -1) {
				size.set(contentLength);
			}

			// open the file and go to the end of it
			file = new RandomAccessFile(getFileName(url.get()), "rw");
			file.seek(downloaded.get());

			stream = connection.getInputStream();

			while (status.get() == DOWNLOADING) {

				// buffor size depending how much left to download
				byte buffer[];

				if (size.get() - downloaded.get() > MAX_BUFFER_SIZE) {
					buffer = new byte[MAX_BUFFER_SIZE];
				} else {
					buffer = new byte[size.get() - downloaded.get()];
				}

				// read from server to the buffer
				int read = stream.read(buffer);

				if (read == -1) {
					break;
				}

				// save the buffer to the file
				file.write(buffer, 0, read);

				downloaded.set(downloaded.get() + read);

			}

			// make complete status if whole file was downloaded
			if (status.get() == DOWNLOADING) {
				status.set(COMPLETE);
			}
		} catch (Exception e) {
			error();
		} finally {

			// close the file
			if (file != null) {
				try {
					file.close();
				} catch (Exception e) {
				}
			}

			// close connection to the server
			if (stream != null) {
				try {
					stream.close();
				} catch (Exception e) {
				}
			}
		}
	}


	@Override
	public String toString() {
		return "Download [url=" + url + ", size=" + size + ", downloaded=" + downloaded + ", status=" + status + "]";
	}

}