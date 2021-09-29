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

//def securityDomain = securityDomains.appendNode('security-domain', ['name':'JBossWS','cache-type':'default'])
//def authentication = securityDomain.appendNode('authentication')
//def loginModule = authentication.appendNode('login-module', ['code':'UsersRoles','flag':'required'])
//loginModule.appendNode('module-option', ['name':'unauthenticatedIdentity','value':'anonymous'])
//loginModule.appendNode('module-option', ['name':'usersProperties','value':usersPropFile])
//loginModule.appendNode('module-option', ['name':'rolesProperties','value':rolesPropFile])

def securityRealms = null
for (element in securitySubsystem) {
    if (element.name().getLocalPart() == 'security-realms') {
        securityRealms = element
    }
}

def propertiesRealm = securityRealms.appendNode('properties-realm', ['name':'JBossWS'])
def usersProperties = propertiesRealm.appendNode('users-properties',['path':usersPropFile, 'plain-text':'true'])
def groupsProperties = propertiesRealm.appendNode('groups-properties',['path':rolesPropFile])

/***************** handlerauth-security-domain **********************/
//def securityDomainBasicAuth = securityDomains.appendNode('security-domain', ['name':'handlerauth-security-domain','cache-type':'default'])
//def authenticationBasicAuth = securityDomainBasicAuth.appendNode('authentication')
//def loginModuleBasicAuth = authenticationBasicAuth.appendNode('login-module', ['code':'UsersRoles','flag':'required'])
//loginModuleBasicAuth.appendNode('module-option', ['name':'usersProperties','value':testResourcesDir + '/jaxws/handlerauth/jbossws-users.properties'])
//loginModuleBasicAuth.appendNode('module-option', ['name':'rolesProperties','value':testResourcesDir + '/jaxws/handlerauth/jbossws-roles.properties'])

def securityDomainHandlerauth = securityDomains.appendNode('security-domain', ['name':'handlerauth-security-domain','default-realm':'handlerauth-security-domain','permission-mapper':'default-permission-mapper'])
def realmHandlerauth = securityDomainHandlerauth.appendNode('realm',['name':'handlerauth-security-domain','role-decoder':'groups-to-roles'])

/***
def basicsecurityDomain = securityDomains.appendNode('security-domain', ['name':'ws-basic-domain','default-realm':'ws-basic-domain','permission-mapper':'default-permission-mapper'])
def basicrealm = basicsecurityDomain.appendNode('realm',['name':'ws-basic-domain','role-decoder':'groups-to-roles'])

def digestDomain = securityDomains.appendNode('security-domain', ['name':'ws-digest-domain','default-realm':'ws-digest-domain','permission-mapper':'default-permission-mapper'])
def digestRefRealm = digestDomain.appendNode('realm',['name':'ws-digest-domain','role-decoder':'groups-to-roles'])
***/
def propertiesRealmHandlerauth = securityRealms.appendNode('properties-realm', ['name':'handlerauth-security-domain'])
def usersPropertiesHandlerauth = propertiesRealmHandlerauth.appendNode('users-properties',['path':testResourcesDir + '/jaxws/handlerauth/jbossws-users.properties', 'plain-text':'true'])
def groupsPropertiesHandlerauth = propertiesRealmHandlerauth.appendNode('groups-properties',['path':testResourcesDir + '/jaxws/handlerauth/jbossws-roles.properties'])

/**************** JBossWSSecurityDomainPermitAllTest ****************/
//def aSecurityDomainBasicAuth = securityDomains.appendNode('security-domain', ['name':'JBossWSSecurityDomainPermitAllTest','cache-type':'default'])
//def aAuthenticationBasicAuth = aSecurityDomainBasicAuth.appendNode('authentication')
//def aLoginModuleBasicAuth = aAuthenticationBasicAuth.appendNode('login-module', ['code':'UsersRoles','flag':'required'])
//aLoginModuleBasicAuth.appendNode('module-option', ['name':'usersProperties','value':testResourcesDir + '/jaxws/samples/securityDomain/jbossws-users.properties'])
//aLoginModuleBasicAuth.appendNode('module-option', ['name':'rolesProperties','value':testResourcesDir + '/jaxws/samples/securityDomain/jbossws-roles.properties'])

def securityDomainJBossWSSecurityDomainPermit = securityDomains.appendNode('security-domain', ['name':'JBossWSSecurityDomainPermitAllTest','default-realm':'handlerauth-security-domain','permission-mapper':'default-permission-mapper'])
def realmJBossWSSecurityDomainPermit = securityDomainJBossWSSecurityDomainPermit.appendNode('realm',['name':'JBossWSSecurityDomainPermitAllTest','role-decoder':'groups-to-roles'])

def propertiesRealmJBossWSSecurityDomainPermit = securityRealms.appendNode('properties-realm', ['name':'JBossWSSecurityDomainPermitAllTest'])
def usersPropertiesJBossWSSecurityDomainPermit = propertiesRealmJBossWSSecurityDomainPermit.appendNode('users-properties',['path':testResourcesDir + '/jaxws/samples/securityDomain/jbossws-users.properties', 'plain-text':'true'])
def groupsPropertiesJBossWSSecurityDomainPermit = propertiesRealmJBossWSSecurityDomainPermit.appendNode('groups-properties',['path':testResourcesDir + '/jaxws/samples/securityDomain/jbossws-roles.properties'])

/********************* JBossWSSecurityDomainTest *********************/
//def bSecurityDomainBasicAuth = securityDomains.appendNode('security-domain', ['name':'JBossWSSecurityDomainTest','cache-type':'default'])
//def bAuthenticationBasicAuth = bSecurityDomainBasicAuth.appendNode('authentication')
//def bLoginModuleBasicAuth = bAuthenticationBasicAuth.appendNode('login-module', ['code':'UsersRoles','flag':'required'])
//bLoginModuleBasicAuth.appendNode('module-option', ['name':'usersProperties','value':testResourcesDir + '/jaxws/samples/securityDomain/jbossws-users.properties'])
//bLoginModuleBasicAuth.appendNode('module-option', ['name':'rolesProperties','value':testResourcesDir + '/jaxws/samples/securityDomain/jbossws-roles.properties'])

def securityDomainJBossWSSecurityDomain = securityDomains.appendNode('security-domain', ['name':'JBossWSSecurityDomainTest','default-realm':'handlerauth-security-domain','permission-mapper':'default-permission-mapper'])
def realmJBossWSSecurityDomain = securityDomainJBossWSSecurityDomain.appendNode('realm',['name':'JBossWSSecurityDomainTest','role-decoder':'groups-to-roles'])

def propertiesRealmJBossWSSecurityDomain = securityRealms.appendNode('properties-realm', ['name':'JBossWSSecurityDomainTest'])
def usersPropertiesJBossWSSecurityDomain = propertiesRealmJBossWSSecurityDomain.appendNode('users-properties',['path':testResourcesDir + '/jaxws/samples/securityDomain/jbossws-users.properties', 'plain-text':'true'])
def groupsPropertiesJBossWSSecurityDomain = propertiesRealmJBossWSSecurityDomain.appendNode('groups-properties',['path':testResourcesDir + '/jaxws/samples/securityDomain/jbossws-roles.properties'])

//def securityRealms = root.management.'security-realms'[0]
//def securityRealm = securityRealms.appendNode('security-realm', ['name':'jbws-test-https-realm'])
//def serverIdentities = securityRealm.appendNode('server-identities')
//def ssl = serverIdentities.appendNode('ssl')
//ssl.appendNode('keystore', ['path':keystorePath,'keystore-password':'changeit','alias':'tomcat'])
//--------- TODO


/**
 * Save the configuration to a new file
 */

def writer = new StringWriter()
writer.println('<?xml version="1.0" encoding="UTF-8"?>')
new XmlNodePrinter(new PrintWriter(writer)).print(root)
def f = new File(outputFile)
f.write(writer.toString())