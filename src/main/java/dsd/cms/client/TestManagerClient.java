package dsd.cms.client;

import org.omg.CORBA.ORB;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;
import org.omg.CosNaming.NamingContextPackage.CannotProceed;
import org.omg.CosNaming.NamingContextPackage.InvalidName;
import org.omg.CosNaming.NamingContextPackage.NotFound;

import dsd.cms.DCMSCorba.DCMSInterface;
import dsd.cms.DCMSCorba.DCMSInterfaceHelper;

public class TestManagerClient {

	/** Maing Context ref for getting server instance. */
	static NamingContextExt ncRef;

	/** ORB object */
	static ORB orb;

	public static void main(String[] args)
			throws NotFound, CannotProceed, InvalidName, org.omg.CORBA.ORBPackage.InvalidName, InterruptedException {

		ORB orb = ORB.init(args, null);
		org.omg.CORBA.Object referenceObject = orb.resolve_initial_references("NameService");
		ncRef = NamingContextExtHelper.narrow(referenceObject);

		TestManagerClient testManagerClient = new TestManagerClient();
		System.out.println("******************* Basic Test Cases *******************\n");

		System.out.println("\nTest 1 : Create teacher record on Montreal Server");
		testManagerClient.createTeacherRecord("MTL", "MTL1111");
		System.out.println("\nTest 2 : Create student record on Montreal Server");
		testManagerClient.createStudentRecord("MTL", "MTL1111");
		System.out.println("\nTest 3 : Edit existing record on Montreal Server");
		testManagerClient.editRecord("MTL", "MTL1111");
		System.out.println("\nTest 4 : Get Record Count from Montreal location");
		testManagerClient.getRecordCount("MTL", "MTL1111");
		System.out.println("\nTest 5 : Transfer teacher record to Laval");
		testManagerClient.transferRecord("MTL", "MTL1111", "LVL", "SR10000");
		System.out.println("\nTest 6 : Get Record Count from Laval location");
		testManagerClient.getRecordCount("LVL", "LVL1111");
		System.out.println("\nTest 7 : Edit transferred record on Laval Server");
		testManagerClient.editRecord2("LVL", "LVL1111");

		System.out.println("\n\n******************* Concurrency Test Cases *******************\n");

		System.out.println(
				"\n** Initially 1 Teacher record with first name : fEditTransfer1 and 1 Student record with first name : fEditTransfer2 will be created **");
		System.out.println(
				"** There are 2 threads trying to attempt edit record and transfer record on these 2 records **");
		System.out.println("** Thread 1 tries to edit teacher record and transfer student record to laval **");
		System.out.println("** Thread 2 tries to transfer teacher record and edit student record to laval **");
		System.out.println(
				"** Application will run without any error. When edit and transfer both are attempted on single record, only 1 of them will be executed. **");

		System.out.println("\nThread " + Thread.currentThread().getId() + " Method : createPlayerAccount() , "
				+ " First Name : fEditTransfer1 , "
				+ createServerInstance("MTL").createTRecord("AutoCreate", "fEditTransfer1", "lEditTransfer1",
						"abc-12, Montreal", "8888888888", "Python|Java", "MTL", "MTL1111", false));
		System.out.println("\nThread " + Thread.currentThread().getId() + " Method : createPlayerAccount() , "
				+ " First Name : fEditTransfer2 , " + createServerInstance("MTL").createSRecord("AutoCreate",
						"fEditTransfer1", "lEditTransfer1", "ML|APP", "Active", "2021-12-24", "MTL1111", false));

		Runnable run1 = () -> {
			try {
				System.out.println("\nThread " + Thread.currentThread().getId()
						+ " Executing Edit Record on TR10001, First Name : fEditTransfer1");
				System.out.println("\nThread " + Thread.currentThread().getId() + " Edit Record Result for TR10001:: "
						+ createServerInstance("MTL").editRecord("TR10001", "PHONE", "9191919191", "MTL1111"));

				System.out.println("\nThread " + Thread.currentThread().getId()
						+ " Executing Transfer Record on SR10001, First Name : fEditTransfer2");
				String recordFound = createServerInstance("MTL").findRecord("SR10001");
				String[] studentRecordArray = recordFound.trim().split(":");
				if(studentRecordArray != null && studentRecordArray.length != 0) {
					String transferStatus = createServerInstance("LVL").createSRecord(studentRecordArray[0],
							studentRecordArray[1], studentRecordArray[2], studentRecordArray[3], studentRecordArray[4],
							studentRecordArray[5], "MTL1111", true);
					System.out.println("\nThread " + Thread.currentThread().getId()
							+ " Transfer Record Result for SR10001 :: " + transferStatus);
				}			
			} catch (NotFound | CannotProceed | InvalidName e) {
				e.printStackTrace();
			}
		};
		Thread t1 = new Thread(run1);

		Runnable run2 = () -> {
			try {
				System.out.println("\nThread " + Thread.currentThread().getId()
						+ " Executing Transfer Record on TR10001, First Name : fEditTransfer1");
				String recordFound = createServerInstance("MTL").findRecord("TR10001");
				String[] teacherRecordArray = recordFound.trim().split(":");
				if(teacherRecordArray != null && teacherRecordArray.length != 0) {
					String transferStatus = createServerInstance("LVL").createTRecord(teacherRecordArray[0],
							teacherRecordArray[1], teacherRecordArray[2], teacherRecordArray[3], teacherRecordArray[4],
							teacherRecordArray[5], teacherRecordArray[6], "MTL1000", true);
					System.out.println("\nThread " + Thread.currentThread().getId()
							+ " Transfer Record Result for TR10001 :: " + transferStatus);
				}
				
				System.out.println("\nThread " + Thread.currentThread().getId()
						+ " Executing Edit Record on SR10001, First Name : fEditTransfer2");
				System.out.println("\nThread " + Thread.currentThread().getId()
						+ " Edit Record Result for SR10001 :: "
						+ createServerInstance("MTL").editRecord("SR10001", "COURSEREGISTERED", "JAVA|DSD", "MTL1000"));
			} catch (NotFound | CannotProceed | InvalidName e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		};
		Thread t2 = new Thread(run2);

		t1.start();
		t2.start();

		Thread.sleep(2000);
		
		//Clear server's hashmap by removing created test records.
		createServerInstance("MTL").findRecord("TR10000");
		createServerInstance("MTL").findRecord("SR10000");
		createServerInstance("MTL").findRecord("TR10001");
		createServerInstance("MTL").findRecord("SR10001");
		createServerInstance("LVL").findRecord("TR10001");
		createServerInstance("LVL").findRecord("SR10001");
	}

	public void createTeacherRecord(String location, String managerID) throws NotFound, CannotProceed, InvalidName {
		System.out.println("Executing : createTeacherRecord() , "
				+ createServerInstance(location).createTRecord("AutoCreate", "testFName1", "testLName1",
						"abc-12, Montreal", "9999999999", "Python|Java", "MTL", managerID, false));
	}

	public void createStudentRecord(String location, String managerID) throws NotFound, CannotProceed, InvalidName {
		System.out.println("Executing : createStudentRecord() , " + createServerInstance(location).createSRecord(
				"AutoCreate", "testFName1", "testLName1", "ML|APP", "Active", "2021-12-24", managerID, false));
	}

	public void editRecord(String location, String managerID) throws NotFound, CannotProceed, InvalidName {
		System.out.println("Executing : editRecord() , "
				+ createServerInstance(location).editRecord("SR10000", "COURSEREGISTERED", "JAVA|HTML5", managerID));
	}

	public void getRecordCount(String location, String managerID) throws NotFound, CannotProceed, InvalidName {
		System.out
				.println("Executing : getRecordCount() , " + createServerInstance(location).getRecordCounts(managerID));
	}

	public void transferRecord(String location, String managerID, String targetLocation, String recordToTransfer)
			throws NotFound, CannotProceed, InvalidName {
		String recordFound = createServerInstance(location).findRecord(recordToTransfer);

		String[] studentRecordArray = recordFound.trim().split(":");
		if(studentRecordArray != null && studentRecordArray.length != 0) {
			String transferStatus = createServerInstance(targetLocation).createSRecord(studentRecordArray[0],
					studentRecordArray[1], studentRecordArray[2], studentRecordArray[3], studentRecordArray[4],
					studentRecordArray[5], managerID, true);
			System.out.println("Executing : transferRecord() , " + transferStatus);
		}
	}

	public void editRecord2(String location, String managerID) throws NotFound, CannotProceed, InvalidName {
		System.out.println("Executing : editRecord2() , "
				+ createServerInstance(location).editRecord("SR10000", "COURSEREGISTERED", "JAVA|DSD", managerID));
	}
	
		/**
		 * This method is used to set the server instance based on location.
		 *
		 * @param server the server
		 * @throws NotFound      the not found
		 * @throws CannotProceed the cannot proceed
		 * @throws InvalidName   the invalid name
		 */
		public static DCMSInterface createServerInstance(String server) throws NotFound, CannotProceed, InvalidName {
			if ("MTL".equalsIgnoreCase(server)) {
				return DCMSInterfaceHelper.narrow(ncRef.resolve_str("Montreal"));
			}
			if ("LVL".equalsIgnoreCase(server)) {
				return DCMSInterfaceHelper.narrow(ncRef.resolve_str("Laval"));
			}
			if ("DDO".equalsIgnoreCase(server)) {
				return DCMSInterfaceHelper.narrow(ncRef.resolve_str("Dollard-des-Ormeaux"));
			}
			return null;
		}
}
