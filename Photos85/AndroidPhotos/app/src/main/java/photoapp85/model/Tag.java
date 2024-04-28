package photoapp85.model;


import android.os.Parcel;
import android.os.Parcelable;

import java.util.Objects;

public class Tag implements Parcelable {

	private String name, value;

	public Tag(String name, String value) {
		this.name = name;
		this.value = value;
	}

	protected Tag(Parcel in) {
		name = in.readString();
		value = in.readString();
	}

	public static final Creator<Tag> CREATOR = new Creator<Tag>() {
		@Override
		public Tag createFromParcel(Parcel in) {
			return new Tag(in);
		}

		@Override
		public Tag[] newArray(int size) {
			return new Tag[size];
		}
	};

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

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(name);
		dest.writeString(value);
	}

	@Override
	public int describeContents() {
		return 0;
	}
}
