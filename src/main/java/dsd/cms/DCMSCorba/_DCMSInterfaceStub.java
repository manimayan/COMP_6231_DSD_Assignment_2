package dsd.cms.DCMSCorba;


/**
* dsd/cms/DCMSCorba/_DCMSInterfaceStub.java .
* Generated by the IDL-to-Java compiler (portable), version "3.2"
* from DCMSCorba.idl
* Thursday, July 1, 2021 12:05:51 AM IST
*/

public class _DCMSInterfaceStub extends org.omg.CORBA.portable.ObjectImpl implements dsd.cms.DCMSCorba.DCMSInterface
{


  // CenterServer corba idl methods
  public String createTRecord (String recordId, String firstName, String lastName, String address, String phone, String specialization, String location, String managerID, boolean transferStatus)
  {
            org.omg.CORBA.portable.InputStream $in = null;
            try {
                org.omg.CORBA.portable.OutputStream $out = _request ("createTRecord", true);
                $out.write_string (recordId);
                $out.write_string (firstName);
                $out.write_string (lastName);
                $out.write_string (address);
                $out.write_string (phone);
                $out.write_string (specialization);
                $out.write_string (location);
                $out.write_string (managerID);
                $out.write_boolean (transferStatus);
                $in = _invoke ($out);
                String $result = $in.read_string ();
                return $result;
            } catch (org.omg.CORBA.portable.ApplicationException $ex) {
                $in = $ex.getInputStream ();
                String _id = $ex.getId ();
                throw new org.omg.CORBA.MARSHAL (_id);
            } catch (org.omg.CORBA.portable.RemarshalException $rm) {
                return createTRecord (recordId, firstName, lastName, address, phone, specialization, location, managerID, transferStatus        );
            } finally {
                _releaseReply ($in);
            }
  } // createTRecord

  public String createSRecord (String recordId, String firstName, String lastName, String courseRegistered, String status, String statusDate, String managerID, boolean transferStatus)
  {
            org.omg.CORBA.portable.InputStream $in = null;
            try {
                org.omg.CORBA.portable.OutputStream $out = _request ("createSRecord", true);
                $out.write_string (recordId);
                $out.write_string (firstName);
                $out.write_string (lastName);
                $out.write_string (courseRegistered);
                $out.write_string (status);
                $out.write_string (statusDate);
                $out.write_string (managerID);
                $out.write_boolean (transferStatus);
                $in = _invoke ($out);
                String $result = $in.read_string ();
                return $result;
            } catch (org.omg.CORBA.portable.ApplicationException $ex) {
                $in = $ex.getInputStream ();
                String _id = $ex.getId ();
                throw new org.omg.CORBA.MARSHAL (_id);
            } catch (org.omg.CORBA.portable.RemarshalException $rm) {
                return createSRecord (recordId, firstName, lastName, courseRegistered, status, statusDate, managerID, transferStatus        );
            } finally {
                _releaseReply ($in);
            }
  } // createSRecord

  public String getRecordCounts (String managerID)
  {
            org.omg.CORBA.portable.InputStream $in = null;
            try {
                org.omg.CORBA.portable.OutputStream $out = _request ("getRecordCounts", true);
                $out.write_string (managerID);
                $in = _invoke ($out);
                String $result = $in.read_string ();
                return $result;
            } catch (org.omg.CORBA.portable.ApplicationException $ex) {
                $in = $ex.getInputStream ();
                String _id = $ex.getId ();
                throw new org.omg.CORBA.MARSHAL (_id);
            } catch (org.omg.CORBA.portable.RemarshalException $rm) {
                return getRecordCounts (managerID        );
            } finally {
                _releaseReply ($in);
            }
  } // getRecordCounts

  public String editRecord (String recordID, String fieldName, String newValue, String managerID)
  {
            org.omg.CORBA.portable.InputStream $in = null;
            try {
                org.omg.CORBA.portable.OutputStream $out = _request ("editRecord", true);
                $out.write_string (recordID);
                $out.write_string (fieldName);
                $out.write_string (newValue);
                $out.write_string (managerID);
                $in = _invoke ($out);
                String $result = $in.read_string ();
                return $result;
            } catch (org.omg.CORBA.portable.ApplicationException $ex) {
                $in = $ex.getInputStream ();
                String _id = $ex.getId ();
                throw new org.omg.CORBA.MARSHAL (_id);
            } catch (org.omg.CORBA.portable.RemarshalException $rm) {
                return editRecord (recordID, fieldName, newValue, managerID        );
            } finally {
                _releaseReply ($in);
            }
  } // editRecord

  public String[] listRecordID ()
  {
            org.omg.CORBA.portable.InputStream $in = null;
            try {
                org.omg.CORBA.portable.OutputStream $out = _request ("listRecordID", true);
                $in = _invoke ($out);
                String $result[] = dsd.cms.DCMSCorba.listRecordIDHelper.read ($in);
                return $result;
            } catch (org.omg.CORBA.portable.ApplicationException $ex) {
                $in = $ex.getInputStream ();
                String _id = $ex.getId ();
                throw new org.omg.CORBA.MARSHAL (_id);
            } catch (org.omg.CORBA.portable.RemarshalException $rm) {
                return listRecordID (        );
            } finally {
                _releaseReply ($in);
            }
  } // listRecordID

  public String findRecord (String recordID)
  {
            org.omg.CORBA.portable.InputStream $in = null;
            try {
                org.omg.CORBA.portable.OutputStream $out = _request ("findRecord", true);
                $out.write_string (recordID);
                $in = _invoke ($out);
                String $result = $in.read_string ();
                return $result;
            } catch (org.omg.CORBA.portable.ApplicationException $ex) {
                $in = $ex.getInputStream ();
                String _id = $ex.getId ();
                throw new org.omg.CORBA.MARSHAL (_id);
            } catch (org.omg.CORBA.portable.RemarshalException $rm) {
                return findRecord (recordID        );
            } finally {
                _releaseReply ($in);
            }
  } // findRecord

  // Type-specific CORBA::Object operations
  private static String[] __ids = {
    "IDL:dsd/cms/DCMSCorba/DCMSInterface:1.0"};

  public String[] _ids ()
  {
    return (String[])__ids.clone ();
  }

  private void readObject (java.io.ObjectInputStream s) throws java.io.IOException
  {
     String str = s.readUTF ();
     String[] args = null;
     java.util.Properties props = null;
     org.omg.CORBA.ORB orb = org.omg.CORBA.ORB.init (args, props);
   try {
     org.omg.CORBA.Object obj = orb.string_to_object (str);
     org.omg.CORBA.portable.Delegate delegate = ((org.omg.CORBA.portable.ObjectImpl) obj)._get_delegate ();
     _set_delegate (delegate);
   } finally {
     orb.destroy() ;
   }
  }

  private void writeObject (java.io.ObjectOutputStream s) throws java.io.IOException
  {
     String[] args = null;
     java.util.Properties props = null;
     org.omg.CORBA.ORB orb = org.omg.CORBA.ORB.init (args, props);
   try {
     String str = orb.object_to_string (this);
     s.writeUTF (str);
   } finally {
     orb.destroy() ;
   }
  }
} // class _DCMSInterfaceStub
