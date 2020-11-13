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
     * Returns the version of ZserioTool which is expected by the extension.
     * ZserioTool then checks if the current version can satisfy the extension
     * (i.e. if the AST and common interfaces are compatible).
     *
     * @return Version string of ZserioTool which is expected by the extension.
     */
    public String getVersion();

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
     * Calls the extension to generate their output.
     *
     * @param parameters The parameters to pass to extension.
     * @param rootNode   The root node of Zserio tree to process.
     *
     * @throws ZserioExtensionException In case of any error in extension.
     */
    public void generate(ExtensionParameters parameters, Root rootNode) throws ZserioExtensionException;
}
