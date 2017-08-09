package com.sparrow.app.store;

import com.sleepycat.bind.tuple.TupleBinding;
import com.sleepycat.je.Environment;
import com.sleepycat.je.EnvironmentConfig;
import com.sleepycat.je.EnvironmentMutableConfig;
import com.sparrow.server.base.command.LoginInfo;
import com.sparrow.app.store.berkeley.IO;

import java.io.File;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.concurrent.TimeUnit;

/**
 * Created by IntelliJ IDEA. User: YZC Date: 13-4-9 Time: 下午8:05 To change this
 * template use File | Settings | File Templates.
 */
public class StoreFacade {
    private static Environment environment;
    private static boolean persist;
    private static DataStore taskStore;


    static Environment getEnvironment() {
        persist =true;
//        "true".equalsIgnoreCase(SystemConfig
//                .getProperty("persist.enable"));
        if (environment == null) {
            synchronized (StoreFacade.class) {
                if (environment == null) {
                    // .equalsIgnoreCase(System.getProperty("persist.enable"));
                    String home = System.getProperty("persist.home",
                            System.getProperty("user.dir"))
                            + "/frontier";
                    EnvironmentConfig envConfig = new EnvironmentConfig();
                    envConfig.setAllowCreate(true);
                    envConfig.setTransactional(false);
                    envConfig.setLocking(false);
                    envConfig.setReadOnly(false);
                    envConfig.setTxnTimeout(10000, TimeUnit.MILLISECONDS);
                    envConfig.setLockTimeout(10000, TimeUnit.MILLISECONDS);

                    /*   其他配置 可以进行更改 */
                    EnvironmentMutableConfig envMutableConfig = new EnvironmentMutableConfig();
                    envMutableConfig.setCachePercent(50);//设置je的cache占用jvm 内存的百分比。
                    envMutableConfig.setCacheSize(123456);//设定缓存的大小为123456Bytes
                    envMutableConfig.setTxnNoSync(true);//设定事务提交时是否写更改的数据到磁盘，true不写磁盘。
                    envMutableConfig.setTxnWriteNoSync(false);//设定事务在提交时，是否写缓冲的log到磁盘。如果写磁盘会影响性能，不写会影响事务的安全。随机应变。
                    /*    */
                    File envHome = new File(home);
                    if (!envHome.exists())
                        envHome.mkdir();
                    if (!persist)
                        IO.deleteFolderContents(envHome);
                    environment = new Environment(envHome, envConfig);
                    environment.setMutableConfig(envMutableConfig);
                    // environment.setThreadTransaction();
                }
            }
        }
        return environment;
    }

    public static Class getGenericType(Class<?> clazz, int index) {
        Type genType = clazz.getGenericSuperclass();
        if (!(genType instanceof ParameterizedType)) {
            return Object.class;
        }
        Type[] params = ((ParameterizedType) genType).getActualTypeArguments();
        if (index >= params.length || index < 0) {
            throw new RuntimeException("Index outof bounds");
        }
        if (!(params[index] instanceof Class)) {
            return Object.class;
        }
        return (Class) params[index];
    }

    public static <T> DataStore<T> createStore(TupleBinding<T> binding) {
        if (binding == null)
            return null;
        Class<T> clz;
        if (BaseTupleBinding.class.isAssignableFrom(binding.getClass()))
            clz = ((BaseTupleBinding) binding).getBindingClass();
        else
            clz = getGenericType(binding.getClass(), 0);
        String table = clz.getSimpleName();
        DataStore<T> store = new DataStore<T>(getEnvironment(), table, false, binding);
        return store;
    }

    public static <T> DataStore<T> createStore(Class<T> clazz) {
        if (clazz == null)
            return null;
        String table = clazz.getSimpleName();
        DataStore<T> store = new DataStore(getEnvironment(), table, false, new JsonTupleBinding(clazz));
        return store;
    }


    public static void save() {
        if (taskStore != null)
            taskStore.sync();
    }

    public static void close() {
        if (taskStore != null) {
            taskStore.sync();
            taskStore.close();
        }

        if (environment != null)
            environment.close();
    }

    public static void main(String args[]) {
        createStore(new JsonTupleBinding(LoginInfo.class));
        createStore(LoginInfo.class);
    }
}
