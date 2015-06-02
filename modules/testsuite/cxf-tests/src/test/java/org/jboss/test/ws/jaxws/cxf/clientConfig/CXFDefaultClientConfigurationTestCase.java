/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2013, Red Hat Middleware LLC, and individual contributors
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
package org.jboss.test.ws.jaxws.cxf.clientConfig;

import java.io.File;
import java.net.URL;

import org.jboss.arquillian.container.test.api.ContainerController;
import org.jboss.arquillian.container.test.api.Deployer;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.OperateOnDeployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.container.test.api.TargetsContainer;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.ws.common.IOUtils;
import org.jboss.wsf.test.JBossWSTest;
import org.jboss.wsf.test.JBossWSTestHelper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Verifies client configuration setup (in-container tests, relying on AS model)
 *
 * @author alessio.soldano@jboss.com
 * @since 31-May-2012
 */
@RunWith(Arquillian.class)
public class CXFDefaultClientConfigurationTestCase extends JBossWSTest
{
   private static final String DEP = "jaxws-cxf-clientConfig-def";
   private static final String CLIENT_DEP = "jaxws-cxf-clientConfig-inContainer-def-client";
   private static final String DEFAULT_CONFIG_TESTS_SERVER = "default-config-tests";
   
   @ArquillianResource
   private Deployer deployer;
   
   @ArquillianResource
   private ContainerController containerController;
   
   @Deployment(name = DEP, testable = false)
   @TargetsContainer(DEFAULT_CONFIG_TESTS_SERVER)
   public static WebArchive createDeployment() {
      WebArchive archive = ShrinkWrap.create(WebArchive.class, DEP + ".war");
      archive.addManifest()
            .addClass(org.jboss.test.ws.jaxws.cxf.clientConfig.Endpoint.class)
            .addClass(org.jboss.test.ws.jaxws.cxf.clientConfig.EndpointImpl.class);
      return archive;
   }

   @Deployment(name = CLIENT_DEP, testable = false)
   @TargetsContainer(DEFAULT_CONFIG_TESTS_SERVER)
   public static WebArchive createDeployment2() {
      WebArchive archive = ShrinkWrap.create(WebArchive.class, CLIENT_DEP + ".war");
      archive.setManifest(new StringAsset("Manifest-Version: 1.0\n"
                  + "Dependencies: org.apache.cxf.impl\n"))
            .addAsResource(new File(JBossWSTestHelper.getTestResourcesDir() + "/jaxws/cxf/clientConfig/META-INF/jaxws-client-config.xml"), "META-INF/jaxws-client-config.xml")
            .addClass(org.jboss.test.ws.jaxws.cxf.clientConfig.Endpoint.class)
            .addClass(org.jboss.test.ws.jaxws.cxf.clientConfig.Helper.class)
            .addClass(org.jboss.test.ws.jaxws.cxf.clientConfig.TestUtils.class)
            .addClass(org.jboss.wsf.test.ClientHelper.class)
            .addClass(org.jboss.wsf.test.TestServlet.class)
            .addAsManifestResource(new File(JBossWSTestHelper.getTestResourcesDir() + "/jaxws/cxf/clientConfig/META-INF/default-client-permissions.xml"), "permissions.xml");
      return archive;
   }
   
   @Before
   public void startContainerAndDeploy() throws Exception {
      if (!containerController.isStarted(DEFAULT_CONFIG_TESTS_SERVER)) {
         containerController.start(DEFAULT_CONFIG_TESTS_SERVER);
      }
   }
   
   /**
    * Verifies the default client configuration from AS model is used
    * 
    * @throws Exception
    */
   @Test
   @RunAsClient
   @OperateOnDeployment(CLIENT_DEP)
   public void testDefaultClientConfigurationInContainer() throws Exception {
      assertEquals("1", runTestInContainer("testDefaultClientConfiguration"));
   }
   
   @Test
   @RunAsClient
   @OperateOnDeployment(CLIENT_DEP)
   public void testDefaultClientConfigurationOnDispatchInContainer() throws Exception {
      assertEquals("1", runTestInContainer("testDefaultClientConfigurationOnDispatch"));
   }
   
   /**
    * Verifies a client configuration from AS model can be set
    * 
    * @throws Exception
    */
   @Test
   @RunAsClient
   @OperateOnDeployment(CLIENT_DEP)
   public void testCustomClientConfigurationInContainer() throws Exception {
      assertEquals("1", runTestInContainer("testCustomClientConfiguration"));
   }
   
   @Test
   @RunAsClient
   @OperateOnDeployment(CLIENT_DEP)
   public void testCustomClientConfigurationOnDispatchInContainer() throws Exception {
      assertEquals("1", runTestInContainer("testCustomClientConfigurationOnDispatch"));
   }
   
   @Test
   @RunAsClient
   @OperateOnDeployment(CLIENT_DEP)
   public void testCustomClientConfigurationUsingFeatureInContainer() throws Exception {
      assertEquals("1", runTestInContainer("testCustomClientConfigurationUsingFeature"));
   }
   
   @Test
   @RunAsClient
   @OperateOnDeployment(CLIENT_DEP)
   public void testCustomClientConfigurationOnDispatchUsingFeatureInContainer() throws Exception {
      assertEquals("1", runTestInContainer("testCustomClientConfigurationOnDispatchUsingFeature"));
   }
   
   // -------------------------
   
   private String runTestInContainer(String test) throws Exception
   {
      URL url = new URL("http://" + getServerHost()
            + ":" + getServerPort(CXF_TESTS_GROUP_QUALIFIER, DEFAULT_CONFIG_TESTS_SERVER) + "/jaxws-cxf-clientConfig-inContainer-def-client?path=/jaxws-cxf-clientConfig-def/EndpointImpl&method=" + test
            + "&helper=" + Helper.class.getName());
      return IOUtils.readAndCloseStream(url.openStream());
   }
}