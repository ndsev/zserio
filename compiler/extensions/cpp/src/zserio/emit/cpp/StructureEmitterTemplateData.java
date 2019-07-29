package zserio.emit.cpp;

import java.util.Arrays;
import zserio.ast.StructureType;
import zserio.emit.common.ZserioEmitException;

public class StructureEmitterTemplateData extends CompoundTypeTemplateData
{
    public StructureEmitterTemplateData(TemplateDataContext context, StructureType structureType,
            boolean usedInRpc) throws ZserioEmitException
    {
        super(context, structureType);

        needsRpcTraits = usedInRpc;
        if (usedInRpc) // TODO: move to FTL
        {
            addHeaderSystemIncludes(Arrays.asList(
                    "vector",
                    "grpcpp/impl/codegen/serialization_traits.h",
                    "grpcpp/impl/codegen/status.h",
                    "grpcpp/impl/codegen/byte_buffer.h"));
        }
    }

    public boolean getNeedsRpcTraits()
    {
        return needsRpcTraits;
    }

    private final boolean needsRpcTraits;
}
