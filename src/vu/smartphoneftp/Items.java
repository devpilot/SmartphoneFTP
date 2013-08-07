package vu.smartphoneftp;

import android.annotation.SuppressLint;

public class Items {
	private String name, fileSize;
	private int icon;

	public Items() {
	}

	public Items(String name, String fileSize, int icon) {
		this.name = name;
		this.fileSize = fileSize;
		this.icon = icon;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getfileSize() {
		return fileSize;
	}

	public void setfileSize(String fileSize) {
		this.fileSize = fileSize;
	}

	public int getIcon() {
		return icon;
	}

	public void setIcon(int icon) {
		this.icon = icon;
	}
	
	@SuppressLint("DefaultLocale")
	public static String humanReadableByteCount(long bytes, boolean si) {
	    int unit = si ? 1000 : 1024;
	    if (bytes < unit) return bytes + " B";
	    int exp = (int) (Math.log(bytes) / Math.log(unit));
	    String pre = (si ? "kMGTPE" : "KMGTPE").charAt(exp-1) + (si ? "" : "i");
	    return String.format("%.1f %sB", bytes / Math.pow(unit, exp), pre);
	}

}
