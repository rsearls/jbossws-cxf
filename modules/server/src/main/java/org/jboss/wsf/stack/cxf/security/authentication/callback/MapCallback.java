package org.jboss.wsf.stack.cxf.security.authentication.callback;

import java.util.HashMap;
import java.util.Map;

import javax.security.auth.callback.Callback;

public class MapCallback implements Callback
{
    private Map<String,Object> info = new HashMap<String,Object>();

    public Object getInfo(String key)
    {
        return info.get(key);
    }
    public void setInfo(String key, Object value)
    {
        info.put(key, value);
    }
}
