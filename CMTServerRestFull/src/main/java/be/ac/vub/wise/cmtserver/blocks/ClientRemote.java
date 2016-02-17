package be.ac.vub.wise.cmtserver.blocks;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ClientRemote extends Remote{
	 public void newFactTypeAdded(IFactType newFactType) throws RemoteException;
	 public void newFunctionClassAdded(IFunctionClass newFunctionClass) throws RemoteException;
	 public void newContextFormAdded(Template form) throws RemoteException;
	 public void contextFormRemoved(Template form) throws RemoteException;
	 public void newFactAdded(IFactType newFact) throws RemoteException;
	 public void actionNotification(Action action) throws RemoteException;
	 public void actionAdded(Action action) throws RemoteException;
 	public void actionRemoved(Action action) throws RemoteException; 
}
