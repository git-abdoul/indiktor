package com.fsi.monitoring.component.table;

import java.util.Comparator;

import com.fsi.monitoring.util.AccessControlBean;


public abstract class SortableList extends AccessControlBean {

    protected String sortColumnName;
    protected boolean ascending;

    
    protected String oldSort;
    protected boolean oldAscending;

	protected Comparator comparator;

    protected SortableList(String defaultSortColumn) {
        sortColumnName = defaultSortColumn;
        ascending = isDefaultAscending(defaultSortColumn);
        oldSort = sortColumnName;
        // make sure sortColumnName on first render
        oldAscending = !ascending;
    }

    /**
     * Sort the list.
     */
//    protected abstract void sort();

    /**
     * Is the default sortColumnName direction for the given column "ascending" ?
     */
    protected abstract boolean isDefaultAscending(String sortColumn);
    protected abstract void sort();
    

    /**
     * Gets the sortColumnName column.
     *
     * @return column to sortColumnName
     */
    public String getSortColumnName() {
        return sortColumnName;
    }

    /**
     * Sets the sortColumnName column
     *
     * @param sortColumnName column to sortColumnName
     */
    public void setSortColumnName(String sortColumnName) {
        oldSort = this.sortColumnName;
        this.sortColumnName = sortColumnName;
    }

    /**
     * Is the sortColumnName ascending.
     *
     * @return true if the ascending sortColumnName otherwise false.
     */
    public boolean isAscending() {
        return ascending;
    }

    /**
     * Set sortColumnName type.
     *
     * @param ascending true for ascending sortColumnName, false for desending sortColumnName.
     */
    public void setAscending(boolean ascending) {
        oldAscending = this.ascending;
        this.ascending = ascending;
        sort();
    }
}
