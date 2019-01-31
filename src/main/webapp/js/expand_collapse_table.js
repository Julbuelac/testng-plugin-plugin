function expandTableRow(row){
	expandSubRows(row);
	var icon = document.querySelector('#'+row+ ' .expandIcon')
	icon.className='collapseIcon';
	icon.setAttribute( 'onClick', 'collapseTableRow("'+row+'");');
	
}

function expandSubRows(row){
	document.querySelectorAll('[parentRow='+row+']').forEach(function(row) {
		row.style.display = 'table-row';
		if (document.querySelector('#'+row.id+' td span.collapseIcon') != null){
			expandSubRows(row.id);
		}
	});
}

function collapseTableRow(row){
	document.querySelectorAll('[parentRow='+row+']').forEach(function(row) {
		row.style.display = 'none';
		collapseSubRows(row.id);
	});
	var icon = document.querySelector('#'+row+ ' .collapseIcon')
	icon.className='expandIcon';
	icon.setAttribute( 'onClick', 'expandTableRow("'+row+'");');
}

function collapseSubRows(row){
	document.querySelectorAll('[parentRow='+row+']').forEach(function(row) {
			row.style.display = 'none';
			collapseSubRows(row.id);
	});
}

function expandTable(table){
	document.querySelectorAll('#'+table+' tbody tr').forEach(function(row) {
		row.style.display = 'table-row';
		var icon = document.querySelector('#'+row.id + ' .expandIcon')
		if (icon != null){
		icon.className='collapseIcon';
		icon.setAttribute( 'onClick', 'collapseTableRow("'+row.id+'");');
		}
	});
}

function collapseTable(table){
	document.querySelectorAll('#'+table+' tbody tr[parentRow]').forEach(function(row) {
		row.style.display = 'none';
		var icon = document.querySelector('#'+row.id + ' .collapseIcon')
		if (icon != null){
			icon.className='expandIcon';
			icon.setAttribute( 'onClick', 'expandTableRow("'+row.id+'");');
			}
	});
	document.querySelectorAll('#'+table+' tbody tr:not([parentRow])').forEach(function(row){
		var icon = document.querySelector('#'+row.id + ' .collapseIcon')
		if (icon != null){
			icon.className='expandIcon';
			icon.setAttribute( 'onClick', 'expandTableRow("'+row.id+'");');
			}
	});
}