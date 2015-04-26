package parser;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.HashMap;
import java.util.Map;

public class BannerXMLParser extends XMLParser {
    private static final String XML_FILE_PATH = "showdata/%d/banners.xml";

    public HashMap<ParseType, String> getImageURLs(int seriesID) {
	NodeList nodeList = parseSeriesInfo(seriesID, ParseType.IMAGE);

	double fanartRating = 0;
	double posterRating = 0;

	String fanartBannerPath = "";
	String posterBannerPath = "";

	HashMap<ParseType, String> imagePaths = new HashMap<>();

	for (int bannerIndex = 0; bannerIndex < nodeList.getLength(); bannerIndex++) {
	    Node bannerNode = nodeList.item(bannerIndex);
	    if (bannerNode.getNodeType() == Node.ELEMENT_NODE) {
		Element bannerElement = (Element) bannerNode;

		String bannerType = bannerElement.getElementsByTagName("BannerType").item(0).getTextContent();
		String rating = bannerElement.getElementsByTagName("Rating").item(0).getTextContent();
		String language = bannerElement.getElementsByTagName("Language").item(0).getTextContent();
		// Make sure there actually is a rating to compare
		if (!rating.isEmpty()) {
		    double fetchedRating = Double.parseDouble(rating);
		    if (bannerType.equals("fanart") && fetchedRating > fanartRating) {
			System.out.println(fetchedRating);
			fanartRating = fetchedRating;
			fanartBannerPath = bannerElement.getElementsByTagName("VignettePath").item(0).getTextContent();
		    } else if (bannerType.equals("poster") && fetchedRating > posterRating) {
			posterRating = fetchedRating;
			posterBannerPath = bannerElement.getElementsByTagName("BannerPath").item(0).getTextContent();
		    }
		}
	    }
	}
	imagePaths.put(ParseType.FANART, fanartBannerPath);
	imagePaths.put(ParseType.POSTER, posterBannerPath);
	return imagePaths;
    }
}
