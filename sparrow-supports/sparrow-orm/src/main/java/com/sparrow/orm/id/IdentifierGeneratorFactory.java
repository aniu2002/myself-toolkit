package com.sparrow.orm.id;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.sparrow.core.utils.ReflectHelper;
import com.sparrow.orm.exceptions.IdentifierGenerationException;


/**
 * IdentifierGenerator的工厂类,提供id生成器的实例对象
 * 
 * @author YK
 */
public final class IdentifierGeneratorFactory {

	private IdentifierGeneratorFactory() {
	}

	// 绑定实现的id生成器策略;
	private static final HashMap<String, Class<?>> GENERATORS = new HashMap<String, Class<?>>();
	// 缓存id生成器对象
	private static final Map<String, IdentifierGenerator> INSTANCES = new ConcurrentHashMap<String, IdentifierGenerator>();
	// 实现的id生成器:uuid,sequence
	static {
		GENERATORS.put("uuid", UUIDGenerator.class);
		GENERATORS.put("sequence", SequenceGenerator.class);
	}

	/**
	 * 生成strategy对应的id生成器,如果存在则返回现有的
	 * 
	 * @param strategy
	 *            id生成器策略,如uuid
	 * @return generator id生成器对象
	 */
	public static IdentifierGenerator get(String strategy) {
		IdentifierGenerator generator = INSTANCES.get(strategy);
		if (generator == null) {
			generator = create(strategy);
			INSTANCES.put(strategy, generator);
		}
		return generator;
	}

	/**
	 * 生成strategy对应的id生成器并返回创建的对象
	 * 
	 * @param strategy
	 *            id生成器策略,例如uuid,如果是sequence策略则传入的参数格式为sequence.xxxx(xxxx为序列名称,
	 *            与数据库序列名称对应)
	 * @return generator id生成器对象
	 */
	public static IdentifierGenerator create(String strategy)
			throws IdentifierGenerationException {
		try {
			int idx = strategy.indexOf('.');
			String strategyKey = strategy;
			String extra = null;
			if (idx != -1) {
				strategyKey = strategy.substring(0, idx);
				extra = strategy.substring(idx + 1);
			}
			Class<?> clazz = getIdentifierGeneratorClass(strategyKey);
			IdentifierGenerator generator = (IdentifierGenerator) clazz
					.newInstance();
			generator.setExtra(extra);
			return generator;
		} catch (Exception e) {
			throw new IdentifierGenerationException(
					"could not instantiate id generator", e);
		}
	}

	/**
	 * 获取strategy对应的id生成器类
	 * 
	 * @param strategy
	 *            id生成器策略,例如uuid
	 * @return clazz id生成器类
	 */
	public static Class<?> getIdentifierGeneratorClass(String strategy) {
		Class<?> clazz = GENERATORS.get(strategy);
		try {
			if (clazz == null)
				clazz = ReflectHelper.classForName(strategy);
		} catch (ClassNotFoundException e) {
			throw new IdentifierGenerationException(
					"could not interpret id generator strategy: " + strategy);
		}
		return clazz;
	}
}
