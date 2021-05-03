package me.light.main;

import java.io.File;
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
	}
	
}
