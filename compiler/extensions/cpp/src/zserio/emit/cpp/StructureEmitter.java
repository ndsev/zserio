package zserio.emit.cpp;

import java.util.Set;

import zserio.ast.StructureType;
import zserio.emit.common.ZserioEmitException;
import zserio.tools.Parameters;

public class StructureEmitter extends CppDefaultEmitter
{
    public StructureEmitter(String outPathName, Parameters extensionParameters, Set<String> rpcTypeNames)
    {
        super(outPathName, extensionParameters);
        this.rpcTypeNames = rpcTypeNames;
    }

    @Override
    public void beginStructure(StructureType structureType) throws ZserioEmitException
    {
        final Object templateData = new StructureEmitterTemplateData(
                getTemplateDataContext(), structureType, rpcTypeNames);

        processHeaderTemplate(TEMPLATE_HEADER_NAME, templateData, structureType);
        processSourceTemplate(TEMPLATE_SOURCE_NAME, templateData, structureType);
    }

    private static final String TEMPLATE_SOURCE_NAME = "Structure.cpp.ftl";
    private static final String TEMPLATE_HEADER_NAME = "Structure.h.ftl";

    private final Set<String> rpcTypeNames;
}
