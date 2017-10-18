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

    public static void addTimesWithDirection(TimeResultsFragment fragment, Line line, StopInformationGetter.TimesWithDirection timesWithDirection){
        if (fragment == null){
            return;
        }
        fragment.addTimeScheduleWithDirection(line, timesWithDirection);
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

    public static<T extends Stop> void showOnMap(ArrayList<T> stations, MainActivity mainActivity){
        if (mainActivity == null){
            return;
        }
        CommunicationUtility.hideSlideUpPanel(mainActivity);
        mainActivity.setPage(Constants.SECTION_MAP_SEARCH_IDX);
        MapSearchFragment f = mainActivity.getMapSearchFragment();
        if (f == null){
            return;
        }
        f.getMapFragment().showOnMap(stations);
    }

    public static<T extends Stop> void showOnMap(T st, MainActivity mainActivity){
        ArrayList<Stop> stations = new ArrayList<>();
        stations.add(st);
        CommunicationUtility.showOnMap(stations, mainActivity);
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

    public static void updateLineInfoSlow(Stop station, ArrayList<Line> lines, MainActivity mainActivity){
        if (mainActivity == null){
            return;
        }
        TimesSearchFragment f = mainActivity.getTimesSearchFragment();
        if (f == null){
            return;
        }
        f.updateLineInfoSlowForSelectLines(station, lines);
    }

    public static void nextSwitch(MainActivity activity){
        activity.getTimesSearchFragment().nextInCodeName();
    }

    public static void checkCodeNameSwitch(int id, MainActivity activity){
        activity.getTimesSearchFragment().checkCodeNameSwitch(id);
    }
}
