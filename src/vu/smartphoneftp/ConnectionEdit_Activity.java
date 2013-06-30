package vu.smartphoneftp;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class ConnectionEdit_Activity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_conection_edit);
		
		// getting component reference
		Button btnsave = (Button) findViewById(R.id.btnSave);
		Button btnCancel = (Button) findViewById(R.id.btnCancel);
		
		// Save button click action
		btnsave.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
			}
		});
		
		// Cancel button click action
		btnCancel.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
				
			}
		});
	}
}
