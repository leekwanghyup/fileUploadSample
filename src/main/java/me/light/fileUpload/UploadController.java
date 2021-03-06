package me.light.fileUpload;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.FileNameMap;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import me.light.domain.AttachFileDTO;
import net.coobird.thumbnailator.Thumbnailator;

@Controller
public class UploadController {
	
	@GetMapping("/uploadForm")
	public void uploadForm() {
		System.out.println("uploadForm");		
	}
	
	@PostMapping("/uploadFormAction")
	public void uploadFormPost(MultipartFile[] uploadFile, Model model) {
		
		String uploadFolder = "C:\\upload"; 
		
		for(MultipartFile multipartFile : uploadFile) {
			System.out.println("---------------------------------");
			System.out.println("Upload File Name : " + multipartFile.getOriginalFilename());
			System.out.println("Upload File Size : " + multipartFile.getSize());
			
			File saveFile = new File(uploadFolder, multipartFile.getOriginalFilename());
			try {
				multipartFile.transferTo(saveFile);
			} catch (IllegalStateException e) {
				System.out.println(e.getMessage());
			} catch (IOException e) {
				System.out.println(e.getMessage());
			}  // try end 
		} // for end 
	}
	
	@GetMapping("/uploadAjax")
	public void uploadAjax() {
		System.out.println("upload Ajajx");
	}
	
	@PostMapping(value="/uploadAjaxAction", produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public ResponseEntity<List<AttachFileDTO>> uploadAjaxPost(MultipartFile[] uploadFile) {
		
		List<AttachFileDTO> list = new ArrayList<AttachFileDTO>(); 
		
		String uploadFolder = "C:\\upload";
		String uploadFolderPath = getFolder(); 
		File uploadPath = new File(uploadFolder, uploadFolderPath); 
		if(uploadPath.exists() == false) {
			uploadPath.mkdirs(); 
		}
		
		for(MultipartFile multipartFile : uploadFile) {
			
			AttachFileDTO attachDTO = new AttachFileDTO(); 
			
			System.out.println("---------------------------------");
			System.out.println("Upload File Name : " + multipartFile.getOriginalFilename());
			System.out.println("Upload File Size : " + multipartFile.getSize());

			String uploadFileName = multipartFile.getOriginalFilename();
			// IE file path 
			uploadFileName = uploadFileName.substring(uploadFileName.lastIndexOf("\\")+1); 
			attachDTO.setFileName(uploadFileName); 
			
			UUID uuid = UUID.randomUUID(); // UUID??? ???????????? file?????? ??????  
			uploadFileName = uuid.toString() + "_" + uploadFileName;

			File saveFile = new File(uploadPath, uploadFileName);
			
			try {
				multipartFile.transferTo(saveFile);
				attachDTO.setUuid(uuid.toString()); 
				attachDTO.setUploadPath(uploadFolderPath); 
				
				// ????????? ?????? ?????? ??? ????????? ?????? 
				if(checkImageType(saveFile)) {
					attachDTO.setImage(true); 
					File thumbImg = new File(uploadPath, "s_"+ uploadFileName); 
					FileOutputStream thumbnail = new FileOutputStream(thumbImg); 
					Thumbnailator.createThumbnail(multipartFile.getInputStream(), thumbnail, 100, 100); 
					thumbnail.close(); 
				}
				
				list.add(attachDTO); 
			} catch (IllegalStateException e) {
				System.out.println(e.getMessage());
			} catch (IOException e) {
				System.out.println(e.getMessage());
			}  // try end 
		} // for end 
		return new ResponseEntity<List<AttachFileDTO>>(list,HttpStatus.OK); 
	}
	
	@GetMapping("/display")
	@ResponseBody
	public ResponseEntity<byte[]> getFiles(String fileName){
		ResponseEntity<byte[]> result = null; 
		
		File file = new File("C:\\upload\\"+fileName); 
		FileNameMap fileNameMap = URLConnection.getFileNameMap();
		HttpHeaders header = new HttpHeaders(); 
		header.add("Content-Type", fileNameMap.getContentTypeFor(file.getName()));
		
		try {
			result = new ResponseEntity<byte[]>(FileCopyUtils.copyToByteArray(file),header,HttpStatus.OK);
		} catch (IOException e) {
			e.printStackTrace();
		} 
		return result; 
	}
	
	// ?????? ???????????? 
	@GetMapping(value = "/download", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
	@ResponseBody
	public ResponseEntity<Resource> downloadFiles(@RequestHeader("User-Agent") String userAgent, String fileName){
	
		Resource resource = new FileSystemResource("c:\\upload\\"+fileName);
		System.out.println("download file : " + fileName);
		System.out.println("resource : " + resource);
		
		 
		
		if(!resource.exists()) {
	        return new ResponseEntity<Resource>(HttpStatus.NOT_FOUND);
	    }
		HttpHeaders headers = new HttpHeaders(); // ???????????? ??? ???????????? ??????
		String downloadName = null; 
	    String resourceName = resource.getFilename();
	    		
		try {
	        if(userAgent.contains("Trident")) { // IE
	            downloadName = URLEncoder.encode(resourceName, "UTF-8").replace("\\+"," ");
	        } else if(userAgent.contains("Edge")) { //Edge
	            downloadName = URLEncoder.encode(resourceName, "UTF-8");
	        } else { //Chrome
	            downloadName = new String(resourceName.getBytes("UTF-8"),"ISO-8859-1");
	        }
			headers.add("Content-Disposition", "attachment; filename="+ downloadName); 
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} 
		return new ResponseEntity<Resource>(resource,headers,HttpStatus.OK); 
	}
	
	// ???????????? ?????? ?????? 
	@PostMapping("deleteFile")
	@ResponseBody
	 public ResponseEntity<String> deleteFile(String fileName, String type){
		 File file; 
		 try {
			file = new File("c:\\upload\\"+URLDecoder.decode(fileName,"UTF-8"));
			file.delete(); 
			if(type.equals("image")) { // ????????? ????????? ?????? 
				String largeFileName = file.getAbsolutePath().replace("s_","");
				file = new File(largeFileName); 
				file.delete(); 
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return new ResponseEntity<String>(HttpStatus.NOT_FOUND); 
		} 
		 return new ResponseEntity<String>("deleted", HttpStatus.OK); 
	 }
	
	private String getFolder() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date date = new Date(); 
		String str = sdf.format(date); 
		return str.replace("-", File.separator);
	}
	
	private boolean checkImageType(File file) {
		FileNameMap fileNameMap = URLConnection.getFileNameMap();
		String contentType = fileNameMap.getContentTypeFor(file.getName()); 
		return contentType.startsWith("image"); 
	}
}
