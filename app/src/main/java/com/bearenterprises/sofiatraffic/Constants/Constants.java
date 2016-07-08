package com.bearenterprises.sofiatraffic.Constants;

/**
 * Created by thalv on 02-Jul-16.
 */
public class Constants {
    public static final String XML_COORDINATE_FILE = "coordinates.xml";
    public static final String XML_TAG_STATION = "station";
    public static final String XML_ATTRIBUTE_LABEL = "label";
    public static final String XML_ATTRIBUTE_CODE = "code";
    public static final String XML_ATTRIBUTE_LAT = "lat";
    public static final String XML_ATTRIBUTE_LON = "lon";
    public static final String JSON_LINES = "lines";
    public static final String JSON_ID = "id";
    public static final String JSON_TYPE = "type";
    public static final String JSON_NAME = "name";
    public static final String JSON_TIME = "time";
    public static final String SECTION_SEARCH = "ТЪРСЕНЕ";
    public static final String SECTION_FAVOURITES = "ЛЮБИМИ";
    public static final String SHARED_PREFERENCES_NAME = "favourites";
    public static final String SHARED_PREFERENCES_DATE_LAST_UPDATE = "last update";
    public static final int SHARED_PREFERENCES_DEFAULT_LAST_UPDATE_TIME = -1;
    public static final long WEEK_IN_MILLISECONDS = 604800000;

    public static final String REST_QUERY_LINES = "https://api.sofiatransport.com/v2/stops/%s";
    public static final String REST_QUERY_TIMES = "https://api.sofiatransport.com/v2/stops/%s/%s";

    public static final String XML_DOWNLOAD_URL = "https://raw.githubusercontent.com/ptanov/sofia-public-transport-navigator/master/sptn/res/raw/coordinates.xml";


}
