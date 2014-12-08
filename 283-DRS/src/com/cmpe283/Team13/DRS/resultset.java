package com.cmpe283.Team13.DRS;

public class resultset 
{
	public String getVhost() {
		return vhost;
	}
	public void setVhost(String vhost) {
		this.vhost = vhost;
	}
	public long getCpu() {
		return cpu;
	}
	public void setCpu(long cpu) {
		this.cpu = cpu;
	}
	public String vhost;
	public long cpu;
	double cpuper;
	public double getCpuper() {
		return cpuper;
	}
	public void setCpuper(double cpuper) {
		this.cpuper = cpuper;
	}
}
