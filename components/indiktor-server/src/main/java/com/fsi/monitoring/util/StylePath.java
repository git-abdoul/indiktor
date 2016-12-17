package com.fsi.monitoring.util;

import java.io.Serializable;

public class StylePath 
implements Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = 5585140822910468920L;
	
	private String cssPath;
    private String imageDirPath;

    public StylePath(String cssPath, String imageDirPath) {
        this.cssPath = cssPath;
        this.imageDirPath = imageDirPath;
    }

    public String getCssPath() {
        return cssPath;
    }

    public String getImageDirPath() {
        return imageDirPath;
    }
}
