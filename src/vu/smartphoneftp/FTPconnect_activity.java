package vu.smartphoneftp;

import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;

public class FTPconnect_activity extends Activity {

	private EditText title, host, port, username, password;
	private Spinner s;
	private final DbHelper db = new DbHelper(this);

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_ftpconnect);

		// view component reference
		s = (Spinner) findViewById(R.id.selectCon);
		title = (EditText) findViewById(R.id.txtConHost);
//		host = (EditText) findViewById(R.id.txtConHost);
		port = (EditText) findViewById(R.id.txtConPort);
		username = (EditText) findViewById(R.id.txtConUser);
		password = (EditText) findViewById(R.id.txtConPass);
		Button btnConnect = (Button) findViewById(R.id.btnConnect);
		Button btnSave = (Button) findViewById(R.id.btnSave);
		Button btnEdit = (Button) findViewById(R.id.btnEdit);
		Button btnDelete = (Button) findViewById(R.id.btnDelete);
		View accDetails = (LinearLayout) findViewById(R.id.accDetails);
		
		// load account list in spinner
		updateServerList();

		// Spinner item select listener
		s.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int pos, long id) {
				// return selected server id
				int itemId = ((Server) parent.getItemAtPosition(pos)).get_id();
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub

			}

		});
		
		// Connect button click
		btnConnect.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
			}
		});
		
		// Save button click
		btnSave.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				final EditText input = new EditText(FTPconnect_activity.this);
				new AlertDialog.Builder(FTPconnect_activity.this)
				.setTitle("Connection name")
				.setView(input)
				.setPositiveButton("Save", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO call function to save details in database
						if(input.getText().toString() != ""){
							
						}
					}
				})
				.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// Canceled
						
					}
				})
				.show();
			}
		});
		
		// Edit button Click
		btnEdit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent i = new Intent(
						"vu.smartphoneftp.ConnectionEdit_Activity");
				startActivityForResult(i, 1);

			}
		});
		
		// Delete button click
		btnDelete.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
			}
		});
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
	}
	
	private void updateServerList() {
		// Get server list from DB
		List<Server> servList = db.getServers();
		// Initialize adapter
		ArrayAdapter<Server> adapter = new ArrayAdapter<Server>(this,
				android.R.layout.simple_spinner_item, servList);
		// Assign adapter
		s.setAdapter(adapter);
	}

	private void validateFields() {
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.ftpconn_activity, menu);
		return true;
	}
}
