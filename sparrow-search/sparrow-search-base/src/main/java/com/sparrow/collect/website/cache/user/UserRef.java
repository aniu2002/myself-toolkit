package com.sparrow.collect.website.cache.user;

import java.util.Set;

/**
 * Created by yangtao on 2015/7/23.
 * 用户评分相关 :
 * 用户消费区间
 * 用户关注分类
 * 用户常去店铺
 * 用户给予好评的商品
 */
public class UserRef {
    //消费价格区间
    private long[] prices;
    //关注分类
    private Set<Integer> attentionCategories;
    //常去店铺
    private Set<Long> attentionShops;
    //给予过好评的商品
    private Set<Long> goodComment;
    //给予过差评的商品
    private Set<Long> badComment;

    public long[] getPrices() {
        return prices;
    }

    public void setPrices(long[] prices) {
        this.prices = prices;
    }

    public Set<Integer> getAttentionCategories() {
        return attentionCategories;
    }

    public void setAttentionCategories(Set<Integer> attentionCategories) {
        this.attentionCategories = attentionCategories;
    }

    public Set<Long> getAttentionShops() {
        return attentionShops;
    }

    public void setAttentionShops(Set<Long> attentionShops) {
        this.attentionShops = attentionShops;
    }

    public Set<Long> getGoodComment() {
        return goodComment;
    }

    public void setGoodComment(Set<Long> goodComment) {
        this.goodComment = goodComment;
    }

    public Set<Long> getBadComment() {
        return badComment;
    }

    public void setBadComment(Set<Long> badComment) {
        this.badComment = badComment;
    }

    /**
     * 判断价格是否在用户消费价格区间之内
     * @param price
     * @return
     */
    public boolean inPrices(long price) {
        if(prices == null) {
            return false;
        }
        if(price >= prices[0] && price <= prices[1]) {
            return true;
        }
        return false;
    }

    /**
     * 判断分类是否是用户关注分类
     * @param categoryId
     * @return
     */
    public boolean isAttentionCategory(Integer categoryId) {
        if(attentionCategories == null) {
            return false;
        }
        if(attentionCategories.contains(categoryId)) {
            return true;
        }
        return false;
    }

    /**
     * 判断店铺是否是用户常去店铺
     * @param shopId
     * @return
     */
    public boolean isAttentionShop(Long shopId) {
        if(attentionShops == null) {
            return false;
        }
        if(attentionShops.contains(shopId)) {
            return true;
        }
        return false;
    }

    /**
     * 判断商品是否是用户给予过好评的商品
     * @param pid
     * @return
     */
    public boolean inGoodsComment(Long pid) {
        if (goodComment == null) {
            return false;
        }
        if(goodComment.contains(pid)) {
            return true;
        }
        return false;
    }
}
