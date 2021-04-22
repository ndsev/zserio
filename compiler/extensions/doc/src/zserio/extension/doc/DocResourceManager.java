package zserio.extension.doc;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.HashSet;

import zserio.ast.AstLocation;
import zserio.ast.Package;
import zserio.extension.common.OutputFileManager;
import zserio.extension.common.ZserioExtensionException;
import zserio.tools.HashUtil;
import zserio.tools.StringJoinUtil;

/**
 * Documentation resource manager.
 *
 * Documentation resource manager manages all referenced resources found in Markdown documentation comments.
 *
 * Markdown documentation comments in schema can contain links to the following external resources:
 *
 * - to another valid zserio schema file (.zs)
 * - to any external file (e.g. png)
 * - to any other Markdown file (.md)
 *
 * This manager is handles properly each such link and it is responsible for copying of all corresponding
 * resources to the output directory.
 */
class DocResourceManager
{
    public DocResourceManager(OutputFileManager outputFileManager, DocExtensionParameters docParameters,
            PackageCollector packageCollector, Package rootPackage, boolean hasSchemaRules)
    {
        this.outputFileManager = outputFileManager;
        this.packageCollector = packageCollector;

        final String outputDir = docParameters.getOutputDir();
        final String htmlPackagesDirectory = StringJoinUtil.joinStrings(
                outputDir, DocDirectories.PACKAGES_DIRECTORY, File.separator);
        final String docResourceDirectory = StringJoinUtil.joinStrings(
                outputDir, DocDirectories.DOC_RESOURCES_DIRECTORY, File.separator);
        packagesDir = Paths.get(htmlPackagesDirectory).toAbsolutePath();
        resourcesDir = Paths.get(docResourceDirectory).toAbsolutePath();

        currentOutputDir = Paths.get(outputDir).toAbsolutePath();

        htmlResourceEmitter =
                new HtmlResourceEmitter(outputFileManager, docParameters, rootPackage, hasSchemaRules);
    }

    public void setCurrentOutputDir(String currentOutputDir)
    {
        this.currentOutputDir = Paths.get(currentOutputDir).toAbsolutePath();
    }

    public String addResource(AstLocation location, String resourceLink) throws ZserioExtensionException
    {
        if (isLocalResource(resourceLink))
        {
            final Path currentSourceDir = getCurrentSourceDir(location);
            final ResourceLink link = new ResourceLink(resourceLink);
            final LocalResource localResource = new LocalResource(currentSourceDir, link.getPath());
            final LocalResource mappedResource = mapLocalResource(localResource);

            return currentOutputDir.relativize(mappedResource.getFullPath()).toString() + link.getAnchor();
        }
        else
        {
            return resourceLink;
        }
    }

    private LocalResource mapLocalResource(LocalResource resource) throws ZserioExtensionException
    {
        final Package zserioPackage = packageCollector.getPathToPackageMap().get(resource.getFullPath());
        if (zserioPackage != null)
        {
            final String packageHtmlLink = PackageEmitter.getPackageHtmlLink(zserioPackage, ".");
            return new LocalResource(packagesDir, packageHtmlLink);
        }

        if (!resource.getFullPath().toFile().exists())
        {
            throw new ZserioExtensionException(
                    "Missing resource: '" + resource.getFullPath().toString() + "'!");
        }

        LocalResource mappedResource = resources.get(resource);
        if (mappedResource == null)
        {
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
        }

        return mappedResource;
    }

    private void copyResource(LocalResource srcResource, LocalResource dstResource)
            throws ZserioExtensionException
    {
        try
        {
            Files.createDirectories(dstResource.getPath());

            if (srcResource.getExtension().equals(MARKDOWN_EXTENSION) &&
                    dstResource.getExtension().equals(HTML_EXTENSION))
            {
                final Path origCurrentOutputDir = currentOutputDir;
                try
                {
                    currentOutputDir = dstResource.getPath();
                    final String markdown = new String(Files.readAllBytes(srcResource.getFullPath()),
                            StandardCharsets.UTF_8);
                    final String bodyContent = DocMarkdownToHtmlConverter.convert(this,
                            new AstLocation(srcResource.getFullPath().toString(), 0, 0), markdown);
                    htmlResourceEmitter.emit(dstResource.getPath(),
                            dstResource.getFileName(), srcResource.getFileName(), bodyContent);
                }
                catch (ZserioExtensionException e)
                {
                    throw new ZserioExtensionException(
                            "Failed to write html resource: " + e.getMessage() + "!");
                }
                finally
                {
                    currentOutputDir = origCurrentOutputDir;
                }
            }
            else
            {
                Files.copy(srcResource.getFullPath(), dstResource.getFullPath(),
                        StandardCopyOption.REPLACE_EXISTING);
                outputFileManager.registerOutputFile(dstResource.getFullPath().toFile());
            }
        }
        catch (IOException e)
        {
            throw new ZserioExtensionException("Failed to copy resource: '" + srcResource.getFullPath() +
                    "' to '" + dstResource.getFullPath() + "'!");
        }
    }

    private LocalResource addMappedResource(Path path, String baseName, String extension)
    {
        LocalResource mappedResource = new LocalResource(path, baseName, extension);
        int duplicityMarkerIndex = 0;
        while (!mappedResources.add(mappedResource))
        {
            mappedResource = new LocalResource(
                    path, baseName + "(" + (++duplicityMarkerIndex) + ")", extension);
        }
        return mappedResource;
    }

    private static boolean isLocalResource(String destination)
    {
        if (destination.startsWith("#"))
            return false; // local anchor

        try
        {
            final URL url = new URL(destination);
            return url.getProtocol().equals(LOCAL_FILE_SCHEME);
        }
        catch (MalformedURLException e)
        {}

        // not an URL, supposing that it's local resource
        return true;
    }

    private Path getCurrentSourceDir(AstLocation location)
    {
        final Path currentSourceDir = Paths.get(location.getFileName()).toAbsolutePath().getParent();
        return currentSourceDir != null ? currentSourceDir : Paths.get("");
    }

    private static class ResourceLink
    {
        public ResourceLink(String resourceLink)
        {
            final int anchorIndex = resourceLink.lastIndexOf('#');

            path = anchorIndex != -1 ? resourceLink.substring(0, anchorIndex) : resourceLink;
            anchor = anchorIndex != -1 ? resourceLink.substring(anchorIndex) : "";
        }

        public String getPath()
        {
            return path;
        }

        public String getAnchor()
        {
            return anchor;
        }

        private final String path;
        private final String anchor;
    }

    private static class LocalResource
    {
        public LocalResource(Path currentDir, String resourcePath)
        {
            final Path fullPath = currentDir.resolve(resourcePath).toAbsolutePath().normalize();
            this.path = fullPath.getParent();

            final String fileName = fullPath.getName(fullPath.getNameCount() - 1).toString();

            final int lastDotIndex = fileName.lastIndexOf('.');
            baseName = (lastDotIndex != -1) ? fileName.substring(0, lastDotIndex) : fileName;
            extension  = (lastDotIndex != -1) ? fileName.substring(lastDotIndex) : "";
        }

        public LocalResource(Path path, String baseName, String extension)
        {
            this.path = path;
            this.baseName = baseName;
            this.extension = extension;
        }

        public Path getFullPath()
        {
            return path.resolve(baseName + extension);
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

        public String getFileName()
        {
            return baseName + extension;
        }

        private final Path path;
        private final String baseName;
        private final String extension;
    }

    private final HashMap<LocalResource, LocalResource> resources = new HashMap<LocalResource, LocalResource>();
    private final HashSet<LocalResource> mappedResources = new HashSet<LocalResource>();

    private final static String LOCAL_FILE_SCHEME = "file";
    private final static String MARKDOWN_EXTENSION = ".md";
    private final static String HTML_EXTENSION = ".html";

    private final OutputFileManager outputFileManager;
    private final PackageCollector packageCollector;
    private final Path packagesDir;
    private final Path resourcesDir;
    private final HtmlResourceEmitter htmlResourceEmitter;

    private Path currentOutputDir;
}
