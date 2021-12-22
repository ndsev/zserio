package zserio.extension.java;

import java.util.ArrayList;
import java.util.List;

import zserio.ast.ServiceMethod;
import zserio.ast.ServiceType;
import zserio.ast.TypeReference;
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

            final TypeReference responseTypeReference = serviceMethod.getResponseTypeReference();
            final JavaNativeType responseNativeType = typeMapper.getJavaType(responseTypeReference);
            responseTypeInfo = new TypeInfoTemplateData(responseTypeReference, responseNativeType);
            responseTypeFullName = responseNativeType.getFullName();

            final TypeReference requestTypeReference = serviceMethod.getRequestTypeReference();
            final JavaNativeType requestNativeType = typeMapper.getJavaType(requestTypeReference);
            requestTypeInfo = new TypeInfoTemplateData(requestTypeReference, requestNativeType);
            requestTypeFullName = requestNativeType.getFullName();
        }

        public String getName()
        {
            return name;
        }

        public TypeInfoTemplateData getResponseTypeInfo()
        {
            return responseTypeInfo;
        }

        public String getResponseTypeFullName()
        {
            return responseTypeFullName;
        }

        public TypeInfoTemplateData getRequestTypeInfo()
        {
            return requestTypeInfo;
        }

        public String getRequestTypeFullName()
        {
            return requestTypeFullName;
        }

        private final String name;
        private final TypeInfoTemplateData responseTypeInfo;
        private final String responseTypeFullName;
        private final TypeInfoTemplateData requestTypeInfo;
        private final String requestTypeFullName;
    }

    private final List<MethodTemplateData> methodList = new ArrayList<MethodTemplateData>();
    private final String servicePackageName;
}
