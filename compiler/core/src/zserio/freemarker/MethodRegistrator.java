package zserio.freemarker;

import freemarker.template.Configuration;

public class MethodRegistrator
{
    public static void register(Configuration config)
    {
        config.setSharedVariable(CStrMethod.NAME, new CStrMethod());
    }
}
