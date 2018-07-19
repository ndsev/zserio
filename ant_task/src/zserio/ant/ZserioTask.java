package zserio.ant;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.Path;

/**
 *
 * An ant task that handles our sophisticated code generation step in ant.
 *
 * There are several requirements, whether or not the code should be generated
 *
 * - If one of the input zserio files has changed
 * - During our development cycle also the generator implementation itself
 *   changes, hence new files should be generated
 *
 * Furthermore there is need to delete already existing files, since the an
 * according zserio type is no longer present.
 *
 * When using the task you have to define a list of dependencies. These are
 * all files, which should force a generation of source classes, if they have
 * changed.
 *
 * On the other hand output covers all files which are generated and which
 * will be deleted and will be compared to the dependencies timestamp.
 *
 * With the contained classpath element the task solves a severe issue:
 *
 * Zserio is loaded during execution time of this target rather
 * than ant's bootstrapping. This has a simple reason: Ant
 * loads all its classes during bootstrapping. If we would just
 * use ant's classpath, we could not load zserio since it is
 * created by the task just before test-classes are called.
 * We avoid this situation by allowing the task to have
 * its own classpath.
 *
 * Supported syntax:
 *
 * <zserio srcPath="src/path" srcFile="zs/all.zs" java="gen/java" cpp="gen/cpp" doc="gen/html"
 *               xml="gen/xml/syntax.xml" clean="true" ignoreError="true">
 *     <arg name="cmdlineOption" value="value"/>
 *     <arg name="anotherOption"/>
 *     <dependencies>
 *         path-like-structure
 *     </dependencies>
 *     <output>
 *         path-like-structure
 *     </output>
 *     <classpath>
 *         path-like-structure
 *     </classpath>
 * </zserio>
 *
 * Supported attributes of the zserio task:
 *
 * srcPath="path"
 * Path to source files.
 *
 * srcFile="file"
 * Path to the input file, relative to srcPath.
 *
 * clean="true"
 * Remove all outputs first. (Requires "output" child node which specifies what to delete.)
 *
 * ignoreError="true"
 * Do not report Zserio compilation error to Ant. Default is false.
 *
 * java="outDir"
 * Generate java sources in given directory.
 *
 * cpp="outDir"
 * Generate C++ sources in given directory.
 *
 * doc="outDir"
 * Generate HTML documentation in given directory.
 *
 * xml="outFile"
 * Generate XML representation of the input to the given file.
 *
 * The options "srcPath" and "srcFile" are required. All the others are optional and can occur in any
 * combination.
 *
 * Supported children nodes:
 *
 * <arg name="name" value="value"/>
 *     Optional. Adds a custom option of the form "-name=value" or "-name" if value is not specified.
 *
 * <dependencies>path-like-structure</dependencies>
 *     Optional. Specifies dependencies. This is used only to avoid generating the output files
 *     if they have been generated already and all the dependencies are older than all the outputs.
 *     When omitted, output files are always generated.
 *
 * <output>path-like-structure</output>
 *     Optional. Specifies output files. This is used only to avoid generating the output files
 *     if they have been generated already and all the dependencies are older than all the outputs.
 *     When omitted, output files are always generated. Furthermore the list specifies what files to
 *     delete when "clean" attribute is set to "true".
 *
 * <classpath>path-like-structure</classpath>
 *     Optional. Classpath used to locate and run Zserio Tool. It must include both the zserio_core.jar and
 *     all the required libraries (e.g. antlr.jar) and plugins (e.g. zserio_java.jar).
 *
 */
public class ZserioTask extends Task
{
    /*
     * "Interface" to ant task attributes.
     *
     * For an attribute 'foo="bar"' ant invokes setFoo("bar").
     */
    public void setSrcFile(String srcFile)
    {
        this.srcFile = srcFile;
    }

    public void setSrcPath(File srcPath)
    {
        this.srcPath = srcPath;
    }

    public void setJava(File javaOutput)
    {
        this.javaOutput = javaOutput;
    }

    public void setCpp(File cppOutput)
    {
        this.cppOutput = cppOutput;
    }

    public void setDoc(File docOutput)
    {
        this.docOutput = docOutput;
    }

    public void setXml(File xmlOutput)
    {
        this.xmlOutput = xmlOutput;
    }

    /**
     * Clean is optional. If it is set, all files matching output
     * are deleted. Hence you have to use it carefully.
     *
     * If not set, the default value is false.
     *
     * @param clean is the output deleted before building?
     */
    public void setClean(boolean clean)
    {
        this.clean = clean;
    }

    public void setIgnoreError(boolean ignoreError)
    {
        this.ignoreError = ignoreError;
    }

    public void addDependencies(Path dependency)
    {
        dependencies.add(dependency);
    }

    public void addOutput(Path output)
    {
        this.output.add(output);
    }

    public void addClasspath(Path classpath)
    {
        this.classpath.add(classpath);
    }

    public Argument createArg()
    {
        Argument arg = new Argument();
        arguments.add(arg);
        return arg;
    }

    /**
     * This method is called from ant to execute the task.
     */
    public void execute() throws BuildException
    {
        validate();

        if (shouldCompile())
        {
            System.out.println("There are changes in depending files.");

            if (clean)
            {
                cleanPreviousOutput();
            }

            ToolWrapper tool = new ToolWrapper("zserio.tools.ZserioTool", classpath, ignoreError);
            tool.callMain(buildArgs());
        }
        else
        {
            System.out.println("Generated sources are up to date.");
        }
    }

    private void validate() throws BuildException
    {
        if (srcPath == null)
        {
            throw new BuildException("srcPath not set");
        }

        if (srcFile == null)
        {
            throw new BuildException("srcFile not set");
        }
    }

    private void tryAddOption(List<String> args, String optionName, String optionValue)
    {
        if (optionValue != null)
        {
            args.add(OptionPrefix + optionName);
            args.add(optionValue);
        }
    }

    private void tryAddOption(List<String> args, String optionName, File optionValue)
    {
        tryAddOption(args, optionName, optionValue != null ? optionValue.toString() : null);
    }

    private String [] buildArgs()
    {
        ArrayList<String> argsList = new ArrayList<String>();

        argsList.add(OptionPrefix + OptionSrcPath);
        argsList.add(srcPath.toString());
        argsList.add(srcFile);

        tryAddOption(argsList, OptionJava, javaOutput);
        tryAddOption(argsList, OptionCpp, cppOutput);
        tryAddOption(argsList, OptionDoc, docOutput);
        tryAddOption(argsList, OptionXml, xmlOutput);

        for (Argument a : arguments)
        {
            argsList.add(OptionPrefix + a.getName());
            if (a.hasValue())
            {
                argsList.add(a.getValue());
            }
        }

        String [] args = new String[argsList.size()];
        argsList.toArray(args);

        return args;
    }

    private boolean shouldCompile()
    {
        if (isPathListEmpty(dependencies) || isPathListEmpty(output))
        {
            verbose("dependency and/or output list is empty, proceeding with generation");
            return true;
        }

        final long latestDep = calcLatestDependency();
        final long earliestOutput = calcEarliestOutput();

        verbose("latest dependency: " + latestDep + ", earliest output: " + earliestOutput);

        return latestDep > earliestOutput;
    }

    private static boolean isPathListEmpty(List<Path> pathList)
    {
        for (Path p: pathList)
        {
            if (p.list().length > 0)
            {
                return false;
            }
        }

        return true;
    }

    private long calcLatestDependency()
    {
        long lastModified = 0;
        for (Path p : dependencies)
        {
            String [] files = p.list();
            for (String file: files)
            {
                final long m = new File(file).lastModified();
                verbose("ea: " + file + " " + m);

                if (m > lastModified)
                {
                    lastModified = m;
                }
            }
        }
        return lastModified;
    }

    private long calcEarliestOutput()
    {
        long lastModified = Long.MAX_VALUE;
        for (Path p : output)
        {
            String [] files = p.list();
            for (String file: files)
            {
                final long m = new File(file).lastModified();

                if (m < lastModified)
                {
                    lastModified = m;
                }
            }
        }
        return lastModified;
    }

    private void verbose(String msg)
    {
        getProject().log(msg, Project.MSG_VERBOSE);
    }

    private void cleanPreviousOutput()
    {
        int count = 0;
        for (Path p : output)
        {
            String [] files = p.list();
            for (String file: files)
            {
                verbose("delete: " + file);
                final File fileObject = new File(file);
                if (!fileObject.delete())
                    System.out.println("File " + file + " cannot be deleted!");
                else
                    count++;
            }
        }
        System.out.println("Deleted " + count + " files.");
    }

    private String  srcFile = null;
    private File    srcPath = null;

    private File    javaOutput;
    private File    cppOutput;
    private File    docOutput;
    private File    xmlOutput;

    private boolean clean = false;
    private boolean ignoreError = false;

    private Vector<Path>        dependencies = new Vector<Path>();
    private Vector<Path>        output = new Vector<Path>();
    private Vector<Path>        classpath = new Vector<Path>();
    private Vector<Argument>    arguments = new Vector<Argument>();

    private static final String OptionPrefix = "-";
    private static final String OptionSrcPath = "src";
    private static final String OptionJava = "java";
    private static final String OptionCpp = "cpp";
    private static final String OptionDoc = "doc";
    private static final String OptionXml = "xml";
}
