package org.xjy.testclassifier;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.xjy.classify.Classify;


/**
 * @author xjy
 * @version 0.5
 * 
 * */

public class Evaluate {
	
	public static double calculateF1(Map<String,String> original, Map<String,String> classfyResult, String type){
		double result = 0d;
		double typeNum = 0;
		double classfyNum = 0;
		double classfyRight = 0;
		for(String key : original.keySet()){
			if(original.get(key).equals(type))
				typeNum++;
			if(classfyResult.get(key).equals(type))
				classfyNum++;
			if(original.get(key).equals(type)&&classfyResult.get(key).equals(type))
				classfyRight++;
		}
		
		double precision = classfyRight/classfyNum;
		double recall = classfyRight/typeNum;
		
		System.out.println("precision:"+precision);
		System.out.println("recall:"+recall);
		
		result = 2*precision*recall/(precision+recall);
		return result;

	}
	
	public static void main(String[] args) {
		File testSetDir = new File("E:\\testSet");
		File[] typeFiles = testSetDir.listFiles();
		Map<String, String> original = new HashMap<String, String>();			
		Map<String, String> classfyResult = new HashMap<String, String>();
		Classify classify = new Classify("E:\\model.txt");
		BufferedReader reader = null;
		try{
		for (int i = 0; i < typeFiles.length; i++) {
			File path = typeFiles[i];
			String type = path.getName();
			File[] htmlList = path.listFiles();
			for (File testFile : htmlList) {
				File html = testFile;
				reader = new BufferedReader(new FileReader(html));
				String tempString = null;
				int line = 0;
				String url = "";
				StringBuffer content = new StringBuffer("");
				while ((tempString = reader.readLine()) != null) {
					line++;
					if (line == 1) {
						url = tempString;
					} else {
						content.append(tempString + "\n");
					}
				}
				reader.close();
				original.put(url, type);
				String classification = classify.doClassify(url,content.toString());
				classfyResult.put(url, classification);
			}
		}
		} catch(IOException e){
			e.printStackTrace();
		} finally {
			if(reader != null){
				try {
					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		double f1 = calculateF1(original,classfyResult,"notsubject");
		System.out.println("F1:"+f1);

	}

}
