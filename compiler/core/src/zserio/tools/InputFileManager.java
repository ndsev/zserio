package zserio.tools;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import zserio.ast.PackageName;

/**
 * The manager which holds all Zserio input file names.
 */
public final class InputFileManager
{
    /**
     * Constructor from command line arguments.
     *
     * @param commandLineArguments Command line arguments.
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
        // imported package name might contain top level package name specified at command line
        final PackageName.Builder packageNameBuilder = new PackageName.Builder();
        packageNameBuilder.append(importedPackageName);
        final int numTopLevelIds = commandLineArguments.getTopLevelPackageNameIds().size();
        for (int i = 0; i < numTopLevelIds; i++)
            packageNameBuilder.removeFirstId();
        final String fileName = packageNameBuilder.get().toFilesystemPath() + inputFileExtension;
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

        if (!lastModifiedTimeError)
        {
            final long lastModifiedTime = new File(fileFullName).lastModified();
            if (lastModifiedTime > this.lastModifiedTime)
            {
                this.lastModifiedTime = lastModifiedTime;
            }
            else if (lastModifiedTime == 0L)
            {
                lastModifiedTimeError = true;
                this.lastModifiedTime = 0L;
                ZserioToolPrinter.printWarning(
                        "InputFileManager: Failed to get timestamp of source file: '" + fileFullName + "'.",
                        commandLineArguments.getWarningsConfig(), WarningsConfig.TIMESTAMP);
            }
        }
    }

    /**
     * Returns true if the input file has been already registered.
     *
     * @param fileFullName Input file full name name to check.
     *
     * @return True when the file is already registered.
     */
    public boolean isFileRegistered(String fileFullName)
    {
        return inputFiles.contains(fileFullName);
    }

    /**
     * Returns last modified timestamp of last modified Zserio source.
     *
     * @return Last modified timestamp (in milliseconds since epoch).
     */
    public long getLastModifiedTime()
    {
        return lastModifiedTime;
    }

    private String getInputFileFullName(String inputFileName)
    {
        final String srcPathName = commandLineArguments.getSrcPathName();

        return (srcPathName == null) ? inputFileName : new File(srcPathName, inputFileName).toString();
    }

    private final CommandLineArguments commandLineArguments;
    private final Set<String> inputFiles = new HashSet<String>();
    private String inputFileExtension = "";
    private long lastModifiedTime = 0L; // last modified timestamp (milliseconds since epoch)
    private boolean lastModifiedTimeError = false;
}
