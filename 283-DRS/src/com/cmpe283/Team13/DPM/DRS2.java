package com.cmpe283.Team13.DPM;

import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.RemoteException;




import com.vmware.vim25.ComputeResourceConfigSpec;
import com.vmware.vim25.HostConnectSpec;
import com.vmware.vim25.HostVMotionCompatibility;
import com.vmware.vim25.TaskInfo;
import com.vmware.vim25.VirtualMachineMovePriority;
import com.vmware.vim25.VirtualMachinePowerState;
import com.vmware.vim25.VirtualMachineRuntimeInfo;
import com.vmware.vim25.mo.ComputeResource;
import com.vmware.vim25.mo.Datacenter;
import com.vmware.vim25.mo.Folder;
import com.vmware.vim25.mo.HostSystem;
import com.vmware.vim25.mo.InventoryNavigator;
import com.vmware.vim25.mo.ManagedEntity;
import com.vmware.vim25.mo.ServiceInstance;
import com.vmware.vim25.mo.Task;
import com.vmware.vim25.mo.VirtualMachine;


public class DRS2 {
	private  String vmname ;
	private ServiceInstance si ;
	private VirtualMachine vm ;
	private Folder rootFolder;
	private String snapshotname;
	static String ip= "130.65.133.11";
	VMURLS vmurl;
	/**
	 * @param args
	 * @throws Exception 
	 */
	public DRS2() throws Exception{
		 vmurl = new VMURLS();
			
		 try {
				
				si =  vmurl.getServiceInstanceTeam();
				rootFolder =  si.getRootFolder();
				
				}catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
	}
	public void addingNewHost()
	{
		
		try 
		{	
			System.out.println("Adding new host to vCenter with ip::::: " + ip );

			ManagedEntity [] mes =  new InventoryNavigator(rootFolder).searchManagedEntities("Datacenter");
			Datacenter dc = new Datacenter(rootFolder.getServerConnection(),  mes[0].getMOR());
			HostConnectSpec hs = new HostConnectSpec();
			
			hs.hostName= ip;
			hs.userName ="root";
			hs.password = "12!@qwQW";
			hs.managementIp = "130.65.133.10";//130.65.133.60
			hs.setSslThumbprint("58:BC:09:CD:90:4F:65:29:DF:A0:B2:B6:86:6E:92:84:37:52:EB:E7");
			//hs.setSslThumbprint("C5:EF:CA:98:96:80:6D:2E:46:CB:B1:D2:BB:87:4A:18:AF:26:83:20");
			//hs.setSslThumbprint("90:BD:8C:C1:4E:F6:E9:A3:1A:DF:4B:FA:16:6B:9A:0D:73:DC:6A:F7");
			ComputeResourceConfigSpec crcs = new ComputeResourceConfigSpec();
			Task t = dc.getHostFolder().addStandaloneHost_Task(hs,crcs, true);
			if(t.waitForTask() == t.SUCCESS)
			{
				System.out.println("==============================================================");
				System.out.println("vHost is added successfully");
				System.out.println("==============================================================");
			}
			else
			{
				System.out.println("There is some error in adding host");
			}


		}   
		catch (Exception re)
		{
			System.out.println(re.toString());
			System.out.println("Unable to connect to Vsphere server");
		}
			}

	public void liveMigrateVM(String new_host_ip, String vmname)throws Exception{   //ip of host where to migrate and vm object of machine which needs to be migrate
		si= vmurl.getServiceInstanceTeam();
		rootFolder = si.getRootFolder();
		ip=new_host_ip;
		try{
			System.out.println("Starting the live migration");
			VirtualMachine vm = (VirtualMachine)new InventoryNavigator(rootFolder).searchManagedEntity("VirtualMachine", vmname);
			HostSystem newHost =(HostSystem) new InventoryNavigator(rootFolder).searchManagedEntity("HostSystem", ip);
			ComputeResource cr =(ComputeResource) newHost.getParent();
			String []checks =new String[]{"cpu","software"};
			HostVMotionCompatibility[] vmcs = si.queryVMotionCompatibility(vm, new HostSystem[]{newHost},checks);
			String[] comps= vmcs[0].getCompatibility();
			if(checks.length !=comps.length){
				System.out.println("CPY/software not compatible");
				return;
			}
			Task task =vm.migrateVM_Task(cr.getResourcePool(), newHost, VirtualMachineMovePriority.highPriority, VirtualMachinePowerState.poweredOn);
			if(task.waitForMe()==Task.SUCCESS){
				System.out.println("==============================================================");
				System.out.println("Live motion of virtual machine done successfully");
				System.out.println("==============================================================");
			}
			else{
				System.out.println("Live migration failed");
				TaskInfo info = task.getTaskInfo();
				System.out.println(info.getError().getFault());
			}
		}catch (Exception e){
			System.out.println(e.toString());
		}
		
	}
	
	
	public void coldMigrateVM(String new_host_ip, String vmName){
		try
		{si= vmurl.getServiceInstanceTeam();
			
		VirtualMachine vm = (VirtualMachine)new InventoryNavigator(rootFolder).searchManagedEntity("VirtualMachine", vmName);
		HostSystem newHost = (HostSystem) new InventoryNavigator(rootFolder).searchManagedEntity("HostSystem",new_host_ip);
		ComputeResource cr = (ComputeResource) newHost.getParent();
		Task task = vm.migrateVM_Task(cr.getResourcePool(),newHost,	VirtualMachineMovePriority.lowPriority,VirtualMachinePowerState.poweredOff);
		if(task.waitForTask() == task.SUCCESS)
		{
			System.out.println("==============================================================");
		System.out.println("Migration to new host completed.");
		System.out.println("==============================================================");
		}else{
			System.out.println("cold migration failed");
			TaskInfo info = task.getTaskInfo();
			System.out.println(info.getError().getFault());
		}
		   	
		} 
		catch (Exception e) 
		{
		// TODO Auto-generated catch block
		e.printStackTrace();
		}
		   
		    }
	
	public boolean  stateOfVM(String vmname)
    {
    boolean res=false;
    Folder folder=si.getRootFolder();
    try{
    ManagedEntity mes = new InventoryNavigator(folder).searchManagedEntity("VirtualMachine", vmname);
    VirtualMachine vm= (VirtualMachine) mes;
    System.out.println("VM name "+ vm.getName());
    VirtualMachineRuntimeInfo vmri=vm.getRuntime();
    String state=vmri.getPowerState().toString();
    if(state.contains("poweredOn"))
    res=true;
    else res=false;
    }
    catch(Exception e)
    {
    	System.out.println("Error Getting ");
    e.printStackTrace();
    }
    return res;
   
    }
		
	public static void main(String[] args)throws Exception{
		DRS2 newDrs= new DRS2();
		System.out.println("==============================================================");
		System.out.println("Running Dynamic Resource Scheduler 2..........");
		System.out.println("==============================================================");
		newDrs.addingNewHost();
		DPMFunctions dpm= new DPMFunctions();
		String vmName= dpm.getAllHostMeticsSorted();
		System.out.println("VmName " + vmName);
		
		if (newDrs.stateOfVM(vmName)){
			System.out.println("==============================================================");
			System.out.println("The state of VM "+ vmName +" is powered on. Migrating in Host "+ ip);
			System.out.println("==============================================================");
			newDrs.liveMigrateVM(ip, vmName);
		}
		else{
			System.out.println("==============================================================");
			System.out.println("The state of VM "+ vmName +" is powered off. Migrating in Host "+ ip);
			System.out.println("==============================================================");
			newDrs.coldMigrateVM(ip, vmName);
		}
	}
	//Migrate vm based on its power
	
}
