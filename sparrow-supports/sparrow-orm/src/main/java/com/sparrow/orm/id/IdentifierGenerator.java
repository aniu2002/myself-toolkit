package com.sparrow.orm.id;

import java.io.Serializable;

import com.sparrow.orm.jdbc.JdbcContext;


/**
 * IdentifierGenerator主键生成
 * 
 * @author YK (2013-10-14-下午2:54:47)
 */
public interface IdentifierGenerator {

	/**
	 * 如果 是auto为true,表明组件不会去初始化id,
	 * 
	 * @return boolean值
	 */
	public boolean isAuto();

	/**
	 * 额外信息 比如：sequence.hibernate_sequence <br/>
	 * extra 是 hibernate_sequence 这样 sequence generator 可以设置 sequence name
	 * 
	 * @param extra
	 *            额外信息
	 */
	public void setExtra(String extra);

	/**
	 * 如果 auto为true, 查看fillchar是否为空，不为空，那么在设置value的时候用该信息填充<br/>
	 * 比如：auto=true , getFillChar = 1000 <br/>
	 * 那么 insert into test(id,name) values(1000,:name);<br/>
	 * 如果：auto=true , getFillChar = null <br/>
	 * 否则 insert into test(id,name) values(:id,:name);
	 * 
	 * @return
	 */
	public String getFillChar();

	/**
	 * 当 auto 为 false，那么需要通过 该函数生成id
	 * 
	 * @param context
	 *            jdbc上下文
	 * @return 返回一个序列化对象
	 * @throws Exception
	 *             可能抛出jdbc异常
	 */
	public Serializable generate(JdbcContext context) throws Exception;

}
