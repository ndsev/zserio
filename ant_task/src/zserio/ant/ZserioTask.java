package zserio.ant;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.Path;

/**
 *
 * An ant task that handles code generation step in ant.
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
 * <zserio srcPath="src/path" srcFile="zs/all.zs" ignoreError="true" extraArgs="-option1 -option2">
 *     <arg name="cmdlineOption" value="value"/>
 *     <arg name="anotherOption"/>
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
 * ignoreError="true"
 * Do not report Zserio compilation error to Ant. Default is false.
 *
 * extraArgs="-option1 -option2"
 * Extra zserio options in the same format as given by command line.
 *
 * The options "srcPath" and "srcFile" are required. All the others are optional and can occur in any
 * combination.
 *
 * Supported children nodes:
 *
 * <arg name="name" value="value"/>
 *     Optional. Adds a custom option of the form "-name=value" or "-name" if value is not specified.
 *
 * <classpath>path-like-structure</classpath>
 *     Optional. Classpath used to locate and run Zserio Tool. It must include both the zserio_core.jar and
 *     all the required libraries (e.g. antlr.jar) and plugins (e.g. zserio_java.jar).
 *
 */
public class ZserioTask extends Task
{
    @SuppressWarnings("unchecked")
    @Override
    public ZserioTask clone() throws CloneNotSupportedException
    {
        ZserioTask cloned = (ZserioTask)super.clone();
        cloned.classpath = (Vector<Path>)classpath.clone();
        cloned.arguments = (Vector<Argument>)arguments.clone();

        return cloned;
    }

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

    public void setIgnoreError(boolean ignoreError)
    {
        this.ignoreError = ignoreError;
    }

    public void setExtraArgs(String extraArgs)
    {
        this.extraArgs = extraArgs;
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

        ToolWrapper tool = new ToolWrapper("zserio.tools.ZserioToolAntTask", classpath, ignoreError);
        tool.callMain(buildArgs());
    }

    private void validate() throws BuildException
    {
        if (srcPath == null)
            throw new BuildException("srcPath not set");

        if (srcFile == null)
            throw new BuildException("srcFile not set");
    }

    private String [] buildArgs()
    {
        ArrayList<String> argsList = new ArrayList<String>();

        argsList.add(OptionPrefix + OptionSrcPath);
        argsList.add(srcPath.toString());
        argsList.add(srcFile);

        for (Argument a : arguments)
        {
            argsList.add(OptionPrefix + a.getName());
            if (a.hasValue())
            {
                argsList.add(a.getValue());
            }
        }

        if (!extraArgs.isEmpty())
            argsList.addAll(Arrays.asList(extraArgs.split(" ")));

        String [] args = new String[argsList.size()];
        argsList.toArray(args);

        return args;
    }

    private String  srcFile = null;
    private File    srcPath = null;

    private boolean ignoreError = false;
    private String  extraArgs = "";

    private Vector<Path>        classpath = new Vector<Path>();
    private Vector<Argument>    arguments = new Vector<Argument>();

    private static final String OptionPrefix = "-";
    private static final String OptionSrcPath = "src";
}
