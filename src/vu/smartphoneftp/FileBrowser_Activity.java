package vu.smartphoneftp;

import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.widget.ListView;

public class FileBrowser_Activity extends Activity {
	
	private ListView fl;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_filebrowser);
		
		Remote r  = new Remote(this);
		r.getList();
		
		fl = (ListView) findViewById(R.id.fileList);
	}
	public void loadFileList(List<Items> i){
		/*String[] values = new String[] { "Android", "iPhone", "WindowsMobile",
				"Blackberry", "WebOS", "Ubuntu", "Solaris", "Windows7",
				"Max OS X", "Linux", "OS/2", "Symbian", "Java", "Nokia",
				"apple" };*/
		FileAdepter adapter = new FileAdepter(this, i);
		// Assign adapter to ListView
		fl.setAdapter(adapter);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.file_browser_, menu);
		return true;
	}
}
