package zserio.extension.doc;

import zserio.ast.Package;
import zserio.ast.Root;
import zserio.ast.RuleGroup;
import zserio.ast.ZserioAstDefaultVisitor;

/*!
 * Rule group visitor.
 *
 * Checks whether the schema contains any rules.
 */
final class RuleGroupVisitor extends ZserioAstDefaultVisitor
{
    public boolean hasSchemaRules()
    {
        return hasSchemaRules;
    }

    @Override
    public void visitRoot(Root root)
    {
        if (!hasSchemaRules)
            root.visitChildren(this);
    }

    @Override
    public void visitPackage(Package pkg)
    {
        if (!hasSchemaRules)
            pkg.visitChildren(this);
    }

    @Override
    public void visitRuleGroup(RuleGroup ruleGroup)
    {
        hasSchemaRules = true;
    }

    private boolean hasSchemaRules = false;
};
