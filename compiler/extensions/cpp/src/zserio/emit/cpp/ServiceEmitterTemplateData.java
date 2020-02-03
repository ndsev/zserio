package zserio.emit.cpp;

import java.util.List;
import java.util.ArrayList;
import zserio.ast.ServiceType;
import zserio.ast.ServiceMethod;
import zserio.ast.ZserioType;
import zserio.emit.common.ZserioEmitException;
import zserio.emit.cpp.types.CppNativeType;

public class ServiceEmitterTemplateData extends UserTypeTemplateData
{
    public ServiceEmitterTemplateData(TemplateDataContext context, ServiceType serviceType)
            throws ZserioEmitException
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

    public String getServicePackageName()
    {
        return servicePackageName;
    }

    public Iterable<MethodTemplateData> getMethodList()
    {
        return methodList;
    }

    public static class MethodTemplateData
    {
        public MethodTemplateData(CppNativeMapper typeMapper, ServiceMethod method)
                throws ZserioEmitException
        {
            name = method.getName();

            final ZserioType responseType = method.getResponseType();
            responseTypeFullName = typeMapper.getCppType(responseType).getFullName();

            final ZserioType requestType = method.getRequestType();
            requestTypeFullName = typeMapper.getCppType(requestType).getFullName();
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

    private final String servicePackageName;
    private final List<MethodTemplateData> methodList = new ArrayList<MethodTemplateData>();
}
