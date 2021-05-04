<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Ajax 방식 파일업로드</title>
<script src="https://cdnjs.cloudflare.com/ajax/libs/jquery/3.6.0/jquery.min.js"></script>
<style type="text/css">
.uploadResult {
	width: 100%; 
	background-color: gray;  
}

.uploadResult ul {
	display: flex; 
	flex-flow: row; 
	justify-content: center;
	align-items: center;
}

.uploadResult .list {
	list-style: none; 
	padding: 10px; 
	align-content: center; 
	text-alighn: center; 
}

.uploadResult ul li img{
	width: 100px; 
}

.bigPictureWrapper {
	position: absolute; 
	display: none; 
	justify-content: center; 
	align-items: center; 
	top:0; 
	width: 100%; height:100%; 
	background-color : gray;
	z-index: 100; 
	background: rgba(255,255,255,0.5); 
}
.bigPicture{
	position: relative; 
	display : flex; 
	justify-content: center; 
	align-items: center;  
}

.bigPicture img {
	width: 600px; 
}


</style>
</head>
<body>
<h1>Ajax 방식 파일업로드</h1>

<div class="uploadDiv">
	<input type="file" name="uploadFile" multiple="multiple"/>
	<button id="uploadBtn">Upload</button>
</div>	
<div class="uploadResult">
	<ul></ul>
</div>

<!-- 원본 이미지를 보여주는 영역  -->
<div class="bigPictureWrapper">
	<div class="bigPicture"></div>
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
	
	var cloneObj = $(".uploadDiv").clone(); //초기화를 위해 복사
	
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
			url: '/uploadAjaxAction', 
			processData : false, 
			contentType : false, 
			data : formData, 
			type : 'POST',
			dataType: 'json', 
			success : function(result){
				showUploadedFile(result);  
				$(".uploadDiv").html(cloneObj.html());  
			}
		}); 
	}); 
	
	var uploadResult = $(".uploadResult ul"); 
	function showUploadedFile(uploadResultArr){
		var str = ""; 
		$(uploadResultArr).each(function(i,obj){ // 여기서 ojb는 AttachDTO객체이다. 
			console.log(obj); 
			if(!obj.image){
				var fileCallPath = encodeURIComponent(obj.uploadPath + "/" + obj.uuid + "_"+obj.fileName); // 다운로드 경로 
				str += "<li class='list'><a href='/download?fileName="+fileCallPath+"'>";  // a 태그 추가 
				str += "<li class='list'><img class='images' src='/resources/img/attach.png'>"+obj.fileName+"</li>";
			} else{	
				var fileCallPath = encodeURIComponent(obj.uploadPath + "/s_" + obj.uuid + "_" + obj.fileName);
				
				// 클릭시 썸네일 원본 이미지 표시 
				var originPath = obj.uploadPath + "\\" + obj.uuid + "_" + obj.fileName;  
				originPath = originPath.replace(new RegExp(/\\/g), "/"); 
				str += "<li class=\"list\"><a href=\"javascript:showImage(\'"+ originPath +"\')\">"
	            str += "<img src='/display?fileName="+ fileCallPath  +"'></a></li>";
			} 
		}); 
		uploadResult.html(str);   
	}
	
	// 원본 이미지 클릭시 사라짐 
	$(".bigPictureWrapper").on("click", function(e){
		$(".bigPicture").animate({width:'0%', height: '0%'}, 1000); 
		setTimeout(()=> {
			$(this).hide(); 
		},100); 
	}); 
}); 

function showImage(fileCallPath){
	$(".bigPictureWrapper").css("display","flex").show();
	$(".bigPicture").html("<img src='/display?fileName=" + encodeURI(fileCallPath)+"'>")
					.animate({width: '100%', height: '100%'}, 1000); 
}
</script>

</body>
</html>