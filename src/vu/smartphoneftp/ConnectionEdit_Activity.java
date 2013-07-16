package vu.smartphoneftp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class ConnectionEdit_Activity extends Activity {
	private String titleIntigtity = "";
	private DbHelper db = new DbHelper(this);
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_conection_edit);
		
		// getting component reference
		final EditText title = (EditText) findViewById(R.id.txtConTitle);
		final EditText host = (EditText) findViewById(R.id.txtConHost);
		final EditText port = (EditText) findViewById(R.id.txtConPort);
		final EditText username = (EditText) findViewById(R.id.txtConUser);
		final EditText password = (EditText) findViewById(R.id.txtConPass);
		Button btnsave = (Button) findViewById(R.id.btnSave);
		Button btnCancel = (Button) findViewById(R.id.btnCancel);
		
		// get connection id from parent activity
		Intent i = getIntent();
		final int id = i.getIntExtra("id", 0);
		// Retrieve connection details from database
		Server server = db.getServer(id);
		titleIntigtity = server.getTitle();
		
		// populate editText with data
		title.setText(titleIntigtity);
		host.setText(server.getHost());
		port.setText(String.valueOf(server.getPort()));
		username.setText(server.getUsername());
		password.setText(server.getPassword());
		
		// Save button click action
		btnsave.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String t = title.getText().toString();
				String h = host.getText().toString();
				String pot = port.getText().toString();
				String user = username.getText().toString();
				String pass = password.getText().toString();
				if(validateFields(id, t, h, pot, user, pass)){
					setResult(RESULT_OK, null);
					finish();
				}
			}
		});
		
		// Cancel button click action
		btnCancel.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				setResult(RESULT_CANCELED, null);
				finish();
				
			}
		});
	}

	private boolean validateFields(int id, String title, String host, String port, String username, String password) {
		if(title.equals("") || host.equals("")){
			showAlert("Connection or host name should not be empty");
			return false;
		}
		
		if(!title.equals(titleIntigtity)){
			// check if connection name exist
			Server s = new Server();
			s.setTitle(title);
			if(db.isExist(s)){
				showAlert("Connection name exist choose another name");
				return false;
			}
		}
		// Setting default value for blank fields
		int port1 = (Integer) (!port.equals("") ? Integer.parseInt(port) : 21);
		username = !username.equals("") ? username : "anonymous";
		password = !password.equals("") ? password : "user@host";
		// update into database
		db.updateServer(new Server(id, title, host, port1, username, password));
		return true;
	}
	
	// show warning dialog
	private void showAlert(String message){
		new AlertDialog.Builder(ConnectionEdit_Activity.this)
		.setTitle(message)
		.setIcon(android.R.drawable.ic_dialog_info)
		.setNegativeButton(android.R.string.ok,	new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog,	int which) {
				// Do nothing
			}
		}).show();
	}
}
