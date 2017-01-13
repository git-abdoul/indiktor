function getElementsByClassName(tag_,class_){
	var i, k;
	var T_Result = new Array(); // tableau des Objets en retour
	//-- Recup le tableau d'objets correspondant au tag
	var O_Tab = document.getElementsByTagName( tag_);
	// pour chaque classe on test si l'objet est du même type de classe...
	for ( var t = 0; t < class_.length; t++){
		for( i=0, k=0; i < O_Tab.length; i++){
			//-- Pour chacun on test la class
			if( O_Tab[i].className == class_[t]){
				 T_Result.push(O_Tab[i]); // stock l'objet
			}
		}
	}
	return( T_Result); // on retourne le tableau d'objet
}
function expandTreeView(tag_,class_) {
	var Obj = getElementsByClassName(tag_,class_);
	for( i=0; i < Obj.length; i++){
		Obj[i].style.display = "block";
		Obj[i].parentNode.style.backgroundImage="url('./i/treeview/minus.gif');";
	}
}
function collapseTreeView(tag_,class_) {
	var Obj = getElementsByClassName(tag_,class_);
	for( i=0; i < Obj.length; i++){
		Obj[i].style.display = "none";
		Obj[i].parentNode.style.backgroundImage="url('./i/treeview/plus.gif');";
	}
}
function switchTreeviewSub(id) {
	clearOrangeLi();
	clearGreenLi();
	clearGrayLi();
	clearRedLi();
	if (document.getElementById(id).style.display!='block') {
		document.getElementById(id).style.display='block';
		document.getElementById(id+'ico').style.backgroundImage="url('./i/treeview/minus.gif');";
	} else {
		document.getElementById(id).style.display='none';
		document.getElementById(id+'ico').style.backgroundImage="url('./i/treeview/plus.gif');";
	}
	document.getElementById(id).parentNode.style.backgroundColor="#006e9c";
}
function showContent(page,id) {
	getPageContent= file('ajax/'+page+'.htm');
	document.getElementById('ajaxContent').innerHTML=getPageContent;
	clearOrangeLi();
	clearGreenLi();
	clearGrayLi();
	clearRedLi();
	document.getElementById(id).style.backgroundColor="#006e9c";
}
function clearOrangeLi() {
	var Obj = getElementsByClassName('li',['orange','orange nomore']);
	for( i=0; i < Obj.length; i++){
		Obj[i].style.backgroundColor = "#f39402";
	}
	return true;
}
function clearGreenLi() {
	var Obj = getElementsByClassName('li',['green','green nomore']);
	for( i=0; i < Obj.length; i++){
		Obj[i].style.backgroundColor = "#58ab27";
	}
	return true;
}
function clearGrayLi() {
	var Obj = getElementsByClassName('li',['gray','gray nomore']);
	for( i=0; i < Obj.length; i++){
		Obj[i].style.backgroundColor = "#9c9d9f";
	}
	return true;
}
function clearRedLi() {
	var Obj = getElementsByClassName('li',['red','red nomore']);
	for( i=0; i < Obj.length; i++){
		Obj[i].style.backgroundColor = "#e2001a";
	}
	return true;
}