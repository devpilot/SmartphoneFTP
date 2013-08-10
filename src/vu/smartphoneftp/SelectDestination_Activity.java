package vu.smartphoneftp;

import java.io.File;
import java.util.ArrayList;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class SelectDestination_Activity extends FileBrowserBase{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_select_destination);
		
		Button btnPaste = (Button) findViewById(R.id.btnPaste);
		Button btnCancel = (Button) findViewById(R.id.btnCancel);
		
		CharSequence ctxAction = "";
		// Get passed intent data
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
		    ctxAction = extras.getCharSequence("ctxAction");
		}
		
		setTitle(ctxAction +" to");
		// set positive button text
		btnPaste.setText(ctxAction);
		
		btnPaste.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent i = new Intent();
				i.putExtra("pastePath", path);
				setResult(RESULT_OK, i);
				finish();
			}
		});
		
		btnCancel.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				setResult(RESULT_CANCELED);
				finish();				
			}
		});
		super.onCreate(savedInstanceState);
		// For cut/ copy mode is =
		if(ctxAction.equals("Upload") || ctxAction.equals("Download")){
			isRemoteMode = !FileBrowser_Activity.shareMode;
		} else {
			isRemoteMode = FileBrowser_Activity.shareMode;
		}
		if(isRemoteMode){
			remote.showRemoteFiles(null);
		} else {
			showLocalfiles(path);
		}
	}
	
	@Override
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
				}
			}
			// display files in listview
			loadFileList(items);
		} else {
			Toast.makeText(this, "Can't read this directory", Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	public void onBackPressed() {
		if(path.equals("/")){
			setResult(RESULT_CANCELED);
			finish();
		}
		super.onBackPressed();
	}
}
