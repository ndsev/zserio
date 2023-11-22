package zserio.tools;

import org.apache.commons.cli.Options;

import zserio.ast.Root;
import zserio.extension.common.ZserioExtensionException;

/**
 * The basic interface for all Zserio extensions.
 */
public interface Extension
{
    /**
     * Gets name of the extension.
     *
     * @return Name of this extension.
     */
    public String getName();

    /**
     * Returns the version of Zserio core which is expected by the extension.
     *
     * Zserio core checks if the current version can satisfy the extension
     * (i.e. if the AST and common interfaces are compatible).
     *
     * @return Version string of Zserio core which is expected by the extension.
     */
    public String getZserioVersion();

    /**
     * Returns the version of Zserio extension.
     *
     * @return Version string of Zserio extension.
     */
    public String getExtensionVersion();

    /**
     * Registers all command line options that extension accepts.
     *
     * @param options Instance where the options should be registered.
     */
    public void registerOptions(Options options);

    /**
     * Calls to check if the extension is enabled or not.
     *
     * @param parameters The parameters to pass to extension.
     *
     * @return true if the extension is enabled, otherwise false.
     */
    public boolean isEnabled(ExtensionParameters parameters);

    /**
     * Calls the extension to check Zserio tree.
     *
     * Extension does its basic checks on Zserio AST. Extensions which generate code should verify that
     * it will be possible to generate the given schema correctly (e.g. checks reserved keywords, etc.).
     *
     * The caller (i.e. Zserio core) is responsible for calling the check() phase before process().
     *
     * @param rootNode   The root node of Zserio tree to check.
     * @param parameters The parameters to pass to extension.
     *
     * @throws ZserioExtensionException In case of any error in extension.
     */
    public void check(Root rootNode, ExtensionParameters parameters) throws ZserioExtensionException;

    /**
     * Calls the extension to process Zserio tree.
     *
     * The caller (i.e. Zserio core) is responsible for calling the check() phase before process().
     *
     * @param rootNode   The root node of Zserio tree to process.
     * @param parameters The parameters to pass to extension.
     *
     * @throws ZserioExtensionException In case of any error in extension.
     */
    public void process(Root rootNode, ExtensionParameters parameters) throws ZserioExtensionException;
}
