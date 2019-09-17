package zserio.ast;

import java.util.List;

/**
 * The interface for all Zserio types which can be templated.
 */
public interface ZserioTemplatableType extends ZserioType
{
    /**
     * Gets list of template parameters.
     *
     * @return List of template parameters.
     */
    public List<String> getTemplateParameters();

    /**
     * Instantiates the template with actual template parameters (i.e. arguments).
     * The instantiated type is stored within the instantiations list which can be accessed by
     * getTemplateInstantiations(). If the template with same arguments is already instantiated,
     * nothing is done.
     *
     * @param templateArguments Actual template parameters.
     */
    public void instantiate(List<ZserioType> templateArguments);

    /**
     * Gets all template's instantiations.
     *
     * @return List of all instantiations.
     */
    public List<ZserioTemplatableType> getInstantiations();

    /**
     * Gets the template instantiation for the given arguments.
     *
     * @param templateArguments Template arguments.
     *
     * @return Template instantiation.
     */
    public ZserioTemplatableType getInstantiation(List<ZserioType> templateArguments);

    /**
     * Gets original template used for instantiation.
     *
     * @return Template of the current instantiation.
     */
    public ZserioTemplatableType getTemplate();
}
