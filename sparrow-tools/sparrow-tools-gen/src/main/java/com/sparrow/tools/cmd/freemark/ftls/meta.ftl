<?xml version="1.0" encoding="UTF-8"?>
<maps>
<#list data as itm>
	<map table="${itm.table?if_exists}" clazz="${itm.pack?if_exists}.${itm.clazzName?if_exists}" desc="${itm.desc?if_exists}">
		<key>${itm.primaryKey?if_exists}</key>
		<select>
			${itm.selectSql?if_exists}
		</select>
		<insert>
			${itm.insertSql?if_exists}
		</insert>
		<update>
			${itm.updateSql?if_exists}
		</update>
		<delete>
			${itm.deleteSql?if_exists}
		</delete>
		<query>
			${itm.querySql?if_exists}
		</query>
	</map>
</#list>
</maps>