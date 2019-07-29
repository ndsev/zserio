package zserio.emit.cpp;

import java.util.Arrays;
import zserio.ast.UnionType;
import zserio.emit.common.ZserioEmitException;

public class UnionEmitterTemplateData extends CompoundTypeTemplateData
{
    public UnionEmitterTemplateData(TemplateDataContext context, UnionType unionType,
            boolean usedInRpc) throws ZserioEmitException
    {
        super(context, unionType);

        needsRpcTraits = usedInRpc;

        if (needsRpcTraits)
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
