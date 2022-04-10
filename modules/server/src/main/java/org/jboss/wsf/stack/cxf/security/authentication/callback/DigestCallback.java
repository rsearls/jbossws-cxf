package org.jboss.wsf.stack.cxf.security.authentication.callback;

import java.security.MessageDigest;
import java.util.Map;

public interface DigestCallback
{
    /** Pass through access to the login module options. When coming from a
     * login module this includes the following keys:
     * javax.security.auth.login.name - for the username
     * javax.security.auth.login.password - for the String password
     */
    public void init(Map<String,Object> options);
    /**
     * Pre-hash callout to allow for content before the password. Any content
     * should be added using the MessageDigest update methods.
     * @param digest - the security digest being used for the one-way hash
     */
    public void preDigest(MessageDigest digest);
    /** Post-hash callout afer the password has been added to allow for content
     * after the password has been added. Any content should be added using the
     * MessageDigest update methods.
     * @param digest - the security digest being used for the one-way hash
     */
    public void postDigest(MessageDigest digest);
}
