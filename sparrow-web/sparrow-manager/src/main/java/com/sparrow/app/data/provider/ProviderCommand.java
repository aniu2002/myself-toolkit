package com.sparrow.app.data.provider;

import com.sparrow.app.common.source.JDBCSource;
import com.sparrow.app.common.source.NativeJDBCSource;
import com.sparrow.app.common.source.Source;
import com.sparrow.app.config.ProviderCfg;
import com.sparrow.app.config.ProviderLoader;
import com.sparrow.app.config.ProviderWrapper;
import com.sparrow.app.config.SourceCfg;
import com.sparrow.http.command.BaseCommand;
import com.sparrow.http.command.BeanWrapper;
import com.sparrow.http.command.Request;
import com.sparrow.http.command.Response;
import com.sparrow.http.command.resp.JsonResponse;
import com.sparrow.http.command.resp.MsgResponse;
import com.sparrow.http.command.resp.OkResponse;
import com.sparrow.orm.session.SessionFactory;
import com.sparrow.core.utils.ClassUtils;
import com.sparrow.core.utils.PropertiesFileUtil;
import com.sparrow.server.config.BeanContext;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class ProviderCommand extends BaseCommand {
    private final Map<String, Source> sources = new HashMap<String, Source>();
    private final Map<String, DataProvider> providers = new HashMap<String, DataProvider>();
    private final ProviderWrapper providerWrapper;
    private final BeanContext context;

    {
        providerWrapper = ProviderLoader.getProviderWrapper();
    }

    public ProviderCommand(BeanContext context) {
        this.context = context;
    }

    synchronized Source getSource(String name) {
        Source s = this.sources.get(name);
        if (s == null) {
            if ("@".equals(name)) {
                SessionFactory sessionFactory = (SessionFactory) context.getBean("sessionFactory");
                s = new NativeJDBCSource(sessionFactory);
            } else {
                SourceCfg sourceCfg = this.providerWrapper.getSource(name);
                if (sourceCfg == null)
                    throw new RuntimeException("source配置不存在-" + name);
                if ("db".equals(sourceCfg.getType())) {
                    Properties props = PropertiesFileUtil.getProps(sourceCfg.getProps());
                    s = new JDBCSource(props.getProperty("jdbc.url"),
                            props.getProperty("jdbc.driver"),
                            props.getProperty("jdbc.user"),
                            props.getProperty("jdbc.password"));
                }
            }
            if (s != null)
                this.sources.put(name, s);
        }
        return s;
    }

    DataProvider getProvider(String name) {
        DataProvider provider = this.providers.get(name);
        if (provider == null) {
            ProviderCfg providerCfg = this.providerWrapper.getProviders(name);
            if (providerCfg == null)
                throw new RuntimeException("provider配置不存在-" + name);
            provider = new DataProvider();
            provider.setName(providerCfg.getName());
            provider.setScript(providerCfg.getScript());
            provider.setDesc(providerCfg.getDesc());
            try {
                provider.setWrapperClass(ClassUtils.loadClass(providerCfg.getClazz()));
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
                throw new RuntimeException("provider配置类错误");
            }
            provider.setSource(this.getSource(providerCfg.getSource()));
            this.providers.put(name, provider);
        }
        return provider;
    }

    @Override
    protected Response doPost(Request request) {
        return OkResponse.OK;
    }

    protected Response doGet(Request request) {
        String p = request.get("_p");
        DataProvider dataProvider = this.getProvider(p);
        if (dataProvider == null)
            return new MsgResponse(MsgResponse.FAILURE, "Provider找不到-" + p);
        Object data = BeanWrapper.wrapBeanSup(dataProvider.getWrapperClass(),
                request);
        return new JsonResponse(dataProvider.query(data));
    }
}
