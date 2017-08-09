package com.sparrow.orm.id;

import java.io.Serializable;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.sparrow.orm.exceptions.IdentifierGenerationException;
import com.sparrow.orm.jdbc.JdbcContext;


/**
 * SequenceGenerator sequenceid生成策略
 * 
 * @author YK
 */

public class SequenceGenerator implements IdentifierGenerator {
	// 日志参数
	private static final Log log = LogFactory.getLog(SequenceGenerator.class);
	// 序列名称
	private String sequenceName;

	/**
	 * 生成主键id
	 * 
	 * @param context
	 * @return seqNo id
	 */
	public Serializable generate(JdbcContext context)
			throws IdentifierGenerationException {
		if (context == null)
			throw new IdentifierGenerationException("JdbcContext is null ... ");
		long seqNo = 0;
		String sql = sqlCreateStrings(this.sequenceName);
		try {
			seqNo = context.findForLong(sql);
		} catch (Exception e) {
			e.printStackTrace();
			throw new IdentifierGenerationException(
					"The database returned no sequence generated identity value");
		}
		if (log.isDebugEnabled()) {
			log.debug("Sequence identifier generated: " + seqNo);
		}
		return seqNo;

	}

	/**
	 * 生成strategy对应的id生成器并返回创建的对象
	 * 
	 * @param seqName
	 *            序列名称
	 * @return generator id生成器对象
	 */
	public String sqlCreateStrings(String seqName)
			throws IdentifierGenerationException {
		return "SELECT " + seqName + ".NEXTVAL AS ID FROM DUAL";
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
		this.sequenceName = extra;
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
