package vu.smartphoneftp;

import android.annotation.SuppressLint;

public class Items {
	private String name;
	private long fileSize;
	private int icon;
	private boolean isDirectory;

	public Items() {
	}
	
	/**
	 * Constructor for directory item
	 * @param name
	 * @param icon
	 */
	public Items(String name, int icon) {
		this.name = name;
		this.icon = icon;
		this.isDirectory = true;
	}
	
	/**
	 * Constructor for file item
	 * @param name
	 * @param fileSize
	 * @param icon
	 */
	public Items(String name, long fileSize, int icon) {
		this.name = name;
		this.fileSize = fileSize;
		this.icon = icon;
		this.isDirectory = false;
	}

	public String getName() {
		return name;
	}

	public String getfileSize() {
		if(isDirectory)
			return "";
		return humanReadableByteCount(fileSize, true);
	}

	public int getIcon() {
		return icon;
	}

	public boolean isDirectory() {
		return isDirectory;
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
