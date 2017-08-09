package com.sparrow.app.store.berkeley;

import java.io.File;

import com.sleepycat.je.Environment;
import com.sleepycat.je.EnvironmentConfig;

/**
 * Created by IntelliJ IDEA. User: YZC Date: 13-4-9 Time: 下午8:05 To change this
 * template use File | Settings | File Templates.
 */
public class StoreFacade {
	public static final Environment environment;
	public static final FinalPageStore pageStore;
	public static final UrlsStore urlsStore;

	static {
		boolean persist = true;// .equalsIgnoreCase(System.getProperty("persist.enable"));
		String home = System.getProperty("persist.home",
				System.getProperty("user.dir"))
				+ "/frontier";

		EnvironmentConfig envConfig = new EnvironmentConfig();
		envConfig.setAllowCreate(true);
		envConfig.setTransactional(persist);
		envConfig.setLocking(persist);
		File envHome = new File(home);
		if (!envHome.exists()) {
			envHome.mkdir();
		}
		if (!persist) {
			IO.deleteFolderContents(envHome);
		}
		environment = new Environment(envHome, envConfig);

		pageStore = new FinalPageStore(environment, "FinalPageStore", persist);
		urlsStore = new UrlsStore(environment, "UrlsStore", persist);
	}

	public static void close() {
		pageStore.sync();
		pageStore.close();
		urlsStore.close();
		environment.close();
	}

	public static void main(String args[]) {
		UrlData urlData = new UrlData();
		urlData.setSiteId(2);
		urlData.setDocId(2);
		urlData.setUrl("222222222");
		StoreFacade.pageStore.put(urlData);
		//StoreFacade.pageStore.put(null);
		PageRecordScanImpl pageRecordScan = new PageRecordScanImpl();
		StoreFacade.pageStore.scan(pageRecordScan);

		// System.out.println(StoreFacade.pageStore.getLength());
		StoreFacade.urlsStore.scan(10);
		close();
	}

}
