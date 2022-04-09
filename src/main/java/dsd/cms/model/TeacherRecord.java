package dsd.cms.model;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

// TODO: Auto-generated Javadoc

/**
 * The Class TeacherRecord.
 */

@Getter
@Setter
public class TeacherRecord implements Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/** The first name. */
	private String firstName;

	/** The last name. */
	private String lastName;

	/** The address. */
	private String address;

	/** The phone. */
	private String phone;

	/** The specialization. */
	private String specialization;

	/** The location. */
	private String location;

	/** The id counter. */
	private static int idCounter = 10000;

	/** The record ID. */
	private String recordID;

	/**
	 * Instantiates a new teacher record.
	 *
	 * @param recordID      the record ID
	 * @param firstName     the first name
	 * @param lastName      the last name
	 * @param address       the address
	 * @param phone         the phone
	 * @param specialization the specialization
	 * @param location      the location
	 */
	public TeacherRecord(String recordID, String firstName, String lastName, String address, String phone,
			String specialization, String location) {

		this.recordID = recordID.equalsIgnoreCase("AutoCreate") ? "TR" + idCounter++ : recordID;
		this.firstName = firstName;
		this.lastName = lastName;
		this.address = address;
		this.phone = phone;
		this.specialization = specialization;
		this.location = location;
	}

	/**
	 * Convert to string.
	 *
	 * @param tr the tr
	 * @return the string
	 */
	public String convertToString(TeacherRecord tr) {
		return tr.getRecordID() + ":" + tr.getFirstName() + ":" + tr.getLastName() + ":" + tr.getAddress() + ":"
				+ tr.getPhone() + ":" + tr.getSpecialization() + ":" + tr.getLocation();
	}
}
