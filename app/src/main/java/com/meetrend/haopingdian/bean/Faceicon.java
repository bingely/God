package com.meetrend.haopingdian.bean;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;

/**
 * 分两类表情，emoji 和 qq表情
 * @author tigereye
 *
 */
public class Faceicon  implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 资源id
     */
    private int iconResId;
    /**
     * 表情文字或者表情符号
     */
    private String value;
    /**
     * emoji字符
     */
    private String emoji;

    
    private Faceicon(int iconResId, String value) {
    	this.iconResId = iconResId;
    	this.value = value; 
    }
    
    private Faceicon(int codePoint) {
    	this.emoji = newString(codePoint); 
    }

    public static Faceicon fromResource(int iconResId, String value) { 
        try {
			return new Faceicon(iconResId, new String(value.getBytes("UTF-8")));
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
    }

    public static Faceicon fromCodePoint(int codePoint) {
        return new Faceicon(codePoint);
    }

    public Faceicon(String emoji) {
        this.emoji = emoji;
    }

    public String getValue() {
        return value;
    }

 
    public int getIcon() {
        return iconResId;
    }
    
    public String getValueOrEmoji() {
    	if (emoji == null)
    		return value;
    	else 
    		return emoji;
    }

    public static final String newString(int codePoint) {
        if (Character.charCount(codePoint) == 1) {
            return String.valueOf(codePoint);
        } else {
            return new String(Character.toChars(codePoint));
        }
    }
}

