package zserio.ast;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * AST abstract node for all templatable types.
 */
abstract class TemplatableType extends DocumentableAstNode implements ZserioTemplatableType
{
    /**
     * Constructor.
     *
     * @param location AST node location.
     * @param templateParameters List of template parameters.
     * @param docComment Documentation comment belonging to this node.
     */
    public TemplatableType(AstLocation location, List<TemplateParameter> templateParameters,
            DocComment docComment)
    {
        super(location, docComment);

        this.templateParameters = templateParameters;
    }

    @Override
    public void visitChildren(ZserioAstVisitor visitor)
    {
        super.visitChildren(visitor);

        for (TemplateParameter templateParameter : templateParameters)
            templateParameter.accept(visitor);
    }

    @Override
    public List<TemplateParameter> getTemplateParameters()
    {
        return templateParameters;
    }

    @Override
    public List<ZserioTemplatableType> getInstantiations()
    {
        return Collections.unmodifiableList(instantiations);
    }

    @Override
    public ZserioTemplatableType getTemplate()
    {
        return template;
    }

    @Override
    public ArrayDeque<TypeReference> getInstantiationReferenceStack()
    {
        return instantiationReferenceStack.clone();
    }

    @Override
    public String getInstantiationName()
    {
        return instantiationName;
    }

    /**
     * Instantiates the referenced template using the given instantiation reference.
     *
     * @param instantiationReferenceStack Stack of instantiations leading to this instantiation.
     * @param instantiationPackage        Package where the template should be instantiated.
     *
     * @return Instantiated template.
     */
    TemplatableType instantiate(ArrayDeque<TypeReference> instantiationReferenceStack,
            Package instantiationPackage)
    {
        final TypeReference instantiationReference = instantiationReferenceStack.peek();
        final List<TemplateArgument> templateArguments = instantiationReference.getTemplateArguments();
        if (templateParameters.size() != templateArguments.size())
        {
            throw new ParserException(instantiationReference,
                    "Wrong number of template arguments for template '" + getName() + "'! Expecting " +
                    templateParameters.size() + ", got " + templateArguments.size() + "!");
        }

        TemplatableType instantiation = instantiateImpl(templateArguments, instantiationPackage);
        instantiation.instantiationReferenceStack = instantiationReferenceStack.clone();
        instantiation.template = this;
        instantiations.add(instantiation);
        return instantiation;
    }

    /**
     * Sets the unique name in the package for the instantiation.
     *
     * @param instantiationName Instantiation name unique in the package where template is located.
     */
    void resolveInstantiationName(String instantiationName)
    {
        this.instantiationName = instantiationName;
    }

    /**
     * Concrete implementation of template instantiation.
     *
     * @param templateArguments    Actual template parameters.
     * @parma instantiationPackage Package where to instantiate the template.
     */
    abstract TemplatableType instantiateImpl(List<TemplateArgument> templateArguments,
            Package instantiationPackage);

    private final List<TemplateParameter> templateParameters;
    private final List<ZserioTemplatableType> instantiations = new ArrayList<ZserioTemplatableType>();
    private ArrayDeque<TypeReference> instantiationReferenceStack = null;
    private TemplatableType template = null;
    private String instantiationName = null;
}
