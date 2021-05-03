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
	
	var regex = new RegExp("(.*?)\.(exe|sh|zip|alz)$") // 확장자 
	var maxSize = 5 * 1024 * 1024 // 파일 크기 

	function checkExtension(fileName, fileSize){
	    if(fileSize > maxSize){ 
	        alert("파일 사이즈 초과")
	        return false; 
	    }
	    
	    if(regex.test(fileName)){
	        alert("해당 종류의 파일은 업로드할 수 없습니다.")
	        return false; 
	    }
	    return true; 
	}
	
	
	$("#uploadBtn").on("click",function(e){
		var formData = new FormData(); 
		var inputFile = $("input[name='uploadFile']"); 
		var files = inputFile[0].files; 
		console.log(files);
		
		for(var i=0; i<files.length; i++){
			if(!checkExtension(files[i].name, files[i].size)){
	            return false;
	        }
			formData.append("uploadFile", files[i])
		}
		
		$.ajax({
			url: 'uploadAjaxAction', 
			processData : false, 
			contentType : false, 
			data : formData, 
			type : 'POST',
			dataType: 'json', 
			sucess : function(result){
				console.log(result) 
			}
		})
	}); 
}); 
</script>
</body>
</html>