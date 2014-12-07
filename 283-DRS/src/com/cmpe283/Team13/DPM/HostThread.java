package com.cmpe283.Team13.DPM;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;

import com.vmware.vim25.InvalidProperty;
import com.vmware.vim25.PerfCounterInfo;
import com.vmware.vim25.PerfEntityMetric;
import com.vmware.vim25.PerfEntityMetricBase;
import com.vmware.vim25.PerfMetricId;
import com.vmware.vim25.PerfMetricIntSeries;
import com.vmware.vim25.PerfMetricSeries;
import com.vmware.vim25.PerfProviderSummary;
import com.vmware.vim25.PerfQuerySpec;
import com.vmware.vim25.PerfSampleInfo;
import com.vmware.vim25.RuntimeFault;
import com.vmware.vim25.mo.HostSystem;
import com.vmware.vim25.mo.InventoryNavigator;
import com.vmware.vim25.mo.PerformanceManager;
import com.vmware.vim25.mo.ServiceInstance;


public class HostThread  {


	static final String SERVER_NAME = "130.65.133.10"; 
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
	

	public HostThread(String hostIP) throws IOException {
		this.ip = hostIP;
	}


	public long run() {
long a=0;
		try {

				URL url = new URL("https://130.65.133.10/sdk");
			
				
					ServiceInstance si = new ServiceInstance(url,"administrator","12!@qwQW", true);
					HostSystem host = (HostSystem) new InventoryNavigator(
							si.getRootFolder()).searchManagedEntity(
							"HostSystem", ip); // 
					//System.out.println("host value : "+host);
					PerformanceManager perfMgr = si.getPerformanceManager();
					
					PerfProviderSummary summary = perfMgr.queryPerfProviderSummary(host); //vm
					
					int perfInterval = summary.getRefreshRate();
					
					PerfMetricId[] queryAvailablePerfMetric = perfMgr.queryAvailablePerfMetric(host, null, null,perfInterval);
					//PerfCounterInfo[] pci = perfMgr.getPerfCounter();
					
					ArrayList<PerfMetricId> list = new ArrayList<PerfMetricId>();
					for (int i2 = 0; i2 < queryAvailablePerfMetric.length; i2++) 
					{
						PerfMetricId perfMetricId = queryAvailablePerfMetric[i2];
						if (SELECTED_COUNTER_ID == perfMetricId.getCounterId()) {
							list.add(perfMetricId);
						}
					}
					PerfMetricId[] pmis = list.toArray(new PerfMetricId[list.size()]);
					PerfQuerySpec qSpec = new PerfQuerySpec();
					qSpec.setEntity(host.getMOR());
					qSpec.setMetricId(pmis);
					qSpec.setMaxSample(new Integer(1));
					qSpec.intervalId = perfInterval;
					//qSpec.intervalId = perfInterval;
					PerfEntityMetricBase[] pembs = perfMgr
							.queryPerf(new PerfQuerySpec[] { qSpec });
					PerfCounterInfo[] pcis3 = perfMgr.getPerfCounter();
					
					for (int i = 0; pembs != null && i < pembs.length; i++) {
						PerfEntityMetricBase val = pembs[i];
						PerfEntityMetric pem = (PerfEntityMetric) val;
						PerfMetricSeries[] vals = pem.getValue();
						PerfSampleInfo[] infos = pem.getSampleInfo();
						System.out.println("Host Name " + host.getName());
						for (int j = 0; vals != null && j < vals.length; ++j) {
							PerfMetricIntSeries val1 = (PerfMetricIntSeries) vals[j];
							long[] longs = val1.getValue();
							
						
							for (int k = 0; k < longs.length; k++) {
								System.out.println(infos[k].getTimestamp().getTime() + " : " + longs[k]);
								return longs[0];
							}
							
						//	return metric_value;
						}
					} 
					si.getServerConnection().logout();
				
		} catch (Exception e) {
			e.printStackTrace();
		}
		return a;
	}

}
