package vu.smartphoneftp;

public class Items {
	private String name, properties;
	private int icon;

	public Items() {
	}

	public Items(String name, String properties, int icon) {
		this.name = name;
		this.properties = properties;
		this.icon = icon;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getProperties() {
		return properties;
	}

	public void setProperties(String properties) {
		this.properties = properties;
	}

	public int getIcon() {
		return icon;
	}

	public void setIcon(int icon) {
		this.icon = icon;
	}

}
