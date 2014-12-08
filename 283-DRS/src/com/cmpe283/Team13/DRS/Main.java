package com.cmpe283.Team13.DRS;

public class Main {
	public static void main(String[] args)
	{
		System.out.println("Running Dynamic Resource Scheduler 1......");
		DRS drs= new DRS();
		String[] hosts= drs.getAllHosts();
		for(int i=0;i<hosts.length;i++)
			System.out.println(hosts[i]);
		
		drs.fetchCpu();

	}
}
