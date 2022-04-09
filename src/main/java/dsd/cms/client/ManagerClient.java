package dsd.cms.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.rmi.NotBoundException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.omg.CORBA.ORB;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;
import org.omg.CosNaming.NamingContextPackage.CannotProceed;
import org.omg.CosNaming.NamingContextPackage.InvalidName;
import org.omg.CosNaming.NamingContextPackage.NotFound;

import dsd.cms.DCMSCorba.DCMSInterface;
import dsd.cms.DCMSCorba.DCMSInterfaceHelper;
import dsd.cms.utils.LogWriter;
import dsd.cms.utils.Validator;

// TODO: Auto-generated Javadoc
/**
 * The Class ManagerClient.
 */
public class ManagerClient {

	/** The Constant TRIM_INPUT. */
	static final String TRANSFER_TRIM_INPUT = "\\s*:\\s*";

	/** The buffer reader. */
	static BufferedReader bufferReader;

	/** The server instance. */
	static DCMSInterface serverInstance;

	/** The validate. */
	static Validator validate = new Validator();

	/** The log. */
	static LogWriter log = new LogWriter();

	/** Maing Context ref for getting server instance. */
	static NamingContextExt ncRef;

	/**
	 * main method to run the client side of Class Management System.
	 *
	 * @param args the arguments
	 */
	public static void main(String[] args) {
		try {
			bufferReader = new BufferedReader(new InputStreamReader(System.in));

			ORB orb = ORB.init(args, null);
			org.omg.CORBA.Object referenceObject = orb.resolve_initial_references("NameService");
			ncRef = NamingContextExtHelper.narrow(referenceObject);

			while (true) {
				System.out.println("\nDistributed Class Management System\n===================================\n"
						+ "\nCreate an User to interact with the system" + "\n Format: MTL**** or LVL**** or DDO****"
						+ "\n\t *****:numbers\n" + "\nEnter an User Name:");
				String userName = bufferReader.readLine().trim();
				if (userName != null && validate.userName(userName)) {
					String managerPrefix = userName.substring(0, 3).toUpperCase();
					String choice;
					Matcher matcher = null;
					do {
						System.out.println(
								"\nChoose any of the options below to access the system\n1 : Create a Teacher Record\n2 : Create a Student Record"
										+ "\n3 : Edit a Record\n4 : Get Record Count\n5 : Transfer Record\nC : To create new user\nSelect : ");
						choice = bufferReader.readLine().trim().toUpperCase();
						log.clientInfo(userName, LogWriter.ACCESS_SYSTEM, choice);
						switch (choice) {
						case "1":
							// create teacher record
							log.clientInfo(userName, LogWriter.PHASE_TR, "selected Teacher Record");
							createTeacherRecord(userName, managerPrefix, bufferReader);
							break;
						case "2":
							// create student record
							log.clientInfo(userName, LogWriter.PHASE_SR, "selected Student Record");
							createStudentRecord(userName, managerPrefix, bufferReader);
							break;
						case "3":
							// edit a record
							log.clientInfo(userName, LogWriter.PHASE_ER, "selected Edit Record");
							editRecord(userName, managerPrefix, bufferReader);
							break;
						case "4":
							// get record count
							log.clientInfo(userName, LogWriter.PHASE_GRC, "selected getRecordCount");
							createServerInstance(managerPrefix);
							String recordCount = serverInstance.getRecordCounts(userName);
							System.out.println(recordCount);
							log.clientInfo(userName, LogWriter.PHASE_GRC, recordCount);
							break;
						case "5":
							// transfer record
							log.clientInfo(userName, LogWriter.PHASE_TRFR, "selected transferRecord");
							transferRecord(userName, managerPrefix, bufferReader);
							break;
						case "C":
							break;
						default:
							System.out.println("Invalid Choice");
							log.clientInfo(userName, LogWriter.ACCESS_SYSTEM, "Invalid Choice");
							continue;
						}
						Pattern pattern = Pattern.compile("^[1-2-3-4]{1}");
						matcher = pattern.matcher(choice);
					} while (matcher != null && matcher.matches() && !choice.equalsIgnoreCase("C"));
				} else {
					System.out.println("===== user name is invalid =====");
				}
			}
		} catch (Exception e) {
			System.out.println("Something went wrong while running manager client.");
			e.printStackTrace();
		}
	}

	/**
	 * Creates the teacher record.
	 *
	 * @param userName      the user name
	 * @param managerPrefix the manager prefix
	 * @param bufferReader  the buffer reader
	 * @throws IOException   Signals that an I/O exception has occurred.
	 * @throws NotBoundException the not bound exception
	 * @throws NotFound the not found
	 * @throws CannotProceed the cannot proceed
	 * @throws InvalidName the invalid name
	 */
	private static void createTeacherRecord(String userName, String managerPrefix, BufferedReader bufferReader)
			throws IOException, NotBoundException, NotFound, CannotProceed, InvalidName {
		System.out.println("Enter teacher record inputs in the following format\n");
		System.out.println(
				"FirstName, LastName, Address, Phone, Specialization(Format: JAVA|HTML5|PHP), location (Format : MTL or LVL or DDO)");
		System.out.println("Example: Kevin, Peterson, 23,Lords street,London-987, 9023123445, Java|Python|Scala, MTL");
		System.out.println("\nenter a teacher record : ");
		String trInput = bufferReader.readLine().trim();

		List<String> trOutput = validate.teacherRecord(userName, trInput);

		if (trOutput != null) {
			createServerInstance(managerPrefix);
			String teacherSaveStatus = serverInstance.createTRecord("AutoCreate", trOutput.get(0), trOutput.get(1),
					trOutput.get(2), trOutput.get(3), trOutput.get(4), trOutput.get(5), userName, false);
			System.out.println(teacherSaveStatus);
			log.clientInfo(userName, LogWriter.PHASE_TR, teacherSaveStatus);
		}
	}

	/**
	 * Creates the student record.
	 *
	 * @param userName      the user name
	 * @param managerPrefix the manager prefix
	 * @param bufferReader  the buffer reader
	 * @throws IOException   Signals that an I/O exception has occurred.
	 * @throws NotBoundException the not bound exception
	 * @throws NotFound the not found
	 * @throws CannotProceed the cannot proceed
	 * @throws InvalidName the invalid name
	 */
	private static void createStudentRecord(String userName, String managerPrefix, BufferedReader bufferReader)
			throws IOException, NotBoundException, NotFound, CannotProceed, InvalidName {
		System.out.println("Enter Student record inputs in the following format\n");
		System.out.println(
				"FirstName, LastName, CourseRegistered(Format: JAVA|HTML5|PHP), Status(Format: 1-Active or 0-InActive), StatusDate(Format: yyyy-mm-dd)");
		System.out.println("Example: James, Anderson, Java|Python|Scala, 1, 2021-12-22");
		System.out.println("\nenter a Student record : ");
		String stInput = bufferReader.readLine().trim();

		List<String> stOutput = validate.studentRecord(userName, stInput);

		if (stOutput != null) {
			createServerInstance(managerPrefix);
			String studentSaveStatus = serverInstance.createSRecord("AutoCreate", stOutput.get(0), stOutput.get(1),
					stOutput.get(2), stOutput.get(3), stOutput.get(4), userName, false);
			System.out.println(studentSaveStatus);
			log.clientInfo(userName, LogWriter.PHASE_SR, studentSaveStatus);
		}
	}

	/**
	 * Edits the record.
	 *
	 * @param userName      the user name
	 * @param managerPrefix the manager prefix
	 * @param bufferReader  the buffer reader
	 * @throws NotFound the not found
	 * @throws CannotProceed the cannot proceed
	 * @throws InvalidName the invalid name
	 * @throws IOException   Signals that an I/O exception has occurred.
	 */
	private static void editRecord(String userName, String managerPrefix, BufferedReader bufferReader)
			throws NotFound, CannotProceed, InvalidName, IOException {
		System.out.println("\nEnter input to edit a record in the following format");
		System.out.println("\nRecordId, fieldName, newValue");
		System.out.println("\nTeacher Record Format: (\"TR*****\", \"ADDRESS or PHONE or LOCATION\", \"newValue\")");
		System.out.println("Example : (TR12345, PHONE, 9023412231)");
		System.out.println(
				"\nStudent Record Format: (\"SR*****\", \"COURSEREGISTERED or STATUS or STATUSDATE\", \"newValue\")");
		System.out.println("Example: (SR12345, COURSEREGISTERED, JAVA|HTML5|PHP)");
		System.out.println("\nenter a record to edit : ");
		String erInput = bufferReader.readLine().trim();

		List<String> erOutput = validate.editRecord(userName, erInput);

		if (erOutput != null) {
			createServerInstance(managerPrefix);
			String editStatus = serverInstance.editRecord(erOutput.get(0), erOutput.get(1), erOutput.get(2), userName);
			System.out.println(editStatus);
			log.clientInfo(userName, LogWriter.PHASE_ER, editStatus);
		}
	}

	/**
	 * Transfer record.
	 *
	 * @param userName      the user name
	 * @param managerPrefix the manager prefix
	 * @param bufferReader  the buffer reader
	 * @throws IOException       Signals that an I/O exception has occurred.
	 * @throws NotBoundException the not bound exception
	 * @throws NotFound the not found
	 * @throws CannotProceed the cannot proceed
	 * @throws InvalidName the invalid name
	 */
	private static void transferRecord(String userName, String managerPrefix, BufferedReader bufferReader)
			throws IOException, NotBoundException, NotFound, CannotProceed, InvalidName {

		System.out.println("Enter inputs in the following format to Transfer Record\n");
		System.out.println("recordID (Format : TR9999/SR9999), remoteCenterServerName(Format : MTL or LVL or DDO)");
		System.out.println("Example: TR9999, LVL\n");
		createServerInstance(managerPrefix);
		String[] listOfRecordID = serverInstance.listRecordID();
		System.out.println("Available records to transfer: " + String.join(",", listOfRecordID));
		System.out.println("\nenter details to transfer record :");

		String tRFInput = bufferReader.readLine().trim();

		List<String> tRFOutput = validate.transferRecord(userName, tRFInput);
		String transferStatus = null;

		if (tRFOutput != null) {

			String recordFound = serverInstance.findRecord(tRFOutput.get(0).toUpperCase());

			if (!recordFound.equalsIgnoreCase("null")) {
				createServerInstance(tRFOutput.get(1).toUpperCase());

				if (tRFOutput.get(0).toUpperCase().startsWith("TR")) {

					String[] teacherRecordArray = recordFound.trim().split(TRANSFER_TRIM_INPUT);
					transferStatus = serverInstance.createTRecord(teacherRecordArray[0], teacherRecordArray[1],
							teacherRecordArray[2], teacherRecordArray[3], teacherRecordArray[4], teacherRecordArray[5],
							teacherRecordArray[6], userName, true);
				}

				if (tRFOutput.get(0).toUpperCase().startsWith("SR")) {

					String[] studentRecordArray = recordFound.trim().split(TRANSFER_TRIM_INPUT);
					transferStatus = serverInstance.createSRecord(studentRecordArray[0], studentRecordArray[1],
							studentRecordArray[2], studentRecordArray[3], studentRecordArray[4], studentRecordArray[5],
							userName, true);
				}
			} else {
				transferStatus = tRFOutput.get(0).toUpperCase() + " Not Found to transfer";
			}
			System.out.println(transferStatus);
			log.clientInfo(userName, LogWriter.PHASE_TRFR, transferStatus);
		}

	}

	/**
	 * This method is used to set the server instance based on location.
	 *
	 * @param server the server
	 * @throws NotFound the not found
	 * @throws CannotProceed the cannot proceed
	 * @throws InvalidName the invalid name
	 */
	public static void createServerInstance(String server) throws NotFound, CannotProceed, InvalidName {
		if ("MTL".equalsIgnoreCase(server)) {
			serverInstance = DCMSInterfaceHelper.narrow(ncRef.resolve_str("Montreal"));
		}
		if ("LVL".equalsIgnoreCase(server)) {
			serverInstance = DCMSInterfaceHelper.narrow(ncRef.resolve_str("Laval"));
		}
		if ("DDO".equalsIgnoreCase(server)) {
			serverInstance = DCMSInterfaceHelper.narrow(ncRef.resolve_str("Dollard-des-Ormeaux"));
		}
	}
}
