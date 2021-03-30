package zserio.tools;

/**
 * The main class for Zserio tool to be run from Python command line.
 */
public class ZserioToolPython
{
    /**
     * The entry point of Zserio tool for Python command line.
     *
     * @param args Command line arguments.
     */
    public static void main(String[] args)
    {
        if (!ZserioTool.runTool(args, ZserioTool.Executor.PYTHON_MAIN))
            System.exit(1);
    }
}
