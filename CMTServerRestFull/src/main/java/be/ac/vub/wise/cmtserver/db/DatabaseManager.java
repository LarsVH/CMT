package be.ac.vub.wise.cmtserver.db;

import java.util.Properties;
import java.util.Set;
import java.util.logging.Logger;



/**
 * This class is the Delegator for the IDatabaseConnector interface.
 * @version 1.0 May 2013
 * @author Sandra Trullemans, strullem@vub.ac.be
 */
public class DatabaseManager implements IDatabaseConnector{
	
	private final static Logger logger = Logger.getLogger(DatabaseManager.class.getName());
	public enum DbSource {Db4o, Jdo, Jdo_Enhanced, no_db}
	private static DatabaseManager instance = null;
	private static DbSource currentSource = null;
	private Settings settings = null;
	private IDatabaseConnector database = null;
	private int amount = 0;
	private long time = 0;
	private int counter = 0;
	
	/**
	 * Constructs a databaseManager instance.
	 * Note that this class is a Singleton.
	 * @param source the specific database type to be used as a ConcreteStrategy.
	 * @param properties the specific database properties. 
	 */
	private DatabaseManager(DbSource source, Properties properties) {
		logger.info("Getting connection to db '"+source+"'...");
		if(source == DbSource.Db4o){
			database = new Db4oConnector();
			database.init();
		} else{
			throw new UnsupportedOperationException("Only Db4o supported for the moment.");
		}
	
		currentSource = source;		

		if(!database.checkSettings()){
			settings = Settings.getSettings("idSize");
			settings.setValue(1);
			database.create(settings);
		} else{
			setInitId();
		}
		logger.info("Db '"+source+"' handle ready");
	}

	/**
	 * Returns the singleton databaseManager instance.
	 * @return the singleton databaseManager instance.
	 */
	public static DatabaseManager getCurrentDatabaseManager(){
		if(instance == null){
			logger.severe("Database is not instantiated - call DatabaseManager.getDatabaseManager(source) before attempting to store or get elements.");
		}
		return instance;
	}
	
	/**
	 * Returns the singleton databaseManager instance.
	 * @param source the specific database type to be used as a ConcreteStrategy.
	 * @param properties the specific database properties. 
	 * @return the singleton databaseManager instance.
	 */
	public static DatabaseManager getDatabaseManager(DbSource source, Properties properties){
		if(instance == null){
			instance = new DatabaseManager(source, properties);
			return instance;
		}else{
			if(currentSource != source){
				instance = new DatabaseManager(source, properties);
				return instance;
			}else{
				return instance;
			}
		}
	}

	/**
	 * Sets the initial id number used to uniquely identify the abstractrslelements.
	 */
	private void setInitId (){
		settings = (Settings)database.read(Settings.class, "name == \"idSize\"").getFirst();
		settings.setValue(settings.getValue() + 2);
	}

	/**
	 * Returns the initial id number used to uniquely identify the abstractrslelements.
	 * @return the initial id number.
	 */
	public long getInitId(){
		return settings.getValue();
	}
	
	/**
	 * Starts auto-commit.
	 * Commit to the database after the specified amount of interactions and after the specified time.
	 * @param amount the amount of interactions between two commits.
	 * @param time the time between two commits.
	 */
	public void startAutoCommit(int amount, long time){
		this.amount = amount;
		this.time = time;
		Thread t = new Thread(){
			public void run(){
				long timePause = getTimeInterval();
				long starttime = System.currentTimeMillis();
				while(true){
				long currentTime = System.currentTimeMillis();
				if(currentTime >= (starttime + timePause) || getCounter() > getAmount()){
					starttime = System.currentTimeMillis();
					commit();
					}
				}
			}
		};
		t.start();
	}
	
	/**
	 * Returns the time interval between two commits.
	 * @return the time interval.
	 */
	private long getTimeInterval(){
		return time;
	}
	
	/**
	 * Returns the amount of interactions between two commits.
	 * @return the amount of interactions.
	 */
	private int getAmount(){
		return amount;
	}
	
	/**
	 * Returns the current counter value of the amount of interactions done so far.
	 * @return the current counter value.
	 */
	private int getCounter(){
		return counter;
	}
	
	/**
	 * Sets the counter to increase with one or resets the current counter.
	 * @param action the action to be done - one to increase the counter with one, zero to reset the counter.
	 */
	private void setCounter(int action){
		if(action == 1)
			counter++;
		counter = 0;
	}
	
	/**
	 * Initialize the database.
	 * @return true if database is initialised.
	 */
	public boolean init(){
		return database.init();
	}
	
	/**
	 * Checks if the settings instance is present in the database.
	 * @return true if the setting object is present.
	 */
	public boolean checkSettings(){
		return database.checkSettings();
	}
	
	/**
	 * Creates a set of root elements in the database.
	 * @param elements the set of elements to be created.
	 * @return true if the database has created the elements.
	 */
	public boolean create(Set<Object> elements) {
		setCounter(1);
		return database.create(elements);
	}
	
	/**
	 * Creates a root elements in the database.
	 * @param element the elements to be created.
	 * @return true if the database has created the element.
	 */
	public boolean create(Object element) {
		setCounter(1);
		settings.setValue(settings.getValue() + 1);
		return database.create(element);
	}

	/**
	 * Returns a set of elements based on the SQL-like SELECT query. (see JDOQL)
	 * @param selectQuery the query to be executed.
	 * @return the result of the query.
	 */
	public Result read(String selectQuery) {
		return database.read(selectQuery);
	}
	
	/**
	 * Returns a set of elements based on the query format class constraint plus filter. (see JDOQL)	
	 * @param classConstraint the class to be queried.
	 * @param filter the filter to be used in the query.
	 * @return the result of the query.
	 */
	public Result read(Class<?> classConstraint, String filter){
		return database.read(classConstraint, filter);
	}
	
	/**
	 * Updates a set of instances in the database.
	 * @param elements the instances to be updated. 
	 * @return true if the database has updated the instances.
	 */
	public boolean update(Set<Object> elements) {
		setCounter(1);
		return database.update(elements);
	}
	
	/**
	 * Updates an instance in the database.
	 * @param element the instance to be updated. 
	 * @return true if the database has updated the instance.
	 */
	public boolean update(Object element) {
		setCounter(1);
		return database.update(element);
		
	}

	/**
	 * Deletes a set of instances in the database.
	 * @param elements the instances to be deleted. 
	 * @return true if the database has deleted the instances.
	 */
	public boolean delete(Set<Object> elements) {
		setCounter(1);
		return database.delete(elements);
	}

	/**
	 * Deletes an instances in the database.
	 * @param element the instance to be deleted. 
	 * @return true if the database has deleted the instance.
	 */
	public boolean delete(Object element) {
		setCounter(1);
		return database.delete(element);
	}
	
	/**
	 * Resets the current database.
	 * Note that the initial unique id of the abstractrslelements are set to zero.
	 * @return true if the database is reseted. 
	 */
	public boolean resetDb(){
		settings.setValue(0);
		AbstractRslElement.setCtr(0);
		return database.resetDb();
	}

	/**
	 * Commits the create, store and delete calls to disk.
	 * Note that before every commit an update is done on the setting object to guarantee that 
	 * the unique id is consistent.
	 * @return true if the database has finished the commit.
	 */
	public synchronized boolean commit(){
		setCounter(0);
		database.update(settings);
		return database.commit();
	}

	/**
	 * Closes the database connection.
	 * @return true if the database is closed.
	 */
	public boolean closeDb() {
		return database.closeDb();
	}

        public void closeAndOpenDb(){
            database.closeDb();
            database = null;
            database = new DatabaseManager(DbSource.Db4o, null);
        }
}
