package zserio.ast;

import java.util.ArrayList;
import java.util.List;

import zserio.antlr.ZserioParserTokenTypes;
import zserio.antlr.util.BaseTokenAST;
import zserio.antlr.util.ParserException;

/**
 * AST node for import declaration.
 */
public class Import extends TokenAST
{
    /**
     * Empty constructor.
     */
    public Import()
    {
        packagePath = new ArrayList<String>();
        typeName = null;
    }

    /**
     * Gets the package path specified by the import.
     *
     * @return List of subpackage names specified by the import.
     */
    public List<String> getPackagePath()
    {
        return packagePath;
    }

    /**
     * Gets the type name specified by the import.
     *
     * @return Type name for single type import or null for package type import.
     */
    public String getTypeName()
    {
        return typeName;
    }

    @Override
    protected boolean evaluateChild(BaseTokenAST child) throws ParserException
    {
        switch (child.getType())
        {
        case ZserioParserTokenTypes.ID:
            if (typeName != null)
                packagePath.add(typeName);
            typeName = child.getText();
            break;

        case ZserioParserTokenTypes.MULTIPLY:
            if (typeName != null)
                packagePath.add(typeName);
            typeName = null;
            break;

        default:
            return false;
        }

        return true;
    }

    private static final long serialVersionUID = 1L;

    private final List<String>  packagePath;
    private String              typeName;
}
