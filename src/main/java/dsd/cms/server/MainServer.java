package dsd.cms.server;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import org.omg.CORBA.ORB;
import org.omg.CosNaming.NameComponent;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.POAHelper;

import dsd.cms.DCMSCorba.DCMSInterface;
import dsd.cms.DCMSCorba.DCMSInterfaceHelper;
import dsd.cms.model.StudentRecord;
import dsd.cms.model.TeacherRecord;
import dsd.cms.utils.LogWriter;

// TODO: Auto-generated Javadoc
/**
 * The Class MainServer.
 */
public class MainServer {

	/** The log writer. */
	static LogWriter logWriter = new LogWriter();

	/**
	 * Instantiates a new main server.
	 */
	public MainServer() {
		super();
	}

	/**
	 * main method to run all the servers.
	 *
	 * @param args arguments for main function
	 */
	public static void main(String args[]) {

		buildLogDirectory("./logs");
		startServers(args);
	}

	/**
	 * This method is used to create logs directory to store the logs.
	 * 
	 * @param path location of the logs folder
	 */
	public static void buildLogDirectory(String path) {
		File outputDir = new File(path);
		if (!outputDir.exists()) {
			outputDir.mkdir();
		}
	}

	/**
	 * Starts all three servers.
	 *
	 * @param args the args
	 */
	public static void startServers(String args[]) {
		try {
			// create and initialize the ORB
			ORB orb = ORB.init(args, null);
			POA rootpoa;
			rootpoa = POAHelper.narrow(orb.resolve_initial_references("RootPOA"));
			rootpoa.the_POAManager().activate();
			org.omg.CORBA.Object objRef = orb.resolve_initial_references("NameService");

			MontrealServer montrealServer = new MontrealServer();
			montrealServer.setORB(orb);

			Runnable montreal = () -> {
				montrealServer.serverConnection(8880, montrealServer);
			};
			Thread t1 = new Thread(montreal);
			t1.start();
			org.omg.CORBA.Object montrealObjectRef = rootpoa.servant_to_reference(montrealServer);
			DCMSInterface dcmsMontrealRef = DCMSInterfaceHelper.narrow(montrealObjectRef);
			NamingContextExt ncRefMontreal = NamingContextExtHelper.narrow(objRef);
			NameComponent pathMontreal[] = ncRefMontreal.to_name("Montreal");
			ncRefMontreal.rebind(pathMontreal, dcmsMontrealRef);

			LavalServer lavalServer = new LavalServer();
			lavalServer.setORB(orb);
			Runnable laval = () -> {
				lavalServer.serverConnection(8881, lavalServer);
			};
			Thread t2 = new Thread(laval);
			t2.start();
			org.omg.CORBA.Object lavalObjectRef = rootpoa.servant_to_reference(lavalServer);
			DCMSInterface dcmsLavalRef = DCMSInterfaceHelper.narrow(lavalObjectRef);
			NamingContextExt ncRefLaval = NamingContextExtHelper.narrow(objRef);
			NameComponent pathLaval[] = ncRefLaval.to_name("Laval");
			ncRefLaval.rebind(pathLaval, dcmsLavalRef);

			DollardServer dollardServer = new DollardServer();
			Runnable dollard = () -> {
				dollardServer.serverConnection(8882, dollardServer);
			};
			Thread t3 = new Thread(dollard);
			t3.start();
			org.omg.CORBA.Object dollardObjectRef = rootpoa.servant_to_reference(dollardServer);
			DCMSInterface dcmsDollardRef = DCMSInterfaceHelper.narrow(dollardObjectRef);
			NamingContextExt ncRefDollard = NamingContextExtHelper.narrow(objRef);
			NameComponent pathDollard[] = ncRefDollard.to_name("Dollard-des-Ormeaux");
			ncRefDollard.rebind(pathDollard, dcmsDollardRef);

		} catch (Exception e) {
			e.printStackTrace(System.out);
		}
	}

	/**
	 * Persists teacher record in map against correct key.
	 *
	 * @param teacherRecord         teacher record to be stored
	 * @param teacherStudentRecords map maintained at server
	 * @return true, if successful
	 */
	public boolean persistTeacherRecordInMap(TeacherRecord teacherRecord,
			ConcurrentHashMap<String, ConcurrentHashMap<String, Object>> teacherStudentRecords) {
		if (teacherStudentRecords.isEmpty() || !teacherStudentRecords
				.containsKey(Character.toString(Character.toUpperCase(teacherRecord.getLastName().charAt(0))))) {
			ConcurrentHashMap<String, Object> innerMap = new ConcurrentHashMap<>();
			innerMap.put(teacherRecord.getRecordID(), teacherRecord);
			teacherStudentRecords.put(Character.toString(Character.toUpperCase(teacherRecord.getLastName().charAt(0))),
					innerMap);
			return true;
		} else if (teacherStudentRecords
				.containsKey(Character.toString(Character.toUpperCase(teacherRecord.getLastName().charAt(0))))) {

			ConcurrentHashMap<String, Object> innerMap = teacherStudentRecords
					.get(Character.toString(Character.toUpperCase(teacherRecord.getLastName().charAt(0))));
			innerMap.put(teacherRecord.getRecordID(), teacherRecord);
			teacherStudentRecords.put(Character.toString(Character.toUpperCase(teacherRecord.getLastName().charAt(0))),
					innerMap);
			return true;
		}
		return false;
	}

	/**
	 * Persists student record in map against correct key.
	 *
	 * @param studentRecord         student record to be stored
	 * @param teacherStudentRecords map maintained at server
	 * @return true, if successful
	 */
	public synchronized boolean persistStudentRecordInMap(StudentRecord studentRecord,
			ConcurrentHashMap<String, ConcurrentHashMap<String, Object>> teacherStudentRecords) {
		if (teacherStudentRecords.isEmpty() || !teacherStudentRecords
				.containsKey(Character.toString(Character.toUpperCase(studentRecord.getLastName().charAt(0))))) {
			ConcurrentHashMap<String, Object> innerMap = new ConcurrentHashMap<>();
			innerMap.put(studentRecord.getRecordID(), studentRecord);
			teacherStudentRecords.put(Character.toString(Character.toUpperCase(studentRecord.getLastName().charAt(0))),
					innerMap);
			return true;
		} else if (teacherStudentRecords
				.containsKey(Character.toString(Character.toUpperCase(studentRecord.getLastName().charAt(0))))) {
			ConcurrentHashMap<String, Object> innerMap = teacherStudentRecords
					.get(Character.toString(Character.toUpperCase(studentRecord.getLastName().charAt(0))));
			innerMap.put(studentRecord.getRecordID(), studentRecord);
			teacherStudentRecords.put(Character.toString(Character.toUpperCase(studentRecord.getLastName().charAt(0))),
					innerMap);
			return true;
		}
		return false;
	}

	/**
	 * Updated the record information in map.
	 *
	 * @param recordID              record id which needs to be updated
	 * @param fieldName             field name which needs to be updated
	 * @param newValue              new value to be updated against field
	 * @param teacherStudentRecords the teacher student records
	 * @return result message
	 */
	public synchronized String updateRecordInMap(String recordID, String fieldName, String newValue,
			ConcurrentHashMap<String, ConcurrentHashMap<String, Object>> teacherStudentRecords) {
		if (recordID.startsWith("SR")) {
			return this.updateStudentRecord(recordID, fieldName, newValue, teacherStudentRecords);
		} else if (recordID.contains("TR")) {
			return this.updateTeacherRecord(recordID, fieldName, newValue, teacherStudentRecords);
		}
		return "Update failed due to invalid Record ID passed";
	}

	/**
	 * Updates teacher record.
	 * 
	 * @param recordID              record id which needs to be updated
	 * @param fieldName             field name which needs to be updated
	 * @param newValue              new value to be updated against field
	 * @param teacherStudentRecords map maintained at server
	 * @return result message
	 */
	public synchronized String updateTeacherRecord(String recordID, String fieldName, String newValue,
			ConcurrentHashMap<String, ConcurrentHashMap<String, Object>> teacherStudentRecords) {
		String resultMsg = null;
		boolean result = false;
		String output = null;
		for (Entry<String, ConcurrentHashMap<String, Object>> entry : teacherStudentRecords.entrySet()) {
			Object matchedObject = entry.getValue().get(recordID);
			if (matchedObject != null && matchedObject instanceof TeacherRecord) {
				if (fieldName.equalsIgnoreCase("ADDRESS")) {
					((TeacherRecord) matchedObject).setAddress(newValue);
					result = true;
				} else if (fieldName.equalsIgnoreCase("PHONE")) {
					((TeacherRecord) matchedObject).setPhone(newValue);
					result = true;
				} else if (fieldName.equalsIgnoreCase("LOCATION")) {
					((TeacherRecord) matchedObject).setLocation(newValue.toUpperCase());
					result = true;
				}
				output = "Id:" + ((TeacherRecord) matchedObject).getRecordID() + ", FN:"
						+ ((TeacherRecord) matchedObject).getFirstName() + ", LN:"
						+ ((TeacherRecord) matchedObject).getLastName() + ", Address:"
						+ ((TeacherRecord) matchedObject).getAddress() + ", Phone:"
						+ ((TeacherRecord) matchedObject).getPhone() + ", Specialization:"
						+ ((TeacherRecord) matchedObject).getSpecialization() + ", Location:"
						+ ((TeacherRecord) matchedObject).getLocation();
			}
		}
		resultMsg = result ? "Edit operation performed successfully.\nUpdated Record: " + output
				: "Teacher Record with given id : " + recordID + " was not found.";
		return resultMsg;
	}

	/**
	 * Updates student record.
	 * 
	 * @param recordID              record id which needs to be updated
	 * @param fieldName             field name which needs to be updated
	 * @param newValue              new value to be updated against field
	 * @param teacherStudentRecords map maintained at server
	 * @return result message
	 */
	public synchronized String updateStudentRecord(String recordID, String fieldName, String newValue,
			ConcurrentHashMap<String, ConcurrentHashMap<String, Object>> teacherStudentRecords) {
		boolean result = false;
		String resultMsg = null;
		String output = null;
		for (Entry<String, ConcurrentHashMap<String, Object>> entry : teacherStudentRecords.entrySet()) {
			Object matchedObject = entry.getValue().get(recordID);
			if (matchedObject != null && matchedObject instanceof StudentRecord) {
				if (fieldName.equalsIgnoreCase("COURSEREGISTERED")) {
					((StudentRecord) matchedObject).setCoursesRegistered(newValue);
					result = true;
				} else if (fieldName.equalsIgnoreCase("STATUS")) {
					((StudentRecord) matchedObject).setStatus(newValue);
					((StudentRecord) matchedObject).setStatusDate(LocalDate.now());
					result = true;
				} else if (fieldName.equalsIgnoreCase("STATUSDATE")) {
					LocalDate localDate = LocalDate.parse(newValue);
					((StudentRecord) matchedObject).setStatusDate(localDate);
					result = true;
				}
				output = "Id:" + ((StudentRecord) matchedObject).getRecordID() + ", FN:"
						+ ((StudentRecord) matchedObject).getFirstName() + ", LN:"
						+ ((StudentRecord) matchedObject).getLastName() + ", CoursesRegistered:"
						+ ((StudentRecord) matchedObject).getCoursesRegistered() + ", Status:"
						+ ((StudentRecord) matchedObject).getStatus() + ", StatusDate:"
						+ ((StudentRecord) matchedObject).getStatusDate();
			}
		}
		resultMsg = result ? "Edit operation performed successfully.\nUpdated Record: " + output
				: "Student Record with given id : " + recordID + " was not found.";
		return resultMsg;
	}

	/**
	 * This method is used to communicate with the other servers to info about
	 * student and teacher counts.
	 *
	 * @param port port of the other servers that are running on
	 * @return String containing total number of students and teacher present at
	 *         server of given port
	 */
	public String datafromOtherServers(int port) {

		try (DatagramSocket socket = new DatagramSocket();) {

			byte[] b = new byte[65535];
			String request = "getRecordCounts";

			TimeUnit.MILLISECONDS.sleep(20);
			DatagramPacket packetToSend = new DatagramPacket(request.getBytes(), request.getBytes().length,
					InetAddress.getByName("localhost"), port);
			socket.send(packetToSend);

			DatagramPacket recievedPacket = new DatagramPacket(b, b.length);
			socket.receive(recievedPacket);
			String returnData = new String(recievedPacket.getData());
			return returnData.trim();
		} catch (IOException | InterruptedException e) {
			return null;
		}
	}

	/**
	 * Computes record from map.
	 *
	 * @param teacherStudentRecords the teacher student records
	 * @return the record count
	 */
	public int getRecordCount(ConcurrentHashMap<String, ConcurrentHashMap<String, Object>> teacherStudentRecords) {
		int count = 0;
		for (Entry<String, ConcurrentHashMap<String, Object>> entry : teacherStudentRecords.entrySet()) {
			count += entry.getValue().size();
		}
		return count;
	}

	/**
	 * Loads initial data into server.
	 * 
	 * @param filePath path at which data is stored
	 * @return updated map
	 */
	public synchronized ConcurrentHashMap<String, ConcurrentHashMap<String, Object>> loadData(String filePath) {
		ConcurrentHashMap<String, ConcurrentHashMap<String, Object>> teacherStudentRecords = new ConcurrentHashMap<>();

		try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {

			String line = reader.readLine();
			while (line != null) {
				String[] listParts = line.split(",");

				// recordID
				String recordID = listParts[0];
				// firstName
				String firstName = listParts[1];
				// lastName
				String lastName = listParts[2];

				if (recordID.contains("TR")) {
					// address
					String[] addArr = Arrays.copyOfRange(listParts, 3, listParts.length - 3);
					String address = String.join(",", addArr).trim();
					// phone
					String phone = listParts[listParts.length - 3];
					// specialization
					String specArr = listParts[listParts.length - 2];
					String specialization = specArr.replace("|", ",");
					// location
					String location = listParts[listParts.length - 1];

					TeacherRecord teacherRecord = new TeacherRecord(recordID, firstName, lastName, address, phone,
							specialization, location);
					this.persistTeacherRecordInMap(teacherRecord, teacherStudentRecords);

				} else if (recordID.contains("SR")) {
					// course registered
					String courseArr = listParts[3];
					String courseRegistered = courseArr.replace("|", ",");
					// status
					String status = listParts[4];
					// status date
					String statusDate = listParts[5];

					StudentRecord studentRecord = new StudentRecord(recordID, firstName, lastName, courseRegistered,
							status, statusDate);
					this.persistStudentRecordInMap(studentRecord, teacherStudentRecords);
				}
				line = reader.readLine();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return teacherStudentRecords;
	}

	/**
	 * Find record inmap.
	 *
	 * @param recordID              the record ID
	 * @param server                the server
	 * @param teacherStudentRecords the teacher student records
	 * @return the object
	 */
	public String findAndRemoveRecordInmap(String recordID, String server,
			ConcurrentHashMap<String, ConcurrentHashMap<String, Object>> teacherStudentRecords) {
		for (Entry<String, ConcurrentHashMap<String, Object>> entry : teacherStudentRecords.entrySet()) {
			Object matchedObject = entry.getValue().get(recordID);
			if (matchedObject != null) {
				entry.getValue().remove(recordID);
				logWriter.serverInfo(server, LogWriter.SYSTEM, LogWriter.PHASE_RESPONSE,
						recordID + " removed from " + server);
				if (recordID.startsWith("TR")) {
					return ((TeacherRecord) matchedObject).convertToString(((TeacherRecord) matchedObject));
				}
				if (recordID.startsWith("SR")) {
					return ((StudentRecord) matchedObject).convertToString(((StudentRecord) matchedObject));
				}
			}
		}
		return "null";
	}

	/**
	 * Find whether any location already has record with particular record id.
	 * 
	 * @param recordID              record id to be looked up
	 * @param teacherStudentRecords all teacher student data present at location
	 * @return true if it already exists or else false
	 */
	public boolean findDuplicateRecordInmap(String recordID,
			ConcurrentHashMap<String, ConcurrentHashMap<String, Object>> teacherStudentRecords) {
		boolean found = false;
		for (Entry<String, ConcurrentHashMap<String, Object>> entry : teacherStudentRecords.entrySet()) {
			if (entry.getValue().containsKey(recordID)) {
				found = true;
				break;
			}
		}
		return found;
	}
}
