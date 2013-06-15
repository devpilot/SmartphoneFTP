package vu.smartphoneftp;

import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

public class FTPconn_activity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_ftpconn);

		DbHelper db = new DbHelper(this);
		
		// Get server list from DB
		List<Server> servList = db.getServers();
		
		// Initialize adapter
		ArrayAdapter<Server> adapter = new ArrayAdapter<Server>(this,
				android.R.layout.simple_spinner_item, servList);
		// Spinner reference
		Spinner s = (Spinner) findViewById(R.id.selectCon);
		// Assign adapter
		s.setAdapter(adapter);
		// Spinner item select listener
		s.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int pos, long id) {
				int itemId = ((Server) parent.getItemAtPosition(pos)).get_id();
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub

			}

		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.ftpconn_activity, menu);
		return true;
	}
}
