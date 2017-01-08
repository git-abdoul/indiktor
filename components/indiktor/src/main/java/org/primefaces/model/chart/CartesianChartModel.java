/*
 * Copyright 2009-2012 Prime Teknoloji.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.primefaces.model.chart;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class CartesianChartModel extends ChartModel implements Serializable {
	private static final long serialVersionUID = 9010345273689168785L;
	
	private int nbOfCategory = 2;
	private List<ChartSeries> series = new ArrayList<ChartSeries>();

    public void setNbOfCategory(int nbOfCategory) {
		this.nbOfCategory = nbOfCategory;
	}

	public List<ChartSeries> getSeries() {
        return series;
    }

    public void addSeries(ChartSeries chartSeries) {
        this.series.add(chartSeries);
    }

    public void clear() {
        this.series.clear();
    }

    /**
     * Finds the categories using first series
     *
     * @return List of categories
     */
    public List<String> getCategories() {
        List<String> categories = new ArrayList<String>();
        if(series.size() > 0) {
            Map<Object,Number> firstSeriesData = series.get(0).getData();
            int seriesDataSz = firstSeriesData.size();
            int intervalData = seriesDataSz/(nbOfCategory-1);
            int i=0;
            for(Iterator<Object> it = firstSeriesData.keySet().iterator(); it.hasNext();) {
                Object key = it.next(); 
                if (key instanceof String) {
                	boolean toAdd = (nbOfCategory>0)?false:true;
                	 if (!toAdd)
                		 toAdd = (i%intervalData==0)?true:false;
                	 
                	 if (toAdd)
                		 categories.add(key.toString());
                }
                else {
                	break;
                }
                i++;
            }
        }
        return categories;
    }
}
