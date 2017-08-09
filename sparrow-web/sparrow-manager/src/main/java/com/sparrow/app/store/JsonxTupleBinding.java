package com.sparrow.app.store;

import com.sleepycat.bind.tuple.TupleInput;
import com.sleepycat.bind.tuple.TupleOutput;
import com.sparrow.core.json.JsonMapper;
import com.sparrow.server.base.command.LoginInfo;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;

import java.io.IOException;

/**
 * Created by IntelliJ IDEA. User: YZC Date: 13-4-9 Time: 下午7:44 To change this
 * template use File | Settings | File Templates.
 */
public class JsonxTupleBinding extends com.sleepycat.bind.tuple.TupleBinding<LoginInfo> {

    @Override
    public LoginInfo entryToObject(TupleInput input) {
        String str = input.readString();
        try {
            LoginInfo obj = JsonMapper.mapper.readValue(str, LoginInfo.class);
            return obj;
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
    public void objectToEntry(LoginInfo obj, TupleOutput output) {
        try {
            String str = JsonMapper.mapper.writeValueAsString(obj);
            output.writeString(str);
        } catch (JsonGenerationException e) {
            e.printStackTrace();
        } catch (JsonMappingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
