package com.zlikun.fang.dom;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.Test;

import java.io.IOException;
import java.net.URL;
import java.util.Iterator;

/**
 * 新房列表页抓取
 * @author zlikun <zlikun-dev@hotmail.com>
 * @date 2017/7/20 22:14
 */
public class NewHouseListTest {

    private String prefix = "http://newhouse.sh.fang.com" ;

    @Test
    public void test() throws IOException {

        // 首页
        // http://newhouse.sh.fang.com/house/s/
        // 中间页及尾页
        // http://newhouse.sh.fang.com/house/s/b92/

        load("http://newhouse.sh.fang.com/house/s/");
//        load("http://newhouse.sh.fang.com/house/s/b92/");

    }

    private void load(String url) throws IOException {

        // 这种方式为了解决编码问题(中文乱码)
        // Document doc = Jsoup.parse(new URL(url).openStream() ,"gb2312" ,url) ;
        Document doc = Jsoup.connect(url).get() ;

        // 获取下一页链接
        String nextUrl = null ;
        Element $a = doc.select("#sjina_C01_47").select("a.active").next().first() ;
        if($a != null) {
            nextUrl = prefix + $a.attr("href") ;
            System.out.println(String.format("%s -> %s" ,$a.text() ,nextUrl));
        }

        // 采集列表信息
        Elements $liList = doc.select("#newhouse_loupai_list").select("li") ;
        if ($liList != null && !$liList.isEmpty()) {
            Iterator<Element> iterator = $liList.iterator() ;
            while (iterator.hasNext()) {
                System.out.println("------------------------");
                process(iterator.next());
            }
        }

    }

    /**
     * 一个房产信息
     * @param $li
     */
    private void process(Element $li) {
        // 封面图片
        Element $img = $li.select("div.nlc_img").select("img:eq(1)").first() ;
        if ($img != null) {
            System.out.println(String.format("封面图片：src = %s / alt = %s / [%s ,%s]" ,$img.attr("src") ,$img.attr("alt") ,$img.attr("width") ,$img.attr("height")));
        }

        // 楼盘名称、详情页链接
        Element $aName = $li.select("div.nlc_details").select("div.nlcd_name").select("a").first() ;
        if ($aName != null) {
            System.out.println(String.format("楼盘名称：%s、详情页链接：%s" ,$aName.text() ,$aName.attr("href")));
        }

        // 1居/	2居/	3居/	4居	－ 116平米 ，这部分略

        // 地址
        Element $aAddress = $li.select("div.nlc_details").select("div.address").select("a").first() ;
        if ($aAddress != null) {
            System.out.println(String.format("楼盘地址：%s、地图链接：%s" ,$aAddress.text() ,$aAddress.attr("href")));
        }

        // 电话
        Element $divTel = $li.select("div.nlc_details").select("div.tel").first() ;
        if ($divTel != null) {
            System.out.println(String.format("楼盘联系电话：%s" ,$divTel.text()));
        }

        // 在售状态、标签
        Element $divTag = $li.select("div.nlc_details").select("div.fangyuan").first() ;
        if ($divTag != null) {
            System.out.println(String.format("楼盘状态：%s", $divTag.select("span").text()));
            Iterator<Element> iterator = $divTag.select("a").iterator();
            System.out.print("楼盘标签：\t");
            while (iterator.hasNext()) {
                System.out.print(iterator.next().text() + "\t");
            }
            System.out.println();
        }

        // 价格
        Element $divPrice = $li.select("div.nlc_details").select("div.nhouse_price").first() ;
        if ($divPrice != null) {
            System.out.println(String.format("楼盘价格：%s" ,$divPrice.text()));
        }

    }

}