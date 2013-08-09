package vu.smartphoneftp;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
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
	private static final int REQUEST_CODE = 2;
	protected List<Items> items;
	Remote r;
	public boolean localMode = true;
	protected String path = "/";
	File f;
	protected ImageButton btnRoot, btnUp;
	
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
		if(path.equals("/")){
			// TODO prompt disconnect and close
			new AlertDialog.Builder(this)
			.setTitle("Disconnect?")
			.setMessage("Would you like to Disconnect and return to connect screen?")
			.setIcon(android.R.drawable.ic_dialog_alert)
			.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO call FTP disconnect method
					finish();
				}
			})
			.setNegativeButton(android.R.string.cancel,	new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog,	int which) {
					// Do nothing
				}
			}).show();
		} else {
			String path = f.getParent();
			showLocalfiles(path);
		}
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		Items currentFile = items.get(position);
		String path = this.path;
		if(!this.path.equals("/"))
			path += "/";
		path += currentFile.getName();
		if (currentFile.isDirectory()) {
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
					items.add(new Items(file.getName(), R.drawable.ic_folder));
				} else {
					items.add(new Items(file.getName(), file.length(), R.drawable.ic_file));
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
		if(seleItem.isDirectory()){
			menu.setHeaderTitle("Folder Action");
		} else {
			menu.setHeaderTitle("File Action");
			menu.add("Upload");
		}
		menu.add("Rename");
		menu.add("Copy");
		menu.add("Cut");
		menu.add("Delete");
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		if(item.getTitle().equals("Cut")){
			Intent i = new Intent("vu.smartphoneftp.SelectDestination_Activity");
			startActivityForResult(i, REQUEST_CODE);
		}
		return super.onContextItemSelected(item);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK && requestCode == REQUEST_CODE) {
			String selectedPath = data.getStringExtra("pastePath");
			// TODO Call methods to perform cut, copy upload download action
			Log.d("FTP", selectedPath);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.file_browser_, menu);
		return true;
	}
}
