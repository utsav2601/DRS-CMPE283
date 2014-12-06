package com.cmpe283.Team13;


import java.net.URL;


import com.vmware.vim25.VirtualMachineCloneSpec;
import com.vmware.vim25.VirtualMachineConfigSpec;
import com.vmware.vim25.VirtualMachineRelocateSpec;
import com.vmware.vim25.VirtualMachineRuntimeInfo;
import com.vmware.vim25.mo.ComputeResource;
import com.vmware.vim25.mo.Datacenter;
import com.vmware.vim25.mo.Folder;
import com.vmware.vim25.mo.HostSystem;
import com.vmware.vim25.mo.InventoryNavigator;
import com.vmware.vim25.mo.ManagedEntity;
import com.vmware.vim25.mo.ResourcePool;
import com.vmware.vim25.mo.ServiceInstance;
import com.vmware.vim25.mo.Task;
import com.vmware.vim25.mo.VirtualMachine;

public class createNewVM {
	VMURLS vmurlTeam = new VMURLS();
	
	private String dcName = "Team13";
	private String ip;
	private Folder rootFolder;
	//private long diskSizeKB = 60000;
	public createNewVM(String ip)
	{
		this.ip=ip;
		System.out.println("Host ip received is : "+ ip);
	}
	
	public void powerOn(VirtualMachine	vm) 
    {
        try 
        {
              // your code here
        	System.out.println("Powering on virtual machine '"+vm.getName() +"'. Please wait...");     
        	Task t=vm.powerOnVM_Task(null);
        	if(t.waitForTask()== Task.SUCCESS)
        	{
	        	System.out.println("Virtual machine powered on.");
	        	System.out.println("====================================");
        	}
        	else
        		System.out.println("Power on failed / VM already powered on...");
        } 
        catch ( Exception e ) 
        { 
        	System.out.println( e.toString() ) ;
        }
    }

	public void doClone() throws Exception{	
         System.out.println("Inside DAO class");
		
		String vmName="NEWLY-CREATED-VM";
		

		try
		{
			
			ServiceInstance si = vmurlTeam.getServiceInstanceTeam();
			this.rootFolder = vmurlTeam.getRootFolderTeam();
			VirtualMachine	vm = (VirtualMachine) new InventoryNavigator(this.rootFolder).searchManagedEntity("VirtualMachine", "VM1");// Template Name
			
			Datacenter dc = (Datacenter) new InventoryNavigator(this.rootFolder).searchManagedEntity("Datacenter", dcName);
			ManagedEntity host =new InventoryNavigator(this.rootFolder).searchManagedEntity("HostSystem", ip);
			ComputeResource cr= (ComputeResource)host.getParent();
			System.out.println(host);
			
			if(vm==null)
			{
				System.out.println("No VM " + vm + " found");
				si.getServerConnection().logout();
				return;
			}
			//create vm config spec



			VirtualMachineRelocateSpec reloc =new VirtualMachineRelocateSpec();
			reloc.setPool(cr.getResourcePool().getMOR());
//			System.out.println("RP MOR : "+ rp.getMOR());
			System.out.println("Host MOR: "+ host.getMOR());
			reloc.setHost(host.getMOR());
			

			
				VirtualMachineCloneSpec cloneSpec = new VirtualMachineCloneSpec();
				cloneSpec.setLocation(reloc);
				cloneSpec.setPowerOn(false);
				cloneSpec.setTemplate(false);
				//cloneSpec.setConfig(vmSpec);
				System.out.println( "Before Calling");
				Task task = vm.cloneVM_Task((Folder) vm.getParent(), 
						vmName, cloneSpec);
				System.out.println("Launching the VM clone task. " +
						"Please wait ...");

				String status = task.waitForMe();
				if(status==Task.SUCCESS)
				
				{
					System.out.println("VM got cloned successfully.");
					System.out.println("Powering on newly created VM.. Please wait.. ");
					powerOn(vm);

				}
				else
				{
					System.out.println("Failure -: VM cannot be cloned");
				}
			

		}catch(Exception e){
			e.printStackTrace();
		
		}
	}
		}
		