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

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;


public class PlexusIoZipFileResourceCollection extends AbstractPlexusIoArchiveResourceCollection
{
    /**
     * The zip file resource collections role hint.
     */
    public static final String ROLE_HINT = "zipFile";

    /**
     * The zip file resource collections role hint for jar files.
     */
    public static final String JAR_ROLE_HINT = "jarFile";
    
    public PlexusIoZipFileResourceCollection()
    {
        
    }

    protected Iterator<PlexusIoResource> getEntries() throws IOException
    {
        final File f = getFile();
        if ( f == null )
        {
            throw new IOException( "The zip file has not been set." );
        }
        final URL url = new URL( "jar:" + f.toURI().toURL() + "!/");
        final ZipFile zipFile = new ZipFile( f );
        final Enumeration en = zipFile.entries();
        return new ZipFileResourceIterator( en, url, zipFile );
    }

    private static class ZipFileResourceIterator
        implements Iterator<PlexusIoResource>, Closeable
    {
        private final Enumeration en;

        private final URL url;

        private final ZipFile zipFile;

        public ZipFileResourceIterator( Enumeration en, URL url, ZipFile zipFile )
        {
            this.en = en;
            this.url = url;
            this.zipFile = zipFile;
        }

        public boolean hasNext()
        {
            return en.hasMoreElements();
        }

        public PlexusIoResource next()
        {
            final ZipEntry entry = (ZipEntry) en.nextElement();
            long l = entry.getTime();
            final long lastModified = l == -1 ? PlexusIoResource.UNKNOWN_MODIFICATION_DATE : l;
            final boolean dir = entry.isDirectory();
            final long size = dir ? PlexusIoResource.UNKNOWN_RESOURCE_SIZE : entry.getSize();

            return new PlexusIoURLResource(url, entry.getName(), lastModified,size,
                                                                    !dir,
                                                                    dir, true );
        }

        public void remove()
        {
            throw new UnsupportedOperationException( "Removing isn't implemented." );
        }

        public void close()
            throws IOException
        {
            zipFile.close();
        }
    }
}