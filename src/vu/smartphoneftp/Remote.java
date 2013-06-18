package vu.smartphoneftp;

import java.io.IOException;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Handler;
import android.util.Log;

public class Remote{

	private static final String TAG = "FTP";
	private Context context;
	private final FTPClient ftp = new FTPClient();
	String server;
	/**
	 * @param context
	 */
	public Remote(Context context) {
		this.context = context;
	}

	public void connect(final String server, final int port, final String username,
			final String password) {
        
		final Handler h = new Handler();
		
		ftp.setConnectTimeout(200);
		
		Runnable runnable = new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub

				try {
					int reply;
					if (port > 0) {

						ftp.connect(server, port);
					} else {
						ftp.connect(server);
					}
					reply = ftp.getReplyCode();

					if (!FTPReply.isPositiveCompletion(reply)) {
						ftp.disconnect();
						Log.e(TAG, "FTP server refused connection.");
						
					}
					Log.d(TAG, "Connected to server");
					if(!ftp.login(username, password)){
						Log.d(TAG, ftp.getReplyString());
						
						h.post(new Runnable() {
							@Override
							public void run() {
								showAlert(ftp.getReplyString());
							}
						});
					}
					// enter passive mode
					ftp.enterLocalPassiveMode();
					
				} catch (IOException e) {
					Log.e(TAG, e.getMessage());
					if (ftp.isConnected()) {
						try {
							ftp.disconnect();
						} catch (IOException f) {
							// do nothing
						}
					}
				}
			}
		};
		new Thread(runnable).start();
	}
	
	private void showAlert(String message){
		// building dialog
		AlertDialog ad = new AlertDialog.Builder(context).create();
		//ad.setTitle("hello");
		ad.setMessage(message);
		ad.show();
	}

	public void getList() {
		Runnable runnable = new Runnable() {
			public void run() {
				try {
					FTPFile[] f = ftp.mlistDir();
					for (FTPFile ftpFile : f) {
						Log.d(TAG, ftpFile.toFormattedString());
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					Log.e(TAG, e.getMessage());
				}
			}
		};
		new Thread(runnable).start();
		
	}
}
