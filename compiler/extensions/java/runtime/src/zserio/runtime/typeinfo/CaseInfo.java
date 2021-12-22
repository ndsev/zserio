package zserio.runtime.typeinfo;

import java.util.Collections;
import java.util.List;

/**
 * Type information for choice type case.
 */
public class CaseInfo
{
    /**
     * Constructor.
     *
     * @param caseExpressions List of case expressions.
     * @param field Field defined for the given cases.
     */
    public CaseInfo(List<String> caseExpressions, FieldInfo field)
    {
        this.caseExpressions = caseExpressions;
        this.field = field;
    }

    /**
     * Gets sequence of case expressions.
     *
     * @return Unmodifiable list of case expressions.
     */
    public List<String> getCaseExpressions()
    {
        return Collections.unmodifiableList(caseExpressions);
    }

    /**
     * Gets type information for the field defined for current cases.
     *
     * @return Field type info or null in case of no field defined for the cases.
     */
    public FieldInfo getField()
    {
        return field;
    }

    private final List<String> caseExpressions;
    private final FieldInfo field;
}
