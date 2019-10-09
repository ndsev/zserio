package zserio.tools;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import zserio.ast.PackageName;

/**
 * The manager which holds all Zserio input file names.
 */
public class InputFileManager
{
    /**
     * Constructor from command line arguments.
     *
     * @param commandLineArguments Command line arguments to construct from.
     */
    public InputFileManager(CommandLineArguments commandLineArguments)
    {
        this.commandLineArguments = commandLineArguments;
    }

    /**
     * Gets the input file full name.
     *
     * @param fileName Relative Input file name without source path.
     *
     * @return Input file full name constructed from given relative file name.
     */
    public String getFileFullName(String fileName)
    {
        final int lastDotIndex = fileName.lastIndexOf(".");
        if (lastDotIndex > 0)
            inputFileExtension = fileName.substring(lastDotIndex);

        final String fileFullName = getInputFileFullName(fileName);

        return fileFullName;
    }

    /**
     * Gets the input file full name.
     *
     * @param importedPackageName Package name from which to construct input file full name.
     *
     * @return Input file full name constructed from given import node.
     */
    public String getFileFullName(PackageName importedPackageName)
    {
        final String fileName = importedPackageName.toString(File.separator) + inputFileExtension;
        final String fileFullName = getInputFileFullName(fileName);

        return fileFullName;
    }

    /**
     * Registers the input file.
     *
     * @param fileFullName Input file full name to register.
     */
    public void registerFile(String fileFullName)
    {
        inputFiles.add(fileFullName);
    }

    /**
     * Returns true if the input file has been already registered.
     *
     * @param fileName Input file full name name to check.
     */
    public boolean isFileRegistered(String fileFullName)
    {
        return inputFiles.contains(fileFullName);
    }

    private String getInputFileFullName(String inputFileName)
    {
        final String srcPathName = commandLineArguments.getSrcPathName();

        return (srcPathName == null) ? inputFileName : new File(srcPathName, inputFileName).toString();
    }

    private final CommandLineArguments commandLineArguments;
    private final Set<String> inputFiles = new HashSet<String>();
    private String inputFileExtension = "";
}
