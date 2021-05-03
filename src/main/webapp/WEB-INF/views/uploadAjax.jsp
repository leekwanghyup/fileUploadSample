<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Ajax 방식 파일업로드</title>
<script src="https://cdnjs.cloudflare.com/ajax/libs/jquery/3.6.0/jquery.min.js"></script>
</head>
<body>
<h1>Ajax 방식 파일업로드</h1>

<div class="uploadDiv">
	<input type="file" name="uploadFile" multiple="multiple"/>
	<button id="uploadBtn">Upload</button>
</div>	
<script>
$(document).ready(function(){
	$("#uploadBtn").on("click",function(e){
		var formData = new FormData(); 
		var inputFile = $("input[name='uploadFile']"); 
		var files = inputFile[0].files; 
		console.log(files);
		
		for(var i=0; i<files.length; i++){
			formData.append("uploadFile", files[i])
		}
		
		$.ajax({
			url: 'uploadAjaxAction', 
			processData : false, 
			contentType : false, 
			data : formData, 
			type : 'POST', 
			sucess : function(result){
				alert('Uploaded'); 
			}
		})
	}); 
}); 
</script>
</body>
</html>