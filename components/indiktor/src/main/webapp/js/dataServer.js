// ##################
// Switch tabs
// show is the id of the Tab & layer to show
// hide is the id of the Tab & layer to hide
function switchDSTab(show,hide) {
	document.getElementById('dataServerContentTab'+show).style.display='block';
	document.getElementById('tab'+show).className='selectedTab';
	document.getElementById('dataServerContentTab'+hide).style.display='none';
	document.getElementById('tab'+hide).className='unselectedTab';
}