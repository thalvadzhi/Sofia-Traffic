package com.bearenterprises.sofiatraffic.constants;

import com.bearenterprises.sofiatraffic.fragments.LoadingFragment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by thalv on 02-Jul-16.
 */
public class Constants {
    public static final String XML_COORDINATE_FILE = "coordinates.xml";
    public static final String JSON_COORDINATE_FILE = "coordinates.json";
    public static final String JSON_COORDINATE_FILE_NEW = "new_coordinates.json";
    public static final String SHOW_DIALOG = "show dialog";
    public static final String DISMISS_DIALOG = "dismiss dialog";
    public static final String DESCRIPTIONS_FILE_NAME = "descriptions.txt";
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
    public static final String SECTION_LINES = "ЛИНИИ";
    public static final String SECTION_MAP_SEARCH = "КАРТА";
    public static final String SHARED_PREFERENCES_FAVOURITES = "favourites";
    public static final String SHARED_PREFERENCES_LAST_UPDATE = "last_update";
    public static final String KEY_LAST_UPDATE = "last update";
    public static final String SHARED_PREFERENCES_HELPER = "helper";
    public static final int SHARED_PREFERENCES_DEFAULT_LAST_UPDATE_TIME = -1;
    public static final long WEEK_IN_MILLISECONDS = 604800000;
    public static final long DAY_IN_MILLISECONDS = 86400000;
    public static final int REQUEST_CODE_FINE_LOCATION = 1;
    public static final float MINIMUM_ACCURACY = 20;
    public static final String BUS = "bus";
    public static final String TRAM = "tram";
    public static final String TROLLEY = "trolley";
    public static final int SECTION_SEARCH_IDX = 0;
    public static final int SECTION_FAVOURITES_IDX = 1;
    public static final int SECTION_LINES_IDX = 2;
    public static final int SECTION_MAP_SEARCH_IDX = 3;
    public static final int MAXIMUM_TIME_GPS_LOCK = 10000;
    public static final int FIVE_SECONDS_MS = 5000;
    public static final String LINE_ID_DEFAULT = "Избери линия";
    public static final String REST_QUERY_LINES = "https://api.sofiatransport.com/v2/stops/%s";
    public static final String REST_QUERY_TIMES = "https://api.sofiatransport.com/v2/stops/%s/%s";
    public static final List<String> TRANSPORTATION_TYPES = Arrays.asList("Трамвай", "Автобус", "Тролей");
    public static final int TRAM_POSITION = 0;
    public static final int BUS_POSITION = 1;
    public static final int TROLLEY_POSITION = 2;
    public static final String IVKOS_API_BASE_URL = "https://api.sofiatransport.com/v3/";
    public static final String SHARED_PREFERENCES_REGISTRATION = "registration";
    public static final String REGISTRATION = "reg";
    public static final String SHARED_PREFERENCES_DEFAULT_REGISTRATION = "no_reg";
    public static final String UNAUTHOROZIED_USER_ID = "UnauthorizedUserIdError";


    public static final String COORDINATES_DOWNLOAD_URL_JSON = "https://raw.githubusercontent.com/thalvadzhi/Sofia-Traffic-Stops-Getter/master/coordinates.json";
    public static final String COORDINATES_DOWNLOAD_URL = "https://raw.githubusercontent.com/ptanov/sofia-public-transport-navigator/master/sptn/res/raw/coordinates.xml";
    public static final String DESCRIPTIONS_DOWNLOAD_URL = "https://raw.githubusercontent.com/thalvadzhi/Sofia-Traffic/master/files/descriptions.txt";

}
