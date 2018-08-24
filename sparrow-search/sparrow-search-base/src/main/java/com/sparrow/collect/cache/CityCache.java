package com.sparrow.collect.cache;

import com.sparrow.collect.space.MSBean;
import com.sparrow.collect.website.domain.City;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.*;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Created by yangtao on 2015/7/31.
 */
public class CityCache implements Cache {
    private Log log = LogFactory.getLog(CityCache.class);

    private MSBean<CityCacheSupport> cacheMSBean = new MSBean<>();

    private ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    private static CityCache instance;

    public static CityCache getInstance() {
        if (instance == null) {
            synchronized (CityCache.class) {
                if (instance == null) {
                    instance = new CityCache();
                }
            }
        }
        return instance;
    }

    private CityCache() {
        CityCacheSupport master = new CityCacheSupport();
        CityCacheSupport slave = new CityCacheSupport();
        cacheMSBean.setMaster(master);
        cacheMSBean.setSlave(slave);
    }

    @Override
    public void init() {
        log.info("init begin");
        try {
            lock.writeLock().lock();
            cacheMSBean.getMaster().init();
        } finally {
            lock.writeLock().unlock();
        }
        log.info("init end");
    }

    @Override
    public void switchOver() {
        log.info("switchOver begin");
        cacheMSBean.getSlave().init();
        try {
            lock.writeLock().lock();
            cacheMSBean.switchOver();
            cacheMSBean.getSlave().clear();
        } finally {
            lock.writeLock().unlock();
        }
        log.info("switchOver end");
    }

    public City get(Integer id) {
        try {
            lock.writeLock().lock();
            return cacheMSBean.getMaster().get(id);
        } finally {
            lock.writeLock().unlock();
        }
    }

    public List<City> get(List<Integer> ids) {
        try {
            lock.writeLock().lock();
            return cacheMSBean.getMaster().get(ids);
        } finally {
            lock.writeLock().unlock();
        }
    }

    public List<City> findLevelCities(List<Integer> ids) {
        try {
            lock.writeLock().lock();
            return cacheMSBean.getMaster().findLevelCities(ids);
        } finally {
            lock.writeLock().unlock();
        }
    }

    private class CityCacheSupport {
        private Map<Integer, City> areas;
        private Map<Integer, City> cities;
        private Map<Integer, City> provinces;
        private Map<Integer, City> countries;
        private Map<Integer, City> all;

        public CityCacheSupport() {
            areas = new LinkedHashMap();
            cities = new LinkedHashMap();
            provinces = new LinkedHashMap();
            countries = new LinkedHashMap();
            all = new LinkedHashMap();
        }

        public City get(Integer id) {
            return all.get(id);
        }

        public List<City> get(List<Integer> ids) {
            List<City> values = new ArrayList(ids.size());
            City value = null;
            for (Integer id : ids) {
                value = get(id);
                if (value == null) {
                    continue;
                }
                values.add(value);
            }
            return values;
        }

        /**
         * 根据城市id对应城市及其所有上级城市
         *
         * @param cityIds
         * @return
         */
        public List<City> findLevelCities(List<Integer> cityIds) {
            Map<Integer, City> cities = new HashMap();
            for (Integer cityId : cityIds) {
                findCities(cityId, cities);
            }
            return new ArrayList(cities.values());
        }

        public void findCities(Integer cityId, Map<Integer, City> cities) {
            if (cities.containsKey(cityId)) {
                return;
            }
            City city = get(cityId);
            if (city == null) {
                return;
            }
            cities.put(cityId, city);
            findCities(city.getParentId(), cities);
        }

        public void clear() {
            all.clear();
            countries.clear();
            provinces.clear();
            cities.clear();
            areas.clear();
        }

        public void init() {
            initService();
            initCountries();
            initProvinces();
            initCities();
            initAreas();
            initAll();
        }

        public void initService() {

        }

        public void initCountries() {

        }

        public void initProvinces() {

        }

        public void initCities() {

        }

        public void initAreas() {

        }

        public void initAll() {
            all.putAll(countries);
            all.putAll(provinces);
            all.putAll(cities);
            all.putAll(areas);
        }

        public City convert(Object cityResp) {

            return new City();
        }
    }
}
