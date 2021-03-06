package org.codehaus.plexus.components.io.resources;

/*
 * Copyright 2007 The Codehaus Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.io.IOException;
import java.util.Iterator;

import org.codehaus.plexus.components.io.filemappers.FileMapper;
import org.codehaus.plexus.components.io.filemappers.PrefixFileMapper;
import org.codehaus.plexus.components.io.fileselectors.FileSelector;
import org.codehaus.plexus.logging.LogEnabled;
import org.codehaus.plexus.logging.Logger;



/**
 * Default implementation of a resource collection.
 */
public abstract class AbstractPlexusIoResourceCollection
    implements PlexusIoResourceCollection, LogEnabled
{
    private String prefix;

    private String[] includes;

    private String[] excludes;

    private FileSelector[] fileSelectors;

    private boolean caseSensitive = true;

    private boolean usingDefaultExcludes = true;

    private boolean includingEmptyDirectories = true;

    private FileMapper[] fileMappers;
    
    private Logger logger;

    protected AbstractPlexusIoResourceCollection()
    {
    }
    
    protected AbstractPlexusIoResourceCollection( Logger logger )
    {
        this.logger = logger;
    }
    
    private Logger getLogger()
    {
        return logger;
    }
    
    /**
     * Sets a string of patterns, which excluded files
     * should match.
     */
    public void setExcludes( String[] excludes )
    {
        this.excludes = excludes;
    }

    /**
     * Returns a string of patterns, which excluded files
     * should match.
     */
    public String[] getExcludes()
    {
        return excludes;
    }

    /**
     * Sets a set of file selectors, which should be used
     * to select the included files.
     */
    public void setFileSelectors( FileSelector[] fileSelectors )
    {
        this.fileSelectors = fileSelectors;
    }

    /**
     * Returns a set of file selectors, which should be used
     * to select the included files.
     */
    public FileSelector[] getFileSelectors()
    {
        return fileSelectors;
    }

    /**
     * Sets a string of patterns, which included files
     * should match.
     */
    public void setIncludes( String[] includes )
    {
        this.includes = includes;
    }

    /**
     * Returns a string of patterns, which included files
     * should match.
     */
    public String[] getIncludes()
    {
        return includes;
    }

    /**
     * Sets the prefix, which the file sets contents shall
     * have.
     */
    public void setPrefix( String prefix )
    {
        this.prefix = prefix;
    }

    /**
     * Returns the prefix, which the file sets contents shall
     * have.
     */
    public String getPrefix()
    {
        return prefix;
    }

    /**
     * Sets, whether the include/exclude patterns are
     * case sensitive. Defaults to true.
     */
    public void setCaseSensitive( boolean caseSensitive )
    {
        this.caseSensitive = caseSensitive;
    }

    /**
     * Returns, whether the include/exclude patterns are
     * case sensitive. Defaults to true.
     */
    public boolean isCaseSensitive()
    {
        return caseSensitive;
    }

    /**
     * Sets, whether the default excludes are being
     * applied. Defaults to true.
     */
    public void setUsingDefaultExcludes( boolean usingDefaultExcludes )
    {
        this.usingDefaultExcludes = usingDefaultExcludes;
    }

    /**
     * Returns, whether the default excludes are being
     * applied. Defaults to true.
     */
    public boolean isUsingDefaultExcludes()
    {
        return usingDefaultExcludes;
    }

    /**
     * Sets, whether empty directories are being included. Defaults
     * to true.
     */
    public void setIncludingEmptyDirectories( boolean includingEmptyDirectories )
    {
        this.includingEmptyDirectories = includingEmptyDirectories;
    }

    /**
     * Returns, whether empty directories are being included. Defaults
     * to true.
     */
    public boolean isIncludingEmptyDirectories()
    {
        return includingEmptyDirectories;
    }

    protected boolean isSelected( PlexusIoResource plexusIoResource ) throws IOException
    {
        FileSelector[] fileSelectors = getFileSelectors();
        if ( fileSelectors != null )
        {
            for (FileSelector fileSelector : fileSelectors) {
                if (!fileSelector.isSelected(plexusIoResource)) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Returns the file name mappers, which are used to transform
     * the resource names.
     */
    public FileMapper[] getFileMappers()
    {
        return fileMappers;
    }

    /**
     * Sets the file name mappers, which are used to transform
     * the resource names.
     */
    public void setFileMappers( FileMapper[] fileMappers )
    {
        this.fileMappers = fileMappers;
    }

    public String getName( PlexusIoResource resource )
        throws IOException
    {
        String name = resource.getName();
        final FileMapper[] mappers = getFileMappers();
        if ( mappers != null )
        {
            for (FileMapper mapper : mappers) {
                name = mapper.getMappedFileName(name);
            }
        }
        return PrefixFileMapper.getMappedFileName( getPrefix(), name );
    }

    public long getLastModified()
        throws IOException
    {
        long lastModified = PlexusIoResource.UNKNOWN_MODIFICATION_DATE;
        for ( final Iterator iter = getResources();  iter.hasNext();  )
        {
            final PlexusIoResource res = (PlexusIoResource) iter.next();
            long l = res.getLastModified();
            if ( l == PlexusIoResource.UNKNOWN_MODIFICATION_DATE )
            {
                return PlexusIoResource.UNKNOWN_MODIFICATION_DATE;
            }
            if ( lastModified == PlexusIoResource.UNKNOWN_MODIFICATION_DATE
                            ||  l > lastModified )
            {
                lastModified = l;
            }
        }
        return lastModified;
    }

    public void enableLogging( Logger logger )
    {
        this.logger = logger;
    }
}