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
    public List<String> getTemplateParameters();

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
     * Gets location of the current instantiation.
     *
     * @return Location of the current instantiation or null when this is not an instantiation.
     */
    public AstLocation getInstantiationLocation();
}
