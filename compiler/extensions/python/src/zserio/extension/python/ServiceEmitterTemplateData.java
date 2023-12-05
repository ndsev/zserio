package zserio.extension.python;

import java.util.ArrayList;
import java.util.List;

import zserio.ast.ServiceMethod;
import zserio.ast.ServiceType;
import zserio.ast.TypeReference;
import zserio.extension.common.ZserioExtensionException;
import zserio.extension.python.types.PythonNativeType;

/**
 * FreeMarker template data for ServiceEmitter.
 */
public final class ServiceEmitterTemplateData extends UserTypeTemplateData
{
    public ServiceEmitterTemplateData(TemplateDataContext context, ServiceType serviceType)
            throws ZserioExtensionException
    {
        super(context, serviceType, serviceType);

        importPackage("typing");
        importPackage("zserio");

        final PythonNativeMapper pythonTypeMapper = context.getPythonNativeMapper();
        final PythonNativeType nativeServiceType = pythonTypeMapper.getPythonType(serviceType);
        // keep Zserio default formatting to ensure that all languages have same name of service methods
        servicePackageName = nativeServiceType.getPackageName().toString();

        final Iterable<ServiceMethod> methodList = serviceType.getMethodList();
        for (ServiceMethod method : methodList)
        {
            final MethodTemplateData templateData = new MethodTemplateData(context, method, this);
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

    public static final class MethodTemplateData
    {
        public MethodTemplateData(TemplateDataContext context, ServiceMethod method,
                ImportCollector importCollector) throws ZserioExtensionException
        {
            name = method.getName();
            snakeCaseName = PythonSymbolConverter.toLowerSnakeCase(name);
            clientMethodName = AccessorNameFormatter.getServiceClientMethodName(method);

            final TypeReference responseTypeReference = method.getResponseTypeReference();
            final PythonNativeMapper pythonTypeMapper = context.getPythonNativeMapper();
            final PythonNativeType pythonResponseType = pythonTypeMapper.getPythonType(responseTypeReference);
            importCollector.importType(pythonResponseType);
            responseTypeInfo = new NativeTypeInfoTemplateData(pythonResponseType, responseTypeReference);
            responseTypeFullName = PythonFullNameFormatter.getFullName(pythonResponseType);

            final TypeReference requestTypeReference = method.getRequestTypeReference();
            final PythonNativeType pythonRequestType = pythonTypeMapper.getPythonType(requestTypeReference);
            importCollector.importType(pythonRequestType);
            requestTypeInfo = new NativeTypeInfoTemplateData(pythonRequestType, requestTypeReference);
            requestTypeFullName = PythonFullNameFormatter.getFullName(pythonRequestType);

            docComments = DocCommentsDataCreator.createData(context, method);
        }

        public String getName()
        {
            return name;
        }

        public String getSnakeCaseName()
        {
            return snakeCaseName;
        }

        public String getClientMethodName()
        {
            return clientMethodName;
        }

        public NativeTypeInfoTemplateData getResponseTypeInfo()
        {
            return responseTypeInfo;
        }

        public String getResponseTypeFullName()
        {
            return responseTypeFullName;
        }

        public NativeTypeInfoTemplateData getRequestTypeInfo()
        {
            return requestTypeInfo;
        }

        public String getRequestTypeFullName()
        {
            return requestTypeFullName;
        }

        public DocCommentsTemplateData getDocComments()
        {
            return docComments;
        }

        private final String name;
        private final String snakeCaseName;
        private final String clientMethodName;
        private final NativeTypeInfoTemplateData responseTypeInfo;
        private final String responseTypeFullName;
        private final NativeTypeInfoTemplateData requestTypeInfo;
        private final String requestTypeFullName;
        private final DocCommentsTemplateData docComments;
    }

    private final String servicePackageName;
    private final List<MethodTemplateData> methodList = new ArrayList<MethodTemplateData>();
}
