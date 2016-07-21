package tengxun;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.downloader.HttpClientDownloader;
import us.codecraft.webmagic.processor.example.MysqlConnect;
import us.codecraft.webmagic.selector.Html;

/**
 * 爬取"腾讯科技最新滚动新闻"类别为4
 * 
 * @author yuanhao
 *
 */
public class PreDeal_T4 {

	// 导入mysqloperation类
	private static MysqlConnect mysqlCon = new MysqlConnect();

	// 准备sql语句
	private static String sql;

	// 影响行数（数据变更后，影响行数都是大于0，等于0时没变更，所以说如果变更失败，那么影响行数必定为负）
	private static int i = -1;

	// 结果集
	// private ResultSet rs;

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		workflow(4);
		// test();
	}

	public static void workflow(int id) {
		mikdir("F:\\data_news\\tengxun\\" + id + "\\000");
		for(int i = 1; i < 11; i++){
			String startUrl = "http://tech.qq.com/c/it_"+i+".htm";
			String filePath = "F:\\data_news\\tengxun\\" + id + 
					"\\000\\finance2016023_科技_IT"+i+".html";
			getPages(startUrl, filePath, id, i);
		}
		
	}

	/**
	 * 爬取总流程
	 * 
	 * @return
	 */
	public static void getPages(String startUrl, String filePath, int ID, int I) {
		// get the html of the url
		// String str = getPageHtmlSelenium(startUrl);
		String str = getPageHtml(startUrl);
		// save the page if not exist
		if (!new File(filePath).exists()) {
			saveHtmlGB(filePath, str);
		}
		// get the html to string
//		String contentString = readFileByLines(filePath);
		
		// get the urls in the html string
		ArrayList<String> links = getLinks(filePath);// 需要修改（不同类型）
		
//		ArrayList<String> errorLinks = new ArrayList<String>();
//		ArrayList<String> errorPath = new ArrayList<String>();
		System.out.println("链接总数为：" + links.size());
		
		// crwaler the page of those urls
		for (int i = 0; i < links.size(); i++) {
			// get each url
			String url = links.get(i);
			
			// 保存网页的文件夹和网页的ID号，增量时需要修改
			int pageId = i + 1 + (I-1)*22 + 340;
			
			System.out.println(pageId + "----url:" + url);
			// set store path
			String html_path = "F:\\data_news\\tengxun\\" + ID + "\\" + pageId;
			// create directory if not exist
			mikdir(html_path);
			// set file name
			String filePath1 = html_path + "\\" + pageId + ".html";

			if (!new File(filePath1).exists()) {
				// get the html of the url
				String urlContent = getPageHtml(url);
				// 保存页面到本地
				saveHtmlGB(filePath1, urlContent);
			}

			// 保存图片
			ArrayList<String> imgUrl = getImgUrl(filePath1);
			for (int j = 0; j < imgUrl.size(); j++) {
				String imgPath = html_path + "\\" + j + ".png";
				if (!new File(imgPath).exists()) {
					String link = imgUrl.get(j);
					try {
						saveImg(imgPath, link);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}

			// 得到存储数据库的信息
			int type = ID;
			ArrayList<String> info = getSqlData(filePath1, url, html_path);// 需要修改（不同类型）
			// 存数据到数据库
			try {
				insertSql(pageId, type, info);// 需要修改（不同类型）（插入表格不同）
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			// // 保存有错的链接
			// boolean error = getErrorPage(filePath1);
			// if(error){
			// errorLinks.add(url);
			// errorPath.add(filePath1);
			// }

		}

		// for(int i = 0; i < errorLinks.size(); i++){
		// if (!new File(errorPath.get(i)).exists()) {
		// // get the html of the url
		// String urlContent = getPageHtml(errorLinks.get(i));
		// // 保存页面到本地
		// saveHtmlGB(errorPath.get(i), urlContent);
		// }
		// }
	}

	/**
	 * 填写wangyi表
	 * 
	 * @throws Exception
	 */
	public static void insertSql(int id, int type, ArrayList<String> info)
			throws Exception {
		// 创建sql语句
		sql = "replace into tengxun_4 values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
		Object[] answerobject = new Object[] { id, info.get(0), info.get(1),
				type, info.get(2), info.get(3), info.get(4), info.get(5),
				info.get(6), info.get(7) };

		// Object[] answerobject = new Object[]
		// {1,"a","a",5,"a","a","a","2016-1-25 15:30:20","2016-1-25 15:30:20","a"};
		mysqlCon.doSql(sql, answerobject);
		i = mysqlCon.getUpdateCount();
		if (i != -1) {
			System.out.println("数据插入成功！");
		} else {
			System.out.println("数据插入失败！");
		}
		// 关闭链接
		mysqlCon.getClose();

	}

	/**
	 * 得到需要存储数据库的时间
	 * 
	 * @return
	 */
	public static ArrayList<String> getSqlData(String filePath,
			String current_link, String html_path) {
		ArrayList<String> list = new ArrayList<String>();
		// 1.id号：为输入变量
		
		
		// 2.origin_link：都为（http://sports.qq.com/nlestcs.htm）
		String origin_link = "http://tech.qq.com/it.htm";// 需要修改（不同类型）
		
		
		// 3.current_link：为输入变量
		// 4.type：都为 3 (体育类)
		// 5.title
		String title = getTitle(filePath);
		// 6.content
		String content = getContent(filePath);
		// 7.deal_content: null
		String deal_content = "";
		// 8.scratch_time:(文件创建时间)
		long time = new File(filePath).lastModified();
		String scratch_time = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss")
				.format(new Date(time));
		// 9.post_time:
		String post_time = getPostTime(filePath);
		// 10.html_path
		list.add(origin_link);
		list.add(current_link);
		list.add(title);
		list.add(content);
		list.add(deal_content);
		list.add(scratch_time);
		list.add(post_time);
		list.add(html_path);
		return list;
	}

	/**
	 * 读取文件中的链接地址保存到ArrayList<String>中
	 */
	public static ArrayList<String> getLinks(String filePath) {
		ArrayList<String> links = new ArrayList<String>();
		// String url = null;
		File input = new File(filePath);
		Document doc = null;
		try {
			doc = Jsoup.parse(input, "GB2312", "");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// / begin to analyse
		Elements urls = doc.select("div#listZone").select("div.Q-tpList")
				.select("h3").select("a[href]");
		if (urls.size() != 0) {
			// System.out.println("url总数："+urls.size());
			for (int i = 0; i < urls.size(); i++) {
				Element url = urls.get(i);
				String link = url.attr("href");
				// System.out.println("link:" + link);
				links.add(link);
			}
		} else {
			// System.out.println("try again, baby~~~");
		}
		return links;

	}

	/**
	 * 实现功能：得到所有连接
	 * 
	 * @param doc
	 * @throws Exception
	 */
	public static String getTitle(String path) {
		String tit = "";
		File input = new File(path);
		Document doc = null;
		try {
			doc = Jsoup.parse(input, "GB2312", "");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// Elements title = doc.select("h1");
		Elements title = doc.select("div.hd").select("h1");
		if (title.size() != 0) {
			tit = title.get(0).text();
			// System.out.println("title:" + tit);

		} else {
			// System.out.println("no title...");
		}
		return tit;
	}

	/**
	 * 实现功能：得到所有连接
	 * 
	 * @param doc
	 * @throws Exception
	 */
	public static String getContent(String path) {
		String con = "";
		File input = new File(path);
		Document doc = null;
		try {
			doc = Jsoup.parse(input, "GB2312", "");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Elements content = doc.select("div[id^=Cnt-Main]").select(
				"p[style^=TEXT-INDENT]");
		if (content.size() != 0) {
			for (int i = 0; i < content.size(); i++) {
				con = con + content.get(i).text();
			}
			// System.out.println("content:" + con);
		} else {
			content = doc.select("div[id^=Cnt-Main]").select("p[style^=MARGIN-TOP]");
			if(content.size()!=0){
				for (int i = 0; i < content.size(); i++) {
					con = con + content.get(i).text();
				}
			} else {
				content = doc.select("div[id^=Cnt-Main]").select("p");
				if(content.size()!=0){
					for (int i = 0; i < content.size(); i++) {
						con = con + content.get(i).text();
					}
				}
			}
			
			// System.out.println("no content...");
		} 

		return con;
	}

	/**
	 * 实现功能：得到所有连接
	 * 
	 * @param doc
	 * @throws Exception
	 */
	public static String getPostTime(String path) {
		String time = "";
		File input = new File(path);
		Document doc = null;
		try {
			doc = Jsoup.parse(input, "GB2312", "");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Elements postTime = doc.select("div.hd").select("div.a_Info").select("span.a_time");
		if (postTime.size() != 0) {
			time = postTime.get(0).text();
			// System.out.println("time: "+time);
		} else {
			postTime = doc.select("div.hd").select("span[class^=pubTime]");
			if(postTime.size()!=0){
				time = postTime.get(0).text();
//				String time = "2016年02月23日12:03";
				String[] time1 = time.split("年");
//				System.out.println(time1[0]+"---->"+time1[1]);
				String[] time2 = time1[1].split("月");
				String[] time3 = time2[1].split("日");
				String year = time1[0];
				String month = time2[0];
				String day = time3[0];
				String clock = time3[1];
				time = year+"-"+month+"-"+day+" "+clock+":00";
//				System.out.println(date);
			} else {
				SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				time = df.format(new Date());
			}
			
		}
		return time;
	}

	/**
	 * 实现功能：得到所有图片
	 * 
	 * @param doc
	 * @throws Exception
	 */
	public static ArrayList<String> getImgUrl(String path) {
		ArrayList<String> imgUrl = new ArrayList<String>();
		File input = new File(path);
		Document doc = null;
		try {
			doc = Jsoup.parse(input, "GB2312", "");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Elements content = doc.select("div[id^=Cnt-Main]").select("p[align]");
		// System.out.println(content.size());
		if (content.size() != 0) {
			Elements img = content.select("img[src]");
			// System.out.println(img.size());
			if (img.size() != 0) {
				for (int i = 0; i < img.size(); i++) {
					String url = img.get(i).attr("src");
					imgUrl.add(url);
					// System.out.println("imgUrl:" + url);
				}
			}

		} else {
			// System.out.println("+++++");
		}

		return imgUrl;
	}

	/**
	 * 得到页面html源码（调用webmagic框架的爬取器）
	 * 
	 * @return url
	 */
	public static String getPageHtml(String url) {
		// Site site = Site.me()//.setHttpProxy(new HttpHost("127.0.0.1",8888))
		// .setRetryTimes(3).setSleepTime(1000).setUseGzip(true);
		HttpClientDownloader httpClientDownloader = new HttpClientDownloader();
		// Html html = httpClientDownloader.download(url);
		Html html = httpClientDownloader.download(url, 7, "gbk");
		String content = html.toString();
		// System.out.println("HTML:"+content);
		return content;
	}

	/**
	 * 实现功能：爬取作者页面，输入是 “页面保存路径filepath” 和 “作者页面链接url” 10秒内没打开页面会重开
	 * 无需下来页面，所需信息都在页面顶部 使用技术：Selenium
	 * 
	 * @param filePath
	 *            , url
	 * 
	 */
	public static String getPageHtmlSelenium(String url) {
		System.out.println(String.format("Fetching %s...", url));
	
		// 设置
		// System.setProperty("webdriver.ie.driver","C:\\Program Files\\Internet Explorer\\IEDriverServer.exe");
		// System.out.println("InternetExplorerDriver opened");
	
		WebDriver driver = new InternetExplorerDriver();
		driver.manage().timeouts().pageLoadTimeout(20, TimeUnit.SECONDS); // 5秒内没打开，重新加载
		while (true) {
			try {
				driver.get(url);
			} catch (Exception e) {
				driver.quit();
				driver = new InternetExplorerDriver();
				driver.manage().timeouts()
						.pageLoadTimeout(20, TimeUnit.SECONDS);
				continue;
			}
			break;
		}
		// save page
		String html = driver.getPageSource();
		System.out.println("save finish...");
		driver.quit();
		return html;
	}

	public static void getGunDong(String url, String filePath1) {
		if (!new File(filePath1).exists()) {
			String urlContent = getPageHtml(url);
			// 保存页面到本地
			saveHtmlGB(filePath1, urlContent);
		} else {
			System.out.println("already exist......");
		}

	}

	/**
	 * 保存图片字节流到文件，已图片文件保存到本地
	 * 
	 * @throws Exception
	 */
	public static void saveImg(String imgPath, String link) throws Exception {
		BufferedOutputStream out = null;
		byte[] bit = download(link);
		if (bit.length > 0) {
			try {
				out = new BufferedOutputStream(new FileOutputStream(imgPath));
				out.write(bit);
				out.flush();
			} finally {
				if (out != null)
					out.close();
			}
		}
	}

	/**
	 * 下载图片，得到字节流
	 * 
	 * @return
	 * @throws Exception
	 */
	private static byte[] download(String link) throws Exception {
		URL url = new URL(link);
		HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
		httpConn.connect();
		InputStream cin = httpConn.getInputStream();
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];
		int len = 0;
		while ((len = cin.read(buffer)) != -1) {
			outStream.write(buffer, 0, len);
		}
		cin.close();
		byte[] fileData = outStream.toByteArray();
		outStream.close();
		return fileData;
	}

	/**
	 * 读取js文件，获得其中的内容用于解析得到连接 以行为单位读取文件，常用于读面向行的格式化文件
	 */
	public static String readFileByLines(String fileName) {
		File file = new File(fileName);
		BufferedReader reader = null;
		StringBuffer outString = new StringBuffer();
		try {
			// System.out.println("以行为单位读取文件内容，一次读一整行：");
			// reader = new BufferedReader(new FileReader(file));
			reader = new BufferedReader(new InputStreamReader(
					new FileInputStream(file), "GB2312"));
			String tempString = null;
			// int line = 1;
			// 一次读入一行，直到读入null为文件结束
			while ((tempString = reader.readLine()) != null) {
				// 显示行号
				// System.out.println("line " + line + ": " + tempString);
				// line++;
				outString.append(tempString);
			}
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e1) {
				}
			}
		}
		String str = outString.toString();
		// System.out.println("str："+str);
		return str;
	}

	/**
	 * 实现功能：保存html字符串流到本地html文件 输入是 “页面保存路径filepath” 和 “html字符串源码”
	 * 
	 * @param filePath
	 *            , str
	 */
	public static void saveHtmlGB(String filePath, String str) {
		try {
			OutputStreamWriter outs = new OutputStreamWriter(
					new FileOutputStream(filePath, true), "GB2312");
			outs.write(str);
			outs.close();
		} catch (IOException e) {
			System.out.println("Error at save html...");
			e.printStackTrace();
		}
	}

	public static void mikdir(String path) {
		// TODO Auto-generated method stub
		File f = new File(path);
		if (!f.exists()) {
			f.mkdir();
		}
	}

	public static void haha(String path) {
		String con = null;
		File input = new File(path);
		Document doc = null;
		try {
			doc = Jsoup.parse(input, "GB2312", "");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Elements content = doc.select("div#endText");
		if (content.size() != 0) {
			con = content.get(0).text();
			System.out.println("content:" + con);
		} else {
			Element picContent = doc.select("div.photo-b").get(0);
			// System.out.println(picContent.size());
			// if(picContent.size()!=0){
			con = picContent.toString();
			System.out.println("picContent:" + con);
			// }
			// System.out.println("sada");
		}
	}

	public static void storePage(Page page, int id) {
		// 用户自己添加（by 郑元浩）
		// 得到网页源码内容
		System.out.println("------");
		if (page != null && page.getStatusCode() == 200) {
			String filePath = "F:/test/" + id + ".html";
			File f = new File(filePath);
			if (f.exists()) {
				System.out.println("already exist-----------");
			} else {
				System.out.println("save page-----------");
				String str = page.getRawText();
				OutputStreamWriter outs;
				try {
					outs = new OutputStreamWriter(new FileOutputStream(
							filePath, true), "GB2312");
					outs.write(str);
					outs.close();
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		}
		// 显示网页源码内容
		// System.out.println(page.getRawText());
	}

	/**
	 * 读取文件中的发布时间保存到ArrayList<String>中
	 */
	public static ArrayList<String> getPostTimeBBB(String contentString) {
		ArrayList<String> links = new ArrayList<String>();
		String[] content = contentString.split("\"");
		for (int i = 0; i < content.length; i++) {
			String text = content[i];
			// System.out.println(text);
			// 正则匹配时间
			Pattern pattern = Pattern
					.compile("^((\\d{2}(([02468][048])|([13579][26]))[\\-\\/\\s]?((((0?[13578])|(1[02]))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(3[01])))|(((0?[469])|(11))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(30)))|(0?2[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])))))|(\\d{2}(([02468][1235679])|([13579][01345789]))[\\-\\/\\s]?((((0?[13578])|(1[02]))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(3[01])))|(((0?[469])|(11))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(30)))|(0?2[\\-\\/\\s]?((0?[1-9])|(1[0-9])|(2[0-8]))))))(\\s(((0?[0-9])|([1-2][0-3]))\\:([0-5]?[0-9])((\\s)|(\\:([0-5]?[0-9])))))?$");
			Matcher matcher = pattern.matcher(text);
			// System.out.println(matcher.matches());
			if (matcher.matches()) {
				// System.out.println(text);
				links.add(text);
			}
		}
		System.out.println("size:" + links.size());
		for (int i = 0; i < links.size(); i++) {
			System.out.println("haha:" + links.get(i));
		}
		return links;

	}

	/**
	 * 实现功能：得到所有连接
	 * 
	 * @param doc
	 * @throws Exception
	 */
	public static boolean getErrorPage(String path) {
		boolean a = false;
		File f = new File(path);
		long len = f.length();
		// System.out.println(f.length());
		if (len < 50000) {
			System.out.println("++++++++++");
			// f.delete();
			a = true;
		}
		return a;
	}

	/**
	 * 读取本地爬取好的网页，得到其中的新闻链接
	 * 
	 * @return
	 */
	public static ArrayList<String> getCrawlerUrls(String fileName) {
		String contentString = readFileByLines(fileName);
		ArrayList<String> links = getLinks(contentString);
		return links;
	}

}
