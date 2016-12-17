// ##################
// Switch tabs
// show is the id of the Tab & layer to show
// totalTabs is the number of total tabs on this page
function switchTab(show,totalTabs) {
	for (i=1;i<=totalTabs;i++) {
		document.getElementById('configurationAlertContentTab'+i).style.display='none';
		document.getElementById('tab'+i).className='unselectedTab';
	}
	document.getElementById('configurationAlertContentTab'+show).style.display='block';
	document.getElementById('tab'+show).className='selectedTab';
}

function addCondition () {
	var newRow = document.getElementById('configurationAlertsConditions').insertRow(-1);
	var newCell = newRow.insertCell(0);
	var randId=Math.round(Math.random()*100);
	newCell.innerHTML = randId;
	newCell = newRow.insertCell(1);
	newCell.innerHTML = '<input type="text">';
	newCell = newRow.insertCell(2);
	newCell.innerHTML = '<input type="text">';
	newCell = newRow.insertCell(3);
	newCell.innerHTML = 'some.text.regarding.the.application';
	newCell = newRow.insertCell(4);
	newCell.innerHTML = '<input type="checkbox" checked>';
	newCell = newRow.insertCell(5);
	newCell.innerHTML = '&nbsp;';
}

function addAction() {
	var newRow = document.getElementById('configurationAlertsActions').insertRow(-1);
	var newCell = newRow.insertCell(0);
	var randId=Math.round(Math.random()*100);
	newCell.innerHTML = randId;
	newCell = newRow.insertCell(1);
	newCell.innerHTML = '<select><option>Mail</option><option>SMS</option></select>';
	newCell = newRow.insertCell(2);
	newCell.innerHTML = '<input type="checkbox" checked>';
	newCell = newRow.insertCell(3);
	newCell.innerHTML = '&nbsp;';
}

function showContent(page) {
	getPageContent= file('ajax/'+page+'.htm');
	document.getElementById('ajaxContent').innerHTML=getPageContent;
}

function showSelectorContent(page) {
	getPageContent= file('ajax/'+page+'.htm');
	document.getElementById('ajaxSelectorContent').innerHTML=getPageContent;
}