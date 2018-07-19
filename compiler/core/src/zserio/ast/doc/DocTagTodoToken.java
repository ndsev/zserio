package zserio.ast.doc;

import java.util.ArrayList;
import java.util.List;

import zserio.antlr.DocCommentParserTokenTypes;
import zserio.antlr.util.BaseTokenAST;
import zserio.antlr.util.ParserException;

/**
 * Implements AST token for type DOC_TAG_TODO.
 */
public class DocTagTodoToken extends DocTokenAST
{
    /**
     * Empty constructor.
     */
    public DocTagTodoToken()
    {
        todoTextList = new ArrayList<String>();
    }

    /**
     * Gets list of text rows which describes the todo tag.
     *
     * @return List of text rows which describes the todo tag.
     */
    public Iterable<String> getTodoTextList()
    {
        return todoTextList;
    }

    @Override
    protected boolean evaluateChild(BaseTokenAST child) throws ParserException
    {
        switch (child.getType())
        {
        case DocCommentParserTokenTypes.DOC_TEXT:
            todoTextList.add(child.getText());
            break;

        default:
            return false;
        }

        return true;
    }

    private static final long serialVersionUID = 1L;

    private List<String>    todoTextList;
}
