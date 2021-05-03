package me.light.main;

import java.io.File;
import java.net.FileNameMap;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainTest {
	private static String getFolder() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date date = new Date(); 
		String str = sdf.format(date); 
		return str.replace("-", File.separator); // 운영체제에 따른 구분자 변경 
	}
	
	public static void main(String[] args) {
		System.out.println(getFolder());
		System.out.println();
		
		String uploadFolder = "C:\\upload"; 
		File uploadPath = new File(uploadFolder, getFolder());
		System.out.println(uploadPath); // 당일 날짜로 폴더 생성 C:\\upload\\2021\\05\\03
		
		// 해당 파일이 이미지 파일인지 여부 
		File file = new File("C:\\Users\\Public\\Pictures\\Sample Pictures\\국화.jpg"); 
		FileNameMap fileNameMap = URLConnection.getFileNameMap();
		String contentType = fileNameMap.getContentTypeFor(file.getName());
		System.out.println(contentType);
		System.out.println(contentType.startsWith(contentType));
	}
	
}
