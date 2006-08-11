package org.codehaus.plexus.xfire.soap;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import javax.xml.namespace.QName;

import org.codehaus.xfire.aegis.AbstractXFireAegisTest;
import org.codehaus.xfire.client.Client;
import org.codehaus.xfire.client.XFireProxyFactory;
import org.codehaus.xfire.service.Service;
import org.codehaus.xfire.service.binding.ObjectServiceFactory;
import org.codehaus.xfire.service.invoker.ObjectInvoker;
import org.codehaus.xfire.soap.Soap12;
import org.codehaus.xfire.soap.Soap12Binding;
import org.codehaus.xfire.soap.SoapConstants;
import org.codehaus.xfire.transport.local.LocalTransport;
import org.codehaus.xfire.wsdl11.builder.WSDLBuilder;
import org.codehaus.xfire.fault.XFireFault;
import org.jdom.Document;

public class AuthFaultTest
    extends AbstractXFireAegisTest
{
    private Service service;
    private String ns = "urn:xfire:authenticate";
    private String name = "AuthService";
    private Soap12Binding soap12Binding;

    protected void setUp()
        throws Exception
    {
        super.setUp();

        ObjectServiceFactory osf = (ObjectServiceFactory) getServiceFactory();
        service = osf.create(AuthService.class, name, ns, null);
        service.setProperty(ObjectInvoker.SERVICE_IMPL_CLASS, AuthServiceImpl.class);

        soap12Binding = osf.createSoap12Binding(service, new QName(service.getTargetNamespace(), "Auth12"), LocalTransport.BINDING_ID);

        getServiceRegistry().register(service);
    }

    public void testDyanamicClient() throws Exception
    {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        getWSDL("AuthService").write(bos);

        Client client = new Client(new ByteArrayInputStream(bos.toByteArray()), null);
        client.setXFire(getXFire());
        client.setUrl("xfire.local://AuthService");
        client.setTransport(getTransportManager().getTransport(LocalTransport.BINDING_ID));

        try {
            Object[] response = client.invoke("authenticate", new Object[] {"yo", "yo"});
            fail("Should have thrown response. Received: " + response);
        }
        catch ( XFireFault fault) {
            assertEquals("message", fault.getReason());
        }
    }

    public void testService() throws Exception
    {
        Document response = invokeService(name, "authenticate12.xml");

        addNamespace("f", "urn:xfire:authenticate:fault");
        addNamespace("s12", Soap12.getInstance().getNamespace());
        assertValid("//s12:Detail/f:AuthenticationFault/f:message[text()='Invalid username/password']", response);

        response = invokeService(name, "authenticate.xml");
        assertValid("//detail/f:AuthenticationFault/f:message[text()='Invalid username/password']", response);
    }

    public void testClient() throws Exception
    {
        XFireProxyFactory factory = new XFireProxyFactory(getXFire());
        AuthService echo = (AuthService) factory.create(service, "xfire.local://AuthService");

        try
        {
            echo.authenticate("yo", "yo");
            fail("Should have thrown custom fault.");
        }
        catch (AuthenticationFault_Exception fault)
        {
            assertEquals("Invalid username/password", fault.getFaultInfo().getMessage());
            assertEquals("message", fault.getMessage());
        }

        echo = (AuthService) factory.create(soap12Binding, "xfire.local://AuthService");

        try
        {
            echo.authenticate("yo", "yo");
            fail("Should have thrown custom fault.");
        }
        catch (AuthenticationFault_Exception fault)
        {
            assertEquals("Invalid username/password", fault.getFaultInfo().getMessage());
            assertEquals("message", fault.getMessage());
        }
    }

    public void testFaultWSDL() throws Exception
    {
        Document wsdl = getWSDLDocument(service.getSimpleName());

        String ns = service.getTargetNamespace();
        addNamespace("xsd", SoapConstants.XSD);
        addNamespace("w", WSDLBuilder.WSDL11_NS);
        addNamespace("ws", WSDLBuilder.WSDL11_SOAP_NS);

        assertValid("//xsd:schema[@targetNamespace='urn:xfire:authenticate:fault']" +
                "/xsd:element[@name='AuthenticationFault']", wsdl);
        assertValid("//w:message[@name='AuthenticationFault']" +
                "/w:part[@name='AuthenticationFault'][@element='ns1:AuthenticationFault']", wsdl);
        assertValid("//w:portType[@name='AuthServicePortType']/w:operation[@name='authenticate']" +
                "/w:fault[@name='AuthenticationFault']", wsdl);
        assertValid("//w:binding[@name='AuthServiceHttpBinding']/w:operation[@name='authenticate']" +
                    "/w:fault[@name='AuthenticationFault']/ws:fault[@name='AuthenticationFault'][@use='literal']", wsdl);
    }
}
