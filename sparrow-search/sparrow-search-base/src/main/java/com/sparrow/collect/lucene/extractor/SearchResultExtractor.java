package com.sparrow.collect.lucene.extractor;

import com.sparrow.collect.lucene.data.IndexVo;
import org.apache.lucene.document.Document;


public class SearchResultExtractor implements ResultExtractor {

	public Object wrapHit(int id, Document document, float score, IRender render) {
		String type = document.get("itype");
		// if (!"1".equals(type))
		// return null;
		IndexVo vo = new IndexVo();
		vo.setItemid(document.get("itemid"));
		vo.setItype(type);
		vo.setAuthor(document.get("author"));
		String resl = document.get("title");
		if (render != null)
			resl = render.render(resl, "title");// 高亮渲染
		vo.setTitle(resl);
		vo.setCtime(document.get("createtime"));
		vo.setTypeName(document.get("typename"));
		vo.setTypeId(Integer.valueOf(document.get("typeid")));
		resl = document.get("content");
		if ("1".equals(vo.getItype()) && render != null)
			resl = render.render(resl, "content");// 高亮渲染
		else if ("2".equals(vo.getItype())) // file
		{
			vo.setPath(document.get("path"));
			vo.setPageNo(Integer.valueOf(document.get("pno")));
			resl = vo.getPath();
		} else if ("3".equals(vo.getItype())) {
			vo.setPath(document.get("path"));
			resl = vo.getPath();
		}
		vo.setContent(resl);
		return vo;
	}

}
