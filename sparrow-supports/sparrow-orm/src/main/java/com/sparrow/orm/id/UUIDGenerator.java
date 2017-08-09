package com.sparrow.orm.id;

import java.io.Serializable;

import com.sparrow.orm.exceptions.IdentifierGenerationException;
import com.sparrow.orm.jdbc.JdbcContext;


/**
 * @author YK
 */
public class UUIDGenerator extends AbstractUUIDGenerator implements
		IdentifierGenerator {
	/**
	 * int格式化为string
	 * 
	 * @param intval
	 * @return
	 */
	protected String format(int intval) {
		String formatted = Integer.toHexString(intval);
		StringBuffer buf = new StringBuffer("00000000");
		buf.replace(8 - formatted.length(), 8, formatted);
		return buf.toString();
	}

	/**
	 * short格式化为string
	 * 
	 * @param shortval
	 * @return
	 */
	protected String format(short shortval) {
		String formatted = Integer.toHexString(shortval);
		StringBuffer buf = new StringBuffer("0000");
		buf.replace(4 - formatted.length(), 4, formatted);
		return buf.toString();
	}

	/**
	 * 生成主键id
	 * 
	 * @param context
	 * @return 根据uuid生成规则生成的id(String)
	 */
	public Serializable generate(JdbcContext context)
			throws IdentifierGenerationException {
		return new StringBuffer(36).append(format(getIP()))
				.append(format(getJVM())).append(format(getHiTime()))
				.append(format(getLoTime())).append(format(getCount()))
				.toString();
	}

	/**
	 * 如果 是auto为true,表明组件不会去初始化id,
	 * 
	 * @return
	 */
	@Override
	public boolean isAuto() {
		return false;
	}

	/**
	 * 额外信息 比如：sequence.hibernate_sequence <br/>
	 * extra 是 hibernate_sequence 这样 sequence generator 可以设置 sequence name
	 * 
	 * @param extra
	 */
	@Override
	public void setExtra(String extra) {
	}

	/**
	 * 如果 auto为true, 查看fillchar是否为空，不为空，那么在设置value的时候用该信息填充<br/>
	 * 比如：auto=true , getFillChar = 1000 <br/>
	 * 那么 insert into test(id,name) values(1000,:name);<br/>
	 * 如果：auto=true , getFillChar = null <br/>
	 * 否则 insert into test(id,name) values(:id,:name);
	 * 
	 * @return
	 */
	@Override
	public String getFillChar() {
		return null;
	}
}
