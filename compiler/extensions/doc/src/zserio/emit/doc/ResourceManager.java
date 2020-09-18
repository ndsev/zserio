package zserio.emit.doc;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.HashSet;

import zserio.tools.HashUtil;
import zserio.tools.StringJoinUtil;
import zserio.tools.ZserioToolPrinter;

class ResourceManager
{
    public static ResourceManager getInstance()
    {
        return instance;
    }

    public void setCurrentSourceDir(String currentSourceDir)
    {
        this.currentSourceDir = Paths.get(currentSourceDir != null ? currentSourceDir : ".");
    }

    public void setCurrentSourceDir(Path currentSourceDir)
    {
        this.currentSourceDir = currentSourceDir;
    }

    public Path getCurrentSourceDir()
    {
        return currentSourceDir;
    }

    public void setOutputRoot(String outputRoot)
    {
        this.outputRoot = Paths.get(outputRoot != null ? outputRoot : ".").toAbsolutePath();
    }

    public void setCurrentOutputDir(String currentOutputDir)
    {
        this.currentOutputDir = Paths.get(currentOutputDir != null ? currentOutputDir : ".").toAbsolutePath();
    }

    public void setSourceRoot(String sourceRoot)
    {
        this.sourceRoot = Paths.get(sourceRoot != null ? sourceRoot : ".").toAbsolutePath();
    }

    public void setSourceExtension(String sourceExtension)
    {
        this.sourceExtension = sourceExtension;
    }

    public String addResource(String destination)
    {
        if (isLocalResource(destination))
        {
            final LocalResource localResource = new LocalResource(currentSourceDir, destination);
            final LocalResource mappedResource = mapLocalResource(localResource);

            return currentOutputDir.relativize(mappedResource.getFullPath()).toString() +
                    localResource.getAnchor();
        }
        else
        {
            return destination;
        }
    }

    private ResourceManager()
    {}

    private LocalResource mapLocalResource(LocalResource resource)
    {
        final boolean isZserioPackage = resource.getExtension().equals(sourceExtension) &&
                resource.getPath().startsWith(sourceRoot);

        if (isZserioPackage)
        {
            final Path relativeSourcePath = sourceRoot.relativize(resource.getPath());
            final String packageName = relativeSourcePath.toString().replace(File.separator, ".");
            final String packageHtmlBaseName = StringJoinUtil.joinStrings(
                    packageName, resource.getBaseName(), ".");
            return new LocalResource(outputRoot.resolve(CONTENT_DIR), packageHtmlBaseName, HTML_EXTENSION,
                    resource.getAnchor());
        }
        else
        {
            LocalResource mappedResource = resources.get(resource);
            if (mappedResource != null)
                return mappedResource;

            final Path resourcesDir = outputRoot.resolve(RESOURCES_DIR);
            if (resource.getExtension().equals(MARKDOWN_EXTENSION))
            {
                mappedResource = addMappedResource(resourcesDir, resource.getBaseName(), HTML_EXTENSION);
            }
            else
            {
                mappedResource = addMappedResource(resourcesDir, resource.getBaseName(),
                        resource.getExtension());
            }

            resources.put(resource, mappedResource);
            copyResource(resource, mappedResource);
            return mappedResource;
        }
    }

    public static boolean isLocalResource(String destination)
    {
        if (destination.startsWith("#"))
            return false; // local anchor

        final Path path = Paths.get(destination);
        return path.toUri().getScheme().equals(LOCAL_FILE_SCHEME);
    }

    private void copyResource(LocalResource srcResource, LocalResource dstResource)
    {
        try
        {
            Files.createDirectories(dstResource.getPath());

            if (srcResource.getExtension().equals(MARKDOWN_EXTENSION) &&
                    dstResource.getExtension().equals(HTML_EXTENSION))
            {
                final Path origCwd = currentSourceDir;
                final Path origCurrentOutputDir = currentOutputDir;
                try
                {
                    currentSourceDir = srcResource.getPath();
                    currentOutputDir = dstResource.getPath();
                    final String markdown = new String(Files.readAllBytes(srcResource.getFullPath()),
                            StandardCharsets.UTF_8);
                    final String html = MarkdownToHtmlConverter.markdownToHtml(markdown);
                    Files.write(dstResource.getFullPath(), html.getBytes(StandardCharsets.UTF_8));
                }
                finally
                {
                    currentSourceDir = origCwd;
                    currentOutputDir = origCurrentOutputDir;
                }
            }
            else
            {
                Files.copy(srcResource.getFullPath(), dstResource.getFullPath(),
                        StandardCopyOption.REPLACE_EXISTING);
            }
        }
        catch (IOException e)
        {
            ZserioToolPrinter.printWarning("Failed to copy resource: '" + srcResource.getFullPath() + "' to '" +
                    dstResource.getFullPath() + "'!");
        }
    }

    private LocalResource addMappedResource(Path path, String baseName, String extension)
    {
        LocalResource mappedResource = new LocalResource(path, baseName, extension, "");
        int duplicityMarkerIndex = 0;
        while (!mappedResources.add(mappedResource))
        {
            mappedResource = new LocalResource(
                    path, baseName + "(" + (++duplicityMarkerIndex) + ")", extension, "");
        }
        return mappedResource;
    }

    private static class LocalResource
    {
        public LocalResource(Path currentDir, String destination)
        {
            final Path resourcePath = currentDir.resolve(destination).toAbsolutePath().normalize();

            path = resourcePath.getParent();
            final String fileNameWithAnchor = resourcePath.getName(resourcePath.getNameCount() - 1).toString();

            final int anchorIndex = fileNameWithAnchor.lastIndexOf('#');
            anchor = anchorIndex != -1 ? fileNameWithAnchor.substring(anchorIndex) : "";

            final String fileName = anchorIndex != -1
                    ? fileNameWithAnchor.substring(0, anchorIndex) : fileNameWithAnchor;
            final int lastDotIndex = fileName.lastIndexOf('.');
            baseName = lastDotIndex != -1 ? fileName.substring(0, lastDotIndex) : fileName;
            extension  = lastDotIndex != -1 ? fileName.substring(lastDotIndex) : "" ;
        }

        public LocalResource(Path path, String baseName, String extension, String anchor)
        {
            this.path = path;
            this.baseName = baseName;
            this.extension = extension;
            this.anchor = anchor;
        }

        public Path getFullPath()
        {
            return path.resolve(baseName + extension);
        }

        @Override
        public String toString()
        {
            return getFullPath().toString() + anchor;
        }

        @Override
        public boolean equals(Object object)
        {
            if (object instanceof LocalResource)
            {
                final LocalResource other = (LocalResource)object;
                return getPath().equals(other.getPath()) && getBaseName().equals(other.getBaseName()) &&
                        getExtension().equals(other.getExtension());
            }

            return false;
        }

        @Override
        public int hashCode()
        {
            int hash = HashUtil.HASH_SEED;
            hash = HashUtil.hash(hash, getPath());
            hash = HashUtil.hash(hash, getBaseName());
            hash = HashUtil.hash(hash, getExtension());
            return hash;
        }

        public Path getPath()
        {
            return path;
        }

        public String getBaseName()
        {
            return baseName;
        }

        public String getExtension()
        {
            return extension;
        }

        public String getAnchor()
        {
            return anchor;
        }

        private final Path path;
        private final String baseName;
        private final String extension;
        private final String anchor;
    }

    private final HashMap<LocalResource, LocalResource> resources = new HashMap<LocalResource, LocalResource>();
    private final HashSet<LocalResource> mappedResources = new HashSet<LocalResource>();

    private final static ResourceManager instance = new ResourceManager();
    private final static String CONTENT_DIR = "content";
    private final static String RESOURCES_DIR = "resources";
    private final static String LOCAL_FILE_SCHEME = "file";
    private final static String MARKDOWN_EXTENSION = ".md";
    private final static String HTML_EXTENSION = ".html";

    private Path outputRoot = Paths.get(".");
    private Path sourceRoot = Paths.get(".");
    private Path currentSourceDir = Paths.get(".");
    private Path currentOutputDir = outputRoot.resolve(CONTENT_DIR);
    private String sourceExtension = "";
}