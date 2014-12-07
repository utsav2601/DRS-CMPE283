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
			sortByValues(hostMetrics);
			HostSystem  host=null;
			HostSystem host2=null;
			Iterator iter = hostMetrics.keySet().iterator();
			if(iter.hasNext()) {
				String key=(String)iter.next();
				
				host=DPMFunctions.getHost(key);    
			}
			
			try {
				VirtualMachine[] virtualMachine=host.getVms();
				DPMFunctions dpm2= new DPMFunctions();
				DRS2 drs2=new DRS2();
				// TODO Auto-generated catch block
			if(iter.hasNext())
			{
				String key=(String)iter.next();
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
	
	
	
	  public static <K extends Comparable,V extends Comparable> Map<K,V> sortByValues(Map<K,V> map){
	        List<Map.Entry<K,V>> entries = new LinkedList<Map.Entry<K,V>>(map.entrySet());
	      
	        Collections.sort(entries, new Comparator<Map.Entry<K,V>>() {

	            @Override
	            public int compare(Entry<K, V> o1, Entry<K, V> o2) {
	                return o1.getValue().compareTo(o2.getValue());
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
}
