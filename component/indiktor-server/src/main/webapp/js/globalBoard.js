function dockIt(id) {
	if (document.getElementById(id).style.display!='none') {
		document.getElementById(id).style.display='none';
		document.getElementById(id+'Dock').style.backgroundPosition='5px 5px';
	} else {
		document.getElementById(id).style.display='block';
		document.getElementById(id+'Dock').style.backgroundPosition='5px -10px';
	}
}