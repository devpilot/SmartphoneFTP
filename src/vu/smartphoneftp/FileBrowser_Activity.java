package vu.smartphoneftp;

import java.util.List;

import android.app.ListActivity;
import android.os.Bundle;
import android.view.Menu;

public class FileBrowser_Activity extends ListActivity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_filebrowser);
		
		Remote r  = new Remote(this);
		r.getList("/");		
	}
	
	public void loadFileList(List<Items> i){
		FileAdepter adapter = new FileAdepter(this, i);
		// Assign adapter to ListView
		setListAdapter(adapter);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.file_browser_, menu);
		return true;
	}
}
