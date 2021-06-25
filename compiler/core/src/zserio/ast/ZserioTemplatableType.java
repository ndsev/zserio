package zserio.ast;

import java.util.List;

/**
 * The interface for all Zserio types which can be templated.
 */
public interface ZserioTemplatableType extends ZserioScopedType
{
    /**
     * Gets list of template parameters.
     *
     * @return List of template parameters.
     */
    public List<TemplateParameter> getTemplateParameters();

    /**
     * Gets all template's instantiations.
     *
     * @return List of all instantiations.
     */
    public List<ZserioTemplatableType> getInstantiations();

    /**
     * Gets original template used for instantiation.
     *
     * @return Template of the current instantiation or null when this is not an instantiation.
     */
    public ZserioTemplatableType getTemplate();

    /**
     * Gets stack of instantiations leading to this instantiation.
     *
     * @return Stack of type references.
     */
    public Iterable<TypeReference> getInstantiationReferenceStack();

    /**
     * Gets reversed stack of instantiations leading to this instantiation.
     *
     * @return Stack of type references.
     */
    public Iterable<TypeReference> getReversedInstantiationReferenceStack();

    /**
     * Gets the unique instantiation name.
     *
     * @return Instantiation name unique in the package where template is located.
     */
    public String getInstantiationName();
}
