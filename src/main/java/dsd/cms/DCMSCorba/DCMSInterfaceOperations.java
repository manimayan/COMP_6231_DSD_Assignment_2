package dsd.cms.DCMSCorba;


/**
* dsd/cms/DCMSCorba/DCMSInterfaceOperations.java .
* Generated by the IDL-to-Java compiler (portable), version "3.2"
* from DCMSCorba.idl
* Thursday, July 1, 2021 12:05:51 AM IST
*/

public interface DCMSInterfaceOperations 
{

  // CenterServer corba idl methods
  String createTRecord (String recordId, String firstName, String lastName, String address, String phone, String specialization, String location, String managerID, boolean transferStatus);
  String createSRecord (String recordId, String firstName, String lastName, String courseRegistered, String status, String statusDate, String managerID, boolean transferStatus);
  String getRecordCounts (String managerID);
  String editRecord (String recordID, String fieldName, String newValue, String managerID);
  String[] listRecordID ();
  String findRecord (String recordID);
} // interface DCMSInterfaceOperations
