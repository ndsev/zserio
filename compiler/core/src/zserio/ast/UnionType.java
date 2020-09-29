package zserio.ast;

import java.util.ArrayList;
import java.util.List;

/**
 * AST node for Union types.
 *
 * Union types are Zserio types as well.
 */
public class UnionType extends CompoundType
{
    /**
     * Constructor.
     *
     * @param location              AST node location.
     * @param pkg                   Package to which belongs the union type.
     * @param name                  Name of the union type.
     * @param templateParameters    List of template parameters.
     * @param typeParameters        List of parameters for the union type.
     * @param fields                List of all fields of the union type.
     * @param functions             List of all functions of the union type.
     * @param docComments           List of documentation comments belonging to this node.
     */
    public UnionType(AstLocation location, Package pkg, String name, List<TemplateParameter> templateParameters,
            List<Parameter> typeParameters, List<Field> fields, List<Function> functions,
            List<DocComment> docComments)
    {
        super(location, pkg, name, templateParameters, typeParameters, fields, functions, docComments);
    }

    @Override
    public void accept(ZserioAstVisitor visitor)
    {
        visitor.visitUnionType(this);
    }

    @Override
    UnionType instantiateImpl(List<TemplateArgument> templateArguments, Package instantiationPackage)
    {
        final List<Parameter> instantiatedTypeParameters = new ArrayList<Parameter>();
        for (Parameter typeParameter : getTypeParameters())
            instantiatedTypeParameters.add(typeParameter.instantiate(getTemplateParameters(), templateArguments));

        final List<Field> instantiatedFields = new ArrayList<Field>();
        for (Field field : getFields())
            instantiatedFields.add(field.instantiate(getTemplateParameters(), templateArguments));

        final List<Function> instantiatedFunctions = new ArrayList<Function>();
        for (Function function : getFunctions())
            instantiatedFunctions.add(function.instantiate(getTemplateParameters(), templateArguments));

        return new UnionType(getLocation(), instantiationPackage, getName(), new ArrayList<TemplateParameter>(),
                instantiatedTypeParameters, instantiatedFields, instantiatedFunctions, getDocComments());
    }

    @Override
    void check()
    {
        super.check();
        checkTableFields();
    }
};
