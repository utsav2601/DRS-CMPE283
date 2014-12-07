package com.cmpe283.Team13.DPM;


import java.rmi.RemoteException;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.ArrayList;
import java.util.Iterator;

import com.vmware.vim25.InvalidProperty;
import com.vmware.vim25.RuntimeFault;
import com.vmware.vim25.mo.HostSystem;
import com.vmware.vim25.mo.VirtualMachine;

public class DPMMain {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		try {
			DPMFunctions dpm = new DPMFunctions();
			HostThread h;
			Map<String, Long> hostMetrics = new HashMap<String, Long>();
			Map<String, Long> sortedMetrics = new HashMap<String, Long>();
			//List<Long> hostCpuCount = new ArrayList<Long>();
			//String[] hostListTest=DPMFunctions.getAllHostsNew();
			
		do
		{
			String[] hostList=DPMFunctions.getAllHostsNew();
			for(String str : hostList)
			{
				try{
					System.out.println("hostList" +str);
				h=new HostThread(str);
				hostMetrics.put(str, h.run());
				//hostCpuCount.add(h.run());
				
				}catch(Exception e){e.printStackTrace();}
			}
			 sortedMetrics=compareEntities(hostMetrics);
			HostSystem  host=null;
			HostSystem host2=null;
			Iterator<String> iter = sortedMetrics.keySet().iterator();
			if(iter.hasNext()) {
				String key=iter.next();
				System.out.println("key " + key);
				host=DPMFunctions.getHost(key);
				System.out.println("Host to be shut down " + host.getName());
			}
			
			try {
				VirtualMachine[] virtualMachine=host.getVms();
				DPMFunctions dpm2= new DPMFunctions();
				DRS2 drs2=new DRS2();
				// TODO Auto-generated catch block
			if(iter.hasNext())
			{
				String key=iter.next();
				host2=DPMFunctions.getHost(key);
			}
			for(VirtualMachine vm : virtualMachine)
			{
				if (dpm2.stateOfVM(vm.getName())){
					
					drs2.liveMigrateVM(host2.getName(), vm.getName());
				}
				else{
					drs2.coldMigrateVM(host2.getName(), vm.getName());
				}
				
			}
			} catch (Exception e) {
				System.out.println("Error in getting VM name" );
				e.printStackTrace();
			
			
			}
			// Collections.sort(hostCpuCount);
			 //hostCpuCount.get(hostCpuCount.size()-1);
			dpm.poweroffhost(host.getName());
			
		}while(DPMFunctions.getAllHostsNew().length>1);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	

	  public static Map<String, Long> compareEntities(Map<String, Long> entityCpuResults){
			List<Entry<String, Long>> list = new LinkedList<Entry<String, Long>>(entityCpuResults.entrySet());

	        // Sorting the list based on values
	        Collections.sort(list, new Comparator<Entry<String, Long>>()
	        {
	            public int compare(Entry<String, Long> o1,
	                    Entry<String, Long> o2)
	            {
	               return o1.getValue().compareTo(o2.getValue());
	            }
	        });

	        Map<String, Long> sortedMap = new LinkedHashMap<String, Long>();
	        for (Entry<String, Long> entry : list)
	        {
	            sortedMap.put(entry.getKey(), entry.getValue());
	        }
	        return sortedMap;
		}


}
