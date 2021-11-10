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
 Elytron security domian
 **/
def securitySubsystem =  getSubsystem(root, "urn:wildfly:elytron:")
def securityDomains = null
for (element in securitySubsystem) {
    if (element.name().getLocalPart() == 'security-domains') {
        securityDomains = element
    }
}
def securityDomain = securityDomains.appendNode('security-domain', ['name':'JBossWS','default-realm':'JBossWS','permission-mapper':'default-permission-mapper'])
def realm = securityDomain.appendNode('realm',['name':'JBossWS','role-decoder':'groups-to-roles'])

def securityDomain2 = securityDomains.appendNode('security-domain', ['name':'handlerauth-security-domain','default-realm':'handlerauth-security-domain','permission-mapper':'default-permission-mapper'])
def realm2 = securityDomain2.appendNode('realm',['name':'handlerauth-security-domain','role-decoder':'groups-to-roles'])

def securityDomain3 = securityDomains.appendNode('security-domain', ['name':'JBossWSSecurityDomainPermitAllTest',
         'default-realm':'JBossWSSecurityDomainPermitAllTest','permission-mapper':'default-permission-mapper'])
def realm3 = securityDomain3.appendNode('realm',['name':'JBossWSSecurityDomainPermitAllTest','role-decoder':'groups-to-roles'])

def securityDomain4 = securityDomains.appendNode('security-domain', ['name':'JBossWSSecurityDomainTest',
           'default-realm':'JBossWSSecurityDomainTest','permission-mapper':'default-permission-mapper'])
def realm4 = securityDomain4.appendNode('realm',['name':'JBossWSSecurityDomainTest','role-decoder':'groups-to-roles'])

def securityDomain5 = securityDomains.appendNode('security-domain',
        ['name':'jbws-test-https-realm','default-realm':'jbws-test-https-realm','permission-mapper':'default-permission-mapper'])
def realm5 = securityDomain5.appendNode('realm',['name':'jbws-test-https-realm','role-decoder':'groups-to-roles'])

/**
 Elytron security realm
 **/

def securityRealms = root.profile.subsystem.'security-realms'[0]
def propertiesRealm = securityRealms.appendNode('properties-realm', ['name':'JBossWS'])
def usersProperties = propertiesRealm.appendNode('users-properties',['path':usersPropFile, 'plain-text':'true'])
def groupsProperties = propertiesRealm.appendNode('groups-properties',['path':rolesPropFile])


def propertiesRealm2 = securityRealms.appendNode('properties-realm', ['name':'handlerauth-security-domain'])
def usersProperties2 = propertiesRealm2.appendNode('users-properties',['path':testResourcesDir + '/jaxws/handlerauth/jbossws-users.properties', 'plain-text':'true'])
def groupsProperties2 = propertiesRealm2.appendNode('groups-properties',['path':testResourcesDir + '/jaxws/handlerauth/jbossws-roles.properties'])


def propertiesRealm3 = securityRealms.appendNode('properties-realm', ['name':'JBossWSSecurityDomainPermitAllTest'])
def usersProperties3 = propertiesRealm3.appendNode('users-properties',['path':testResourcesDir + '/jaxws/samples/securityDomain/jbossws-users.properties', 'plain-text':'true'])
def groupsProperties3 = propertiesRealm3.appendNode('groups-properties',['path':testResourcesDir + '/jaxws/samples/securityDomain/jbossws-roles.properties'])

def propertiesRealm4 = securityRealms.appendNode('properties-realm', ['name':'JBossWSSecurityDomainTest'])
def usersProperties4 = propertiesRealm4.appendNode('users-properties',['path':testResourcesDir + '/jaxws/samples/securityDomain/jbossws-users.properties', 'plain-text':'true'])
def groupsProperties4 = propertiesRealm4.appendNode('groups-properties',['path':testResourcesDir + '/jaxws/samples/securityDomain/jbossws-roles.properties'])

def propertiesRealm5 = securityRealms.appendNode('properties-realm', ['name':'jbws-test-https-realm'])
def usersProperties5 = propertiesRealm5.appendNode('users-properties',['path':usersPropFile, 'plain-text':'true'])
def groupsProperties5 = propertiesRealm5.appendNode('groups-properties',['path':rolesPropFile])


/**
 HttpAuthentication Factory
 **/

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


def httpAuthenticationFactory2 = httpAuthen.appendNode('http-authentication-factory', ['name':'handlerauth-security-domain','http-server-mechanism-factory':'global', 'security-domain':'handlerauth-security-domain'])
def mechanismConfiguration2 = httpAuthenticationFactory2.appendNode('mechanism-configuration')
def mechanism2 = mechanismConfiguration2.appendNode('mechanism',['mechanism-name':'BASIC'])
def mechanismRealm2=mechanism2.appendNode('mechanism-realm',['realm-name':'handlerauth-security-domain'])

def httpAuthenticationFactory3 = httpAuthen.appendNode('http-authentication-factory', ['name':'JBossWSSecurityDomainPermitAllTest','http-server-mechanism-factory':'global', 'security-domain':'JBossWSSecurityDomainPermitAllTest'])
def mechanismConfiguration3 = httpAuthenticationFactory3.appendNode('mechanism-configuration')
def mechanism3 = mechanismConfiguration3.appendNode('mechanism',['mechanism-name':'BASIC'])
def mechanismRealm3=mechanism3.appendNode('mechanism-realm',['realm-name':'JBossWSSecurityDomainPermitAllTest'])


def httpAuthenticationFactory4 = httpAuthen.appendNode('http-authentication-factory', ['name':'JBossWSSecurityDomainTest','http-server-mechanism-factory':'global', 'security-domain':'JBossWSSecurityDomainTest'])
def mechanismConfiguration4 = httpAuthenticationFactory4.appendNode('mechanism-configuration')
def mechanism4 = mechanismConfiguration4.appendNode('mechanism',['mechanism-name':'BASIC'])
def mechanismRealm4=mechanism4.appendNode('mechanism-realm',['realm-name':'JBossWSSecurityDomainTest'])

def httpAuthenticationFactory5 = httpAuthen.appendNode('http-authentication-factory',
        ['name':'jbws-test-https-realm','http-server-mechanism-factory':'global', 'security-domain':'jbws-test-https-realm'])
def mechanismConfiguration5 = httpAuthenticationFactory5.appendNode('mechanism-configuration')
def mechanism5 = mechanismConfiguration5.appendNode('mechanism',['mechanism-name':'BASIC'])
def mechanismRealm5 =mechanism5.appendNode('mechanism-realm',['realm-name':'jbws-test-https-realm'])


//add this to ejb
def ejbSubsystem = getSubsystem(root, "urn:jboss:domain:ejb3:")
def appSecurityDomains = null
for (element in ejbSubsystem) {
    if (element.name().getLocalPart() == 'application-security-domains') {
        appSecurityDomains = element
    }
}

def ejbSecurityDomain1 = appSecurityDomains.appendNode('application-security-domain', ['name':'JBossWS','security-domain':'JBossWS'])
def ejbSecurityDomain2 = appSecurityDomains.appendNode('application-security-domain', ['name':'handlerauth-security-domain','security-domain':'handlerauth-security-domain'])
def ejbSecurityDomain3 = appSecurityDomains.appendNode('application-security-domain', ['name':'JBossWSSecurityDomainPermitAllTest','security-domain':'JBossWSSecurityDomainPermitAllTest'])
def ejbSecurityDomain4 = appSecurityDomains.appendNode('application-security-domain', ['name':'JBossWSSecurityDomainTest','security-domain':'JBossWSSecurityDomainTest'])

//add to undertow
def undertowSubsystem = getSubsystem(root, "urn:jboss:domain:undertow:")
def undertowAppSecurityDomains = null
for (element in undertowSubsystem) {
    if (element.name().getLocalPart() == 'application-security-domains') {
        undertowAppSecurityDomains = element
    }
}
def appSecurityDomain = undertowAppSecurityDomains.appendNode('application-security-domain', ['name':'JBossWS','http-authentication-factory':'JBossWS'])
def basicAppSecurityDomain = undertowAppSecurityDomains.appendNode('application-security-domain', ['name':'handlerauth-security-domain','http-authentication-factory':'handlerauth-security-domain'])
def basicAppSecurityDomain2 = undertowAppSecurityDomains.appendNode('application-security-domain', ['name':'JBossWSSecurityDomainPermitAllTest','http-authentication-factory':'JBossWSSecurityDomainPermitAllTest'])
def basicAppSecurityDomain3 = undertowAppSecurityDomains.appendNode('application-security-domain', ['name':'JBossWSSecurityDomainTest','http-authentication-factory':'JBossWSSecurityDomainTest'])


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
 * Save the configuration to a new file
 */

def writer = new StringWriter()
writer.println('<?xml version="1.0" encoding="UTF-8"?>')
new XmlNodePrinter(new PrintWriter(writer)).print(root)
def f = new File(outputFile)
f.write(writer.toString())