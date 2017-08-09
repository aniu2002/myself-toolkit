/**
 * Project Name:http-server  
 * File Name:TextMessageConverter.java  
 * Package Name:com.sparrow.core.web.converter  
 * Date:2013-12-13上午9:16:59  
 * Copyright (c) 2013, Boco.com All Rights Reserved.  
 *
 */

package com.sparrow.server.web.converter;

import com.sparrow.core.io.FastInputStream;
import com.sparrow.core.json.JsonMapper;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;

import java.io.IOException;
import java.io.InputStream;


/**
 * ClassName:TextMessageConverter <br/>
 * Date: 2013-12-13 上午9:16:59 <br/>
 *
 * @author YZC
 * @see
 * @since JDK 1.6
 */
public class JsonMessageConverter implements MessageConverter {

    @Override
    public String convert(Object object) {
        try {
            return JsonMapper.mapper.writeValueAsString(object);
        } catch (JsonGenerationException e) {
            e.printStackTrace();
        } catch (JsonMappingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public InputStream convertStream(Object object) {
        try {
            return new FastInputStream(JsonMapper.mapper.writeValueAsBytes(object));
        } catch (JsonGenerationException e) {
            e.printStackTrace();
        } catch (JsonMappingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public String getMimeType() {
        return "application/json";
    }

}
