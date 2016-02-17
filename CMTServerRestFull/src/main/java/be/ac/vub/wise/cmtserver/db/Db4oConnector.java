package be.ac.vub.wise.cmtserver.db;

import java.io.File;
import java.util.Set;
import java.util.StringTokenizer;



import com.db4o.Db4o;
import com.db4o.Db4oEmbedded;
import com.db4o.ObjectContainer;
import com.db4o.ObjectSet;
import com.db4o.activation.ActivationPurpose;
import com.db4o.query.Query;
import com.db4o.config.EmbeddedConfiguration;
import com.db4o.ext.StoredClass;
import com.db4o.ta.Activatable;
import com.db4o.ta.TransparentActivationSupport;


/**
 * One of the ConcreteStrategy implementations using the Db4O database technology.
 * @version 1.0 May 2013
 * @author Sandra Trullemans, strullem@vub.ac.be
 */
public class Db4oConnector implements IDatabaseConnector {
	public static ObjectContainer db = null;
	
	/**
	 * Returns the database object container.
	 * @return the object container.
	 */
	public static ObjectContainer getDb(){
		return db ;
	}
	
	/**
	 * Checks if the settings instance is present in the database.
	 * @return true if the setting object is present.
	 */
	public boolean checkSettings(){
		int numberOfObjects = 0;
	    for(StoredClass storedClass : db.ext().storedClasses()){
	        if(storedClass.getName().contains("Settings")){
	            numberOfObjects += storedClass.instanceCount();
	        }
	    }
	    if(numberOfObjects == 1)
	        	return true;
	    return false;
	}
	
	/**
	 * Initialize the database file and sets the default properties.
	 * @return true if the database file is opened. 
	 */
	public boolean init() {
		System.out.println(Db4oDefaultProperties.DB4O_FILENAME);
		db = Db4oEmbedded.openFile(Db4oEmbedded.newConfiguration(), Db4oDefaultProperties.DB4O_FILENAME);	
	        db.ext().configure().add(new TransparentActivationSupport());
	
         //       db.ext().configure().classActivationDepthConfigurable(true);
//		db.ext().configure().updateDepth(100);
//		
//		db.ext().configure().activationDepth(100);
		return true;
	}
	
	/**
	 * Returns the initial id number used to uniquely identify the abstractrslelements.
	 * @return the initial id number.
	 */
	public long getInitId(){
		throw new UnsupportedOperationException("Can only be used by the Delegator.");
	}
	
	/**
	 * Starts auto-commit.
	 * Commit to the database after the specified amount of interactions and after the specified time.
	 * @param amount the amount of interactions between two commits.
	 * @param time the time between two commits.
	 */
	public void startAutoCommit(int amount, long time){
		throw new UnsupportedOperationException("Can only be used by the Delegator.");
	}

	/**
	 * Creates a set of root elements in the database.
	 * @param elements the set of elements to be created.
	 * @return true if the database has created the elements.
	 */
	public boolean create(Set<Object> elements) {
		for(Object element: elements){
			//db.store(element);
			db.ext().store(element);
		}
		return true;
	}

	/**
	 * Creates a root elements in the database.
	 * @param element the elements to be created.
	 * @return true if the database has created the element.
	 */
	public boolean create(Object element) {
		//db.store(element);
		db.ext().store(element, 15);
		return true;
	}

	/**
	 * Updates a set of instances in the database.
	 * @param elements the instances to be updated. 
	 * @return true if the database has updated the instances.
	 */
	public boolean update(Set<Object> elements) {
		for(Object element: elements){
			db.ext().store(element);
		}
		return true;
	}
	
	/**
	 * Updates an instance in the database.
	 * @param element the instance to be updated. 
	 * @return true if the database has updated the instance.
	 */
	public boolean update(Object element) {
		//db.store(element);
		db.ext().store(element);
		return true;
	}

	/**
	 * Deletes a set of instances in the database.
	 * @param elements the instances to be deleted. 
	 * @return true if the database has deleted the instances.
	 */
	public boolean delete(Set<Object> elements) {
		for(Object element: elements){
			db.delete(element);
		}
		return true;
	}
	
	/**
	 * Deletes an instances in the database.
	 * @param element the instance to be deleted. 
	 * @return true if the database has deleted the instance.
	 */
	public boolean delete(Object element) {
		db.delete(element);
		return true;
	}

	/**
	 * Resets the current database.
	 * @return true if the database is reseted. 
	 */
	public boolean resetDb() {
		db.close();
		new File(Db4oDefaultProperties.DB4O_FILENAME).delete();
		db = Db4oEmbedded.openFile(Db4oEmbedded.newConfiguration(), Db4oDefaultProperties.DB4O_FILENAME);
		return true;
	}

	/**
	 * TODO Returns a set of elements based on the SQL-like SELECT query. (see JDOQL) 
	 * @param selectQuery the query to be executed.
	 * @return the result of the query.
	 */
	public Result read(String selectQuery) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Db4o connection to be implemented");
	}

	/**
	 * Returns a set of elements based on the query format class constraint plus filter. (see JDOQL)	
	 * @param classConstraint the class to be queried.
	 * @param filter the filter to be used in the query.
	 * @return the result of the query.
	 */
	public Result read(Class<?> classConstraint, String filter) {
		Query q = db.query();
		q.constrain(classConstraint);
		if(!filter.equals("")){
			parse(q, filter);
		}
		ObjectSet<Object> result = q.execute();
		Result res = new Result(result);
		for(Object obj : res.getResults()){
                    db.ext().activate(obj, 15);
                }
                return res;
	}
	
	/**
	 * Parses a query.
	 * @param q the query to be parsed.
	 * @param filter the filter to be parsed.
	 */
	private void parse(Query q, String filter){
		StringTokenizer tokens = new StringTokenizer(filter, " \"");
		if(tokens.countTokens() >=4){
			String var = tokens.nextToken();
			String op = tokens.nextToken();
			String val = tokens.nextToken();
			for(int i = 0 ; i < tokens.countTokens();i++){
				val = " "+ tokens.nextToken();
			}
			if(op.equals("==")){
				q.descend(var).constrain(val);
			}else{
				throw new UnsupportedOperationException("Db4o extended parsing to be implemented; filter was '"+"'");
			}
		}else{
		if(tokens.countTokens() == 3){
			String var = tokens.nextToken();
			String op = tokens.nextToken();
			String val = tokens.nextToken();
			if(op.equals("==")){
				q.descend(var).constrain(val);
			}else{
				throw new UnsupportedOperationException("Db4o extended parsing to be implemented; filter was '"+"'");
			}
		}else{
			
			throw new UnsupportedOperationException("Db4o extended parsing to be implemented; filter was '"+"'");
		}
		}
	}
	
	/**
	 * Commits the create, store and delete calls to disk.
	 * @return true if the database has finished the commit.
	 */
	public boolean commit(){
		db.commit();
		return true;
	}

	/**
	 * Closes the database connection.
	 * @return true if the database is closed.
	 */
	public boolean closeDb() {
		db.commit();
		db.close();
		return true;
	}
        
        public void closeAndOpenDb(){
                db.close();
		db = Db4oEmbedded.openFile(Db4oEmbedded.newConfiguration(), Db4oDefaultProperties.DB4O_FILENAME);
        }
}
