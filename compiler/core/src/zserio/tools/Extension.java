package zserio.tools;

import org.apache.commons.cli.Options;

import zserio.ast.Root;

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
     * Returns the version of the extension. The version must match the ZserioTool version.
     *
     * @return Version string which must match the ZserioTool version.
     */
    public String getVersion();

    /**
     * Registers all command line options that extension accepts.
     *
     * @param options Instance where the options should be registered.
     */
    public void registerOptions(Options options);

    /**
     * Calls the extension to generate their output.
     *
     * @param parameters The parameters to pass to extension.
     * @param rootNode   The root node of Zserio types tree to use for emitting.
     */
    public void generate(Parameters parameters, Root rootNode);
}
