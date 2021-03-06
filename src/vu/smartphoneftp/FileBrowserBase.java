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
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

public class FileBrowserBase extends ListActivity {
	private static final int REQUEST_CODE = 2;
	protected List<Items> items;
	protected boolean isRemoteMode = true;
	protected String path = "/";
	protected ImageButton btnRoot, btnUp;
	protected File f;
	protected final Remote remote = new Remote(this);
	private Items seleItem;
	private CharSequence ctxAction;
	private String tempPath;

	@SuppressLint("SdCardPath")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Get button reference
		btnUp = (ImageButton) findViewById(R.id.btnUp);
		btnRoot = (ImageButton) findViewById(R.id.btnRoot);

		// if(isRemoteMode){
		// // load remote root directory in listview
		// remote.showRemoteFiles(null);
		// } else {
		// // Check if sdcard exist
		// String state = Environment.getExternalStorageState();
		// if (Environment.MEDIA_MOUNTED.equals(state)) {
		// path = "/sdcard";
		// }
		// showLocalfiles(path);
		// }

		btnUp.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (isRemoteMode) {
					remote.getParent();
				} else {
					String path = f.getParent();
					showLocalfiles(path);
				}
			}
		});

		// Go to filesystem root
		btnRoot.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (isRemoteMode) {
					remote.getRootDir();
				} else {
					path = "/";
					showLocalfiles(path);
				}
			}
		});
	}

	@Override
	public void onBackPressed() {
		String backPath;
		if (isRemoteMode) {
			backPath = remote.ftpWorkingDirectory;
		} else {
			backPath = path;
		}
		if (backPath.equals("/")) {
			disconnectDialog();
		} else {
			if (isRemoteMode) {
				remote.getParent();
			} else {
				String path = f.getParent();
				showLocalfiles(path);
			}
		}
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		Items currentFile = items.get(position);
		if (currentFile.isDirectory()) {
			if (isRemoteMode) {
				remote.showRemoteFiles(currentFile.getName());
			} else {
				String path = this.path;
				if (!this.path.equals("/"))
					path += "/";
				path += currentFile.getName();
				showLocalfiles(path);
			}
		}
	}

	public void showLocalfiles(String path) {
		f = new File(path);
		if (f.canRead()) {
			this.path = path;
			toggleUpbtn(path);
			File[] files = f.listFiles();
			items = new ArrayList<Items>();
			for (File file : files) {
				if (file.isDirectory()) {
					items.add(new Items(file.getName(), R.drawable.ic_folder));
				} else {
					items.add(new Items(file.getName(), file.length(),
							R.drawable.ic_file));
				}
			}
			// display files in listview
			loadFileList(items);
		} else {
			Toast.makeText(this, "Can't read this directory",
					Toast.LENGTH_SHORT).show();
		}
	}

	/**
	 * Disable up button for file root
	 * 
	 * @param path
	 *            String
	 */
	public void toggleUpbtn(String path) {
		if (path.equals("/")) {
			btnUp.setEnabled(false);
			btnRoot.setEnabled(false);
		} else {
			btnUp.setEnabled(true);
			btnRoot.setEnabled(true);
		}
	}

	public void loadFileList(List<Items> items) {
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
		seleItem = items.get(position);
		if (seleItem.isDirectory()) {
			menu.setHeaderTitle("Folder Action");
		} else {
			menu.setHeaderTitle("File Action");
			if (isRemoteMode) {
				menu.add("Download");
			} else {
				menu.add("Upload");
			}
		}
		menu.add("Rename");
		menu.add("Cut");
		menu.add("Copy");
		menu.add("Delete");
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		ctxAction = item.getTitle();
		if (ctxAction.equals("Download")) {
			destinationDialog(ctxAction);
		} else if (ctxAction.equals("Upload")) {
			destinationDialog(ctxAction);
		} else if (ctxAction.equals("Rename")) {
			final EditText input = new EditText(FileBrowserBase.this);
			input.setText(seleItem.getName());
			new AlertDialog.Builder(FileBrowserBase.this)
					.setTitle("Rename")
					.setView(input)
					.setPositiveButton(android.R.string.ok,
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									String t = input.getText().toString();
									if (!t.equals("")) {
										if (isRemoteMode) {
											remote.rename(seleItem.getName(), t);
										} else {
											String p = path;
											if (!path.equals("/"))
												p += "/";
											File from = new File(p
													+ seleItem.getName());
											File to = new File(p + t);
											if (from.renameTo(to))
												// reload list view
												showLocalfiles(p);
										}
									}
								}
							})
					.setNegativeButton(android.R.string.cancel,
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									// Canceled
								}
							}).show();
		} else if (ctxAction.equals("Cut")) {
			tempPath = remote.ftpWorkingDirectory;
			destinationDialog(ctxAction);
		} else if (ctxAction.equals("Copy")) {
			// TODO Copy call
		} else {
			new AlertDialog.Builder(FileBrowserBase.this)
					.setTitle(
							"Do you want to delete " + seleItem.getName()
									+ " ?")
					.setIcon(android.R.drawable.ic_dialog_alert)
					.setPositiveButton(android.R.string.yes,
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									if (isRemoteMode) {
										remote.delete(seleItem.getName());
										// remote.showRemoteFiles(path);
									} else {
										String p = path;
										if (!path.equals("/"))
											p += "/";
										File file = new File(p + "/"
												+ seleItem.getName());
										file.delete();
										// show delete success message
										Toast.makeText(getApplicationContext(),
												"File Deleted!",
												Toast.LENGTH_SHORT).show();

										showLocalfiles(path);
									}
								}
							})
					.setNegativeButton("Cancel",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									// Canceled
								}
							}).show();
		}
		return super.onContextItemSelected(item);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK && requestCode == REQUEST_CODE) {
			String selectedPath = data.getStringExtra("pastePath");
			if (ctxAction.equals("Download")) {
				remote.download(remote.ftpWorkingDirectory + "/" +seleItem.getName(),selectedPath + "/" + seleItem.getName());
			} else if (ctxAction.equals("Upload")) {
				remote.upload(selectedPath + "/" + seleItem.getName(), path + "/" + seleItem.getName());
			} else if (ctxAction.equals("Cut")) {
				remote.move(tempPath + "/" + seleItem.getName(), selectedPath + "/" + seleItem.getName());
			} else if (ctxAction.equals("Copy")) {
				// TODO Copy call
			}
			Log.d("FTP", selectedPath + "/" + seleItem.getName());
		}
	}

	private void destinationDialog(CharSequence ctxAction) {
		Intent i = new Intent("vu.smartphoneftp.SelectDestination_Activity");
		i.putExtra("ctxAction", ctxAction);
		startActivityForResult(i, REQUEST_CODE);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.file_browser, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		if(item.getItemId() == R.id.action_disconnect)
			disconnectDialog();
		return super.onOptionsItemSelected(item);
	}
	
	private void disconnectDialog() {
		new AlertDialog.Builder(this)
		.setTitle("Disconnect?")
		.setMessage(
				"Would you like to Disconnect and return to connect screen?")
		.setIcon(android.R.drawable.ic_dialog_alert)
		.setPositiveButton(android.R.string.yes,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog,
							int which) {
						remote.disconnect();
						finish();
					}
				})
		.setNegativeButton(android.R.string.cancel,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog,
							int which) {
						// Do nothing
					}
				}).show();
	}
}
