package be.ac.vub.wise.cmtserver.db;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;


/**
 * Represents the result instance for database query results.
 * @version 1.0 May 2013
 * @author Sandra Trullemans, strullem@vub.ac.be
 */
public class Result {

	private Collection<Object> res = null;
	
	/**
	 * Instantiates a result object with the specified collection of elements.
	 * @param results the collection of query results.
	 */
	public Result(Collection<Object> results) {
		this.res = results;
		if (res == null){
			res = new ArrayList<Object>();
		}
	}

	/**
	 * Sets the result object with the specified collection of elements.
	 * @param results the collection of query results.
	 */
	public void setResults(Collection<Object> results) {
		this.res = results;
		if (res == null){
			res = new ArrayList<Object>();
		}
	}
	
	/**
	 * Returns the collection of query results for the result instance.
	 * @return the collection of query results
	 */
	public Collection<Object> getResults(){
		return res;
	}

	/**
	 * Returns the first query result element of the collection.
	 * @return the first query result.
	 */
	public Object getFirst() {
		if(res == null || res.size() == 0){
			return null;
		}else{
			Iterator<Object> iter=res.iterator();
			return iter.next();
		}
	}
	
}
