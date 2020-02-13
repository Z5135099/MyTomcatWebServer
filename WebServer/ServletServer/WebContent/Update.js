function normalUpdate(){
	var id = sessionStorage.getItem('Username');    
    var token = sessionStorage.getItem('Token');
    var xhr;
    
    if(window.XMLHttpRequest) {
        xhr = new XMLHttpRequest();
    }else{
        xhr = new ActiveXObject("Microsoft.XMLHTTP");
    }
    
    xhr.onreadystatechange=function(){
		if(xhr.readyState==4){
			if(xhr.status==200){
				var respond = unescape(xhr.responseText);
				
				if(respond === "Wrong token"){
					window.location.href='Login.html';
				}else{
					var jsonObj=JSON.parse(respond.replace(/\n/g,"\\n").replace(/\r/g,"\\r"));
					console.log(jsonObj);
				}
			}
		}
	}
    
    //xhr.open("GET","Server");
    xhr.open("GET","Server")
    xhr.setRequestHeader("MessageType", "NormalUpdate");
    xhr.setRequestHeader("UserName", id);
    xhr.setRequestHeader("Token", token);
    xhr.send(null);
}

function personalUpdate(){
	var id = sessionStorage.getItem('Username');    
    var token = sessionStorage.getItem('Token');
    var xhr;
    
    if(window.XMLHttpRequest) {
        xhr = new XMLHttpRequest();
    }else{
        xhr = new ActiveXObject("Microsoft.XMLHTTP");
    }
    
    xhr.onreadystatechange=function(){
		if(xhr.readyState==4){
			if(xhr.status==200){
				var respond = unescape(xhr.responseText);
				
				if(respond === "Wrong token"){
					window.location.href='Login.html';
				}else{
					var jsonObj=JSON.parse(respond.replace(/\n/g,"\\n").replace(/\r/g,"\\r"));
					console.log(jsonObj);
					
					//var jsonStr = JSON.parse(respond);
					//console.log(respond);
				}
			}
		}
	}
    
    //xhr.open("GET","Server");
    xhr.open("GET","Server")
    xhr.setRequestHeader("MessageType", "PersonalUpdate");
    xhr.setRequestHeader("UserName", id);
    xhr.setRequestHeader("Token", token);
    xhr.send(null);
	
}