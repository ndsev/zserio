package zserio.extension.java;

import java.util.ArrayList;
import java.util.List;

import zserio.ast.DocComment;
import zserio.ast.ServiceMethod;
import zserio.ast.ServiceType;
import zserio.ast.TypeReference;
import zserio.extension.common.ZserioExtensionException;
import zserio.extension.java.types.JavaNativeType;

/**
 * FreeMarker template data for ServiceEmitter.
 */
public final class ServiceEmitterTemplateData extends UserTypeTemplateData
{
    public ServiceEmitterTemplateData(TemplateDataContext context, ServiceType serviceType)
            throws ZserioExtensionException
    {
        super(context, serviceType, serviceType.getDocComments());

        final JavaNativeMapper javaTypeMapper = context.getJavaNativeMapper();

        final JavaNativeType nativeServiceType = javaTypeMapper.getJavaType(serviceType);
        // keep Zserio default formatting to ensure that all languages have same name of service methods
        final String servicePackageName = nativeServiceType.getPackageName().toString();
        serviceFullName = servicePackageName.isEmpty() ? getName() : servicePackageName + "." + getName();

        final Iterable<ServiceMethod> methodList = serviceType.getMethodList();
        for (ServiceMethod method : methodList)
        {
            MethodTemplateData templateData = new MethodTemplateData(context, method);
            this.methodList.add(templateData);
        }
    }

    public Iterable<MethodTemplateData> getMethodList()
    {
        return methodList;
    }

    public String getServiceFullName()
    {
        return serviceFullName;
    }

    public static class MethodTemplateData
    {
        public MethodTemplateData(TemplateDataContext context, ServiceMethod method)
                throws ZserioExtensionException
        {
            name = method.getName();

            final TypeReference responseTypeReference = method.getResponseTypeReference();
            final JavaNativeMapper javaTypeMapper = context.getJavaNativeMapper();
            final JavaNativeType responseNativeType = javaTypeMapper.getJavaType(responseTypeReference);
            responseTypeInfo = new NativeTypeInfoTemplateData(responseNativeType, responseTypeReference);

            final TypeReference requestTypeReference = method.getRequestTypeReference();
            final JavaNativeType requestNativeType = javaTypeMapper.getJavaType(requestTypeReference);
            requestTypeInfo = new NativeTypeInfoTemplateData(requestNativeType, requestTypeReference);

            final List<DocComment> methodDocComments = method.getDocComments();
            docComments = methodDocComments.isEmpty()
                    ? null : new DocCommentsTemplateData(context, methodDocComments);
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

    private final List<MethodTemplateData> methodList = new ArrayList<MethodTemplateData>();
    private final String serviceFullName;
}
