package org.xjy.classify;


import java.util.Vector;

/**
 * @author xjy
 * @version 0.5
 * */

public class DocModel {


	/** ��ҳ���� */
	private String type = "";
	
	/** ��ҳ���������� */
	private Vector<Double> featureVector = new Vector<Double>();
	
	public DocModel(String type, Vector<Double> featureVector){
		this.type = type;
		this.featureVector = featureVector;
	}
	
	public DocModel(){
		
	}
	
	public String getType() {
		return type;
	}



	public void setType(String type) {
		this.type = type;
	}



	public Vector<Double> getFeatureVector() {
		return featureVector;
	}



	public void setFeatureVector(Vector<Double> featureVector) {
		this.featureVector = featureVector;
	}


}
