package zserio.emit.doc;

import java.util.ArrayList;
import java.util.List;

import zserio.ast.Rpc;
import zserio.ast.ServiceType;

public class ServiceTemplateData
{
    public ServiceTemplateData(ServiceType serviceType, String outputPath, boolean withSvgDiagrams)
    {
        name = serviceType.getName();
        packageName = serviceType.getPackage().getPackageName().toString();
        docComment = new DocCommentTemplateData(serviceType.getHiddenDocComment());
        for (Rpc rpc : serviceType.getRpcList())
        {
            rpcList.add(new RpcTemplateData(rpc));
        }
        collaborationDiagramSvgFileName = (withSvgDiagrams)
                ? DocEmitterTools.getTypeCollaborationSvgUrl(outputPath, serviceType) : null;
    }

    public String getName()
    {
        return name;
    }

    public String getPackageName()
    {
        return packageName;
    }

    public DocCommentTemplateData getDocComment()
    {
        return docComment;
    }

    public Iterable<RpcTemplateData> getRpcList()
    {
        return rpcList;
    }

    public String getCollaborationDiagramSvgFileName()
    {
        return collaborationDiagramSvgFileName;
    }

    public static class RpcTemplateData
    {
        public RpcTemplateData(Rpc rpc)
        {
            name = rpc.getName();
            requestType = new LinkedType(rpc.getRequestType());
            hasRequestStreaming = rpc.hasRequestStreaming();
            responseType = new LinkedType(rpc.getResponseType());
            hasResponseStreaming = rpc.hasResponseStreaming();
            docComment = new DocCommentTemplateData(rpc.getHiddenDocComment());
        }

        public String getName()
        {
            return name;
        }

        public LinkedType getRequestType()
        {
            return requestType;
        }

        public LinkedType getResponseType()
        {
            return responseType;
        }

        public boolean getHasRequestStreaming()
        {
            return hasRequestStreaming;
        }

        public boolean getHasResponseStreaming()
        {
            return hasResponseStreaming;
        }

        public DocCommentTemplateData getDocComment()
        {
            return docComment;
        }

        private final String name;
        private final LinkedType requestType;
        private final boolean hasRequestStreaming;
        private final LinkedType responseType;
        private final boolean hasResponseStreaming;
        private final DocCommentTemplateData docComment;
    }

    private final String name;
    private final String packageName;
    private final DocCommentTemplateData docComment;
    private final List<RpcTemplateData> rpcList = new ArrayList<RpcTemplateData>();
    private final String collaborationDiagramSvgFileName;
}