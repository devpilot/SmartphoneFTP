package vu.smartphoneftp;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.ListActivity;
import android.os.Bundle;
import android.os.Environment;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

public class FileBrowser_Activity extends ListActivity {
	private List<Items> items;
	Remote r;
	public boolean localMode = true;
	String path = "/";
	File f;
	private ImageButton btnRoot, btnUp;
	
	@SuppressLint("SdCardPath")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_filebrowser);
		setTitle("Local files");
		// Get button reference
		btnUp = (ImageButton) findViewById(R.id.btnUp);
		btnRoot = (ImageButton) findViewById(R.id.btnRoot);
		
//		r  = new Remote(this);
//		r.getList("/");
		
		String state = Environment.getExternalStorageState();
		if (Environment.MEDIA_MOUNTED.equals(state)) {
			path = "/sdcard";
		}
		showLocalfiles(path);
		
		btnUp.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String path = f.getParent();
				showLocalfiles(path);
			}
		});
		
		// Go to filesystem root
		btnRoot.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				path = "/";
				showLocalfiles(path);
			}
		});
		
		// Register context menu
		registerForContextMenu(this.getListView());
	}
	
	@Override
	public void onBackPressed() {
//		super.onBackPressed();
		if(!path.equals("/")){
			String path = f.getParent();
			showLocalfiles(path);
		}
		// TODO prompt disconnect and close
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		Items currentFile = items.get(position);
		String path = this.path;
		if(!this.path.equals("/"))
			path += "/";
		path += currentFile.getName();
		File f = new File(path);
		if (f.isDirectory()) {
			showLocalfiles(path);
		}
	}
	
	public void showLocalfiles(String path) {
		f = new File(path);
		if(f.canRead()){
			this.path = path;
			toggleUpbtn(path);
			File[] files = f.listFiles();
			items = new ArrayList<Items>();
			for (File file : files) {
				if(file.isDirectory()){
					items.add(new Items(file.getName(), "", R.drawable.ic_folder));
				} else {
					items.add(new Items(file.getName(), Items.humanReadableByteCount(file.length(), true), R.drawable.ic_file));
				}
			}
			// display files in listview
			loadFileList(items);
		} else {
			Toast.makeText(this, "Can't read this directory", Toast.LENGTH_SHORT).show();
		}
	}
	
	/**
	 * Disable up button for file root
	 * @param path String
	 */
	public void toggleUpbtn(String path) {
		if(path.equals("/")){
			btnUp.setEnabled(false);
			btnRoot.setEnabled(false);
		} else {
			btnUp.setEnabled(true);
			btnRoot.setEnabled(true);
		}
	}
	
	public void loadFileList(List<Items> items){
		this.items = items;
		FileAdepter adapter = new FileAdepter(this, items);
		// Assign adapter to ListView
		setListAdapter(adapter);
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) menuInfo;
        int position = info.position;
        Items seleItem = items.get(position);
        // TODO check file or directory
		seleItem.getName();
		// Add context title
		menu.setHeaderTitle("File Action");
		menu.add("Download");
		menu.add("Rename");
		menu.add("Move");
		menu.add("Copy");
		menu.add("Delete");
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		return super.onContextItemSelected(item);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.file_browser_, menu);
		return true;
	}
}
