package org.xjy.classify;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;


/**
 * @author xjy
 * @version 0.1
 * 
 * */

public class Classify {


	/** 训练文档模型列表 */
	private List<DocModel> trainSetModel = new ArrayList<DocModel>();
	
	/** 类别列表 */
	private List<String> typeList = new ArrayList<String>(); 
	
	/** 训练集中最大url深度*/
	private double maxUrlDepth = 0;
	
	/** 训练集中最大句号个数 */
	private double maxMarkNum = 0;
	
	/** 训练集中最大行块长度 */
	private double maxLineBlockLen = 0;
	
	/** knn算法中取最近邻居的数目 */
	private static final int k = 15;
	
	/** 新闻网页阈值 */
	private static final double newsThreshold = 12.677165d;
	
	/** 博客网页阈值 */
	private static final double blogsThreshold = 12.684834d;
	
	/** 论坛网页阈值 */
	private static final double forumsThreshold = 12.915243d;
	
	public Classify(String modelLocation){
		init(modelLocation);
	}
	
	private void init(String modelLocation){
		File modelFile = new File(modelLocation);
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(modelFile));
			String tempString = null;
			while ((tempString = reader.readLine()) != null) {
				String type = tempString.substring(0, tempString.indexOf(":"));
				if(type.equals("maxUrlDepth")){
					maxUrlDepth = Double.valueOf(tempString.substring(tempString.indexOf(":")+1));
				} else if(type.equals("maxMarkNum")){
					maxMarkNum = Double.valueOf(tempString.substring(tempString.indexOf(":")+1));
				} else if(type.equals("maxLineBlockLen")){
					maxLineBlockLen = Double.valueOf(tempString.substring(tempString.indexOf(":")+1));
				} else {
					if(!typeList.contains(type)){
						typeList.add(type);
					}
					String[] vec = tempString.substring(tempString.indexOf("[")+1, tempString.lastIndexOf("]")).split(",");
					Vector<Double> featureVec = new Vector<Double>();
					for(String vecElement:vec){
						featureVec.add(Double.valueOf(vecElement.trim()));
					}
					trainSetModel.add(new DocModel(type, featureVec));
				}
			}
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public String doClassify(String url, String content){
		String classification = "";
		ExtractFeature extractFeature = new ExtractFeature();
		Vector<Double> featureVec = extractFeature.getFeature(url, content);
		Vector<Double> normalizedFeatureVec = Utility.normalizeVec(featureVec,maxUrlDepth,maxMarkNum,maxLineBlockLen);
		
//		System.out.println(normalizedFeatureVec.toString());
		
		Map<Integer,Double> similarityMap = new HashMap<Integer,Double>();	
		for(int i=0;i<trainSetModel.size();i++){
			Vector<Double> trainVec = trainSetModel.get(i).getFeatureVector();
			double similarity = Utility.calcutateSim(normalizedFeatureVec,trainVec);
			similarityMap.put(i, similarity);
		}
		Map.Entry[] entries = Utility.getSortedHashtableByValue(similarityMap);
		Map<DocModel,Double> kNN = new HashMap<DocModel,Double>();
		for(int i=entries.length-1; i>entries.length-1-k; i--){
			kNN.put(trainSetModel.get((Integer)entries[i].getKey()), (Double) entries[i].getValue());
		}
		Map<String, Double> finalType = new HashMap<String, Double>();
		for(String item : typeList){
			finalType.put(item, measure(kNN,item));
			
			System.out.println(measure(kNN,item));
			
		}

		Map.Entry<String, Double>[] sortedFinalType = Utility.getSortedHashtableByValue(finalType);
		classification = sortedFinalType[sortedFinalType.length-1].getKey();
		double typeValue = sortedFinalType[sortedFinalType.length-1].getValue();
		if(classification.equals("news")){
			classification = typeValue > newsThreshold ? "news" : "notsubject";
		} else if(classification.equals("blogs")){
			classification = typeValue > blogsThreshold ? "blogs" : "notsubject";
		} else {
			classification = typeValue > forumsThreshold ? "forums" : "notsubject";
		}
		
		return classification;
	}
	
	private double measure(Map<DocModel,Double> kNN, String type){
		double result = 0;
		for(Map.Entry<DocModel,Double> item : kNN.entrySet()){
			if(!item.getKey().getType().equals(type))
				continue;
			result += item.getValue();
		}
		return result;
	}
	
	public static void main(String[] args) {
		
		Long start = System.currentTimeMillis();
		
		File file = new File("E:\\testhtml.txt");
        BufferedReader reader = null;
        StringBuffer content = new StringBuffer();
        try {
            reader = new BufferedReader(new FileReader(file));
            String tempString = null;
            while ((tempString = reader.readLine()) != null) {
            	content.append(tempString+"\n");
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1) {
                }
            }
        }
       
		Classify classify = new Classify("E:\\model.txt");
		
		Long ioTime = System.currentTimeMillis();
		String type = classify.doClassify("http://blog.sina.com.cn/lm/auto/index.html",content.toString());
		System.out.println(type);
		Long end = System.currentTimeMillis();
		System.out.println("runtime:"+(end-start));
		System.out.println("iotime:"+(ioTime-start));
		System.exit(0);

	}

}

