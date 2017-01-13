// ##################
// Display/hide specific rows.
// sourceRow is the id of the row click
// totalSubItems is the number of rows to show
function switchTableRow(sourceRow,totalSubItems) {
	for (i=0;i<totalSubItems;i++) {
		if (document.getElementById(sourceRow+'Sub'+i).style.display!='table-row') {
			document.getElementById(sourceRow+'Sub'+i).style.display='table-row';
		} else {
			document.getElementById(sourceRow+'Sub'+i).style.display='none';
		}
	}
	if (document.getElementById(sourceRow).className=='default') {
		document.getElementById(sourceRow).className='bold';
	} else {
		document.getElementById(sourceRow).className='default';
	}
}

// ##################
// Display/hide specific layer.
// layerId is the id of the layer to show / hide
function switchAlertLayer(layerId) {
	if (document.getElementById(layerId).style.display!='block') {
		document.getElementById(layerId).style.display='block';
	} else {
		document.getElementById(layerId).style.display='none';
	}
}