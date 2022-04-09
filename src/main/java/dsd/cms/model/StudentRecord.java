package dsd.cms.model;

import java.io.Serializable;
import java.time.LocalDate;

import lombok.Getter;
import lombok.Setter;

// TODO: Auto-generated Javadoc

/**
 * The Class StudentRecord.
 */

@Getter
@Setter
public class StudentRecord implements Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/** The first name. */
	private String firstName;

	/** The last name. */
	private String lastName;

	/** The courses registered. */
	private String coursesRegistered;

	/** The status. */
	private String status;

	/** The id counter. */
	private static int idCounter = 10000;

	/** The status date. */
	private LocalDate statusDate;

	/** The record ID. */
	private String recordID;

	/**
	 * Instantiates a new student record.
	 *
	 * @param recordID          the record ID
	 * @param firstName         the first name
	 * @param lastName          the last name
	 * @param coursesRegistered the courses registered
	 * @param status            the status
	 * @param statusDate        the status date
	 */
	public StudentRecord(String recordID, String firstName, String lastName, String coursesRegistered, String status,
			String statusDate) {
		this.recordID = recordID.equalsIgnoreCase("AutoCreate") ? "SR" + idCounter++ : recordID;
		this.firstName = firstName;
		this.lastName = lastName;
		this.coursesRegistered = coursesRegistered;
		this.status = status;
		LocalDate localDate = LocalDate.parse(statusDate);
		this.statusDate = localDate;
	}

	/**
	 * Convert to string.
	 *
	 * @param sr the sr
	 * @return the string
	 */
	public String convertToString(StudentRecord sr) {
		return sr.getRecordID() + ":" + sr.getFirstName() + ":" + sr.getLastName() + ":" + sr.getCoursesRegistered()
				+ ":" + sr.getStatus() + ":" + sr.getStatusDate().toString();
	}

}
