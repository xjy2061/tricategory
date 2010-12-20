package org.xjy.classify;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;


/**
 * @author xjy
 * @version 0.1
 * */

public class ExtractFeature {


	/** url��� */
	private int urlDepth = -1;
	
	/** ���ź;�Ÿ��� */
	private int markNum = 0;
	
	/** url���Ƿ����blog */
	private int isBlog = 0;
	
	/** url���Ƿ����bbs��forum��club */
	private int isBBS = 0;
	
	/** ����п鳤�� */
	private int maxLineBlockLength = 0;
	
	/** �п�������� */
	private static final int _block = 3;
	
	
	public Vector<Double> getFeature(String url, String content) {
		Vector<Double> vec = new Vector<Double>();
		if (url.toLowerCase().contains("blog")) {
			isBlog = 1;
		}
		if (url.toLowerCase().contains("bbs")
				|| url.toLowerCase().contains("forum")||url.toLowerCase().contains("club")) {
			isBBS = 1;
		}
		if (url.charAt(url.length() - 1) == '/') {
			url = url.substring(0, url.length() - 1);
		}
		for (int i = 0; i < url.length()-1; i++) {
			if (url.charAt(i) == '/')
				urlDepth++;
		}
		content = preProcess(content);
		for (int i = 0; i < content.length(); i++) {
			if (content.charAt(i) == '��')
				markNum++;
		}
		maxLineBlockLength = getMaxLineBlockLength(content);
		vec.add((double)urlDepth);
		vec.add((double)markNum);
		vec.add((double)maxLineBlockLength);
		vec.add((double)isBlog);
		vec.add((double)isBBS);
		
		System.out.println(vec.toString());
		
		return vec;
	}

	private int getMaxLineBlockLength(String content) {
		int length = 0;
		List<String> lines = Arrays.asList(content.split("\n"));
		List<Integer> indexDistribution = lineBlockDistribute(lines);

		List<String> textList = new ArrayList<String>();
		List<Integer> textBeginList = new ArrayList<Integer>();
		List<Integer> textEndList = new ArrayList<Integer>();

		for (int i = 0; i < indexDistribution.size(); i++) {
			if (indexDistribution.get(i) > 0) {
				StringBuilder tmp = new StringBuilder();
				textBeginList.add(i);
				while (i < indexDistribution.size()
						&& indexDistribution.get(i) > 0) {
					tmp.append(lines.get(i)).append("\n");
					i++;
				}
				textEndList.add(i);
				textList.add(tmp.toString());
			}
		}

		// �������ֻ���������У���������������־��϶࣬����п�ϲ������ֲ�������ȡ�����ȱ��
		for (int i = 1; i < textList.size(); i++) {
			if (textBeginList.get(i) == textEndList.get(i - 1) + 1
					&& textEndList.get(i) > textBeginList.get(i) + _block
					&& textList.get(i).replaceAll("\\s+", "").length() > 40) {
				if (textEndList.get(i - 1) == textBeginList.get(i - 1) + _block
						&& textList.get(i - 1).replaceAll("\\s+", "").length() < 40) {
					continue;
				}
				textList.set(i - 1, textList.get(i - 1) + textList.get(i));
				textEndList.set(i - 1, textEndList.get(i));

				textList.remove(i);
				textBeginList.remove(i);
				textEndList.remove(i);
				--i;
			}
		}

		String result = "";
		for (String text : textList) {
			// System.out.println("text:" + text + "\n" +
			// text.replaceAll("\\s+", "").length());
			if (text.replaceAll("\\s+", "").length() > result.replaceAll(
					"\\s+", "").length())
				result = text;
		}
		length = result.replaceAll("\\s+", "").length();

		return length;

	}

	/**
	 * Pre processing.
	 * 
	 * @param htmlText
	 *            the html text
	 * 
	 * @return the string
	 */
	private String preProcess(String htmlText) {
		// DTD
		htmlText = htmlText.replaceAll("(?is)<!DOCTYPE.*?>", "");
		// html comment
		htmlText = htmlText.replaceAll("(?is)<!--.*?-->", "");
		// js
		htmlText = htmlText.replaceAll("(?is)<script.*?>.*?</script>", "");
		// css
		htmlText = htmlText.replaceAll("(?is)<style.*?>.*?</style>", "");
		//anchor text
		htmlText = htmlText.replaceAll("(?is)<a.*?>.*?</a>", "");
		// html
		htmlText = htmlText.replaceAll("(?is)<.*?>", "");

		return replaceSpecialChar(htmlText);
	}

	/**
	 * Replace special char.
	 * 
	 * @param content
	 *            the content
	 * 
	 * @return the string
	 */
	private String replaceSpecialChar(String content) {
		String text = content.replaceAll("&quot;", "\"");
		text = text.replaceAll("&ldquo;", "��");
		text = text.replaceAll("&rdquo;", "��");
		text = text.replaceAll("&middot;", "��");
		text = text.replaceAll("&#8231;", "��");
		text = text.replaceAll("&#8212;", "����");
		text = text.replaceAll("&#28635;", "��");
		text = text.replaceAll("&hellip;", "��");
		text = text.replaceAll("&#23301;", "��");
		text = text.replaceAll("&#27043;", "�l");
		text = text.replaceAll("&#8226;", "��");
		text = text.replaceAll("&#40;", "(");
		text = text.replaceAll("&#41;", ")");
		text = text.replaceAll("&#183;", "��");
		text = text.replaceAll("&amp;", "&");
		text = text.replaceAll("&bull;", "��");
		text = text.replaceAll("&lt;", "<");
		text = text.replaceAll("&#60;", "<");
		text = text.replaceAll("&gt;", ">");
		text = text.replaceAll("&#62;", ">");
		text = text.replaceAll("&nbsp;", " ");
		text = text.replaceAll("&#160;", " ");
		text = text.replaceAll("&tilde;", "~");
		text = text.replaceAll("&mdash;", "��");
		text = text.replaceAll("&copy;", "@");
		text = text.replaceAll("&#169;", "@");
		text = text.replaceAll("��", "");
		text = text.replaceAll("\r\n|\r", "\n");

		return text;
	}

	/**
	 * Line block distribute.
	 * 
	 * @param lines
	 *            the lines
	 * 
	 * @return the list< integer>
	 */
	private List<Integer> lineBlockDistribute(List<String> lines) {
		List<Integer> indexDistribution = new ArrayList<Integer>();

		for (int i = 0; i < lines.size(); i++) {
			indexDistribution.add(lines.get(i).replaceAll("\\s+", "").length());
		}
		// ɾ�����´����������е�������
		for (int i = 0; i + 4 < lines.size(); i++) {
			if (indexDistribution.get(i) == 0
					&& indexDistribution.get(i + 1) == 0
					&& indexDistribution.get(i + 2) > 0
					&& indexDistribution.get(i + 2) < 40
					&& indexDistribution.get(i + 3) == 0
					&& indexDistribution.get(i + 4) == 0) {
				// System.out.println("line:" + lines.get(i+2));
				lines.set(i + 2, "");
				indexDistribution.set(i + 2, 0);
				i += 3;
			}
		}

		for (int i = 0; i < lines.size() - _block; i++) {
			int wordsNum = indexDistribution.get(i);
			for (int j = i + 1; j < i + _block && j < lines.size(); j++) {
				wordsNum += indexDistribution.get(j);
			}
			indexDistribution.set(i, wordsNum);
		}

		return indexDistribution;
	}
	
	public static void main(String[] args) {
				

	}

}

