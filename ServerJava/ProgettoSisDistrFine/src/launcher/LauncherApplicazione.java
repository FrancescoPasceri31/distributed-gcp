package launcher;

import java.rmi.RemoteException;

import entita.LoadBalancerImpl;

public class LauncherApplicazione {

	public static void main(String[] args) {
		try {
			LoadBalancerImpl lb = new LoadBalancerImpl();
		} catch (RemoteException e1) {
			e1.printStackTrace();
		}
	}

}
