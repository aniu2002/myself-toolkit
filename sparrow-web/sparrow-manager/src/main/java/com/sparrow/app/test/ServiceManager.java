package com.sparrow.app.test;

import com.sparrow.orm.dao.simple.NormalDao;
import com.sparrow.orm.session.simple.ConfigureSessionFactory;
import com.sparrow.orm.template.simple.NormalOperateTemplate;
import com.sparrow.app.services.user.SysUserService;

public class ServiceManager {
    private static ServiceManager instance;

    private final NormalDao baseDao;
    private final SysUserService sysUserService;


    private ServiceManager() {
        ConfigureSessionFactory sessionFactory = new ConfigureSessionFactory();
        sessionFactory.setConfigFile("classpath:conf/jdbc.properties");
        sessionFactory.setMapXml("classpath:eggs/mapConfig.xml");
        NormalOperateTemplate operateTemplate = new NormalOperateTemplate();
        operateTemplate.setSessionFactory(sessionFactory);

        this.baseDao = new NormalDao();
        this.baseDao.setOperateTemplate(operateTemplate);
        this.baseDao.setSessionFactory(sessionFactory);

        SysUserService newSysUserSrv = new SysUserService();
        newSysUserSrv.setBaseDao(this.baseDao);
        this.sysUserService = newSysUserSrv;
    }

    private static ServiceManager getInstance() {
        if (instance == null) {
            synchronized (ServiceManager.class) {
                if (instance == null)
                    instance = new ServiceManager();
            }
        }
        return instance;
    }


    public static SysUserService getSysUser() {
        return getInstance().sysUserService;
    }

}
