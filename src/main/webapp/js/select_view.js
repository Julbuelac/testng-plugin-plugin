function toggleView(){
	showPkgView = !showPkgView;
	pkgDisplay = 'none';
	suiteDisplay = 'block';
	if (showPkgView) {
		pkgDisplay = 'block';
		suiteDisplay = 'none';
	}
		
	document.querySelectorAll('.pkgView').forEach(function(element){
		element.style.display = pkgDisplay;
	});
	
	document.querySelectorAll('.suiteView').forEach(function(element){
		element.style.display = suiteDisplay
	});
	
}
