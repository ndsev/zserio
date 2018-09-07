package zserio.emit.doc;

import java.util.ArrayList;
import java.util.List;

import zserio.ast.Rpc;
import zserio.ast.ServiceType;

public class ServiceTemplateData
{
    public ServiceTemplateData(ServiceType serviceType)
    {
        name = serviceType.getName();
        packageName = serviceType.getPackage().getPackageName();
        docComment = new DocCommentTemplateData(serviceType.getHiddenDocComment());
        for (Rpc rpc : serviceType.getRpcList())
        {
            rpcList.add(new RpcTemplateData(rpc));
        }
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

    public static class RpcTemplateData
    {
        public RpcTemplateData(Rpc rpc)
        {
            name = rpc.getName();
            requestType = new LinkedType(rpc.getRequestType());
            responseType = new LinkedType(rpc.getResponseType());
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

        public DocCommentTemplateData getDocComment()
        {
            return docComment;
        }

        private String name;
        private LinkedType requestType;
        private LinkedType responseType;
        private DocCommentTemplateData docComment;
    }

    private String name;
    private String packageName;
    private DocCommentTemplateData docComment;
    private List<RpcTemplateData> rpcList = new ArrayList<RpcTemplateData>();
}