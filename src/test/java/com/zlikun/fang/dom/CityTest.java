package com.zlikun.fang.dom;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jsoup.Jsoup;
import org.jsoup.helper.StringUtil;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;

/**
 * 城市列表查询
 * @auther zlikun <zlikun-dev@hotmail.com>
 * @date 2017/7/20 13:05
 */
public class CityTest {

    @Test
    public void test() throws IOException {

        Document doc = Jsoup.connect("http://fang.com/SoufunFamily.htm").get() ;

        // 获取表格节点
        Element $table = doc.getElementById("senfe") ;

        // 获取行节点列表
        Elements $trList = $table.select("tr") ;
        Assert.assertFalse($trList.isEmpty());

        // 省份列表
        final LinkedList<Province> provinces = new LinkedList<Province>() ;

        // 遍历行节点
        Iterator<Element> iter = $trList.iterator() ;
        while(iter.hasNext()) {
            process(iter.next() ,provinces);
        }

        // 输出省份列表
        print(provinces);
    }

    /**
     * 遍历输出省份、城市信息
     * @param provinces
     */
    private void print(final LinkedList<Province> provinces) {
        for (Province province : provinces) {
            if (province.getName().equals("其它")) continue;
            System.out.println(String.format("- %s\t(%s)" ,province.getName() ,province.getInitial() != null ? province.getInitial() : " "));
            if (CollectionUtils.isEmpty(province.getCities())) continue;
            for (City city : province.getCities()) {
                System.out.println(String.format("\t- %s (<%s>)" ,city.getName() ,city.getUrl()));
            }
        }
    }

    /**
     * 处理行节点
     * @param $tr
     * @param provinces
     */
    private void process(Element $tr, LinkedList<Province> provinces) {
        if ($tr == null) return ;

        // 省份名称
        Element $province = $tr.select("td:eq(1)").first() ;
        String name = null ;    // 当前省份名称，判断省份是否沿用上一行省份的标志
        if($province != null) {
            // 填充省份名称(去除空格，包含首尾、中间全部空格)
            String _name = StringUtils.trimAllWhitespace($province.text()) ;
            if (_name != null && !_name.equals(" ")) name = _name ;
        }

        Province province = null ;
        if (name != null) {
            // 省份名非空，表示是一个新的省份
            province = new Province() ;
            province.setName(name);
            // 新的省份需要提取首字母
            Element $initial = $tr.select("td:eq(0)").first();
            if ($initial != null) {
                // 填充省份首字母(去除空格，包含首尾、中间全部空格)
                String initial = StringUtils.trimAllWhitespace($initial.text());
                // 这里有一个特殊空格(| |)，不好处理，直接写死
                if (!StringUtil.isBlank(initial) && !initial.equals(" ")) province.setInitial(initial);
            }
            // 如果首字母空，用上一个省份首字母填充
            if (province.getInitial() == null && !CollectionUtils.isEmpty(provinces)) {
                province.setInitial(provinces.getLast().getInitial());
            }
            // 新省份添加到省份列表，并更新当前省份
            provinces.add(province) ;
        } else {
            // 否则沿用上一行中的省份
            province = provinces.getLast() ;
        }

        if (province == null) return ;

        // 城市列表
        Elements $aList = $tr.select("a") ;
        if ($aList != null && !$aList.isEmpty()) {
            Iterator<Element> iter = $aList.iterator() ;
            while(iter.hasNext()) {
                Element $a = iter.next() ;
                // 将城市添加到当前省份中
                province.addCity(new City(StringUtils.trimAllWhitespace($a.text()) ,StringUtils.trimAllWhitespace($a.attr("href")))) ;
            }
        }

    }

    /**
     * 省份
     */
    @Data
    private class Province {
        private String name ;
        private String initial ;
        private Set<City> cities ;

        /**
         * 添加城市
         * @param city
         * @return
         */
        public Province addCity(City city) {
            if(city == null || city.getName() == null) return this;
            if (cities == null) cities = new HashSet<>() ;
            cities.add(city) ;
            return this ;
        }
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    private class City {
        private String name ;
        private String url ;
    }

    @Test
    public void blank() {
        // 城市列表中一个特殊空格
        String blank = " " ;
        // ascii(| |) -> 160
        System.out.println(String.format("ascii(|%s|) -> %d" ,blank ,(int) blank.charAt(0)));
    }

}
