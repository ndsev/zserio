package zserio.extension.doc;

import java.util.ArrayList;
import java.util.List;

import zserio.ast.ServiceMethod;
import zserio.ast.ServiceType;
import zserio.extension.common.ZserioExtensionException;

/**
 * FreeMarker template data for services in the package used by Package emitter.
 */
public final class ServiceTemplateData extends PackageTemplateDataBase
{
    public ServiceTemplateData(PackageTemplateDataContext context, ServiceType serviceType)
            throws ZserioExtensionException
    {
        super(context, serviceType);

        for (ServiceMethod method : serviceType.getMethodList())
            methodList.add(new MethodTemplateData(context, serviceType, method));
    }

    public Iterable<MethodTemplateData> getMethodList()
    {
        return methodList;
    }

    public static final class MethodTemplateData
    {
        public MethodTemplateData(PackageTemplateDataContext context, ServiceType serviceType,
                ServiceMethod serviceMethod) throws ZserioExtensionException
        {
            symbol = SymbolTemplateDataCreator.createData(context, serviceType, serviceMethod);
            requestSymbol =
                    SymbolTemplateDataCreator.createData(context, serviceMethod.getRequestTypeReference());
            responseSymbol =
                    SymbolTemplateDataCreator.createData(context, serviceMethod.getResponseTypeReference());
            docComments = new DocCommentsTemplateData(context, serviceMethod.getDocComments());
        }

        public SymbolTemplateData getSymbol()
        {
            return symbol;
        }

        public SymbolTemplateData getRequestSymbol()
        {
            return requestSymbol;
        }

        public SymbolTemplateData getResponseSymbol()
        {
            return responseSymbol;
        }

        public DocCommentsTemplateData getDocComments()
        {
            return docComments;
        }

        private final SymbolTemplateData symbol;
        private final SymbolTemplateData requestSymbol;
        private final SymbolTemplateData responseSymbol;
        private final DocCommentsTemplateData docComments;
    }

    private final List<MethodTemplateData> methodList = new ArrayList<MethodTemplateData>();
}
