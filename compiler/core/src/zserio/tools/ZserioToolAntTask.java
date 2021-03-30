package zserio.tools;

/**
 * The main class for Zserio tool to be run from Ant task.
 */
public class ZserioToolAntTask
{
    /**
     * The entry point of Zserio tool for Ant task.
     *
     * Calling System.exit() method throws if it is called from Ant task. Therefore this special entry point
     * has been introduced.
     *
     * @param args Command line arguments.
     *
     * @return Returns true in case of success, otherwise returns false.
     */
    public static boolean main(String[] args)
    {
        return ZserioTool.runTool(args, ZserioTool.Executor.ANT_TASK_MAIN);
    }
}
