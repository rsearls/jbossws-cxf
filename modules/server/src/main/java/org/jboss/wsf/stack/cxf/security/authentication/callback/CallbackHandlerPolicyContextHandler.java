package org.jboss.wsf.stack.cxf.security.authentication.callback;

import javax.security.auth.callback.CallbackHandler;
import javax.security.jacc.PolicyContextException;
import javax.security.jacc.PolicyContextHandler;

public class CallbackHandlerPolicyContextHandler implements PolicyContextHandler
{
    private static final String CALLBACK_HANDLER_KEY = "org.jboss.security.auth.spi.CallbackHandler";
    private static ThreadLocal<CallbackHandler> requestContext = new ThreadLocal<CallbackHandler>();

    public static void setCallbackHandler(CallbackHandler bean)
    {
        requestContext.set(bean);
    }

    /** Access the CallbackHandler policy context data.
     * @param key - "org.jboss.security.auth.spi.CallbackHandler"
     * @param data currently unused
     * @return The active CallbackHandler
     * @throws javax.security.jacc.PolicyContextException
     */
    public Object getContext(String key, Object data)
            throws PolicyContextException
    {
        Object context = null;
        if (key.equalsIgnoreCase(CALLBACK_HANDLER_KEY))
            context = requestContext.get();
        return context;
    }

    public String[] getKeys()
            throws PolicyContextException
    {
        String[] keys = {CALLBACK_HANDLER_KEY};
        return keys;
    }

    public boolean supports(String key)
            throws PolicyContextException
    {
        return key.equalsIgnoreCase(CALLBACK_HANDLER_KEY);
    }

}
