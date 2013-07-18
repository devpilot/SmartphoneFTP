package vu.smartphoneftp;

import java.io.IOException;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;

import android.app.AlertDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

public class Remote {

	private static final String TAG = "FTP";
	private Context context;
	private final FTPClient ftp = new FTPClient();

	/**
	 * @param context
	 */
	public Remote(Context context) {
		this.context = context;
	}
	
	public void connect(final Server server) {
		
		AsyncTask<Void, Void, Boolean> task = new AsyncTask<Void, Void, Boolean>() {
			String msg;
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
					if (!ftp.login(server.getUsername(), server.getPassword())) {
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
				if(!result){
					showAlert(msg);
				}
			}
		};
		task.execute();
	}

	 private void showAlert(String message){
		 // building dialog
		 AlertDialog ad = new AlertDialog.Builder(context).create();
		 ad.setIcon(android.R.drawable.ic_dialog_alert);
		 ad.setTitle("Error");
		 ad.setMessage(message);
		 ad.show();
	 }

	public void getList() {
		Runnable runnable = new Runnable() {
			public void run() {
				try {
					// enter passive mode
					ftp.enterLocalPassiveMode();
					FTPFile[] f = ftp.listFiles();
					for (FTPFile ftpFile : f) {
						Log.d(TAG, ftpFile.toFormattedString());
					}
				} catch (IOException e) {
					Log.e(TAG, e.getMessage());
				}
			}
		};
		new Thread(runnable).start();

	};
}
