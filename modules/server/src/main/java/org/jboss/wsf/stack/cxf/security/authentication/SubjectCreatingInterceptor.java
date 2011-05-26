/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2011, Red Hat Middleware LLC, and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
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
package org.jboss.wsf.stack.cxf.security.authentication;

import java.util.HashMap;
import java.util.Map;

import javax.security.auth.Subject;

import org.apache.cxf.binding.soap.SoapMessage;
import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.ws.security.wss4j.AbstractUsernameTokenAuthenticatingInterceptor;
import org.jboss.wsf.spi.deployment.Endpoint;
import org.jboss.wsf.spi.security.SecurityDomainContext;
import org.jboss.wsf.stack.cxf.security.nonce.NonceStore;

/**
 * Interceptor which authenticates a current principal and populates Subject
 * 
 * @author Sergey Beryozkin
 * @author alessio.soldano@jboss.com
 *
 */
public class SubjectCreatingInterceptor extends AbstractUsernameTokenAuthenticatingInterceptor
{
   private ThreadLocal<SecurityDomainContext> sdc = new ThreadLocal<SecurityDomainContext>();
   
   private SubjectCreator helper = new SubjectCreator();

   public SubjectCreatingInterceptor()
   {
      this(new HashMap<String, Object>());
   }

   public SubjectCreatingInterceptor(Map<String, Object> properties)
   {
      super(properties);
   }

   @Override
   public void handleMessage(SoapMessage msg) throws Fault {
      Endpoint ep = msg.getExchange().get(Endpoint.class);
      sdc.set(ep.getSecurityDomainContext());
      try
      {
         super.handleMessage(msg);
      }
      finally
      {
         if (sdc != null)
         {
            sdc.remove();
         }
      }
   }

   @Override
   public Subject createSubject(String name, String password, boolean isDigest, String nonce, String created)
   {
      return helper.createSubject(sdc.get(), name, password, isDigest, nonce, created);
   }

   public void setPropagateContext(boolean propagateContext)
   {
      this.helper.setPropagateContext(propagateContext);
   }

   public void setTimestampThreshold(int timestampThreshold)
   {
      this.helper.setTimestampThreshold(timestampThreshold);
   }

   public void setNonceStore(NonceStore nonceStore)
   {
      this.helper.setNonceStore(nonceStore);
   }

   public void setDecodeNonce(boolean decodeNonce)
   {
      this.helper.setDecodeNonce(decodeNonce);
   }

}
