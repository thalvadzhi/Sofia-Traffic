package com.bearenterprises.sofiatraffic.utilities.communication;

import com.bearenterprises.sofiatraffic.activities.MainActivity;
import com.bearenterprises.sofiatraffic.constants.Constants;
import com.bearenterprises.sofiatraffic.fragments.FavouritesFragment;
import com.bearenterprises.sofiatraffic.fragments.LinesFragment;
import com.bearenterprises.sofiatraffic.fragments.MapSearchFragment;
import com.bearenterprises.sofiatraffic.fragments.TimeResultsFragment;
import com.bearenterprises.sofiatraffic.fragments.TimesSearchFragment;
import com.bearenterprises.sofiatraffic.restClient.Stop;
import com.bearenterprises.sofiatraffic.restClient.StopInformationGetter;
import com.bearenterprises.sofiatraffic.restClient.Time;
import com.bearenterprises.sofiatraffic.restClient.Line;
import com.bearenterprises.sofiatraffic.routesExpandableRecyclerView.Direction;
import com.bearenterprises.sofiatraffic.stations.GeoLine;

import java.util.ArrayList;
import java.util.List;

/**
 * Methods that implement some sort of communication between different fragments
 */


public class CommunicationUtility {

    public static void setEnablednessRefreshLayout(boolean enable, MainActivity mainActivity){
        if (mainActivity == null){
            return;
        }
        TimesSearchFragment f = mainActivity.getTimesSearchFragment();
        if (f == null){
            return;
        }
        f.setEnablednessRefreshLayout(enable);
    }

    public static void addTimes(TimeResultsFragment fragment, Line line, List<Time> times){
        if (fragment == null){
            return;
        }
        fragment.addTimeSchedule(line, (ArrayList<Time>) times);
    }


    public static void addScheduleTime(TimeResultsFragment fragment, Line line){
        if (fragment == null){
            return;
        }
        fragment.addScheduleTimes(line);
    }

    public static boolean checkIfTimesAlreadySet(TimeResultsFragment fragment, Line line) throws Exception {
        if (fragment == null){
            throw new Exception("Fragment is null.");
        }
        return fragment.checkIfAlreadySet(line);
    }

    public static void removeLine(TimeResultsFragment fragment, Line line){
        if (fragment == null){
            return;
        }
        fragment.removeLine(line);
    }

    public static void showTimes(String code, MainActivity mainActivity) {
        if (mainActivity == null){
            return;
        }
        mainActivity.setPage(Constants.SECTION_SEARCH_IDX);
        TimesSearchFragment f = mainActivity.getTimesSearchFragment();
        if (f == null){
            return;
        }
        f.showStationTimes(code);
    }

    /**
     *
     * @param stations
     * @param ordered whether to mark the first stop of a list of stops by making it semitransparent
     * @param mainActivity
     * @param <T>
     */
    public static<T extends Stop> void showOnMap(ArrayList<T> stations, boolean ordered, MainActivity mainActivity){
        if (mainActivity == null){
            return;
        }
        CommunicationUtility.hideSlideUpPanel(mainActivity);
        mainActivity.setPage(Constants.SECTION_MAP_SEARCH_IDX);
        MapSearchFragment f = mainActivity.getMapSearchFragment();
        if (f == null){
            return;
        }
        f.getMapFragment().showOnMap(stations, ordered);
    }

    public static void showPolyLine(Direction direction, MainActivity activity){
        if (activity == null){
            return;
        }
        CommunicationUtility.hideSlideUpPanel(activity);
        activity.setPage(Constants.SECTION_MAP_SEARCH_IDX);
        MapSearchFragment f = activity.getMapSearchFragment();
        if (f == null){
            return;
        }
        List<GeoLine> geoLines = activity.getGeoLines();
        if (geoLines == null){
            return;
        }
        for(GeoLine l : geoLines){
            String tr_type = symbolToNumberType(direction.getTransportationType());
            if(tr_type.equals(Integer.toString(l.getType()))){
                String start = Integer.toString(direction.getFrom().getCode());
                String end = Integer.toString(direction.getTo().getCode());

                if(l.getFirstStop().equals(start) && l.getLastStop().equals(end)){
                    f.getMapFragment().showPolyOnMap(l.getGeo(), tr_type);
                    return;
                }
            }
        }
    }

    public static String symbolToNumberType(String symbol){
        switch (symbol){
            case "tram": return "0";
            case "bus": return "1";
            case "trolley": return "2";
            default: return "-1";
        }
    }


    public static<T extends Stop> void showOnMap(T st, MainActivity mainActivity){
        ArrayList<Stop> stations = new ArrayList<>();
        stations.add(st);
        CommunicationUtility.showOnMap(stations, false, mainActivity);
    }

    public static void hideSlideUpPanel(MainActivity mainActivity){
        if (mainActivity == null){
            return;
        }
        MapSearchFragment f = mainActivity.getMapSearchFragment();
        if (f == null){
            return;
        }
        f.hideSlideUpPanel();
    }

    public static void showRoute(Line line, Integer stopCode, MainActivity mainActivity){
        if (mainActivity == null){
            return;
        }
        mainActivity.setPage(Constants.SECTION_LINES_IDX);
        LinesFragment f = mainActivity.getLinesFragment();
        if (f == null){
            return;
        }
        f.showRoute(line, stopCode);
    }

    public static void addFavourite(Stop st, MainActivity mainActivity){
        if (mainActivity == null || st == null){
            return;
        }
        FavouritesFragment f = mainActivity.getFavouritesFragment();
        if (f == null){
            return;
        }
        f.addFavourite(st);
    }

    public static void removeFavourite(int code, MainActivity mainActivity){
        if (mainActivity == null){
            return;
        }
        FavouritesFragment f = mainActivity.getFavouritesFragment();
        if (f == null){
            return;
        }
        f.removeFavourite(code);
    }


    public static void nextSwitch(MainActivity activity){
        activity.getTimesSearchFragment().nextInCodeName();
    }

    public static void checkCodeNameSwitch(int id, MainActivity activity){
        activity.getTimesSearchFragment().checkCodeNameSwitch(id);
    }
}
