package org.jboss.wsf.stack.cxf.jaspi.tmp;

import javax.security.auth.Subject;

import javax.security.auth.message.config.ServerAuthContext;
import javax.security.auth.message.AuthStatus;
import javax.security.auth.message.AuthException;
import javax.security.auth.message.MessageInfo;

public class TmpServerAuthContext implements ServerAuthContext {
    public AuthStatus validateRequest(MessageInfo messageInfo,
                                      Subject clientSubject,
                                      Subject serviceSubject) throws AuthException
    {   return null; }
    public AuthStatus secureResponse(MessageInfo messageInfo,
                                     Subject serviceSubject) throws AuthException
    {   return null; }
    public void cleanSubject(MessageInfo messageInfo, Subject subject) throws AuthException
    {}
}
