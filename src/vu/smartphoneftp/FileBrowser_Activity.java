package vu.smartphoneftp;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class FileBrowser_Activity extends FileBrowserBase {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		setContentView(R.layout.activity_filebrowser);
		
		setTitle("Local files");
		// Get button reference
		Button btnSwitchMode = (Button) findViewById(R.id.btnSwitchMode);
		
		btnSwitchMode.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Switch mode
			}
		});
		
		// Register context menu
		registerForContextMenu(this.getListView());
		super.onCreate(savedInstanceState);
	}
}