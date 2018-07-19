package zserio.tools;

import org.apache.commons.cli.Options;

import zserio.antlr.ZserioEmitter;
import zserio.ast.TokenAST;

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
     * @param params   The parameters to pass to extension.
     * @param emitter  The initialized zserio emitter to use.
     * @param rootNode The root node of AST tree.
     */
    public void generate(Parameters parameters, ZserioEmitter emitter, TokenAST rootNode);
}
