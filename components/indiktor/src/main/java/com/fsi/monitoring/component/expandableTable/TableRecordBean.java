package com.fsi.monitoring.component.expandableTable;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.Observable;

import javax.faces.event.ActionEvent;

import com.fsi.monitoring.util.StyleBean;


public abstract class TableRecordBean extends Observable 
implements Serializable {	
	
    /**
	 * 
	 */
	private static final long serialVersionUID = -7812957518989020025L;


	// list of child FilesRecords.
    protected ArrayList<TableRecordBean> childFilesRecords = null;

    public ArrayList<TableRecordBean> getChildFilesRecords() {
        return childFilesRecords;
    } 
    
    public TableRecordBean parent = null;
    
    
    // style for column that holds expand/contract image toggle, in the files
    // record row.
    protected String indentStyleClass = "";

    // style for all other columns in the files record row.
    protected String rowStyleClass = "";

    protected StyleBean styleBean;

    // Images used to represent expand/contract, spacer by default
    protected String expandImage;   // + or >
    protected String contractImage; // - or v
    protected String spacerImage;

    // callback to list which contains all data in the dataTable.  This callback
    // is needed so that a node can be set in the expanded state at construction time.
    protected ArrayList<TableRecordBean> tableData;

    // indicates if node is in expanded state.
    protected boolean isExpanded;

    /**
     * <p>Creates a new <code>FilesGroupRecordBean</code>.  This constructor
     * should be used when creating FilesGroupRecordBeans which will contain
     * children</p>
     *
     * @param isExpanded true, indicates that the specified node will be
     *                   expanded by default; otherwise, false.
     */
    public TableRecordBean(String indentStyleClass,
                           String rowStyleClass,
                           StyleBean styleBean,
                           String expandImage,
                           String contractImage,
                           ArrayList<TableRecordBean> tableData,
                           boolean isExpanded) {

        this.indentStyleClass = indentStyleClass;
        this.rowStyleClass = rowStyleClass;
        this.styleBean = styleBean;
        this.expandImage = expandImage;
        this.contractImage = contractImage;
        this.tableData = tableData;
        this.tableData.add(this);
        this.isExpanded = isExpanded;
        // update the default state of the node.
        if (this.isExpanded) {
            expandNodeAction();
        }
    }

    /**
     * <p>Creates a new <code>FilesGroupRecordBean</code>.  This constructor
     * should be used when creating a FilesGroupRecordBean which will be a child
     * of some other FilesGroupRecordBean.</p>
     * <p/>
     * <p>The created FilesGroupRecordBean has no image states defined.</p>
     *
     * @param indentStyleClass
     * @param rowStyleClass
     */
    public TableRecordBean(String indentStyleClass,
                           String rowStyleClass,
                           StyleBean styleBean,
                           String spacerImage) {

        this.indentStyleClass = indentStyleClass;
        this.rowStyleClass = rowStyleClass;
        this.styleBean = styleBean;
        this.spacerImage = spacerImage;
    }    

    public TableRecordBean() {
		super();
	}

	public TableRecordBean getParent() {
    	return parent;
    }
    
    public void setParent(TableRecordBean parent) {
    	this.parent = parent;
    }
    
    /**
     * Toggles the expanded state of this FilesGroup Record.
     *
     * @param event
     */
    public void toggleSubGroupAction(ActionEvent event) {
        // toggle expanded state
        isExpanded = !isExpanded;

        // add sub elements to list
        if (isExpanded) {
            expandNodeAction();
        }
        // remove items from list
        else {
            contractNodeAction();
        }
    }

    /**
     * Adds a child files record to this files group.
     *
     * @param filesGroupRecord child files record to add to this record.
     */
    public void addChildRecord(TableRecordBean childRecord) {
        if (childFilesRecords == null) {
        	childFilesRecords = new ArrayList<TableRecordBean>();
        }
    	
       childFilesRecords.add(childRecord);
       
       childRecord.setParent(this);
       
       if (isExpanded) {
    	   // to keep elements in order, remove all
           contractNodeAction();
           // then add them again.
           expandNodeAction();
       }
    }

    /**
     * Removes the specified child files record from this files group.
     *
     * @param filesGroupRecord child files record to remove.
     */
    public void removeChildFilesGroupRecord(TableRecordBean filesGroupRecord) {
        if (this.childFilesRecords != null && filesGroupRecord != null) {
            if (isExpanded) {
                // remove all, make sure we are removing the specified one too.
                contractNodeAction();
            }
            // remove the current node
            this.childFilesRecords.remove(filesGroupRecord);
            // update the list if needed.
            if (isExpanded) {
                // to keep elements in order, remove all
                contractNodeAction();
                // then add them again.
                expandNodeAction();
            }
        }
    }

    /**
     * Utility method to add all child nodes to the parent dataTable list.
     */
    protected void expandNodeAction() {
        if (childFilesRecords != null && childFilesRecords.size() > 0) {
            // get index of current node
            int index = tableData.indexOf(this);

            // add all items in childFilesRecords to the parent list
            tableData.addAll(index + 1, getSortedChildFilesRecords());
        }
    }
    
    protected abstract ArrayList<TableRecordBean> getSortedChildFilesRecords();

    /**
     * Utility method to remove all child nodes from the parent dataTable list.
     */
    public void contractNodeAction() {
        if (childFilesRecords != null && childFilesRecords.size() > 0) {
            // remove all items in childFilesRecords from the parent list
        	tableData.removeAll(childFilesRecords);
        }
    }

    /**
     * Gets the style class name used to define the first column of a files
     * record row.  This first column is where a expand/contract image is
     * placed.
     *
     * @return indent style class as defined in css file
     */
    public String getIndentStyleClass() {
        return indentStyleClass;
    }

    /**
     * Gets the style class name used to define all other columns in the files
     * record row, except the first column.
     *
     * @return style class as defined in css file
     */
    public String getRowStyleClass() {
        return rowStyleClass;
    }

    /**
     * Gets the image which will represent either the expanded or contracted
     * state of the <code>FilesGroupRecordBean</code>.
     *
     * @return name of image to draw
     */
    public String getExpandContractImage() {
    	String dir = styleBean.getImageDirectory();
    	
    	if (spacerImage != null) {
    		return dir + spacerImage;
    	}
    	if (styleBean != null) {
            String img = isExpanded ? contractImage : expandImage;
            return dir + img;
        }   

        return null;
    }

	public boolean isExpanded() {
		return isExpanded;
	}

		
    
}