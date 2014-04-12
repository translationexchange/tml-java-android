/*
 *  Copyright (c) 2014 Michael Berkovich, http://tr8nhub.com All rights reserved.
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in
 *  all copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 *  THE SOFTWARE.
 */

package com.tr8n.android.cache;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import com.tr8n.core.Tr8n;
import com.tr8n.core.Utils;

public class FileCache extends com.tr8n.core.cache.FileCache {
	
	public FileCache(Map<String, Object> config) {
		super(config);
	}

	protected File getApplicationPath() {
		if (applicationPath == null) {
			applicationPath = (File) getConfig().get("cache_dir");
		}
		return applicationPath;
	}

	protected File getCachePath(String cacheKey) {
		List<String> parts = new ArrayList<String>(Arrays.asList(cacheKey.split(Pattern.quote("/"))));
		String fileName = parts.remove(parts.size()-1);

		File fileCachePath = getCachePath();
		if (parts.size() > 0)
			fileCachePath = new File(getCachePath(), Utils.join(parts.toArray(), File.separator));

		fileCachePath.mkdirs();
		return new File(fileCachePath, fileName + ".json");
	}
	
	protected File getCachePath() {
		if (cachePath == null) {
			cachePath = new File(getApplicationPath(), "Tr8n");
			cachePath.mkdirs();
	        Tr8n.getLogger().debug("Cache path: " + cachePath.toString());
		}
		return cachePath;
	}

}
