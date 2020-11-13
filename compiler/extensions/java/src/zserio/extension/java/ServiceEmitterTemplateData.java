package zserio.extension.java;

import java.util.ArrayList;
import java.util.List;

import zserio.ast.ServiceMethod;
import zserio.ast.ServiceType;
import zserio.ast.ZserioType;
import zserio.extension.common.ZserioExtensionException;
import zserio.extension.java.types.JavaNativeType;

public final class ServiceEmitterTemplateData extends UserTypeTemplateData
{
    public ServiceEmitterTemplateData(TemplateDataContext context, ServiceType serviceType)
            throws ZserioExtensionException
    {
        super(context, serviceType);

        final JavaNativeMapper javaTypeMapper = context.getJavaNativeMapper();

        final JavaNativeType nativeServiceType = javaTypeMapper.getJavaType(serviceType);
        // keep Zserio default formatting to ensure that all languages have same name of service methods
        servicePackageName = nativeServiceType.getPackageName().toString();

        final Iterable<ServiceMethod> methodList = serviceType.getMethodList();
        for (ServiceMethod method : methodList)
        {
            MethodTemplateData templateData = new MethodTemplateData(javaTypeMapper, method);
            this.methodList.add(templateData);
        }
    }

    public Iterable<MethodTemplateData> getMethodList()
    {
        return methodList;
    }

    public String getServiceFullName()
    {
        return servicePackageName.isEmpty() ? getName() : servicePackageName + "." + getName();
    }

    public static class MethodTemplateData
    {
        public MethodTemplateData(JavaNativeMapper typeMapper, ServiceMethod serviceMethod)
                throws ZserioExtensionException
        {
            name = serviceMethod.getName();

            final ZserioType responseType = serviceMethod.getResponseType();
            responseTypeFullName = typeMapper.getJavaType(responseType).getFullName();

            final ZserioType requestType = serviceMethod.getRequestType();
            requestTypeFullName = typeMapper.getJavaType(requestType).getFullName();
        }

        public String getName()
        {
            return name;
        }

        public String getResponseTypeFullName()
        {
            return responseTypeFullName;
        }

        public String getRequestTypeFullName()
        {
            return requestTypeFullName;
        }

        private final String name;
        private final String responseTypeFullName;
        private final String requestTypeFullName;
    }

    private final List<MethodTemplateData> methodList = new ArrayList<MethodTemplateData>();
    private final String servicePackageName;
}
