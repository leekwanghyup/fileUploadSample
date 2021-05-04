package me.light.fileUpload;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.FileNameMap;
import java.net.URLConnection;
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
			
			UUID uuid = UUID.randomUUID(); // UUID를 이용하여 file이름 생성  
			uploadFileName = uuid.toString() + "_" + uploadFileName;

			File saveFile = new File(uploadPath, uploadFileName);
			
			try {
				multipartFile.transferTo(saveFile);
				attachDTO.setUuid(uuid.toString()); 
				attachDTO.setUploadPath(uploadFolderPath); 
				
				// 이미지 여부 확인 후 썸네일 생성 
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
	
	// 파일 다운로드 
	@GetMapping(value = "/download", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
	@ResponseBody
	public ResponseEntity<Resource> downloadFiles(String fileName){
	
		Resource resource = new FileSystemResource("c:\\upload\\"+fileName);
		System.out.println("download file : " + fileName);
		System.out.println("resource : " + resource);
		
		String resourceName = resource.getFilename(); 
		HttpHeaders headers = new HttpHeaders(); // 다운로드 시 파일이름 처리 
		try {
			headers.add("Content-Disposition", 
				"attachment; filename="+ new String(resourceName.getBytes("UTF-8"), "ISO-8859-1"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} 
		return new ResponseEntity<Resource>(resource,headers,HttpStatus.OK); 
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
