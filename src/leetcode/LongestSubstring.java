package leetcode;

import org.apache.commons.lang3.StringUtils;


/** 
 * Given a string, find the longest substring that contains
 *  only two unique characters. For example, given "abcbbbbcccbdddadacb", 
 *  the longest substring that contains 2 unique character is "bcbbbbcccb".
 */
public class LongestSubstring {
	public static void main(String[] args) {
		System.out.println(resolution2("abcdd"));
	}
	private static String  resolution(String str){
		if(StringUtils.isEmpty(str))return str;
		int first =0;int second =0;int max=0;int endIndex=0;
		char temp='-';
		for(int i=0;i<str.length();i++){
			if(temp == str.charAt(i)){
				second++;
			}else{
				temp =str.charAt(i);
				if(first !=0 && max<first+second) {
					max=first+second;
					endIndex =i;
				}
				first =second;
				second=1;
			}			
		}
		if(first !=0 && max<first+second){
			max=first+second;
			endIndex = str.length();
		}
		return str.substring(endIndex-max,endIndex);
	}
	
	private static String resolution2(String str){
		if(StringUtils.isEmpty(str))return str;
		int first=0;int second=0;char firstChar= str.charAt(0) ; char secondChar = str.charAt(0);
		int max=0;int endIndex=0;
		for(int i=0;i<str.length();i++){
			char temp = str.charAt(i);
			if(secondChar  == temp){
				second++;
			}else if(firstChar == temp){
				first++;
			}else{
				firstChar = secondChar;
				secondChar = temp;
				if(first !=0 && max<first+second){
					max=first+second;
					endIndex = i;
				}
				first=second;
				second=1;
				
			}
		}
		if(first !=0 && max<first+second){
			max=first+second;
			endIndex = str.length();
		}
		return str.substring(endIndex-max,endIndex);
	}
}
