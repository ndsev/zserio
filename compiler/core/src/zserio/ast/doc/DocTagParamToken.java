package zserio.ast.doc;

import java.util.ArrayList;
import java.util.List;

import zserio.antlr.DocCommentParserTokenTypes;
import zserio.antlr.util.BaseTokenAST;
import zserio.antlr.util.ParserException;

/**
 * Implements AST token for type DOC_TAG_PARAM.
 */
public class DocTagParamToken extends DocTokenAST
{
    /**
     * Empty constructor.
     */
    public DocTagParamToken()
    {
        paramDescriptionList = new ArrayList<String>();
    }

    @Override
    public void evaluate() throws ParserException
    {
        paramName = getText();
    }

    /**
     * Gets parameter name of the tag.
     *
     * @return Parameter name of the tag.
     */
    public String getParamName()
    {
        return paramName;
    }

    /**
     * Gets list of text rows which describes the parameter of the tag.
     *
     * @return The list of text rows which describes the parameter of the tag.
     */
    public Iterable<String> getParamDescriptionList()
    {
        return paramDescriptionList;
    }

    @Override
    protected boolean evaluateChild(BaseTokenAST child) throws ParserException
    {
        switch (child.getType())
        {
        case DocCommentParserTokenTypes.DOC_TEXT:
            paramDescriptionList.add(child.getText());
            break;

        default:
            return false;
        }

        return true;
    }

    private static final long serialVersionUID = 1L;

    private String          paramName;
    private List<String>    paramDescriptionList;
}
