function logout(){
	var id = sessionStorage.getItem('Username');    
    var xhr;
    
    if(window.XMLHttpRequest) {
        xhr = new XMLHttpRequest();
    }else{
        xhr = new ActiveXObject("Microsoft.XMLHTTP");
    }
    
    xhr.onreadystatechange=function(){

    }
    
    xhr.open("POST","Server");
    xhr.setRequestHeader("MessageType", "Logout");
    xhr.send("UserName=" + id);
    sessionStorage.clear();
    window.location.href='Login.html';
} 