package com.cmpe283.Team13.DPM;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.RemoteException;

import java.util.ArrayList;

import java.util.HashMap;
import com.vmware.vim25.InvalidProperty;

import com.vmware.vim25.PerfEntityMetric;
import com.vmware.vim25.PerfEntityMetricBase;
import com.vmware.vim25.PerfMetricId;
import com.vmware.vim25.PerfMetricIntSeries;
import com.vmware.vim25.PerfMetricSeries;
import com.vmware.vim25.PerfProviderSummary;
import com.vmware.vim25.PerfQuerySpec;

import com.vmware.vim25.RuntimeFault;
import com.vmware.vim25.mo.HostSystem;
import com.vmware.vim25.mo.InventoryNavigator;
import com.vmware.vim25.mo.VirtualMachine;

import com.vmware.vim25.mo.PerformanceManager;
import com.vmware.vim25.mo.ServiceInstance;


public class VMThread {

	/**
	 * @param args
	 */
	static final String SERVER_NAME = "130.65.133.74"; 
	static final String USER_NAME = "administrator";
	static final String PASSWORD = "12!@qwQW";
	public String ip;
	private ServiceInstance serviceInst;
	private static final int SELECTED_COUNTER_ID = 6; 
	static Integer[] a = {  23 };
	static String[] aName = { "cpu" };
	private HashMap<String, String> infoList = new HashMap<String, String>();
	int counter = 0;
   // static LogGeneratorForHost writer;
	

	public VMThread(String vmIP) throws IOException {
		this.ip = vmIP;
	}


	public long run() {
long a=0;
		try {

				URL url = new URL("https://130.65.133.74/sdk");
			
				
					ServiceInstance si = new ServiceInstance(url,"administrator","12!@qwQW", true);
				VirtualMachine vm = (VirtualMachine) new InventoryNavigator(si.getRootFolder()).searchManagedEntity("VirtualMachine", ip);
						//(VirtualMachine) new InventoryNavigator(si.getRootFolder()).searchManagedE.ntity("VirtualMachine", ip); // 
					
					PerformanceManager perfMgr = si.getPerformanceManager();
					
					PerfProviderSummary summary = perfMgr.queryPerfProviderSummary(vm); //vm
					
					int perfInterval = summary.getRefreshRate();
					
					PerfMetricId[] queryAvailablePerfMetric = perfMgr.queryAvailablePerfMetric(vm, null, null,perfInterval);
					//PerfCounterInfo[] pci = perfMgr.getPerfCounter();
					
					ArrayList<PerfMetricId> list = new ArrayList<PerfMetricId>();
					if(queryAvailablePerfMetric!=null)
					{
						for (int i2 = 0; i2 < queryAvailablePerfMetric.length-1; i2++) 
						{
							PerfMetricId perfMetricId = queryAvailablePerfMetric[i2];
							if (SELECTED_COUNTER_ID == perfMetricId.getCounterId()) {
								list.add(perfMetricId);
							}
						}
					}
					PerfMetricId[] pmis = list.toArray(new PerfMetricId[list
							.size()]);
					PerfQuerySpec qSpec = new PerfQuerySpec();
					qSpec.setEntity(vm.getMOR());
					qSpec.setMetricId(pmis);

					qSpec.intervalId = perfInterval;
					PerfEntityMetricBase[] pembs = perfMgr
							.queryPerf(new PerfQuerySpec[] { qSpec });
					
					
					
					for (int i = 0; pembs != null && i < pembs.length; i++) {

						PerfEntityMetricBase val = pembs[i];
						PerfEntityMetric pem = (PerfEntityMetric) val;
						PerfMetricSeries[] vals = pem.getValue();
//						PerfSampleInfo[] infos = pem.getSampleInfo();
//
//						Date date1 = new Date();
//						SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy h:mm:ss");
//						String formattedDate = sdf.format(date1);
				
						
					//	infoList.put(aName[counter], formattedDate);
						
						for (int j = 0; vals != null && j < vals.length; ++j) {
							PerfMetricIntSeries val1 = (PerfMetricIntSeries) vals[j];
							long[] longs = val1.getValue();
							
							return longs[5];
							//infoList.put(aName[0],String.valueOf(longs[5]));
							
						}
					}
					si.getServerConnection().logout();
				
		} catch (Exception e) {
			e.printStackTrace();
		}
		return a;


	}
}
