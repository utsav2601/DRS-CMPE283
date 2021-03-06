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
		
		Integer conditionPerc = 30;
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
				
				}catch(Exception e){
					System.out.println("DPM-Main 0x1234 ERROR CONNECTION");
					e.printStackTrace();}
			}
			 sortedMetrics=compareEntities(hostMetrics);
			HostSystem  host=null;
			HostSystem host2=null;
			//Iterator<String> iter = sortedMetrics.keySet().iterator();
			Iterator<Entry<String, Long>> iter2 = sortedMetrics.entrySet().iterator();
			Boolean flag = false;
			while(iter2.hasNext()) {
				Map.Entry pairs = (Map.Entry)iter2.next();
			    System.out.println("Host " +pairs.getKey() + " = " + pairs.getValue());
				String key=pairs.getKey().toString();
				Integer k = (int)(long)pairs.getValue();
				Integer checkCondition = k/4786* 100;
				//Check the 30% condition 
				if( checkCondition < conditionPerc  ){
				host=DPMFunctions.getHost(key);
				
				flag = true;
				}
				else {
					System.out.println("Looking for unused host ");
				}
			}
			
			
			//if host < 30% usage is found 
			if(flag == true){
				
				System.out.println("Host to be shut down " + host.getName());
				
			try {
				VirtualMachine[] virtualMachine=host.getVms();
				DPMFunctions dpm2= new DPMFunctions();
				DRS2 drs2=new DRS2();
				// TODO Auto-generated catch block
			if(iter2.hasNext())
			{
				
				String key=iter2.next().toString();
				
				//System.out.println("New key " + key);
				host2=DPMFunctions.getHost(key);
				System.out.println("Target Host " + host2.getName());
			}else {
				//if the host is present then find the new target host 
				Iterator<Entry<String, Long>> iter3 = sortedMetrics.entrySet().iterator();
				Map.Entry pairs = (Map.Entry)iter3.next();
			   // System.out.println("Host " +pairs.getKey() + " = " + pairs.getValue());
				host2=DPMFunctions.getHost(pairs.getKey().toString());
				System.out.println("Target Host " + host2.getName());
				
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
				System.out.println("DPM-MAIN 0X1236 Error in getting VM name" );
				e.printStackTrace();
			
			
			}
			dpm.poweroffhost(host.getName());
			}//no host <30% found 
			else {
				System.out.println("No host with less 30% usage found ");
			}
		}while(DPMFunctions.getAllHostsNew().length>1);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.out.println("Error DPM-MAIN 0x1235  ");
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
