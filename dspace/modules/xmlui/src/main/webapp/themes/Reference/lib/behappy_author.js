function urlencode(str) {
	str = escape(str);
	str = str.replace('+', '%2B');
	str = str.replace('%20', '+');
	str = str.replace('*', '%2A');
	str = str.replace('/', '%2F');
	str = str.replace('@', '%40');
	str = str.replace('#', '%23');
	return str;
}

function call_author(json_data) {
	
	// Codificamos los parámetros para que no haya conflictos al pasarlos por URL
	json_data_enc = urlencode(json_data);
	
	// Pasamos el string de json a un objeto para poder hacer uso de los datos
    obj = JSON.parse(json_data);
	/*alert(obj.div_style);*/
	
	// Añadimos la hoja de estilos
	var fileref=document.createElement('link');
	fileref.setAttribute("rel", "stylesheet");
	fileref.setAttribute("type", "text/css");
	fileref.setAttribute("href", 'https://static.behappy.co/author/behappy_author.php?json_data=' + json_data_enc); 
	
	if (typeof fileref!="undefined") {
		document.getElementsByTagName("head")[0].appendChild(fileref);
	}
	
	// Añadimos el HTML
	document.write('<div class="author_copyright"> <a target="_black" href="http://behappy.co">by <img align="absbottom" longdesc="https://behappy.co" src="https://behappy.co/copyright/BeHappy-logo-small.png" alt="BeHappy Co."></a> </div>');
}