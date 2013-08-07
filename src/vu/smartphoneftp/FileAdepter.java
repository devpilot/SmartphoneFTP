package vu.smartphoneftp;

import java.util.List;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class FileAdepter extends ArrayAdapter<Items> {
	private final Activity context;
	private final List<Items> values;

	static class ViewHolder {
		public TextView name, fileSize;
		public ImageView image;
	}

	/**
	 * Custom listview ArrayAdapter
	 * @param context
	 * @param i List<Items>
	 */
	public FileAdepter(Activity context, List<Items> i) {
		super(context, R.layout.rowlayout,i);
		this.context = context;
		this.values = i;
	}

	@Override
	  public View getView(int position, View convertView, ViewGroup parent) {
	    View rowView = convertView;
	    if (rowView == null) {
	      LayoutInflater inflater = context.getLayoutInflater();
	      rowView = inflater.inflate(R.layout.rowlayout, null);
	      ViewHolder viewHolder = new ViewHolder();
	      viewHolder.name = (TextView) rowView.findViewById(R.id.textName);
	      viewHolder.fileSize = (TextView) rowView.findViewById(R.id.textProp);
	      viewHolder.image = (ImageView) rowView.findViewById(R.id.imageIcon);
	      rowView.setTag(viewHolder);
	    }

	    ViewHolder holder = (ViewHolder) rowView.getTag();
	    Items s =  values.get(position);
	    holder.image.setImageResource(s.getIcon());
	    holder.name.setText(s.getName());
	    holder.fileSize.setText(s.getfileSize());
	    return rowView;
	  }
}
