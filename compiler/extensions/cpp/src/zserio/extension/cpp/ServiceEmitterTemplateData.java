package zserio.extension.cpp;

import java.util.List;
import java.util.ArrayList;
import zserio.ast.ServiceType;
import zserio.ast.TypeReference;
import zserio.ast.DocComment;
import zserio.ast.ServiceMethod;
import zserio.extension.common.ZserioExtensionException;
import zserio.extension.cpp.types.CppNativeType;

/**
 * FreeMarker template data for ServiceEmitter.
 */
public class ServiceEmitterTemplateData extends UserTypeTemplateData
{
    public ServiceEmitterTemplateData(TemplateDataContext context, ServiceType serviceType)
            throws ZserioExtensionException
    {
        super(context, serviceType, serviceType.getDocComments());

        final CppNativeMapper cppTypeMapper = context.getCppNativeMapper();

        final CppNativeType nativeServiceType = cppTypeMapper.getCppType(serviceType);
        // keep Zserio default formatting to ensure that all languages have same name of service methods
        servicePackageName = nativeServiceType.getPackageName().toString();

        Iterable<ServiceMethod> methodList = serviceType.getMethodList();
        for (ServiceMethod method : methodList)
        {
            addHeaderIncludesForType(cppTypeMapper.getCppType(method.getResponseTypeReference()));
            addHeaderIncludesForType(cppTypeMapper.getCppType(method.getRequestTypeReference()));
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

            final TypeReference responseTypeReference = method.getResponseTypeReference();
            final CppNativeType cppResponseType = typeMapper.getCppType(responseTypeReference);
            responseTypeInfo = new NativeTypeInfoTemplateData(cppResponseType, responseTypeReference);

            final TypeReference requestTypeReference = method.getRequestTypeReference();
            final CppNativeType cppRequestType = typeMapper.getCppType(requestTypeReference);
            requestTypeInfo = new NativeTypeInfoTemplateData(cppRequestType, requestTypeReference);

            final List<DocComment> methodDocComments = method.getDocComments();
            docComments = methodDocComments.isEmpty() ? null : new DocCommentsTemplateData(methodDocComments);
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
