package zserio.extension.cpp;

import java.util.List;
import java.util.ArrayList;
import zserio.ast.ServiceType;
import zserio.ast.TypeReference;
import zserio.ast.ServiceMethod;
import zserio.extension.common.ZserioExtensionException;
import zserio.extension.cpp.types.CppNativeType;

/**
 * FreeMarker template data for ServiceEmitter.
 */
public final class ServiceEmitterTemplateData extends UserTypeTemplateData
{
    public ServiceEmitterTemplateData(TemplateDataContext context, ServiceType serviceType)
            throws ZserioExtensionException
    {
        super(context, serviceType, serviceType);

        final CppNativeMapper cppTypeMapper = context.getCppNativeMapper();
        final CppNativeType nativeServiceType = cppTypeMapper.getCppType(serviceType);
        // keep Zserio default formatting to ensure that all languages have same name of service methods
        servicePackageName = nativeServiceType.getPackageName().toString();

        Iterable<ServiceMethod> methodList = serviceType.getMethodList();
        for (ServiceMethod method : methodList)
        {
            addHeaderIncludesForType(cppTypeMapper.getCppType(method.getResponseTypeReference()));
            addHeaderIncludesForType(cppTypeMapper.getCppType(method.getRequestTypeReference()));
            final MethodTemplateData templateData = new MethodTemplateData(context, method);
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
        public MethodTemplateData(TemplateDataContext context, ServiceMethod method)
                throws ZserioExtensionException
        {
            name = method.getName();

            final TypeReference responseTypeReference = method.getResponseTypeReference();
            final CppNativeMapper cppTypeMapper = context.getCppNativeMapper();
            final CppNativeType cppResponseType = cppTypeMapper.getCppType(responseTypeReference);
            responseTypeInfo = new NativeTypeInfoTemplateData(cppResponseType, responseTypeReference);

            final TypeReference requestTypeReference = method.getRequestTypeReference();
            final CppNativeType cppRequestType = cppTypeMapper.getCppType(requestTypeReference);
            requestTypeInfo = new NativeTypeInfoTemplateData(cppRequestType, requestTypeReference);

            docComments = DocCommentsDataCreator.createData(context, method);
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

        public DocCommentsTemplateData getDocComments()
        {
            return docComments;
        }

        private final String name;
        private final NativeTypeInfoTemplateData responseTypeInfo;
        private final NativeTypeInfoTemplateData requestTypeInfo;
        private final DocCommentsTemplateData docComments;
    }

    private final String servicePackageName;
    private final List<MethodTemplateData> methodList = new ArrayList<MethodTemplateData>();
}
