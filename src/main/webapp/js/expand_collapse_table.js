function expandTableRow(row, table){
	expandSubRows(row, table);
	var icon = document.querySelector('#'+table+' tr[node="'+row+'"] .expandIcon')
	icon.className='collapseIcon';
	icon.setAttribute( 'onClick', 'collapseTableRow("'+row+'", "'+table+'");');
	
}

function expandSubRows(row, table){
	document.querySelectorAll('#'+table+' tr[parentRow="'+row+'"]').forEach(function(row) {
		row.style.display = 'table-row';
		if (document.querySelector('#'+table+' [node="'+row.getAttribute('node')+'"] td span.collapseIcon') != null){
			expandSubRows(row.getAttribute('node'), table);
		}
	});
}

function collapseTableRow(row, table){
	document.querySelectorAll('#'+table+' tr[parentRow="'+row+'"]').forEach(function(row) {
		row.style.display = 'none';
		collapseSubRows(row.getAttribute('node'), table);
	});
	var icon = document.querySelector('#'+table+' tr[node="'+row+'"] .collapseIcon')
	icon.className='expandIcon';
	icon.setAttribute( 'onClick', 'expandTableRow("'+row+'", "'+table+'");');
}

function collapseSubRows(row, table){
	document.querySelectorAll('#'+table+' tr[parentRow="'+row+'"]').forEach(function(row) {
			row.style.display = 'none';
			collapseSubRows(row.getAttribute('node'), table);
	});
}

function expandTable(table){
	document.querySelectorAll('#'+table+' tbody tr').forEach(function(row) {
		row.style.display = 'table-row';
		var icon = document.querySelector('#'+table+ ' [node="'+row.getAttribute('node')+'"] .expandIcon')
		if (icon != null){
		icon.className='collapseIcon';
		icon.setAttribute( 'onClick', 'collapseTableRow("'+row.getAttribute('node')+'", "'+table+'");');
		}
	});
}

function collapseTable(table){
	document.querySelectorAll('#'+table+' tbody tr[parentRow]').forEach(function(row) {
		row.style.display = 'none';
		console.log('#'+table+' [node="'+row.getAttribute('node')+'"] .collapseIcon')
		var icon = document.querySelector('#'+table+' [node="'+row.getAttribute('node')+'"] .collapseIcon')
		if (icon != null){
			icon.className='expandIcon';
			icon.setAttribute( 'onClick', 'expandTableRow("'+row.getAttribute('node')+'", "'+table+'");');
			}
	});
	document.querySelectorAll('#'+table+' tbody tr:not([parentRow])').forEach(function(row){
		var icon = document.querySelector('#'+table+' [node="'+row.getAttribute('node')+'"] .collapseIcon')
		if (icon != null){
			icon.className='expandIcon';
			icon.setAttribute( 'onClick', 'expandTableRow("'+row.getAttribute('node')+'", "'+table+'");');
			}
	});
}