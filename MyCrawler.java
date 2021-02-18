import com.opencsv.CSVWriter;
import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.crawler.WebCrawler;
import edu.uci.ics.crawler4j.parser.HtmlParseData;
import edu.uci.ics.crawler4j.url.WebURL;

import javax.print.DocFlavor;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

public class MyCrawler extends WebCrawler {

    private final static Pattern FILTERS = Pattern.compile(".*(\\.(css|js|json|webmanifest|ttf|svg|wav|avi|mov|mpeg|mpg|ram|m4v|wma|wmv|mid|txt|mp2|mp3|mp4|zip|rar|gz|exe|ico))$");
    File file = new File("./data/crawl/res.csv");
    File file2 = new File("./data/crawl/visit_NewsSite.csv");
    File file3 = new File("./data/crawl/urls_NewsSite.csv");

    @Override
    public boolean shouldVisit(Page referringPage, WebURL url) {
        String href = url.getURL().toLowerCase();
        Boolean res =  !FILTERS.matcher(href).matches() && (href.startsWith("https://www.nytimes.com") || href.startsWith("http://www.nytimes.com")
        || href.startsWith("www.nytimes.com") || href.startsWith("nytimes.com"));
        try {

            FileWriter fw = new FileWriter(file3, true);
            CSVWriter writer = new CSVWriter(fw);
            if(res){
                writer.writeNext(new String[]{"OK",href});
            }else{
                writer.writeNext(new String[]{"N_OK",href});
            }
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return res;
    }

    /**
     * This function is called when a page is fetched and ready
     * to be processed by your program.
     */
    @Override
    public void visit(Page page) {
        String url = page.getWebURL().getURL();
        String statuscode = String.valueOf(page.getStatusCode());
        try {
            FileWriter fw = new FileWriter(file, true);
            CSVWriter writer = new CSVWriter(fw);
            writer.writeNext(new String[]{statuscode, url});
            writer.close();

            //Write to File2 URLs, size of downloaded files, # of outlinks found, content type;
            fw = new FileWriter(file2, true);
            writer = new CSVWriter(fw);
            Set<WebURL> links = new HashSet<>();

            //If there are outgoing links
            if(page.getParseData() instanceof HtmlParseData){
                HtmlParseData htmlParseData = (HtmlParseData) page.getParseData();
                links = htmlParseData.getOutgoingUrls();
            }
            String[] data = {url, String.valueOf(page.getContentData().length), String.valueOf(links.size()),page.getContentType()};
            writer.writeNext(data);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}