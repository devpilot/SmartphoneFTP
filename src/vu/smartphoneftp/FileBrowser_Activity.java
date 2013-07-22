package vu.smartphoneftp;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;

public class FileBrowser_Activity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_filebrowser);
		
		Remote r  = new Remote(this);
		r.getList();
//		ListView fl = (ListView) findViewById(R.id.fileList);
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.file_browser_, menu);
		return true;
	}
}
