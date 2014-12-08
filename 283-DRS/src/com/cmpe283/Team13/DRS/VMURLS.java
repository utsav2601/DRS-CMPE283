package com.cmpe283.Team13.DRS;

import java.net.URL;
import java.rmi.RemoteException;

import com.vmware.vim25.InvalidProperty;
import com.vmware.vim25.RuntimeFault;
import com.vmware.vim25.mo.Folder;
import com.vmware.vim25.mo.InventoryNavigator;
import com.vmware.vim25.mo.ManagedEntity;
import com.vmware.vim25.mo.ServiceInstance;

public class VMURLS {
	// Common between Admin vCenter and Team vCenter
	public String username() {
		return "administrator";
	}

	public String password() {
		return "12!@qwQW";
	}

	// Team vCenter
	public URL urlTeam() throws Exception {
		return new URL("https://130.65.133.10/sdk");
	}

	public ServiceInstance getServiceInstanceTeam() throws Exception {
		return new ServiceInstance(urlTeam(), username(), password(), true);
	}

	public Folder getRootFolderTeam() throws Exception {
		return getServiceInstanceTeam().getRootFolder();
	};

	public ManagedEntity[] mesTeam() throws Exception {
		return new InventoryNavigator(getRootFolderTeam())
				.searchManagedEntities("HostSystem");
	}

	// Admin vCenter
	public URL urlVcenter() throws Exception {
		return new URL("https://130.65.132.14/sdk");
	}

	public ServiceInstance getServiceInstanceAdmin() throws Exception {
		return new ServiceInstance(urlVcenter(), username(), password(), true);
	}

	public Folder getRootFolderAdmin() throws Exception {
		return getServiceInstanceAdmin().getRootFolder();
	}

	public ManagedEntity[] mesAdmin() throws Exception {
		return new InventoryNavigator(getRootFolderAdmin())
				.searchManagedEntities("HostSystem");
	}

}