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
	private static final FTPClient client = new FTPClient();
	private Activity activity;
	private static Server server;	
	public String ftpWorkingDirectory;

	/**
	 * 
	 * @param activity
	 * @param server
	 */
	public Remote(Activity activity, Server server) {
		this.activity = activity;
		Remote.server = server;
	}
	
	/**
	 * 
	 * @param activity
	 */
	public Remote(Activity activity){
		this.activity = activity;
	}
	
	public void connectServer() {
		new ClientTask<Void, Void, Boolean>() {

			@Override
			protected Boolean doInBackground(Void... params) {
				return connect();
			}

			@Override
			protected void onPostExecute(Boolean result) {
				super.onPostExecute(result);
				Log.d(TAG, String.valueOf(result));
				if (!result) {
					showAlert(msg);
				} else {
					Intent i = new Intent("vu.smartphoneftp.FileBrowser_Activity");
					activity.startActivity(i);
				}
			}
		}.execute();
	}
	
	public void showRemoteFiles(final String ftpPath) {
		new ClientTask<Void, Void, List<Items>>() {
			@Override
			protected List<Items> doInBackground(Void... params) {
				return showRemoteFiles(ftpPath);
			}
			
			@Override
			protected void onPostExecute(List<Items> result) {
				super.onPostExecute(result);
				((FileBrowserBase) activity).toggleUpbtn(ftpWorkingDirectory);
				// call content load method
				if(result != null)
					((FileBrowserBase) activity).loadFileList(result);
			}
		}.execute();
	}
	public void getParent() {
		new ClientTask<Void, Void, List<Items>>() {

			@Override
			protected List<Items> doInBackground(Void... params) {
				try {
					if(client.changeToParentDirectory()){
						return showRemoteFiles(null);
					}
				} catch (IOException e) {
					// TODO reconnect
					Log.e(TAG, e.getMessage());
				}
				return null;
			}
			
			@Override
			protected void onPostExecute(List<Items> result) {
				super.onPostExecute(result);
				((FileBrowserBase) activity).toggleUpbtn(ftpWorkingDirectory);
				// call content load method
				if(result != null)
					((FileBrowserBase) activity).loadFileList(result);
			}
		}.execute();
	}
	
	public void getRootDir() {
		new ClientTask<Void, Void, List<Items>>() {

			@Override
			protected List<Items> doInBackground(Void... params) {
				try {
					if(client.changeWorkingDirectory("/")){
						return showRemoteFiles(null);
					}
				} catch (IOException e) {
					// TODO reconnect
					Log.e(TAG, e.getMessage());
				}
				return null;
			}
			
			@Override
			protected void onPostExecute(List<Items> result) {
				super.onPostExecute(result);
				((FileBrowserBase) activity).toggleUpbtn(ftpWorkingDirectory);
				// call content load method
				if(result != null)
					((FileBrowserBase) activity).loadFileList(result);
			}
		}.execute();
	}
	
	private abstract class ClientTask<Params, Progress, Result> extends AsyncTask<Params, Progress, Result>{
		
		protected String msg;
		protected boolean connect() {
			if (isNetworkAvailable()) {
				client.setConnectTimeout(500);
				try {
					int reply;
					client.connect(server.getHost(), server.getPort());
					reply = client.getReplyCode();
					if (!FTPReply.isPositiveCompletion(reply)) {
						client.disconnect();
						Log.e(TAG, "FTP server refused connection.");
					}
					Log.d(TAG, "Connected to server");
					if (!client.login(server.getUsername(), server.getPassword())) {
						msg = client.getReplyString();
						Log.d(TAG, client.getReplyString());
						client.logout();
						return false;
					}
					// should be connected here
					// enter passive mode
					client.enterLocalPassiveMode();
					Log.d(TAG, client.getReplyString());
					return true;
				} catch (IOException e) {
					if (client.isConnected()) {
						try {
							client.disconnect();
						} catch (IOException f) {
							// do nothing
						}
					}
					msg = "Could not connect to server";
					Log.e(TAG, e.getMessage());
				}
			} else {
				msg = "Not connected to internet";
			}
			return false;
		}

		protected List<Items> showRemoteFiles(String path){
			try {
				if(path != null)
					client.changeWorkingDirectory(path);
				ftpWorkingDirectory = client.printWorkingDirectory();
				FTPFile[] files = client.listFiles();
				// if remote directory not empty
				if(files != null){
					List<Items> i = new ArrayList<Items>();
					for (FTPFile ftpFile : files) {
						if(ftpFile.isDirectory()){
							// add directories to List
							i.add(new Items(ftpFile.getName(), R.drawable.ic_folder));
						} else {
							// add files
							i.add(new Items(ftpFile.getName(), ftpFile.getSize(), R.drawable.ic_file));
						}
					}
					// return remote contents
					return i;
				}
			} catch (IOException e) {
				Log.e(TAG, e.getMessage());
				// TODO need to reconnect if connection failed
			}
			return null;
		}
		
		protected void showAlert(String message) {
			// building dialog
			AlertDialog ad = new AlertDialog.Builder(activity).create();
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
			ConnectivityManager cm = (ConnectivityManager) activity
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo networkInfo = cm.getActiveNetworkInfo();
			// if no network is available networkInfo will be null
			// otherwise check if we are connected
			if (networkInfo != null && networkInfo.isConnected()) {
				return true;
			}
			return false;
		}
	};
}
