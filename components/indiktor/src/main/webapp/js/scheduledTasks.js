// ##################
// Switch tabs
// show is the id of the Tab & layer to show
// totalTabs is the number of total tabs on this page
function switchTab(show,totalTabs) {
	for (i=1;i<=totalTabs;i++) {
		document.getElementById('scheduledTasksContentTab'+i).style.display='none';
		document.getElementById('tab'+i).className='unselectedTab';
	}
	document.getElementById('scheduledTasksContentTab'+show).style.display='block';
	document.getElementById('tab'+show).className='selectedTab';
}