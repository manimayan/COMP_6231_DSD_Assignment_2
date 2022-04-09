package dsd.cms.server;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;
import org.omg.CORBA.ORB;
import dsd.cms.DCMSCorba.DCMSInterfacePOA;
import dsd.cms.model.StudentRecord;
import dsd.cms.model.TeacherRecord;
import dsd.cms.utils.LogWriter;

// TODO: Auto-generated Javadoc
/**
 * The Class DollardServer.
 */
public class DollardServer extends DCMSInterfacePOA {

	/** The main server. */
	MainServer mainServer;

	/** The teacher student records. */
	public ConcurrentHashMap<String, ConcurrentHashMap<String, Object>> teacherStudentRecords;

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -8994808684143682770L;

	/** The log writer. */
	static LogWriter logWriter = new LogWriter();

	/** Object request broker. */
	private ORB orb;

	/**
	 * Instantiates a new dollard server.
	 *
	 */
	protected DollardServer() {
		super();
		this.mainServer = new MainServer();
		this.teacherStudentRecords = this.mainServer.loadData("src/main/resources/DollardData.txt");
	}

	/** The logger. */
	private static Logger logger = Logger.getLogger("Dollard");

	/**
	 * Initializes class level orb object.
	 * 
	 * @param orb_val orb object
	 */
	public void setORB(ORB orb_val) {
		orb = orb_val;
	}

	/**
	 * This is used to connect and retrieve data from server with specified port.
	 *
	 * @param port           port of the server to which communication has to be
	 *                       performed
	 * @param dollardServer2 server object
	 */
	public void serverConnection(int port, DollardServer dollardServer2) {

		/*
		 * DollardServer dollardServer = dollardServer2; Registry registry3 =
		 * LocateRegistry.createRegistry(9993); registry3.bind("Dollard-des-Ormeaux",
		 * dollardServer);
		 */

		logger.info("Dollard-des-Ormeaux Server Started");
		System.out.println(
				"\nDollard-des-Ormeaux server is loaded with initial data. Map size : " + teacherStudentRecords.size());

		logWriter.serverInfo(LogWriter.DOLLARD, LogWriter.SYSTEM, LogWriter.PHASE_STARTUP,
				"Dollard-des-Ormeaux server started");
		logWriter.serverInfo(LogWriter.DOLLARD, LogWriter.SYSTEM, LogWriter.PHASE_STARTUP, "Initial data loaded.");

		while (true) {
			try (DatagramSocket ds = new DatagramSocket(port)) {

				byte[] receive = new byte[65535];
				DatagramPacket dp = new DatagramPacket(receive, receive.length);
				ds.receive(dp);
				byte[] data = dp.getData();
				String serviceName = new String(data);
				String outputStr = "";
				if (serviceName.trim().equalsIgnoreCase("getRecordCounts")) {
					outputStr = "DDO : " + this.mainServer.getRecordCount(this.teacherStudentRecords);
				}
				DatagramPacket dp1 = new DatagramPacket(outputStr.getBytes(), outputStr.length(), dp.getAddress(),
						dp.getPort());
				ds.send(dp1);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Creates the T record.
	 *
	 * @param recordId       the record id
	 * @param firstName      the first name
	 * @param lastName       the last name
	 * @param address        the address
	 * @param phone          the phone
	 * @param specialization the specialization
	 * @param location       the location
	 * @param clientId       the client id
	 * @param transferStatus the transfer status
	 * @return the string
	 */
	@Override
	public synchronized String createTRecord(String recordId, String firstName, String lastName, String address,
			String phone, String specialization, String location, String clientId, boolean transferStatus) {

		String resultMsg = null;
		String outputType = (transferStatus) ? " transferred to " : " created in ";

		logWriter.serverInfo(LogWriter.DOLLARD, clientId, LogWriter.PHASE_REQUEST,
				"Teacher record creation requested with : (First Name : " + firstName + ", Last Name : " + lastName
						+ ", Address : " + address + ", Phone : " + phone + ", Specialization : " + specialization
						+ ", Location : " + location + ")");
		if (this.mainServer.findDuplicateRecordInmap(recordId, this.teacherStudentRecords)) {
			logWriter.serverInfo(LogWriter.DOLLARD, clientId, LogWriter.PHASE_REQUEST,
					recordId + "is already existing");
			resultMsg = recordId + "is already existing";
		} else {
			TeacherRecord teacherRecord = new TeacherRecord(recordId, firstName, lastName, address, phone,
					specialization, location);
			resultMsg = this.mainServer.persistTeacherRecordInMap(teacherRecord, this.teacherStudentRecords)
					? "Teacher record " + teacherRecord.getRecordID() + outputType
							+ "Dollard-des-Ormeaux location with name : " + firstName + " " + lastName
					: "Error in creating Teacher Record.";
			logWriter.serverInfo(LogWriter.DOLLARD, clientId, LogWriter.PHASE_RESPONSE, resultMsg);
		}
		return resultMsg;
	}

	/**
	 * Creates the S record.
	 *
	 * @param recordId         the record id
	 * @param firstName        the first name
	 * @param lastName         the last name
	 * @param courseRegistered the course registered
	 * @param status           the status
	 * @param statusDate       the status date
	 * @param clientId         the client id
	 * @param transferStatus   the transfer status
	 * @return the string
	 */
	@Override
	public synchronized String createSRecord(String recordId, String firstName, String lastName,
			String courseRegistered, String status, String statusDate, String clientId, boolean transferStatus) {

		String resultMsg = null;
		String outputType = (transferStatus) ? " transferred to " : " created in ";

		logWriter.serverInfo(LogWriter.DOLLARD, clientId, LogWriter.PHASE_REQUEST,
				"Teacher record creation requested with : (First Name : " + firstName + ", Last Name : " + lastName
						+ ", Courses Registered : " + courseRegistered + ", Status : " + status + ", Status Date : "
						+ statusDate + ")");
		if (this.mainServer.findDuplicateRecordInmap(recordId, this.teacherStudentRecords)) {
			logWriter.serverInfo(LogWriter.DOLLARD, clientId, LogWriter.PHASE_REQUEST,
					recordId + "is already existing");
			resultMsg = recordId + "is already existing";
		} else {
			StudentRecord studentRecord = new StudentRecord(recordId, firstName, lastName, courseRegistered, status,
					statusDate);

			resultMsg = this.mainServer.persistStudentRecordInMap(studentRecord, this.teacherStudentRecords)
					? "Student record " + studentRecord.getRecordID() + outputType
							+ "Dollard-des-Ormeaux location with name : " + firstName + " " + lastName
					: "Error in creating Student Record.";
			logWriter.serverInfo(LogWriter.DOLLARD, clientId, LogWriter.PHASE_RESPONSE, resultMsg);
		}
		return resultMsg;
	}

	/**
	 * Edits the record.
	 *
	 * @param recordID  the record ID
	 * @param fieldName the field name
	 * @param newValue  the new value
	 * @param clientId  the client id
	 * @return the string
	 */
	@Override
	public synchronized String editRecord(String recordID, String fieldName, String newValue, String clientId) {

		logWriter.serverInfo(LogWriter.DOLLARD, clientId, LogWriter.PHASE_REQUEST,
				"Edit Record requested for record with ID : " + recordID + ", Edit Field : " + fieldName
						+ ", New Value : " + newValue);

		String resultMsg = this.mainServer.updateRecordInMap(recordID, fieldName, newValue, this.teacherStudentRecords);

		System.out.println(resultMsg);
		logWriter.serverInfo(LogWriter.DOLLARD, clientId, LogWriter.PHASE_RESPONSE, resultMsg);
		return resultMsg;
	}

	/**
	 * Gets the record counts.
	 *
	 * @param clientId the client id
	 * @return the record counts
	 */
	@Override
	public synchronized String getRecordCounts(String clientId) {

		logWriter.serverInfo(LogWriter.DOLLARD, clientId, LogWriter.PHASE_REQUEST,
				"Request to get record count of teachers and student from all three server locations.");

		String server1 = this.mainServer.datafromOtherServers(8880);
		String server2 = this.mainServer.datafromOtherServers(8881);
		String server3 = "DDO : " + this.mainServer.getRecordCount(this.teacherStudentRecords);

		String resultMsg = (server1 != null && server2 != null && server3 != null)
				? server1 + ", " + server2 + ", " + server3
				: "Error in retrieving record count. Please try again.";

		System.out.println(resultMsg);
		logWriter.serverInfo(LogWriter.DOLLARD, clientId, LogWriter.PHASE_RESPONSE, resultMsg);
		return resultMsg;
	}

	/**
	 * Find record.
	 *
	 * @param recordID the record ID
	 * @return true, if successful
	 */
	@Override
	public String findRecord(String recordID) {
		return this.mainServer.findAndRemoveRecordInmap(recordID, LogWriter.DOLLARD, this.teacherStudentRecords);
	}

	/**
	 * List record ID.
	 *
	 * @return the list
	 */
	@Override
	public String[] listRecordID() {
		List<String> listOfRecordID = new ArrayList<>();
		for (Entry<String, ConcurrentHashMap<String, Object>> entry : teacherStudentRecords.entrySet()) {
			for (Entry<String, Object> innerEntry : entry.getValue().entrySet()) {
				listOfRecordID.add(innerEntry.getKey());
			}
		}
		return listOfRecordID.stream().toArray(String[]::new);
	}
}
