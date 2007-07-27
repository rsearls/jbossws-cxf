/*
 * JBoss, Home of Professional Open Source
 * Copyright 2005, JBoss Inc., and individual contributors as indicated
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.jboss.wsf.stack.xfire;

//$Id$

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import javax.management.ObjectName;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.ws.WebServiceException;

import org.apache.cxf.transport.DestinationFactory;
import org.apache.cxf.transport.servlet.CXFServlet;
import org.apache.cxf.transport.servlet.ServletController;
import org.apache.cxf.transport.servlet.ServletTransportFactory;
import org.jboss.logging.Logger;
import org.jboss.wsf.common.ObjectNameFactory;
import org.jboss.wsf.spi.invocation.EndpointAssociation;
import org.jboss.wsf.spi.deployment.Endpoint;
import org.jboss.wsf.spi.invocation.RequestHandler;
import org.jboss.wsf.spi.management.EndpointRegistry;
import org.jboss.wsf.spi.management.EndpointRegistryFactory;
import org.jboss.wsf.spi.SPIProviderResolver;
import org.jboss.wsf.spi.SPIProvider;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.io.InputStreamResource;

/**
 * An extension to the CXF servlet
 * 
 * @author Thomas.Diesler@jboss.org
 * @since 21-Apr-2007
 */
public class CXFServletExt extends CXFServlet
{
   public static final String PARAM_CXF_BEANS_URL = "jbossws.cxf.beans.url";

   private static Logger log = Logger.getLogger(CXFServletExt.class);

   protected Endpoint endpoint;
   protected EndpointRegistry epRegistry;
   protected GenericApplicationContext childCtx;

   public void init(ServletConfig servletConfig) throws ServletException
   {
      super.init(servletConfig);

      // Init the Endpoint
      SPIProvider spiProvider = SPIProviderResolver.getInstance().getProvider();
      epRegistry = spiProvider.getSPI(EndpointRegistryFactory.class).createEndpointRegistry();
      
      ServletContext context = servletConfig.getServletContext();
      String contextPath = context.getContextPath();
      endpoint = initServiceEndpoint(contextPath);
      
      context.setAttribute(ServletController.class.getName(), getController());
   }

   public ServletController createServletController()
   {
      ServletTransportFactory stf = (ServletTransportFactory)createServletTransportFactory();
      return new ServletControllerExt(stf, this);
   }

   protected void loadAdditionalConfig(ApplicationContext ctx, ServletConfig servletConfig) throws ServletException
   {
      String location = servletConfig.getServletContext().getInitParameter(PARAM_CXF_BEANS_URL);

      InputStream is;
      try
      {
         is = new URL(location).openStream();
      }
      catch (IOException e)
      {
         throw new ServletException(e);
      }

      if (is != null)
      {
         childCtx = new GenericApplicationContext(ctx);
         XmlBeanDefinitionReader reader = new XmlBeanDefinitionReader(childCtx);
         reader.setValidationMode(XmlBeanDefinitionReader.VALIDATION_XSD);
         reader.loadBeanDefinitions(new InputStreamResource(is, location));

         childCtx.refresh();
      }
   }

   public void service(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
   {
      try
      {
         EndpointAssociation.setEndpoint(endpoint);
         RequestHandler requestHandler = (RequestHandler)endpoint.getRequestHandler();
         requestHandler.handleHttpRequest(endpoint, req, res, getServletContext());
      }
      finally
      {
         EndpointAssociation.removeEndpoint();
      }
   }

   public void destroy()
   {
      if (childCtx != null)
         childCtx.destroy();
   }

   /** Initialize the service endpoint
    */
   protected Endpoint initServiceEndpoint(String contextPath)
   {
      if (contextPath.startsWith("/"))
         contextPath = contextPath.substring(1);

      Endpoint endpoint = null;
      String servletName = getServletName();
      for (ObjectName sepId : epRegistry.getEndpoints())
      {
         String propContext = sepId.getKeyProperty(Endpoint.SEPID_PROPERTY_CONTEXT);
         String propEndpoint = sepId.getKeyProperty(Endpoint.SEPID_PROPERTY_ENDPOINT);
         if (servletName.equals(propEndpoint) && contextPath.equals(propContext))
         {
            endpoint = epRegistry.getEndpoint(sepId);
            break;
         }
      }

      if (endpoint == null)
      {
         ObjectName oname = ObjectNameFactory.create(Endpoint.SEPID_DOMAIN + ":" + Endpoint.SEPID_PROPERTY_CONTEXT + "=" + contextPath + ","
               + Endpoint.SEPID_PROPERTY_ENDPOINT + "=" + servletName);
         throw new WebServiceException("Cannot obtain endpoint for: " + oname);
      }

      return endpoint;
   }
}
