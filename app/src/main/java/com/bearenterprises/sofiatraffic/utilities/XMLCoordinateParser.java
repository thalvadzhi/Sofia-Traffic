package com.bearenterprises.sofiatraffic.utilities;

import android.content.Context;

import com.bearenterprises.sofiatraffic.constants.Constants;
import com.bearenterprises.sofiatraffic.stations.Station;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

/**
 * Created by thalv on 02-Jul-16.
 */
public class XMLCoordinateParser {

    public XMLCoordinateParser(){
    }

    public static ArrayList<Station> parse(Context context, String filename) throws ParserConfigurationException, IOException, SAXException {
        ArrayList<Station> stations = new ArrayList<>();

        File  file = new File(context.getFilesDir(), filename);

        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.parse(file);
        doc.getDocumentElement().normalize();
        NodeList nList = doc.getElementsByTagName(Constants.XML_TAG_STATION);

        for (int i = 0; i < nList.getLength(); i++) {
            Node nNode = nList.item(i);
            Element e = (Element) nNode;

            String label = e.getAttribute(Constants.XML_ATTRIBUTE_LABEL);
            String code = e.getAttribute(Constants.XML_ATTRIBUTE_CODE);
            String lat = e.getAttribute(Constants.XML_ATTRIBUTE_LAT);
            String lon = e.getAttribute(Constants.XML_ATTRIBUTE_LON);

            stations.add(new Station(label, code, lat, lon));
        }

        return stations;

    }

}
