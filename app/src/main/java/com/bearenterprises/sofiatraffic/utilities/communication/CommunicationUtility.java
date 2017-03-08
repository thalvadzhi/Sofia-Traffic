package com.bearenterprises.sofiatraffic.utilities.communication;

import com.bearenterprises.sofiatraffic.activities.MainActivity;
import com.bearenterprises.sofiatraffic.constants.Constants;
import com.bearenterprises.sofiatraffic.fragments.TimeResultsFragment;
import com.bearenterprises.sofiatraffic.restClient.Station;
import com.bearenterprises.sofiatraffic.restClient.Time;
import com.bearenterprises.sofiatraffic.restClient.second.Line;
import com.bearenterprises.sofiatraffic.restClient.second.Stop;

import java.util.ArrayList;
import java.util.List;

/**
 * Methods that implement some sort of communication between different fragments
 */


public class CommunicationUtility {

    public static void setEnablednessRefreshLayout(boolean enable, MainActivity mainActivity){
        mainActivity.getTimesSearchFragment().setEnablednessRefreshLayout(enable);
    }

    public static void addTimes(TimeResultsFragment fragment, Line line, List<Time> times){
        if (fragment == null){
            return;
        }
        fragment.addTimeSchedule(line, (ArrayList<Time>) times);
    }

    public static void showTimes(String code, MainActivity mainActivity) {
        mainActivity.setPage(Constants.SECTION_SEARCH_IDX);
        mainActivity.getTimesSearchFragment().showStationTimes(code);
    }

    public static void showOnMap(ArrayList<Stop> stations, MainActivity mainActivity){
        CommunicationUtility.hideSlideUpPanel(mainActivity);
        mainActivity.setPage(Constants.SECTION_MAP_SEARCH_IDX);
        mainActivity.getMapSearchFragment().getMapFragment().showOnMap(stations);
    }

    public static void showOnMap(Stop st, MainActivity mainActivity){
        ArrayList<Stop> stations = new ArrayList<>();
        stations.add(st);
        CommunicationUtility.showOnMap(stations, mainActivity);
    }

    public static void hideSlideUpPanel(MainActivity mainActivity){
        mainActivity.getMapSearchFragment().hideSlideUpPanel();
    }

    public static void showRoute(String trId, String lineId, MainActivity mainActivity){
        mainActivity.setPage(Constants.SECTION_LINES_IDX);
        mainActivity.getLinesFragment().showRoute(trId, lineId);
    }

    public static void addFavourite(Stop st, MainActivity mainActivity){
        mainActivity.getFavouritesFragment().addFavourite(st);
    }

    public static void removeFavourite(int code, MainActivity mainActivity){
        mainActivity.getFavouritesFragment().removeFavourite(code);
    }

    public static void updateLineInfoSlow(Station station, ArrayList<Line> lines, MainActivity mainActivity){
        //Move to utility
        mainActivity.getTimesSearchFragment().updateLineInfoSlowForSelectLines(station, lines);
    }
}
