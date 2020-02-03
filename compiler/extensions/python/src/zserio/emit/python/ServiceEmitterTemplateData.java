package zserio.emit.python;

import java.util.ArrayList;
import java.util.List;

import zserio.ast.ServiceMethod;
import zserio.ast.ServiceType;
import zserio.ast.ZserioType;
import zserio.emit.common.ZserioEmitException;
import zserio.emit.python.types.PythonNativeType;

public final class ServiceEmitterTemplateData extends UserTypeTemplateData
{
    public ServiceEmitterTemplateData(TemplateDataContext context, ServiceType serviceType)
            throws ZserioEmitException
    {
        super(context, serviceType);

        final PythonNativeMapper pythonTypeMapper = context.getPythonNativeMapper();

        final PythonNativeType nativeServiceType = pythonTypeMapper.getPythonType(serviceType);
        // keep Zserio default formatting to ensure that all languages have same name of service methods
        servicePackageName = nativeServiceType.getPackageName().toString();

        final Iterable<ServiceMethod> methodList = serviceType.getMethodList();
        for (ServiceMethod method : methodList)
        {
            final MethodTemplateData templateData = new MethodTemplateData(pythonTypeMapper, method, this);
            this.methodList.add(templateData);
        }

        importPackage("zserio");
        importPackage("grpc");
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
        public MethodTemplateData(PythonNativeMapper typeMapper, ServiceMethod serviceMethod,
                ImportCollector importCollector) throws ZserioEmitException
        {
            name = serviceMethod.getName();

            final ZserioType responseType = serviceMethod.getResponseType();
            final PythonNativeType pythonResponseType = typeMapper.getPythonType(responseType);
            importCollector.importType(pythonResponseType);
            responseTypeFullName = pythonResponseType.getFullName();

            final ZserioType requestType = serviceMethod.getRequestType();
            final PythonNativeType pythonRequestType = typeMapper.getPythonType(requestType);
            importCollector.importType(pythonRequestType);
            requestTypeFullName = pythonRequestType.getFullName();
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
