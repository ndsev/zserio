package zserio.ast;

import zserio.antlr.util.ParserException;

public interface LinkAction
{
    void link(Scope ctxt) throws ParserException;
}
