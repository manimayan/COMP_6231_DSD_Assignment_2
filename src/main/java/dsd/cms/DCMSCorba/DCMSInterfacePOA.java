package dsd.cms.DCMSCorba;


/**
* dsd/cms/DCMSCorba/DCMSInterfacePOA.java .
* Generated by the IDL-to-Java compiler (portable), version "3.2"
* from DCMSCorba.idl
* Thursday, July 1, 2021 12:05:51 AM IST
*/

public abstract class DCMSInterfacePOA extends org.omg.PortableServer.Servant
 implements dsd.cms.DCMSCorba.DCMSInterfaceOperations, org.omg.CORBA.portable.InvokeHandler
{

  // Constructors

  private static java.util.Hashtable _methods = new java.util.Hashtable ();
  static
  {
    _methods.put ("createTRecord", new java.lang.Integer (0));
    _methods.put ("createSRecord", new java.lang.Integer (1));
    _methods.put ("getRecordCounts", new java.lang.Integer (2));
    _methods.put ("editRecord", new java.lang.Integer (3));
    _methods.put ("listRecordID", new java.lang.Integer (4));
    _methods.put ("findRecord", new java.lang.Integer (5));
  }

  public org.omg.CORBA.portable.OutputStream _invoke (String $method,
                                org.omg.CORBA.portable.InputStream in,
                                org.omg.CORBA.portable.ResponseHandler $rh)
  {
    org.omg.CORBA.portable.OutputStream out = null;
    java.lang.Integer __method = (java.lang.Integer)_methods.get ($method);
    if (__method == null)
      throw new org.omg.CORBA.BAD_OPERATION (0, org.omg.CORBA.CompletionStatus.COMPLETED_MAYBE);

    switch (__method.intValue ())
    {

  // CenterServer corba idl methods
       case 0:  // dsd/cms/DCMSCorba/DCMSInterface/createTRecord
       {
         String recordId = in.read_string ();
         String firstName = in.read_string ();
         String lastName = in.read_string ();
         String address = in.read_string ();
         String phone = in.read_string ();
         String specialization = in.read_string ();
         String location = in.read_string ();
         String managerID = in.read_string ();
         boolean transferStatus = in.read_boolean ();
         String $result = null;
         $result = this.createTRecord (recordId, firstName, lastName, address, phone, specialization, location, managerID, transferStatus);
         out = $rh.createReply();
         out.write_string ($result);
         break;
       }

       case 1:  // dsd/cms/DCMSCorba/DCMSInterface/createSRecord
       {
         String recordId = in.read_string ();
         String firstName = in.read_string ();
         String lastName = in.read_string ();
         String courseRegistered = in.read_string ();
         String status = in.read_string ();
         String statusDate = in.read_string ();
         String managerID = in.read_string ();
         boolean transferStatus = in.read_boolean ();
         String $result = null;
         $result = this.createSRecord (recordId, firstName, lastName, courseRegistered, status, statusDate, managerID, transferStatus);
         out = $rh.createReply();
         out.write_string ($result);
         break;
       }

       case 2:  // dsd/cms/DCMSCorba/DCMSInterface/getRecordCounts
       {
         String managerID = in.read_string ();
         String $result = null;
         $result = this.getRecordCounts (managerID);
         out = $rh.createReply();
         out.write_string ($result);
         break;
       }

       case 3:  // dsd/cms/DCMSCorba/DCMSInterface/editRecord
       {
         String recordID = in.read_string ();
         String fieldName = in.read_string ();
         String newValue = in.read_string ();
         String managerID = in.read_string ();
         String $result = null;
         $result = this.editRecord (recordID, fieldName, newValue, managerID);
         out = $rh.createReply();
         out.write_string ($result);
         break;
       }

       case 4:  // dsd/cms/DCMSCorba/DCMSInterface/listRecordID
       {
         String $result[] = null;
         $result = this.listRecordID ();
         out = $rh.createReply();
         dsd.cms.DCMSCorba.listRecordIDHelper.write (out, $result);
         break;
       }

       case 5:  // dsd/cms/DCMSCorba/DCMSInterface/findRecord
       {
         String recordID = in.read_string ();
         String $result = null;
         $result = this.findRecord (recordID);
         out = $rh.createReply();
         out.write_string ($result);
         break;
       }

       default:
         throw new org.omg.CORBA.BAD_OPERATION (0, org.omg.CORBA.CompletionStatus.COMPLETED_MAYBE);
    }

    return out;
  } // _invoke

  // Type-specific CORBA::Object operations
  private static String[] __ids = {
    "IDL:dsd/cms/DCMSCorba/DCMSInterface:1.0"};

  public String[] _all_interfaces (org.omg.PortableServer.POA poa, byte[] objectId)
  {
    return (String[])__ids.clone ();
  }

  public DCMSInterface _this() 
  {
    return DCMSInterfaceHelper.narrow(
    super._this_object());
  }

  public DCMSInterface _this(org.omg.CORBA.ORB orb) 
  {
    return DCMSInterfaceHelper.narrow(
    super._this_object(orb));
  }


} // class DCMSInterfacePOA
