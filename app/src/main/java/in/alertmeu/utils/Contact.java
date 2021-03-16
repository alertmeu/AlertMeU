package in.alertmeu.utils;

import java.util.ArrayList;

public class Contact {
	public String id;
	public String name;
	public ArrayList<ContactEmail> emails;
	public ArrayList<ContactPhone> numbers;
	String checked_status;
	private boolean isSelected = false;
	public Contact(String id, String name) {
		this.id = id;
		this.name = name;
		this.emails = new ArrayList<ContactEmail>();
		this.numbers = new ArrayList<ContactPhone>();
	}

	@Override
	public String toString() {
		String result = name;
		if (numbers.size() > 0) {
			ContactPhone number = numbers.get(0);
			result += " (" + number.number + " - " + number.type + ")";
		}
		if (emails.size() > 0) {
			ContactEmail email = emails.get(0);
			result += " [" + email.address + " - " + email.type + "]";
		}
		return result;
	}

	public void addEmail(String address, String type) {
		emails.add(new ContactEmail(address, type));
	}

	public void addNumber(String number, String type) {
		numbers.add(new ContactPhone(number, type));
	}

	public String getChecked_status() {
		return checked_status;
	}

	public void setChecked_status(String checked_status) {
		this.checked_status = checked_status;
	}

	public boolean isSelected() {
		return isSelected;
	}

	public void setSelected(boolean selected) {
		isSelected = selected;
	}
}
