package zserio.emit.cpp;

import java.util.Set;

import zserio.ast.CompoundType;
import zserio.ast.UnionType;
import zserio.emit.common.ZserioEmitException;
import zserio.tools.Parameters;

public class UnionEmitter extends CppDefaultEmitter
{
    public UnionEmitter(String outPathName, Parameters extensionParameters, Set<CompoundType> rpcTypes)
    {
        super(outPathName, extensionParameters);
        this.rpcTypes = rpcTypes;
    }

    @Override
    public void beginUnion(UnionType unionType) throws ZserioEmitException
    {
        final Object templateData = new UnionEmitterTemplateData(getTemplateDataContext(), unionType,
                rpcTypes.contains(unionType));

        processHeaderTemplate(TEMPLATE_HEADER_NAME, templateData, unionType);
        processSourceTemplate(TEMPLATE_SOURCE_NAME, templateData, unionType);
    }

    private static final String TEMPLATE_HEADER_NAME = "Union.h.ftl";
    private static final String TEMPLATE_SOURCE_NAME = "Union.cpp.ftl";

    private final Set<CompoundType> rpcTypes;
}
