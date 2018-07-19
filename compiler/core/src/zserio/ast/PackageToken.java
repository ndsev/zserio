package zserio.ast;

import java.util.ArrayList;
import java.util.List;

import zserio.antlr.ZserioParserTokenTypes;
import zserio.antlr.util.BaseTokenAST;
import zserio.antlr.util.ParserException;

/**
 * The representation of AST PACKAGE node.
 */
public class PackageToken extends TokenAST
{
    /**
     * Empty constructor.
     */
    public PackageToken()
    {
        domainList = new ArrayList<String>();
    }

    /**
     * Returns the list of package domains (= identifiers separated by dots).
     */
    public Iterable<String> getDomainList()
    {
        return domainList;
    }

    @Override
    protected boolean evaluateChild(BaseTokenAST child) throws ParserException
    {
        switch (child.getType())
        {
        case ZserioParserTokenTypes.ID:
            domainList.add(child.getText());
            break;

        default:
            return false;
        }

        return true;
    }

    private static final long serialVersionUID = -1L;

    private List<String>    domainList;
}
