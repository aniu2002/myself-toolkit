package com.sparrow.app.information.domain;


/**
 *	表模型: "" <br/>
 *  ============================== <br/>
 *	选择: SELECT * FROM gif_info WHERE id=? <br/>
 *	统计: SELECT COUNT(1) FROM gif_info <br/>
 *	插入: INSERT INTO gif_info(id,alias,icons,gif_url,gif_desc) VALUES(:id,:alias,:icons,:gifUrl,:gifDesc) <br/>
 *	更新: UPDATE gif_info SET alias=:alias,icons=:icons,gif_url=:gifUrl,gif_desc=:gifDesc WHERE id=:id <br/>
 *	删除: DELETE FROM gif_info WHERE id=? <br/>
 *  ============================== <br/>
 * @author YZC
 */
public class GifInfo {
	/** (id) */
	private Long id;
	/** (alias) */
	private String alias;
	/** (icons) */
	private String icons;
	/** (gif_url) */
	private String gifUrl;
	/** (gif_desc) */
	private String gifDesc;

	/**
	 * 
	 * 获取值    
	 *  
	 * @return (Long)
	 */
	public Long getId() {
		return id;
	}
	
	/**
	 * 
	 * 设置值   
	 *  
	 * @param id 
	 *        
	 */
	public void setId(Long id) {
		this.id = id;
	}
	/**
	 * 
	 * 获取值    
	 *  
	 * @return (String)
	 */
	public String getAlias() {
		return alias;
	}
	
	/**
	 * 
	 * 设置值   
	 *  
	 * @param alias 
	 *        
	 */
	public void setAlias(String alias) {
		this.alias = alias;
	}
	/**
	 * 
	 * 获取值    
	 *  
	 * @return (String)
	 */
	public String getIcons() {
		return icons;
	}
	
	/**
	 * 
	 * 设置值   
	 *  
	 * @param icons 
	 *        
	 */
	public void setIcons(String icons) {
		this.icons = icons;
	}
	/**
	 * 
	 * 获取值    
	 *  
	 * @return (String)
	 */
	public String getGifUrl() {
		return gifUrl;
	}
	
	/**
	 * 
	 * 设置值   
	 *  
	 * @param gifUrl 
	 *        
	 */
	public void setGifUrl(String gifUrl) {
		this.gifUrl = gifUrl;
	}
	/**
	 * 
	 * 获取值    
	 *  
	 * @return (String)
	 */
	public String getGifDesc() {
		return gifDesc;
	}
	
	/**
	 * 
	 * 设置值   
	 *  
	 * @param gifDesc 
	 *        
	 */
	public void setGifDesc(String gifDesc) {
		this.gifDesc = gifDesc;
	}
}