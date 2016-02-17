package be.ac.vub.wise.cmtserver.db;


/**
 * Is the root class of all RSL model elements.
 * @version 1.0 May 2013
 * @author Sandra Trullemans, strullem@vub.ac.be
 */
public class AbstractRslElement {
	
	/** the unique id of the instance.*/
	private String id;
	/** the name of the instance.*/
	private String name = null;
	/** counter to set when restarting the iServer*/
	private static long ctr = 0; 
	
	/**
	 * Constructs a new instance with a unique id.
	 */
	protected AbstractRslElement(){
		id = String.valueOf(ctr++);
	}
	
	/**
	 * Sets the counter to a specified initial value. 
	 * Note that the counter is set by the setting instance when starting up the iServer.
	 * @param count the specified value of the initial counter. 
	 */
	public static void setCtr(long count){ 
		ctr = count;
	} 
	
	/**
	 * Returns the counter value.
	 * @return the counter value.
	 */
	public static long getCtr(){
		return ctr;
	}
	

	
	/**
	 * Returns the id of the abstract element.
	 * @return the id of the abstract element.
	 */
	public String getId() {
		return id;
	}
	
        
	/**
	 * Returns a string representation of the instance.
	 * @return the string representation of the instance.
	 */
	public String toString(){
		 return ("["+id+"]");
	}

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 53 * hash + (this.id != null ? this.id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(java.lang.Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final AbstractRslElement other = (AbstractRslElement) obj;
        if ((this.id == null) ? (other.id != null) : !this.id.equals(other.id)) {
            return false;
        }
        return true;
    }

	
	
	
		
}


