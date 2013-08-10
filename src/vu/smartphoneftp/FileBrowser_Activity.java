package vu.smartphoneftp;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class FileBrowser_Activity extends FileBrowserBase {
	
	private Button btnSwitchMode;
	public static boolean shareMode;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_filebrowser);
		// Get button reference
		btnSwitchMode = (Button) findViewById(R.id.btnSwitchMode);
		btnSwitchMode.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				isRemoteMode = !isRemoteMode;
				changeModeText();
			}
		});
		
		// Register context menu
		registerForContextMenu(this.getListView());
		
		super.onCreate(savedInstanceState);
		// load default mode
		changeModeText();
	}
	
	private void changeModeText() {
		if(isRemoteMode){
			setTitle("Remote directory");
			btnSwitchMode.setText("Browse Device");
			remote.showRemoteFiles(null);
		} else {
			setTitle("Device directory");
			btnSwitchMode.setText("Browse Server");
			showLocalfiles(path);
		}
		shareMode = isRemoteMode;
	}
}