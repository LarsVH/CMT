package be.ac.vub.wise.cmtserver.db;

import java.util.Set;


/**
 * The Strategy interface for the different underlying database technologies.
 * @version 1.0 May 2013
 * @author Sandra Trullemans, strullem@vub.ac.be
 */
public interface IDatabaseConnector {
	
	public boolean init();
	
	public boolean resetDb();
	
	public boolean checkSettings();
	
	public void startAutoCommit(int amount, long time);
	
	public long getInitId();
	
	public boolean create(Set<Object> elements);
	
	public boolean create(Object element);
	
	public Result read(String selectQuery);
	
	public Result read(Class<?> classConstraint, String filter);
	
	public boolean update(Set<Object> elements);

	public boolean update(Object element);
	
	public boolean delete(Set<Object> elements);
	
	public boolean delete(Object element);
	
	public boolean commit();
	
	public boolean closeDb();
	
        public void closeAndOpenDb();
}
