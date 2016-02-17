package be.ac.vub.wise.cmtserver.db;

/**
 * Contains the default db4o file path.
 * @version 1.0 May 2013
 * @author Sandra Trullemans, strullem@vub.ac.be
 */
public class Db4oDefaultProperties {

	final static String DB4O_FILENAME = System.getProperty("user.dir") + System.getProperty("file.separator") + "cmt.db4o";
	
}
