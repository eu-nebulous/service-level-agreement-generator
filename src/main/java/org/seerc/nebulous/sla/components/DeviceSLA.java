
package org.seerc.nebulous.sla.components;

import java.util.List;

public class DeviceSLA {

    private String name;
    private LoginCredential loginCredential;
    private List<IpAddress> ipAddresses;
    private NodeProperties nodeProperties;
    private String port;
    private String jobId;
    private String systemArch;
    private String scriptURL;
    private String jarURL;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LoginCredential getLoginCredential() {
        return loginCredential;
    }

    public void setLoginCredential(LoginCredential loginCredential) {
        this.loginCredential = loginCredential;
    }

    public List<IpAddress> getIpAddresses() {
        return ipAddresses;
    }

    public void setIpAddresses(List<IpAddress> ipAddresses) {
        this.ipAddresses = ipAddresses;
    }

    public NodeProperties getNodeProperties() {
        return nodeProperties;
    }

    public void setNodeProperties(NodeProperties nodeProperties) {
        this.nodeProperties = nodeProperties;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    public String getSystemArch() {
        return systemArch;
    }

    public void setSystemArch(String systemArch) {
        this.systemArch = systemArch;
    }

    public String getScriptURL() {
        return scriptURL;
    }

    public void setScriptURL(String scriptURL) {
        this.scriptURL = scriptURL;
    }

    public String getJarURL() {
        return jarURL;
    }

    public void setJarURL(String jarURL) {
        this.jarURL = jarURL;
    }

	@Override
	public String toString() {
		return "DeviceSLA [name = " + name + ", loginCredential = " + loginCredential + ", ipAddresses = " + ipAddresses
				+ ", nodeProperties = " + nodeProperties + ", port = " + port + ", jobId = " + jobId + ", systemArch = "
				+ systemArch + ", scriptURL = " + scriptURL + ", jarURL = " + jarURL + "]";
	}
    

}
