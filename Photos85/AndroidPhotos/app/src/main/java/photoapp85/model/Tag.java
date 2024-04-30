package photoapp85.model;


import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.Objects;

public class Tag implements Serializable {

	private String name, value;
	private static final long serialVersionUID = 1L;

	public Tag(String name, String value) {
		this.name = name;
		this.value = value;
	}

	protected Tag(Parcel in) {
		name = in.readString();
		value = in.readString();
	}

	public String getName() {
		return name;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {this.value = value; }

	public void setName (String name) {this.name = name; }

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null || getClass() != obj.getClass()) {
			return false;
		}
		Tag other = (Tag) obj;
		return Objects.equals(name, other.name) && Objects.equals(value, other.value);
	}

	@Override
	public int hashCode() {
		return Objects.hash(name, value);
	}

	@Override
	public String toString() {
		return name + " - " + value;
	}

}
