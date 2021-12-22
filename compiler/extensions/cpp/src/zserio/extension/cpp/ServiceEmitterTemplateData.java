package zserio.extension.cpp;

import java.util.List;
import java.util.ArrayList;
import zserio.ast.ServiceType;
import zserio.ast.CompoundType;
import zserio.ast.ServiceMethod;
import zserio.extension.common.ZserioExtensionException;
import zserio.extension.cpp.types.CppNativeType;

public class ServiceEmitterTemplateData extends UserTypeTemplateData
{
    public ServiceEmitterTemplateData(TemplateDataContext context, ServiceType serviceType)
            throws ZserioExtensionException
    {
        super(context, serviceType);

        final CppNativeMapper cppTypeMapper = context.getCppNativeMapper();

        final CppNativeType nativeServiceType = cppTypeMapper.getCppType(serviceType);
        // keep Zserio default formatting to ensure that all languages have same name of service methods
        servicePackageName = nativeServiceType.getPackageName().toString();

        Iterable<ServiceMethod> methodList = serviceType.getMethodList();
        for (ServiceMethod method : methodList)
        {
            addHeaderIncludesForType(cppTypeMapper.getCppType(method.getResponseType()));
            addHeaderIncludesForType(cppTypeMapper.getCppType(method.getRequestType()));
            final MethodTemplateData templateData = new MethodTemplateData(cppTypeMapper, method);
            this.methodList.add(templateData);
        }
    }

    public String getServiceFullName()
    {
        return servicePackageName.isEmpty() ? getName() : servicePackageName + "." + getName();
    }

    public Iterable<MethodTemplateData> getMethodList()
    {
        return methodList;
    }

    public static class MethodTemplateData
    {
        public MethodTemplateData(CppNativeMapper typeMapper, ServiceMethod method)
                throws ZserioExtensionException
        {
            name = method.getName();

            final CompoundType responseType = method.getResponseType();
            final CppNativeType cppResponseType = typeMapper.getCppType(responseType);
            responseTypeInfo = new NativeTypeInfoTemplateData(cppResponseType, responseType);

            final CompoundType requestType = method.getRequestType();
            final CppNativeType cppRequestType = typeMapper.getCppType(requestType);
            requestTypeInfo = new NativeTypeInfoTemplateData(cppRequestType, requestType);
        }

        public String getName()
        {
            return name;
        }

        public NativeTypeInfoTemplateData getResponseTypeInfo()
        {
            return responseTypeInfo;
        }

        public NativeTypeInfoTemplateData getRequestTypeInfo()
        {
            return requestTypeInfo;
        }

        private final String name;
        private final NativeTypeInfoTemplateData responseTypeInfo;
        private final NativeTypeInfoTemplateData requestTypeInfo;
    }

    private final String servicePackageName;
    private final List<MethodTemplateData> methodList = new ArrayList<MethodTemplateData>();
}
