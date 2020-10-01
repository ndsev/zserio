package zserio.emit.doc;

import java.util.ArrayList;
import java.util.List;

import zserio.ast.ServiceMethod;
import zserio.ast.ServiceType;
import zserio.emit.common.ZserioEmitException;

public class ServiceTemplateData
{
    public ServiceTemplateData(TemplateDataContext context, ServiceType serviceType) throws ZserioEmitException
    {
        name = serviceType.getName();
        packageName = serviceType.getPackage().getPackageName().toString();
        anchorName = DocEmitterTools.getAnchorName(serviceType);
        docComments = new DocCommentsTemplateData(serviceType.getDocComments());
        for (ServiceMethod method : serviceType.getMethodList())
        {
            methodList.add(new MethodTemplateData(serviceType, method, context.getSymbolTemplateDataMapper()));
        }
        collaborationDiagramSvgFileName = context.getWithSvgDiagrams()
                ? DocEmitterTools.getTypeCollaborationSvgUrl(context.getOutputPath(), serviceType) : null;
    }

    public String getName()
    {
        return name;
    }

    public String getPackageName()
    {
        return packageName;
    }

    public String getAnchorName()
    {
        return anchorName;
    }

    public DocCommentsTemplateData getDocComments()
    {
        return docComments;
    }

    public Iterable<MethodTemplateData> getMethodList()
    {
        return methodList;
    }

    public String getCollaborationDiagramSvgFileName()
    {
        return collaborationDiagramSvgFileName;
    }

    public static class MethodTemplateData
    {
        public MethodTemplateData(ServiceType serviceType, ServiceMethod serviceMethod,
                SymbolTemplateDataMapper symbolTemplateDataMapper) throws ZserioEmitException
        {
            name = serviceMethod.getName();
            anchorName = DocEmitterTools.getAnchorName(serviceType, name);
            requestSymbol = symbolTemplateDataMapper.getSymbol(serviceMethod.getRequestType());
            responseSymbol = symbolTemplateDataMapper.getSymbol(serviceMethod.getResponseType());
            docComments = new DocCommentsTemplateData(serviceMethod.getDocComments());
        }

        public String getName()
        {
            return name;
        }

        public String getAnchorName()
        {
            return anchorName;
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

        private final String name;
        private final String anchorName;
        private final SymbolTemplateData requestSymbol;
        private final SymbolTemplateData responseSymbol;
        private final DocCommentsTemplateData docComments;
    }

    private final String name;
    private final String packageName;
    private final String anchorName;
    private final DocCommentsTemplateData docComments;
    private final List<MethodTemplateData> methodList = new ArrayList<MethodTemplateData>();
    private final String collaborationDiagramSvgFileName;
}
