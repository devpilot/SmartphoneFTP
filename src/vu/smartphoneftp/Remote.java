package vu.smartphoneftp;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;

public class Remote {

	private static final String TAG = "FTP";
	private Activity context;
	private static FTPClient ftp;
	private static Server server;

	/**
	 * @param context
	 */
	public Remote(Activity context) {
		this.context = context;
	}

	public void connectServer(final Server server, final boolean reconnect) {
		if (isNetworkAvailable()) {
			Remote.server = server;
			ftp = new FTPClient();
			new AsyncTask<Void, Void, Boolean>() {
				String msg; // warning message for dialog
				
				@Override
				protected Boolean doInBackground(Void... params) {
					ftp.setConnectTimeout(500);
					try {
						int reply;
						ftp.connect(server.getHost(), server.getPort());
						reply = ftp.getReplyCode();
						if (!FTPReply.isPositiveCompletion(reply)) {
							ftp.disconnect();
							Log.e(TAG, "FTP server refused connection.");
						}
						Log.d(TAG, "Connected to server");
						if (!ftp.login(server.getUsername(),
								server.getPassword())) {
							msg = ftp.getReplyString();
							Log.d(TAG, ftp.getReplyString());
							ftp.logout();
							return false;
						}
						// should be connected here
						Log.d(TAG, ftp.getReplyString());
						return true;
					} catch (IOException e) {
						if (ftp.isConnected()) {
							try {
								ftp.disconnect();
							} catch (IOException f) {
								// do nothing
							}
						}
						msg = "Could not connect to server";
						Log.e(TAG, e.getMessage());
					}
					return false;
				}
				
				@Override
				protected void onPostExecute(Boolean result) {
					super.onPostExecute(result);
					Log.d(TAG, String.valueOf(result));
					if (!result) {
						showAlert(msg);
					} else if (!reconnect) {
						Intent i = new Intent("vu.smartphoneftp.FileBrowser_Activity");
						context.startActivity(i);
					}
				}
			}.execute();
		} else {
			showAlert("Not connected to internet");
		}
	}

	public void getList(final String path) {
		new AsyncTask<Void, Void, List<Items>>(){
			@Override
			protected List<Items> doInBackground(Void... params) {
				try {
					// enter passive mode
					ftp.enterLocalPassiveMode();
					FTPFile[] files = ftp.listFiles(path);
					// if remote directory not empty
					if(files != null){
						List<Items> i = new ArrayList<Items>();
						for (FTPFile ftpFile : files) {
							if(ftpFile.isDirectory()){
								// add directories
								i.add(new Items(ftpFile.getName(), "", R.drawable.ic_folder));
							} else {
								// add files
								// TODO need to format file size
								i.add(new Items(ftpFile.getName(), String.valueOf(ftpFile.getSize()), R.drawable.ic_file));
							}
//							Log.d(TAG, ftpFile.toFormattedString());
						}
						// return remote contents
						return i;
					}
				} catch (IOException e) {
					if (ftp.isConnected()) {
						try {
							ftp.disconnect();
						} catch (IOException f) {
							// do nothing
						}
					}
					Log.e(TAG, e.getMessage());
					// TODO need to reconnect if connection failed
				}
				return null;
			}

			@Override
			protected void onPostExecute(List<Items> result) {
				super.onPostExecute(result);
				// call content load method
				((FileBrowser_Activity) context).loadFileList(result);
			}
		}.execute();
	}

	private void showAlert(String message) {
		// building dialog
		AlertDialog ad = new AlertDialog.Builder(context).create();
		ad.setIcon(android.R.drawable.ic_dialog_alert);
		ad.setTitle("Error");
		ad.setMessage(message);
		ad.show();
	}

	/**
	 * Check network connection availability
	 * 
	 * @return boolean
	 */
	private boolean isNetworkAvailable() {
		ConnectivityManager cm = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = cm.getActiveNetworkInfo();
		// if no network is available networkInfo will be null
		// otherwise check if we are connected
		if (networkInfo != null && networkInfo.isConnected()) {
			return true;
		}
		return false;
	};
}
