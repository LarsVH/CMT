package be.ac.vub.wise.cmtserver.rest;

import be.ac.vub.wise.cmtserver.db.DbComponent;
import be.ac.vub.wise.cmtserver.db.IDbComponent;
import be.ac.vub.wise.cmtserver.drools.DroolsComponent;
import be.ac.vub.wise.cmtserver.drools.IDroolsComponent;

import be.ac.vub.wise.cmtserver.blocks.IFactType;
import be.ac.vub.wise.cmtserver.blocks.Template;
import java.util.HashSet;

import javax.swing.JFrame;



/// dont forget start up to add facts from db to drools

public class Main {
	

    public static final void main(String[] args) {
        
    	final IDbComponent database  = DbComponent.getDbComponent();
    	IDroolsComponent drools = DroolsComponent.getDroolsComponent();
    	
    	HashSet<IFactType> facts = database.getFacts();
    	for(IFactType fact : facts){
    		drools.addFact(fact);
    	}
    	
    	
    	
    	JFrame fr = new JFrame();
    	fr.setSize(200, 200);
    	fr.setVisible(true);
    	fr.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    	 Runtime.getRuntime().addShutdownHook(new Thread() {
    		    @Override
    		    public void run() {    
    		     System.out.println("Inside Add Shutdown Hook : " + Thread.currentThread().getName()) ;
    		     database.closeDb();
    		    }
    		   }); 
    	
    	   System.out.println("in main----------------------------------------------");
    	
       
    }


}
