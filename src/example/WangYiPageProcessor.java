package example;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.processor.PageProcessor;

/**
 * @author code4crafter@gmail.com <br>
 * @since 0.4.0
 */
public class WangYiPageProcessor implements PageProcessor {

	// 部分一：抓取网站的相关配置，包括编码、抓取间隔、重试次数等
    private Site site = Site.me()//.setHttpProxy(new HttpHost("127.0.0.1",8888))
            .setRetryTimes(3).setSleepTime(1000).setUseGzip(true);

    @Override
	public Site getSite() {
	    return site;
	}

    @Override
    // process是定制爬虫逻辑的核心接口，在这里编写抽取逻辑
    public void process(Page page) {
    	// 部分三：从页面发现后续的url地址来抓取
    	System.out.println("page.getHtml().links()："+page.getHtml().links());
    	page.addTargetRequests(page.getHtml().links().regex("(http://sports\\.163\\.com/\\w+)").all());
    	//        System.out.println(page.getHtml());
    	//        System.out.println(page.getHtml().links().regex("(http://news\\.163\\.com/16/\\d+)").all());
    	//        page.addTargetRequests(page.getHtml().links().regex("(https://github\\.com/\\w+/\\w+)").all());
    	// 部分二：定义如何抽取页面信息，并保存下来
    	page.putField("title", page.getHtml().css("h1[id=h1title]","text").toString());
    	page.putField("content", page.getHtml().xpath("//div[@id='endText']/allText()"));

    	//        page.putField("tidycontent", page.getHtml().xpath("//div[@id='endText']/tidyText()"));// 处理后的内容，保持内容空格等信息
//    	PreDeal.storePage(page, 5);
    }

	public static void main(String[] args) {
//    	getUrls("F:/test/体育2016116.html");
//    	System.out.println("+++++++++++");
        //single download
        Spider spider = Spider.create(new WangYiPageProcessor())
        		.thread(2);
        
        String urlTemplate = "http://sports.163.com/special/0005rt/news_json.js?0.3484169826927056";
//        String urlTemplate = "http://military.people.com.cn/GB/172467/index.html";
        ResultItems resultItems = spider.<ResultItems>get(urlTemplate);
        System.out.println(resultItems.get("title"));
        System.out.println(resultItems.getAll());

        
        //multidownload
//        List<String> list = getUrls("F:/test/体育2016116.html");
//        List<ResultItems> resultItemses = spider.<ResultItems>getAll(list);
//        for (ResultItems resultItemse : resultItemses) {
////            System.out.println(resultItemse);
//            System.out.println(resultItemse.getAll());
//        }
        spider.close();
    }
}
