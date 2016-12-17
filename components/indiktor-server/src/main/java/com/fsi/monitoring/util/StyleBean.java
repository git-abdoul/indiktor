/*
 * Version: MPL 1.1/GPL 2.0/LGPL 2.1
 *
 * "The contents of this file are subject to the Mozilla Public License
 * Version 1.1 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations under
 * the License.
 *
 * The Original Code is ICEfaces 1.5 open source software code, released
 * November 5, 2006. The Initial Developer of the Original Code is ICEsoft
 * Technologies Canada, Corp. Portions created by ICEsoft are Copyright (C)
 * 2004-2006 ICEsoft Technologies Canada, Corp. All Rights Reserved.
 *
 * Contributor(s): _____________________.
 *
 * Alternatively, the contents of this file may be used under the terms of
 * the GNU Lesser General Public License Version 2.1 or later (the "LGPL"
 * License), in which case the provisions of the LGPL License are
 * applicable instead of those above. If you wish to allow use of your
 * version of this file only under the terms of the LGPL License and not to
 * allow others to use your version of this file under the MPL, indicate
 * your decision by deleting the provisions above and replace them with
 * the notice and other provisions required by the LGPL License. If you do
 * not delete the provisions above, a recipient may use your version of
 * this file under either the MPL or the LGPL License."
 *
 */

package com.fsi.monitoring.util;

import java.util.HashMap;
import java.util.TimeZone;
import java.io.Serializable;


public class StyleBean 
implements Serializable {

	private static final long serialVersionUID = -4480134537638160823L;

	// possible theme choices
    private final String RIME = "rime";
    private final String XP = "xp";
    private final String ROYALE = "royale";

    // default theme
    private String currentStyle = RIME;

    private HashMap<String,StylePath> styleMap;

    /**
     * Creates a new instance of the StyleBean.
     */
    public StyleBean() {
        styleMap = new HashMap<String,StylePath>(3);
        styleMap.put(RIME, new StylePath(
        		"/xmlhttp/css/rime/rime.css",
        		"/xmlhttp/css/rime/css-images/"));
        styleMap.put(XP, new StylePath(
        		"/xmlhttp/css/xp/xp.css",
        		"/xmlhttp/css/xp/css-images/"));
        styleMap.put(ROYALE, new StylePath(
        		"/xmlhttp/css/royale/royale.css",
        		"/xmlhttp/css/royale/css-images/"));
    }

    /**
     * Gets the current style.
     *
     * @return current style
     */
    public String getCurrentStyle() {
        return currentStyle;
    }
    
    public TimeZone getTimeZone() {
    	return TimeZone.getDefault();
    }

    /**
     * Gets the html needed to insert a valid css link tag.
     *
     * @return the tag information needed for a valid css link tag
     */
    public String getStyle() {
        return (styleMap.get(currentStyle)).getCssPath();
    }

    /**
     * Gets the image directory to use for the selectinputdate and tree
     * theming.
     *
     * @return image directory used for theming
     */
    public String getImageDirectory() {
        return (styleMap.get(currentStyle)).getImageDirPath();
    }
}
