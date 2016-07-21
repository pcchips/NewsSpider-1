package example;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.downloader.HttpClientDownloader;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.selector.Html;
import us.codecraft.webmagic.selector.Selectable;

/**
 * @author code4crafter@gmail.com <br>
 * @since 0.4.0
 */
public class RenMinPageProcessor implements PageProcessor {

	// 部分一：抓取网站的相关配置，包括编码、抓取间隔、重试次数等
	private Site site = Site.me()// .setHttpProxy(new
									// HttpHost("127.0.0.1",8888))
			.setRetryTimes(3).setSleepTime(1000).setUseGzip(true);

	@Override
	public Site getSite() {
		return site;
	}

	@Override
	// process是定制爬虫逻辑的核心接口，在这里编写抽取逻辑
	public void process(Page page) {
		// 部分二：定义如何抽取页面信息，并保存下来
		Selectable title = page.getHtml()
				.xpath("//h1[@id='h1title']/allText()");
		// System.out.println(title);
		if (!title.equals(null)) {
			page.putField("title",
					page.getHtml().xpath("//h1[@id='h1title']/allText()"));
			page.putField("content",
					page.getHtml().xpath("//div[@id='endText']/allText()"));
		} else {
			page.putField("title",
					page.getHtml().xpath("//div[@class='headline']/allText()"));
			page.putField("content",
					page.getHtml().xpath("//div[@id='endText']/allText()"));
		}

		// 保存网站
		// PreDeal.storePage(page, 4);
	}

	public static void spiderHa() {
		Spider spider = Spider.create(new RenMinPageProcessor()).thread(2);
		// multidownload
		// ArrayList<String> list = PreDeal.getCrawlerUrls();
		// List<ResultItems> resultItemses = spider.<ResultItems>getAll(list);
		// Page[] page = spider.getPage(request);
		// for (ResultItems resultItemse : resultItemses) {
		// System.out.println(resultItemse.getAll());
		// }
		spider.close();
	}

	public static void getQuora() {
		// 调用下载器       下载Quora页面
//		String url = "https://www.quora.com/As-a-programmer-what-tasks-have-you-automated-to-make-your-everyday-life-easier";
		String url = "https://www.quora.com/search?q=binary+tree";
		HttpClientDownloader httpClientDownloader = new HttpClientDownloader();
		Html html = httpClientDownloader.download(url);
//		Html html = httpClientDownloader.download(url,5,"gb2312");
		String content = html.toString();
		System.out.println("HTML:"+content);
	}

	public static void main(String[] args) {
		// spiderHa();
		getQuora();
	}
}
