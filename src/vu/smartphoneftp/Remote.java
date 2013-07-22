package vu.smartphoneftp;

import java.io.IOException;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;

public class Remote {

	private static final String TAG = "FTP";
	private Context context;
	private static FTPClient ftp;
	private static Server server;

	/**
	 * @param context
	 */
	public Remote(Context context) {
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

	public void getList() {
		new AsyncTask<Void, Void, FTPFile[]>(){
			@Override
			protected FTPFile[] doInBackground(Void... params) {
				try {
					// enter passive mode
					ftp.enterLocalPassiveMode();
					return ftp.listFiles();
				} catch (IOException e) {
					Log.e(TAG, e.getMessage());
					connectServer(server, true);
					getList();
				}
				return null;
			}

			@Override
			protected void onPostExecute(FTPFile[] result) {
				super.onPostExecute(result);
				if(result != null){
					for (FTPFile ftpFile : result) {
						Log.d(TAG, ftpFile.toFormattedString());
					}
				}
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
