package com.cmpe283.Team13;

import java.io.IOException;
import java.net.URL;
import com.vmware.vim25.ComputeResourceConfigSpec;
import com.vmware.vim25.HostConnectSpec;
import com.vmware.vim25.mo.Datacenter;
import com.vmware.vim25.mo.Folder;
import com.vmware.vim25.mo.InventoryNavigator;
import com.vmware.vim25.mo.ManagedEntity;
import com.vmware.vim25.mo.ServiceInstance;
import com.vmware.vim25.mo.Task;

public class DRS 
{
	//Retrieve CPU Load of vHost1
	//Retrieve CPU Load of vHost2
	//Compare both. 
	//Create new VM
	
	    private static Folder folder;
	    private ServiceInstance si ;


    public DRS()
    {
    	VMURLS  vmurlTeam = new VMURLS();
    	try
    	{
			this.si= vmurlTeam.getServiceInstanceTeam();
			this.folder = si.getRootFolder();
    	}
    	catch(Exception e)
    	{
    		e.printStackTrace();
    	}
    }
	
	
	public static String[] getAllHosts()
	{
		String sample[]={""};
    	try
    	{
    		ManagedEntity[] hosts = new InventoryNavigator(folder).searchManagedEntities("HostSystem");
    		if(hosts.length <=1) 
    		{
    			System.out.println("no more hosts present.. Should create new one..");
    			//return false;
    		}
    		else
    		{
    			System.out.println("Multiple hosts present.. Searching in vCenter..");
    		}
    		
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

	public static void comparehosts(resultset[] r)
	{
		int len= r.length;
		int flag=5;
		double check=90.0;
		int count=0;
		for(int i=0;i<r.length;i++)
		{
			if(r[i].getCpuper()>check)
			{
				System.out.println("Vhost "+ r[i].getVhost()+" is using more that 90% of CPU. Very Bulky..");
				count++;
			}
		}
		if(count==len)
		{
			System.out.println("All vhosts are using high CPU resources.. Cannot accommodate new VM.."
					+ " Creating new host for new VM...");
			String ip= addingNewHost();
			createNewVM c1 = new createNewVM(ip);
			try {
				c1.doClone();
				return;
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		else if (len==2)
		{
			if(r[0].getCpu()>r[1].getCpu())
			{
				System.out.println("Host '"+ r[1].getVhost()+"' is less bulky!");
				System.out.println("Creating new VM on this vHost..");
				flag=1;
			}
			else
			{
				System.out.println("Host '"+ r[0].getVhost()+"' is less bulky!");
				System.out.println("Creating new VM on this vHost..");
				flag=0;
			}
		}
		else if(len==3)
		{
			if(r[0].getCpu()<r[1].getCpu() && r[0].getCpu()<r[2].getCpu()  )
			{
				System.out.println("Host '"+ r[0].getVhost()+"' is less bulky!");
				System.out.println("Creating new VM on this vHost..");
				flag=0;
			}
			else if(r[1].getCpu()<r[0].getCpu() && r[1].getCpu()<r[2].getCpu() )
			{
				System.out.println("Host '"+ r[1].getVhost()+"' is less bulky!");
				System.out.println("Creating new VM on this vHost..");
				flag=1;
			}
			else
			{
				System.out.println("Host '"+ r[2].getVhost()+"' is less bulky!");
				System.out.println("Creating new VM on this vHost..");
				flag=2;
			}
		}
		
		createNewVM c = new createNewVM(r[flag].getVhost());
		try {
			c.doClone();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static String addingNewHost()
    {
        String ip="130.65.133.14";
        try 
        {    

            ManagedEntity [] mes =  new InventoryNavigator(folder).searchManagedEntities("Datacenter");
            Datacenter dc = new Datacenter(folder.getServerConnection(),  mes[0].getMOR());
            HostConnectSpec hs = new HostConnectSpec();
            
            hs.hostName= ip;
            hs.userName ="root";
            hs.password = "12!@qwQW";
            hs.managementIp = "130.65.133.10";
            hs.setSslThumbprint("A9:96:0D:D4:6B:C4:27:22:05:DD:EF:30:5F:10:62:8F:4B:83:FF:64");
            //hs.setSslThumbprint("9f:02:9D:6A:91:C6:EB:9A:14:5D:BD:13:39:C8:23:BE:3F:C9:46:3A");
            //hs.setSslThumbprint("C5:EF:CA:98:96:80:6D:2E:46:CB:B1:D2:BB:87:4A:18:AF:26:83:20");
            //hs.setSslThumbprint("90:BD:8C:C1:4E:F6:E9:A3:1A:DF:4B:FA:16:6B:9A:0D:73:DC:6A:F7");
            ComputeResourceConfigSpec crcs = new ComputeResourceConfigSpec();
            Task t = dc.getHostFolder().addStandaloneHost_Task(hs,crcs, true);
            
            if(t.waitForTask() == t.SUCCESS)
            {
                System.out.println("vHost is added successfully");
                return ip;
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
        return ip;
 }
	
	public void fetchCpu()
	{
		String[] hosts= getAllHosts();
		System.out.println("Fetching CPU usages of all vHosts....");
		try 
		{
			
			resultset[] r = new resultset[hosts.length];
			for(int i=0;i<hosts.length;i++)
			{
				HostThread ht= new HostThread(hosts[i]);
				long a =ht.run();
				float c=(float)a;
				double b= (c/4786) *100;
				r[i]=new resultset();
				r[i].setVhost(hosts[i]);
				r[i].setCpu(a);
				r[i].setCpuper(b);
				System.out.println(hosts[i]+" : "+ b+ "% usage..");
			}
			comparehosts(r);
			
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}




