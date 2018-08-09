package zserio.ast;

import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;

import zserio.antlr.ZserioParserTokenTypes;
import zserio.antlr.util.BaseTokenAST;
import zserio.antlr.util.ParserException;
import zserio.tools.ZserioToolPrinter;

/**
 * AST node for service types.
 *
 * Service types are Zserio types as well.
 */
public class ServiceType extends CompoundType
{
    public ServiceType()
    {
        rpcs = new ArrayList<RpcType>();
    }

    @Override
    public void callVisitor(ZserioTypeVisitor visitor)
    {
        visitor.visitServiceType(this);
    }

    @Override
    protected boolean evaluateChild(BaseTokenAST child) throws ParserException
    {
        switch (child.getType())
        {
        case ZserioParserTokenTypes.ID:
            if (!(child instanceof IdToken))
                return false;
            setName(child.getText());
            break;

        case ZserioParserTokenTypes.RPC:
            if (!(child instanceof RpcType))
                return false;
            addRpc((RpcType)child);
            break;

        default:
            return false;
        }

        return true;
    }

    public void addRpc(RpcType r)
    {
        rpcs.add(r);
    }

    public Iterable<RpcType> getRpcList()
    {
            return rpcs;
    }

    public Iterable<ZserioType> getParameterTypes()
    {
        Set<ZserioType> parameterTypes = new HashSet<ZserioType>();
        rpcs.forEach((r) -> {
                parameterTypes.add(r.getRequestType());
                parameterTypes.add(r.getResponseType());
        });
        return parameterTypes;
    }

    @Override
    protected void evaluate() throws ParserException
    {
        evaluateHiddenDocComment(this);
        setDocComment(getHiddenDocComment());
    }

    final private List<RpcType> rpcs;
    private RpcType currentRpc;
}
