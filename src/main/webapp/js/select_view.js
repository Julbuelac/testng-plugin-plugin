function toggleView(){
	showPkgView = !showPkgView;
	var pkgDisplay = 'none';
	var suiteDisplay = 'block';
	var buttonText = 'Switch to package view';
	
	
	if (showPkgView) {
		pkgDisplay = 'block';
		suiteDisplay = 'none';
		buttonText = 'Switch to suite view';
	}
	
	document.getElementById('toggleViewButton').innerHTML = buttonText;
		
	document.querySelectorAll('.pkgView').forEach(function(element){
		element.style.display = pkgDisplay;
	});
	
	document.querySelectorAll('.suiteView').forEach(function(element){
		element.style.display = suiteDisplay
	});
	
}
