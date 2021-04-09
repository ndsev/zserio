package zserio.ast;

import java.util.Collections;
import java.util.List;

/**
 * AST node for group of rules.
 */
public class RuleGroup extends DocumentableAstNode implements PackageSymbol
{
    /**
     * Constructor
     *
     * @param location      AST node location.
     * @param pkg           Package to which belongs the rules type.
     * @param name          Name of the rule group.
     * @param rules         List of rules belonging to this rule group.
     * @param docComments   List of documentation comments belonging to this node.
     */
    public RuleGroup(AstLocation location, Package pkg, String name, List<Rule> rules,
            List<DocComment> docComments)
    {
        super(location, docComments);

        this.pkg = pkg;
        this.name = name;
        this.rules = rules;
    }

    @Override
    public void accept(ZserioAstVisitor visitor)
    {
        visitor.visitRuleGroup(this);
    }

    @Override
    public void visitChildren(ZserioAstVisitor visitor)
    {
        super.visitChildren(visitor);

        for (Rule rule : rules)
            rule.accept(visitor);
    }

    @Override
    public Package getPackage()
    {
        return pkg;
    }

    @Override
    public String getName()
    {
        return name;
    }

    /**
     * Gets rules defined within this group.
     *
     * @return List of rules.
     */
    public List<Rule> getRules()
    {
        return Collections.unmodifiableList(rules);
    }

    private final Package pkg;
    private final String name;
    private final List<Rule> rules;
}
