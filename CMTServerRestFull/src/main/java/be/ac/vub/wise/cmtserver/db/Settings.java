package be.ac.vub.wise.cmtserver.db;

/**
 * Represents the settings for the iServer unique id value storage.
 * Note that this setting class is a Singleton to assure that there exists only one initial id value.
 * @version 1.0 May 2013
 * @author Sandra Trullemans, strullem@vub.ac.be
 */
public class Settings{
	
	private String name;
	private long value;
	private static Settings settings = null;
	
	/**
	 * Constructs a setting instance with the specified name.
	 * @param name the name of the setting instance.
	 */
	private Settings(String name){
		this.setName(name);
	}

	/**
	 * Returns the singleton setting instance.
	 * @param name the name of the setting instance.
	 * @return the setting instance.
	 */
	public static Settings getSettings(String name){
		if(settings == null){
			settings = new Settings(name);
			return settings;
		}
		return settings;
	}
	
	/**
	 * Sets the value of the setting instance namely the unique id value to store.
	 * @param id the value of the id to be stored.
	 */
	public void setValue(long id){
		value = id;
	}
	
	/**
	 * Returns the value of the setting instance namely the current highest id value.
	 * @return the value of the instance.
	 */
	public long getValue(){
		return value;
	}

	/**
	 * Returns the name of the setting instance.
	 * @return the name of the instance. 
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets the name of the setting instance.
	 * @param name the name to be set.
	 */
	public void setName(String name) {
		this.name = name;
	}
	
}
