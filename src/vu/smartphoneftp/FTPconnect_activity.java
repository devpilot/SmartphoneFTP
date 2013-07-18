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
import android.widget.Toast;

public class FTPconnect_activity extends Activity {

	private static final int REQUEST_CODE = 1;
	private EditText host, port, username, password;
	private Spinner s;
	private final DbHelper db = new DbHelper(this);
	private int selectedConnectionId;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_ftpconnect);

		// view component reference
		s = (Spinner) findViewById(R.id.selectCon);
		host = (EditText) findViewById(R.id.txtConHost);
		port = (EditText) findViewById(R.id.txtConPort);
		username = (EditText) findViewById(R.id.txtConUser);
		password = (EditText) findViewById(R.id.txtConPass);
		Button btnConnect = (Button) findViewById(R.id.btnConnect);
		final Button btnSave = (Button) findViewById(R.id.btnSave);
		final Button btnEdit = (Button) findViewById(R.id.btnEdit);
		final Button btnDelete = (Button) findViewById(R.id.btnDelete);
		final View accDetails = (LinearLayout) findViewById(R.id.accDetails);
		// load account list in spinner
		updateServerList();

		// Spinner item select listener
		s.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int pos, long id) {
				// return selected server id
				selectedConnectionId = ((Server) parent.getItemAtPosition(pos)).get_id();
				// toggle components according to item selected
				if (selectedConnectionId == 0) {
					// while quick connect is selected
					accDetails.setVisibility(View.VISIBLE);
					btnDelete.setEnabled(false);
					btnEdit.setEnabled(false);
					btnSave.setEnabled(true);
				} else {
					// while saved connection is selected
					accDetails.setVisibility(View.GONE);
					btnDelete.setEnabled(true);
					btnEdit.setEnabled(true);
					btnSave.setEnabled(false);
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0){
				// Do nothing
			}

		});

		// Connect button click
		btnConnect.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if(selectedConnectionId == 0){
					// get values from editText
					String h = host.getText().toString();
					// Host should not be empty
					if(!h.equals("")){
						String pot = port.getText().toString();
						String user = username.getText().toString();
						String pass = password.getText().toString();
						// Setting default value for blank fields
						int port1 = (Integer) (!pot.equals("") ? Integer.parseInt(pot) : 21);
						user = !user.equals("") ? user : "anonymous";
						pass = !pass.equals("") || !user.equals("anonymous") ? pass : "user@host";
						// pass value to connect method
						Server server = new Server("", h, port1, user, pass);
						Remote remote = new Remote(FTPconnect_activity.this);
						remote.connect(server);
					} else {
						showAlert("Host should not be empty");
					}
				} else {
					// get values from database
					Server server = db.getServer(selectedConnectionId);
					Remote remote = new Remote(FTPconnect_activity.this);
					remote.connect(server);
				}
			}
		});

		// Save button click
		btnSave.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				final String h = host.getText().toString();
				if(!h.equals("")){
					final EditText input = new EditText(FTPconnect_activity.this);
					new AlertDialog.Builder(FTPconnect_activity.this)
							.setTitle("Connection name")
							.setView(input)
							.setPositiveButton("Save", new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,	int which) {
									String t = input.getText().toString();
									if(!t.equals("")){
										String pot = port.getText().toString();
										String user = username.getText().toString();
										String pass = password.getText().toString();
										//
										validateFields(t, h, pot, user, pass);
									} else {
										showAlert("Connection name shouldn't be blank");
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
				} else {
					showAlert("Host name shouldn't be blank");
				}
			}
		});

		// Edit button Click
		btnEdit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent i = new Intent("vu.smartphoneftp.ConnectionEdit_Activity");
				// Pass selected connection id
				i.putExtra("id", selectedConnectionId);
				startActivityForResult(i, REQUEST_CODE);

			}
		});

		// Delete button click
		btnDelete.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				new AlertDialog.Builder(FTPconnect_activity.this)
				.setTitle("Do you want to delete "+s.getSelectedItem().toString()+" ?")
				.setIcon(android.R.drawable.ic_dialog_alert)
				.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						Server s = new Server();
						s.set_id(selectedConnectionId);
						db.deleteServer(s);
						//update spinner
						updateServerList();
						// show delete success message
						Toast.makeText(getApplicationContext(), "Connection deleted", Toast.LENGTH_SHORT).show();
					}
				})
				.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener(){
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// Do nothing
					}
				})
				.show();
			}
		});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		 if (resultCode == RESULT_OK && requestCode == REQUEST_CODE) {
			// update new info to spinner
				updateServerList();
				Toast.makeText(this, "Connection Updated", Toast.LENGTH_SHORT).show();
		 }
		super.onActivityResult(requestCode, resultCode, data);
	}

	private void updateServerList() {
		// Get server list from DB
		List<Server> servList = db.getServers();
		// Initialize adapter
		ArrayAdapter<Server> adapter = new ArrayAdapter<Server>(this,
				android.R.layout.simple_spinner_item, servList);
		// Inserting quick connect option
		Server serv = new Server();
		serv.set_id(0);
		serv.setTitle("Quick connect");
		adapter.insert(serv, 0);
		// Assign adapter
		s.setAdapter(adapter);
	}

	private void validateFields(String title, String host, String port, String username, String password) {
		// check if connection name exist
		Server s = new Server();
		s.setTitle(title);
		if(db.isExist(s)){
			showAlert("Connection name exist choose another name");
		} else {
			// Setting default value for blank fields
			int port1 = (Integer) (!port.equals("") ? Integer.parseInt(port) : 21);
			username = !username.equals("") ? username : "anonymous";
			password = !password.equals("") || !username.equals("anonymous") ? password : "user@host";
			// Insert into database
			db.addServer(new Server(title, host, port1, username, password));
			// update new info to spinner
			updateServerList();
			Toast.makeText(this, "Connection saved", Toast.LENGTH_SHORT).show();
		}
	}
	
	// show warning dialog
	private void showAlert(String message){
		new AlertDialog.Builder(FTPconnect_activity.this)
		.setTitle(message)
		.setIcon(android.R.drawable.ic_dialog_info)
		.setNegativeButton(android.R.string.ok,	new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog,	int which) {
				// Do nothing
			}
		}).show();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.ftpconn_activity, menu);
		return true;
	}
}
