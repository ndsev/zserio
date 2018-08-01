package zserio.emit.cpp;

import antlr.collections.AST;
import zserio.ast.RpcType;
import zserio.tools.Parameters;

public class RpcEmitter extends CppDefaultEmitter
{
    public RpcEmitter(String outPathName, Parameters extensionParameters)
    {
        super(outPathName, extensionParameters);
    }

    @Override
    public void beginRpc(AST token) throws ZserioEmitCppException
    {
        if (!(token instanceof RpcType))
            throw new ZserioEmitCppException("Unexpected token type in beginRpc!");
        rpcType = (RpcType)token;
    }

    @Override
    public void endRoot() throws ZserioEmitCppException
    {
        if (rpcType == null)
            return;

        final TemplateDataContext templateDataContext = getTemplateDataContext();
        final Object templateData = new RpcEmitterTemplateData(templateDataContext, rpcType);

        processHeaderTemplate(TEMPLATE_HEADER_NAME, templateData, rpcType);
        processSourceTemplate(TEMPLATE_SOURCE_NAME, templateData, rpcType);
    }

    private RpcType rpcType;

    private static final String TEMPLATE_SOURCE_NAME = "Rpc.cpp.ftl";
    private static final String TEMPLATE_HEADER_NAME = "Rpc.h.ftl";
}
