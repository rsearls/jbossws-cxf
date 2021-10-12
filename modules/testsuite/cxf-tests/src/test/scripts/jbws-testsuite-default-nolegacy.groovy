def root = new XmlParser().parse(inputFile)

/**
 * Fix logging: optionally remove CONSOLE handler and set a specific log file
 *
 */

def logHandlers = root.profile.subsystem.'root-logger'.handlers[0]
def consoleHandler = logHandlers.find{it.@name == 'CONSOLE'}
if (!session.userProperties['enableServerLoggingToConsole'] && !project.properties['enableServerLoggingToConsole']) logHandlers.remove(consoleHandler)
def file = root.profile.subsystem.'periodic-rotating-file-handler'.file[0]
file.attributes()['path'] = serverLog

// rls debug start
def loggingSubsystem =  getSubsystem(root, "urn:jboss:domain:logging:")
def logger1 = new groovy.util.Node(null, 'logger', ['category' : 'org.wildfly'])
loggingSubsystem.append(logger1)
def level1 = new groovy.util.Node(null, 'level', ['name' : 'DEBUG'])
logger1.append(level1)
// rls debug end

/**
 * Helper method to get subsystem element by xmlns prefix
 */
private getSubsystem(root, xmlnsPrefix) {
  for (item in root.profile.subsystem) {
    if (item.name().getNamespaceURI().startsWith(xmlnsPrefix)) {
      return item;
    }
  }
}

/**
 * Add a security-domain block like this:
 *
 *        <subsystem xmlns="urn:wildfly:elytron:1.0">
 *           <security-domains>
 *                <security-domain name="JBossWS" default-realm="JBossWS" permission-mapper="login-permission-mapper" role-mapper="combined-role-mapper">
 *                   <realm name="JBossWS" role-decoder="groups-to-roles"/>
 *               </security-domain>
 *               <security-domain name="ws-basic-domain" default-realm="ws-basic-domain" permission-mapper="login-permission-mapper" role-mapper="combined-role-mapper">
 *                   <realm name="ws-basic-domain" role-decoder="groups-to-roles"/>
 *               </security-domain>
 *           </security-domains>
 * 
 *
 */
def securitySubsystem =  getSubsystem(root, "urn:wildfly:elytron:")
def securityDomains = null
for (element in securitySubsystem) {
  if (element.name().getLocalPart() == 'security-domains') {
    securityDomains = element
  }
}
def securityDomain = securityDomains.appendNode('security-domain', ['name':'JBossWS','default-realm':'JBossWS','permission-mapper':'default-permission-mapper'])
def realm = securityDomain.appendNode('realm',['name':'JBossWS','role-decoder':'groups-to-roles'])

def basicsecurityDomain = securityDomains.appendNode('security-domain', ['name':'ws-basic-domain','default-realm':'ws-basic-domain','permission-mapper':'default-permission-mapper'])
def basicrealm = basicsecurityDomain.appendNode('realm',['name':'ws-basic-domain','role-decoder':'groups-to-roles'])


def digestDomain = securityDomains.appendNode('security-domain', ['name':'ws-digest-domain','default-realm':'ws-digest-domain','permission-mapper':'default-permission-mapper'])
def digestRefRealm = digestDomain.appendNode('realm',['name':'ws-digest-domain','role-decoder':'groups-to-roles'])


def legacyDomain = securityDomains.appendNode('security-domain', ['name':'JAASJBossWS','default-realm':'JAASJBossWS','permission-mapper':'default-permission-mapper'])
def jaasJBossWSRealm = legacyDomain.appendNode('realm',['name':'JAASJBossWS'])

def legacyDigestDomain = securityDomains.appendNode('security-domain', ['name':'JBossWSDigest','default-realm':'JAASJBossWSDigestRealm','permission-mapper':'default-permission-mapper'])
def jaasJBossWDigestRealm = legacyDigestDomain.appendNode('realm',['name':'JAASJBossWSDigestRealm'])

def testSecurityDomain = securityDomains.appendNode('security-domain',
        ['name':'jbws-test-https-realm','default-realm':'jbws-test-https-realm','permission-mapper':'default-permission-mapper'])
def testRealm = testSecurityDomain.appendNode('realm',['name':'jbws-test-https-realm','role-decoder':'groups-to-roles'])

def jaspiDomain = securityDomains.appendNode('security-domain', ['name':'jaspi','default-realm':'jaspi','permission-mapper':'default-permission-mapper'])
def jaspiRealm = jaspiDomain.appendNode('realm',['name':'jaspi'])

def clientJaspiDomain = securityDomains.appendNode('security-domain', ['name':'clientJaspi','default-realm':'clientJaspi','permission-mapper':'default-permission-mapper'])
def clientJaspiRealm = clientJaspiDomain.appendNode('realm',['name':'clientJaspi'])


/**
 *            <security-realms>
 *               <properties-realm name="JBossWS">
 *                   <users-properties path="/mnt/ssd/jbossws/stack/cxf/trunk/modules/testsuite/cxf-tests/target/test-classes/jbossws-users.properties"/>
 *                   <groups-properties path="application-roles.properties" relative-to="jboss.server.config.dir"/>
 *               </properties-realm>
 *               <properties-realm name="ws-basic-domain">
 *                   <users-properties path="/mnt/ssd/jbossws/stack/cxf/trunk/modules/testsuite/cxf-tests/target/test-classes/ws-users.properties"/>
 *                   <groups-properties path="application-roles.properties"/>
 *               </properties-realm>
 *            </security-realms>
 *
 *
 */
def securityRealms = root.profile.subsystem.'security-realms'[0]

def propertiesRealm = securityRealms.appendNode('properties-realm', ['name':'JBossWS'])
def usersProperties = propertiesRealm.appendNode('users-properties',['path':usersPropFile, 'plain-text':'true'])
def groupsProperties = propertiesRealm.appendNode('groups-properties',['path':rolesPropFile])


def basicPropertiesRealm = securityRealms.appendNode('properties-realm', ['name':'ws-basic-domain'])
def basicUsersProperties = basicPropertiesRealm.appendNode('users-properties',['path': testResourcesDir + '/jaxws/cxf/httpauth/WEB-INF/ws-users.properties', 'plain-text':'true'])
def basicGroupsProperties = basicPropertiesRealm.appendNode('groups-properties',['path': testResourcesDir + '/jaxws/cxf/httpauth/WEB-INF/ws-roles.properties'])

def digestRealm = securityRealms.appendNode('properties-realm', ['name':'ws-digest-domain'])
def digestUserProperties = digestRealm.appendNode('users-properties',['path': testResourcesDir + '/jaxws/cxf/httpauth/WEB-INF/ws-digest-users.properties'])
def digestGroupsProperties = digestRealm.appendNode('groups-properties',['path': testResourcesDir + '/jaxws/cxf/httpauth/WEB-INF/ws-roles.properties'])

def testPropertiesRealm = securityRealms.appendNode('properties-realm', ['name':'jbws-test-https-realm'])
def tesUsersProperties = testPropertiesRealm.appendNode('users-properties',['path':usersPropFile, 'plain-text':'true'])
def testGroupsProperties = testPropertiesRealm.appendNode('groups-properties',['path':rolesPropFile])

def JAASJBossWSPropertiesRealm = securityRealms.appendNode('properties-realm', ['name':'JAASJBossWS'])
def JAASJBossWSUsersProperties = JAASJBossWSPropertiesRealm.appendNode('users-properties',['path':usersPropFile, 'plain-text':'true'])
def JAASJBossWSGroupsProperties = JAASJBossWSPropertiesRealm.appendNode('groups-properties',['path':rolesPropFile])

def trustStsPropertiesRealm = securityRealms.appendNode('properties-realm', ['name':'JBossWS-trust-sts'])
def trustStsUsersProperties = trustStsPropertiesRealm.appendNode('users-properties',['path':testResourcesDir + '/jaxws/samples/wsse/policy/trust/WEB-INF/jbossws-users.properties', 'plain-text':'true'])
def trustStsGroupsProperties = trustStsPropertiesRealm.appendNode('groups-properties',['path':testResourcesDir + '/jaxws/samples/wsse/policy/trust/WEB-INF/jbossws-roles.properties'])

def JBossWSDigestPropertiesRealm = securityRealms.appendNode('properties-realm', ['name':'JAASJBossWSDigestRealm'])
def JBossWSDigestUsersProperties = JBossWSDigestPropertiesRealm.appendNode('users-properties',['path':testResourcesDir + '/jaxws/samples/wsse/policy/jaas/digest/WEB-INF/jbossws-users.properties', 'plain-text':'true'])
def JBossWSDigestGroupsProperties = JBossWSDigestPropertiesRealm.appendNode('groups-properties',['path':testResourcesDir + '/jaxws/samples/wsse/policy/jaas/digest/WEB-INF/jbossws-roles.properties'])

def jaspiPropertiesRealm = securityRealms.appendNode('properties-realm', ['name':'jaspi'])
def jaspiUsersProperties = jaspiPropertiesRealm.appendNode('users-properties',['path':usersPropFile, 'plain-text':'true'])
def jaspiGroupsProperties = jaspiPropertiesRealm.appendNode('groups-properties',['path':rolesPropFile])

def clientJaspiPropertiesRealm = securityRealms.appendNode('properties-realm', ['name':'clientJaspi'])
def clientJaspiUsersProperties = clientJaspiPropertiesRealm.appendNode('users-properties',['path':usersPropFile, 'plain-text':'true'])
def clientJaspiGroupsProperties = clientJaspiPropertiesRealm.appendNode('groups-properties',['path':rolesPropFile])

/**
 *             <http>
 *               <http-authentication-factory name="JBossWS" http-server-mechanism-factory="global" security-domain="JBossWS">
 *                   <mechanism-configuration>
 *                       <mechanism mechanism-name="BASIC">
 *                           <mechanism-realm realm-name="JBossWS Realm"/>
 *                       </mechanism>
 *                   </mechanism-configuration>
 *               </http-authentication-factory>
 *               <http-authentication-factory name="ws-basic-domain" http-server-mechanism-factory="global" security-domain="ws-basic-domain">
 *                   <mechanism-configuration>
 *                       <mechanism mechanism-name="BASIC">
 *                           <mechanism-realm realm-name="ws-basic-domain"/>
 *                       </mechanism>
 *                   </mechanism-configuration>
 *               </http-authentication-factory>
 *
 *
 */
def httpAuthen = null
for (element in securitySubsystem) {
    if (element.name().getLocalPart() == 'http') {
       httpAuthen = element
       break
    }
}
def httpAuthenticationFactory = httpAuthen.appendNode('http-authentication-factory', ['name':'JBossWS','http-server-mechanism-factory':'global', 'security-domain':'JBossWS'])
def mechanismConfiguration = httpAuthenticationFactory.appendNode('mechanism-configuration')
def mechanism = mechanismConfiguration.appendNode('mechanism',['mechanism-name':'BASIC'])
def mechanismRealm=mechanism.appendNode('mechanism-realm',['realm-name':'JBossWS'])

def basicHttpAuthenticationFactory = httpAuthen.appendNode('http-authentication-factory', ['name':'ws-basic-domain','http-server-mechanism-factory':'global', 'security-domain':'ws-basic-domain'])
def basicMechanismConfiguration = basicHttpAuthenticationFactory.appendNode('mechanism-configuration')
def basicMechanism = basicMechanismConfiguration.appendNode('mechanism',['mechanism-name':'BASIC'])
def basicmechanismRealm = basicMechanism.appendNode('mechanism-realm',['realm-name':'ws-basic-domain'])

def digestHttpAuthenticationFactory = httpAuthen.appendNode('http-authentication-factory', ['name':'ws-digest-domain','http-server-mechanism-factory':'global', 'security-domain':'ws-digest-domain'])
def digestMechanismConfiguration = digestHttpAuthenticationFactory.appendNode('mechanism-configuration')
def digestMechanism = digestMechanismConfiguration.appendNode('mechanism',['mechanism-name':'DIGEST'])
def digestMechanismRealm = digestMechanism.appendNode('mechanism-realm',['realm-name':'ws-digest-domain'])

def testHttpAuthenticationFactory = httpAuthen.appendNode('http-authentication-factory',
        ['name':'jbws-test-https-realm','http-server-mechanism-factory':'global', 'security-domain':'jbws-test-https-realm'])
def testMechanismConfiguration = testHttpAuthenticationFactory.appendNode('mechanism-configuration')
def testMechanism = testMechanismConfiguration.appendNode('mechanism',['mechanism-name':'BASIC'])
def testMechanismRealm=testMechanism.appendNode('mechanism-realm',['realm-name':'jbws-test-https-realm'])

def jaspiAuthen = null
for (element in securitySubsystem) {
    if (element.name().getLocalPart() == 'jaspi') {
        jaspiAuthen = element
        break
    }
}

if (jaspiAuthen == null) {
    jaspiAuthen = new groovy.util.Node(null, 'jaspi', [])
    securitySubsystem.append(jaspiAuthen)
}
def jaspiConfiguration1 = new groovy.util.Node(null, 'jaspi-configuration', ['name' : 'jaspi-jaas-lm-stack'])
jaspiAuthen.append(jaspiConfiguration1)
def serverAuthModules1 = new groovy.util.Node(null, 'server-auth-modules', [])
jaspiConfiguration1.append(serverAuthModules1)
def servAuthModule1 = new groovy.util.Node(null, 'server-auth-module',
        ['class-name' : 'org.jboss.wsf.stack.cxf.jaspi.module.UsernameTokenServerAuthModule', 'module' : 'org.jboss.ws.jaxws-client'])
serverAuthModules1.append(servAuthModule1)

def jaspiConfiguration2 = new groovy.util.Node(null, 'jaspi-configuration', ['name' : 'clientJaspi-jaas-lm-stack'])
jaspiAuthen.append(jaspiConfiguration2)
def serverAuthModules2 = new groovy.util.Node(null, 'server-auth-modules', [])
jaspiConfiguration2.append(serverAuthModules2)
def servAuthModule2 = new groovy.util.Node(null, 'server-auth-module',
        ['class-name' : 'org.jboss.wsf.stack.cxf.jaspi.client.module.SOAPClientAuthModule', 'module' : 'org.jboss.ws.jaxws-client'])
serverAuthModules2.append(servAuthModule2)

/**
 */

def ejbSubsystem = getSubsystem(root, "urn:jboss:domain:ejb3:")
def appSecurityDomains = null
for (element in ejbSubsystem) {
    if (element.name().getLocalPart() == 'application-security-domains') {
        appSecurityDomains = element
    }
}

def ejbSecurityDomain1 = appSecurityDomains.appendNode('application-security-domain', ['name':'JBossWS','security-domain':'JBossWS'])
def ejbSecurityDomain2 = appSecurityDomains.appendNode('application-security-domain', ['name':'JAASJBossWS','security-domain':'JAASJBossWS'])
def ejbSecurityDomain3 = appSecurityDomains.appendNode('application-security-domain', ['name':'ws-basic-domain','security-domain':'ws-basic-domain'])
def ejbSecurityDomain4 = appSecurityDomains.appendNode('application-security-domain', ['name':'JBossWSDigest','security-domain':'JBossWSDigest'])
def ejbSecurityDomain5 = appSecurityDomains.appendNode('application-security-domain', ['name':'jbws-test-https-realm','security-domain':'jbws-test-https-realm'])

//add to undertow
def undertowSubsystem = getSubsystem(root, "urn:jboss:domain:undertow:")
def undertowAppSecurityDomains = null
for (element in undertowSubsystem) {
    if (element.name().getLocalPart() == 'application-security-domains') {
        undertowAppSecurityDomains = element
    }
}
def appSecurityDomain = undertowAppSecurityDomains.appendNode('application-security-domain', ['name':'JBossWS','http-authentication-factory':'JBossWS'])
def basicAppSecurityDomain = undertowAppSecurityDomains.appendNode('application-security-domain', ['name':'ws-basic-domain','http-authentication-factory':'ws-basic-domain'])
def digestAppSecurityDomain = undertowAppSecurityDomains.appendNode('application-security-domain', ['name':'ws-digest-domain','http-authentication-factory':'ws-digest-domain'])
def jaspiAppSecurityDomain = undertowAppSecurityDomains.appendNode('application-security-domain', ['name':'jaspi','security-domain':'jaspi'])
def clientJaspiAppSecurityDomain = undertowAppSecurityDomains.appendNode('application-security-domain', ['name':'clientJaspi','security-domain':'clientJaspi'])


def tls = securitySubsystem.appendNode('tls')

def keyStores = tls.appendNode('key-stores')
def keyStore = keyStores.appendNode('key-store', ['name':'twoWayKS'])
def credentialReference = keyStore.appendNode('credential-reference',['clear-text':'changeit'])
def implementation = keyStore.appendNode('implementation',['type':'JKS'])
def filePath = keyStore.appendNode('file',['path':keystorePath])

def keyManagers = tls.appendNode('key-managers')
def keyManager = keyManagers.appendNode('key-manager', ['name':'twoWayKM','key-store':'twoWayKS'])
def credentialReferenceKM = keyManager.appendNode('credential-reference',['clear-text':'changeit'])

def serverSslContexts = tls.appendNode('server-ssl-contexts')
def serverSslContext = serverSslContexts.appendNode('server-ssl-context',
        ['name':'twoWaySSC','protocols':'TLSv1.2','need-client-auth':'true',
         'key-manager':'twoWayKM'])


def server = root.profile.subsystem.server[0]
def curHttpsListener = server.'https-listener'[0]
if (curHttpsListener != null) server.remove(curHttpsListener)
server.appendNode('https-listener', ['name':'jbws-test-https-listener','socket-binding':'https','security-realm':'jbws-test-https-realm'])


/**
 *
 * Add a system property for JBWS-3628 testcase
 */
root.children().add(1, new Node(null, 'system-properties', null)) //add system-properties node after the extensions
def systemProperties = root.'system-properties'[0]
systemProperties.appendNode('property', ['name':'org.jboss.wsf.test.JBWS3628TestCase.policy','value':'WS-Addressing_policy'])


/**
 * Save the configuration to a new file
 */

def writer = new StringWriter()
writer.println('<?xml version="1.0" encoding="UTF-8"?>')
new XmlNodePrinter(new PrintWriter(writer)).print(root)
def f = new File(outputFile)
f.write(writer.toString())
