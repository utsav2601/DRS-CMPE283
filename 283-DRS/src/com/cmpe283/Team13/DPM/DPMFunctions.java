package com.cmpe283.Team13.DPM;

import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.Map.Entry;

import com.vmware.vim25.InvalidProperty;
import com.vmware.vim25.RuntimeFault;
import com.vmware.vim25.VirtualMachineRuntimeInfo;
import com.vmware.vim25.mo.*;


public class DPMFunctions {
	
	public ServiceInstance si;
	public static Folder rf;
	VMURLS vmurl;
	
	public DPMFunctions()

	{
		try {
			vmurl = new VMURLS();
			si =  vmurl.getServiceInstanceTeam();
			rf =  si.getRootFolder();
					} 
		catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}
	
	public static <K extends Comparable,V extends Comparable> Map<K,V> sortByValues(Map<K,V> map){
        List<Map.Entry<K,V>> entries = new LinkedList<Map.Entry<K,V>>(map.entrySet());
      
        Collections.sort(entries, new Comparator<Map.Entry<K,V>>() {

            @Override
            public int compare(Entry<K, V> o1, Entry<K, V> o2) {
                return o2.getValue().compareTo(o1.getValue());
            }
        });
      
        //LinkedHashMap will keep the keys in the order they are inserted
        //which is currently sorted on natural ordering
        Map<K,V> sortedMap = new LinkedHashMap<K,V>();
      
        for(Map.Entry<K,V> entry: entries){
            sortedMap.put(entry.getKey(), entry.getValue());
        }
      
        return sortedMap;
    }

	public boolean  stateOfVM(String vmname)
    {
    boolean res=false;
    Folder folder=si.getRootFolder();
    try{
    ManagedEntity mes = new InventoryNavigator(folder).searchManagedEntity("VirtualMachine", vmname);
    VirtualMachine vm= (VirtualMachine) mes;
   
    VirtualMachineRuntimeInfo vmri=vm.getRuntime();
    String state=vmri.getPowerState().toString();
    if(state.contains("poweredOn"))
    res=true;
    else res=false;
    }
    catch(Exception e)
    {
    e.printStackTrace();
    }
    return res;
   
    }

	public  String getAllHostMeticsSorted() 
	{
		String[] hostList=DPMFunctions.getAllHosts();
		Map<String, Long> hostMetrics = new HashMap<String, Long>();
		Map<String, Long> vmMetrics = new HashMap<String, Long>();
		String vmName="";
		for(String str : hostList)
		{
			
			try{
				HostThread h;
			h=new HostThread(str);
		//	System.out.println("Getting VMs in HOST" + str + "Value " + h.run());
			hostMetrics.put(str, h.run());
			//hostCpuCount.add(h.run());
			
			}catch(Exception e){e.printStackTrace();}
		}
		Compare comparator = new Compare(hostMetrics);

	    Map<String, Long> newMap = new TreeMap<String, Long>(comparator);
	    newMap.putAll(hostMetrics);
	    HostSystem host =null;
	    Iterator iter = newMap.keySet().iterator();
	    if(iter.hasNext()) {
			String key=(String)iter.next();
			System.out.println("==============================================================");
			System.out.println("The host with maximum cpu utilization is  "+key);
			System.out.println("==============================================================");
	      	host=DPMFunctions.getHost(key);
		}
	    try{
		VirtualMachine[] virtualmachine = host.getVms();
		
		for (VirtualMachine vm : virtualmachine){
			
			vmMetrics.put(vm.getName(), new VMThread(vm.getName()).run());
		}
		Compare comparatorVM = new Compare(vmMetrics);
		Map<String, Long> newVmMap = new TreeMap<String, Long>(comparatorVM);
	    newVmMap.putAll(vmMetrics);
	    Iterator iterVm = newVmMap.keySet().iterator();
	    
	    if(iterVm.hasNext()) {
			String key=(String)iterVm.next();
			System.out.println("==============================================================");
			System.out.println("The Virtual Machine with maximum cpu utilization is  "+key);
			System.out.println("==============================================================");
			vmName=key;  
		}
	    
	    }catch(Exception e)
	    {
	    	
	    }
	    
	    return vmName; 
	}

	
	public void poweroffhost(String hostname) throws InterruptedException
	{
		try {
			HostSystem host = (HostSystem) new InventoryNavigator(si.getRootFolder()).searchManagedEntity(
					"HostSystem", hostname);
			//host.getHostCpuSchedulerSystem();
			if (host.getVms().length==0)
			{
				Task m_task = host.enterMaintenanceMode(600, false);
				String status= m_task.waitForTask();
				if (status==Task.SUCCESS)
				{
			//	host.shutdownHost_Task(true);
				}
				else 
					System.out.println("Host can't enter maintainance mode");
			}
			
		} catch (InvalidProperty e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (RuntimeFault e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	public void powerOffvm(String vmname) 
    {
        try 
        {
             // your code here
        	 VirtualMachine vm = (VirtualMachine) new InventoryNavigator(si.getRootFolder()).searchManagedEntity(
					"VirtualMachine", vmname);
        	System.out.println("Powering off virtual machine '"+vm.getName() +"'. Please wait...");     
        	Task t=vm.powerOffVM_Task();
        	if(t.waitForTask()== Task.SUCCESS)
        	{
	        	System.out.println("Virtual machine powered off.");
	        	System.out.println("====================================");
        	}
        	else
        		System.out.println("Power off failed");
        	
        } catch ( Exception e ) 
        { 
        	System.out.println( e.toString() ) ; 
        }
    }
	
//	public void Performancedata()
//	{
//	//	ServiceInstance si = new ServiceInstance(url,SJSULabConfig.getvCenterUsername(),SJSULabConfig.getPassword(), true);
//		VirtualMachine host = (VirtualMachine) new InventoryNavigator(
//				si.getRootFolder()).searchManagedEntity(
//				"VirtualMachine", ip);
//		
//		PerformanceManager pm =si.getPerformanceManager();
//		
//		
//	}
	public static String[] getAllHosts()
	{
		String sample[]={""};
    	try
    	{
    		ManagedEntity[] hosts =  new InventoryNavigator(rf).searchManagedEntities("HostSystem");
    		if(hosts.length <=1) 
    		{
    			System.out.println("no more hosts present.. Should create new one..");
    			//return false;
    		}
    		else
    		{
    			System.out.println("Multiple hosts present.. Searching in vCenter..");
    		}    		  		
    		//System.out.println("Lists of new hosts: ");
    		//System.out.println(hosts.length);
    		String[] hostnames= new String[hosts.length];
			for(int i=0;i<hosts.length;i++)
			{
				hostnames[i]=hosts[i].getName();
				
				
			}
			return hostnames;
    		
    	}
    	catch(Exception e)
    	{
    		System.out.println(e.toString());
    	}
    	
    	return sample;
	}
	
	
	public static String[] getAllHostsNew()
	{
		String sample[]={""};
    	try
    	{
    		ManagedEntity[] hosts =  new InventoryNavigator(rf).searchManagedEntities("HostSystem");
    		if(hosts.length <=1) 
    		{
    			System.out.println("no more hosts present.. Should create new one..");
    			//return false;
    		}
    		else
    		{
    			System.out.println("Multiple hosts present.. Searching in vCenter..");
    		}    		  		
    		//System.out.println("Lists of new hosts: ");
    		//System.out.println(hosts.length);
    		String[] hostnames= new String[hosts.length];
    		System.out.println("Host length" + hosts.length);
			int count = 0;
    		for(int i=0;i<hosts.length;i++)
			{
				if(((HostSystem)hosts[i]).getVms().length>=0)
				{	
					hostnames[i]=hosts[i].getName();
					System.out.println("Host Name "+hosts[i].getName());
				}
				//System.out.println(hostnames[i]);
			}
			return hostnames;
    		
    	}
    	catch(Exception e)
    	{
    		System.out.println(e.toString());
    		System.out.println("Error in getting hosts");
    		return sample;
    	}
    	
    	
	}
	
	//Added new 
	public static HostSystem getHost(String ip)
	{
		
    	try
    	{
    		ManagedEntity hosts = new InventoryNavigator(rf).searchManagedEntity("HostSystem",ip);
    		
			return (HostSystem)hosts;
    		
    	}
    	catch(Exception e)
    	{
    		System.out.println(e.toString());
    	}
    	return null;
	}
	
	
	
}
