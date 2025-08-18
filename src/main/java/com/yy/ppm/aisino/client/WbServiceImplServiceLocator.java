/**
 * WbServiceImplServiceLocator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.yy.ppm.aisino.client;

public class WbServiceImplServiceLocator extends org.apache.axis.client.Service implements com.yy.ppm.aisino.client.WbServiceImplService {

    public WbServiceImplServiceLocator() {
    }


    public WbServiceImplServiceLocator(org.apache.axis.EngineConfiguration config) {
        super(config);
    }

    public WbServiceImplServiceLocator(String wsdlLoc, javax.xml.namespace.QName sName) throws javax.xml.rpc.ServiceException {
        super(wsdlLoc, sName);
    }

    // Use to get a proxy class for WbServiceImplPort
    private String WbServiceImplPort_address = "";

    public String getWbServiceImplPortAddress() {
        return WbServiceImplPort_address;
    }

    // The WSDD service name defaults to the port name.
    private String WbServiceImplPortWSDDServiceName = "WbServiceImplPort";

    public String getWbServiceImplPortWSDDServiceName() {
        return WbServiceImplPortWSDDServiceName;
    }

    public void setWbServiceImplPortWSDDServiceName(String name) {
        WbServiceImplPortWSDDServiceName = name;
    }

    public com.yy.ppm.aisino.client.WbServiceI getWbServiceImplPort() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(WbServiceImplPort_address);
        }
        catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getWbServiceImplPort(endpoint);
    }

    public com.yy.ppm.aisino.client.WbServiceI getWbServiceImplPort(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            com.yy.ppm.aisino.client.WbServiceImplServiceSoapBindingStub _stub = new com.yy.ppm.aisino.client.WbServiceImplServiceSoapBindingStub(portAddress, this);
            _stub.setPortName(getWbServiceImplPortWSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setWbServiceImplPortEndpointAddress(String address) {
        WbServiceImplPort_address = address;
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        try {
            if (com.yy.ppm.aisino.client.WbServiceI.class.isAssignableFrom(serviceEndpointInterface)) {
                com.yy.ppm.aisino.client.WbServiceImplServiceSoapBindingStub _stub = new com.yy.ppm.aisino.client.WbServiceImplServiceSoapBindingStub(new java.net.URL(WbServiceImplPort_address), this);
                _stub.setPortName(getWbServiceImplPortWSDDServiceName());
                return _stub;
            }
        }
        catch (Throwable t) {
            throw new javax.xml.rpc.ServiceException(t);
        }
        throw new javax.xml.rpc.ServiceException("There is no stub implementation for the interface:  " + (serviceEndpointInterface == null ? "null" : serviceEndpointInterface.getName()));
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(javax.xml.namespace.QName portName, Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        if (portName == null) {
            return getPort(serviceEndpointInterface);
        }
        String inputPortName = portName.getLocalPart();
        if ("WbServiceImplPort".equals(inputPortName)) {
            return getWbServiceImplPort();
        }
        else  {
            java.rmi.Remote _stub = getPort(serviceEndpointInterface);
            ((org.apache.axis.client.Stub) _stub).setPortName(portName);
            return _stub;
        }
    }

    public javax.xml.namespace.QName getServiceName() {
        return new javax.xml.namespace.QName("com.htxx.service", "WbServiceImplService");
    }

    private java.util.HashSet ports = null;

    public java.util.Iterator getPorts() {
        if (ports == null) {
            ports = new java.util.HashSet();
            ports.add(new javax.xml.namespace.QName("com.htxx.service", "WbServiceImplPort"));
        }
        return ports.iterator();
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(String portName, String address) throws javax.xml.rpc.ServiceException {

if ("WbServiceImplPort".equals(portName)) {
            setWbServiceImplPortEndpointAddress(address);
        }
        else
{ // Unknown Port Name
            throw new javax.xml.rpc.ServiceException(" Cannot set Endpoint Address for Unknown Port" + portName);
        }
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(javax.xml.namespace.QName portName, String address) throws javax.xml.rpc.ServiceException {
        setEndpointAddress(portName.getLocalPart(), address);
    }

}
