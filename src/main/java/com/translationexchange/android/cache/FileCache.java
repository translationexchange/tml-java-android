/**
 * Copyright (c) 2015 Translation Exchange, Inc. All rights reserved.
 * <p/>
 * _______                  _       _   _             ______          _
 * |__   __|                | |     | | (_)           |  ____|        | |
 * | |_ __ __ _ _ __  ___| | __ _| |_ _  ___  _ __ | |__  __  _____| |__   __ _ _ __   __ _  ___
 * | | '__/ _` | '_ \/ __| |/ _` | __| |/ _ \| '_ \|  __| \ \/ / __| '_ \ / _` | '_ \ / _` |/ _ \
 * | | | | (_| | | | \__ \ | (_| | |_| | (_) | | | | |____ >  < (__| | | | (_| | | | | (_| |  __/
 * |_|_|  \__,_|_| |_|___/_|\__,_|\__|_|\___/|_| |_|______/_/\_\___|_| |_|\__,_|_| |_|\__, |\___|
 * __/ |
 * |___/
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 * <p/>
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 * <p/>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.translationexchange.android.cache;

import com.translationexchange.android.Tml;
import com.translationexchange.core.Application;
import com.translationexchange.core.Utils;
import com.translationexchange.core.cache.CacheVersion;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class FileCache extends com.translationexchange.core.cache.FileCache {

    public FileCache(Map<String, Object> config) {
        super(config);
    }

    protected File getApplicationPath() {
        if (applicationPath == null) {
            applicationPath = (File) getConfig().get("cache_dir");
        }
        return applicationPath;
    }

    protected File getCachePath(String cacheKey, Map<String, Object> options) {
        List<String> parts = new ArrayList<>(Arrays.asList(cacheKey.split(Pattern.quote("/"))));
        String fileName = parts.remove(parts.size() - 1);

        if (!cacheKey.equals("version")) {
            // check if the version was hardcoded in the init, and use it, otherwise check options
            String version = getConfigProperty("version", (String) options.get(CacheVersion.VERSION_KEY));
            if (version != null)
                parts.add(0, version);
        }

        File fileCachePath = getCachePath();
        if (parts.size() > 0)
            fileCachePath = new File(getCachePath(), Utils.join(parts.toArray(), File.separator));

        if (!fileCachePath.exists()) {
            Tml.getLogger().debug("FileCache", fileCachePath.getPath() + " created: " + fileCachePath.mkdirs());
        }
        return new File(fileCachePath, fileName + ".json");
    }

    public File getCachePath() {
        if (cachePath == null) {
            cachePath = new File(getApplicationPath(), "Tml_cache");
            if (!cachePath.exists()) {
                Tml.getLogger().debug("FileCache", cachePath.getPath() + " path created: " + cachePath.mkdirs());
            }
        }
        return cachePath;
    }

    @Override
    public Object fetch(String key, Map<String, Object> options) {
        if (key.contains("source")) {
            String local = key.substring(0, key.indexOf("/"));
            String currentVersion = (String) options.get("current_version");
            File fileCachePath = getCachePath();
            File file = new File(fileCachePath, currentVersion + File.separator + local + "/translations.json");
            if (!file.exists()) {
                Tml.getLogger().debug("FileCache", "Cache miss: " + key);
                return null;
            }

            try {
                Tml.getLogger().debug("FileCache", "Cache hit: " + key);
                return readFile(file);
            } catch (Exception ex) {
                Tml.getLogger().logException(ex);
                return null;
            }

        } else {
            return super.fetch(key, options);
        }
    }

    /**
     * {@inheritDoc}
     *
     * @param key
     * @param data
     * @param options
     */
    @Override
    public void store(String key, Object data, Map<String, Object> options) {
//        if (Tml.canWriteToCache) {
        if (key.contains("source")) {
            String local = key.substring(0, key.indexOf("/"));
            String currentVersion = (String) options.get("current_version");
            File file = new File(getCachePath(), currentVersion + File.separator + local + "/translations.json");
            try {
                Tml.getLogger().debug("Writing cache to :" + file);
                writeFile(file, data);
            } catch (Exception ex) {
                Tml.getLogger().logException("Failed to write cache to file", ex);
            }
        } else {
            super.store(key, data, options);
        }
//        }
    }

    /**
     * Verify that the current cache version is correct
     * Check it against the API
     */
    public CacheVersion verifyCacheVersion(Application application) throws Exception {
        if (cacheVersion == null) {
            // load version from local cache
            Tml.getLogger().debug("load version from local cache...");
            cacheVersion = new TmlCacheVersion();
            cacheVersion.fetchFromCache();
        }

//        // load version from server
//        if (cacheVersion.isExpired() || cacheVersion.isUnreleased()) {
//            Tml.getLogger().debug("load version from the server...");
//            cacheVersion.updateFromCDN(application.getHttpClient().getFromCDN("version", Utils.map("uncompressed", true)));
//        }
//        Tml.getLogger().debug("Cache version: " + cacheVersion.getVersion() + " " + cacheVersion.getExpirationMessage());
        return cacheVersion;
    }
}
