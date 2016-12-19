package com.bearenterprises.sofiatraffic.fragments;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import com.bearenterprises.sofiatraffic.MainActivity;
import com.bearenterprises.sofiatraffic.R;
import com.bearenterprises.sofiatraffic.constants.Constants;
import com.bearenterprises.sofiatraffic.restClient.ApiError;
import com.bearenterprises.sofiatraffic.restClient.SofiaTransportApi;
import com.bearenterprises.sofiatraffic.restClient.Station;
import com.bearenterprises.sofiatraffic.restClient.Time;
import com.bearenterprises.sofiatraffic.stations.LineTimes;
import com.bearenterprises.sofiatraffic.utilities.ParseApiError;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * Created by thalv on 02-Jul-16.
 */

public class TimesSearchFragment extends Fragment {
    private  EditText t;
    private SwipeRefreshLayout refreshLayout;
    public TimesSearchFragment() {
    }

    private CoordinatorLayout coordinatorLayout;
    public static TimesSearchFragment newInstance() {
        TimesSearchFragment fragment = new TimesSearchFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public void requestFocusOnEditText(){
        if(t.requestFocus()) {
            getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        coordinatorLayout = ((MainActivity)getActivity()).getCoordinatorLayout();
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        Button searchButton = (Button) rootView.findViewById(R.id.button_search);
        refreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swiperefresh);
        refreshLayout.setEnabled(false);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                String code = t.getText().toString();
                showStationTimes(code);
                refreshLayout.setRefreshing(false);
            }
        });
        t = (EditText) rootView.findViewById(R.id.station_code);
        t.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                }
            }
        });
        t.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int keyCode, KeyEvent keyEvent) {
                if (keyEvent.getAction() == KeyEvent.ACTION_DOWN)
                {
                    if(keyCode == KeyEvent.KEYCODE_ENTER){
                        String code = t.getText().toString();
                        showStationTimes(code);
                    }
                }
                return false;
            }
        });
        searchButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String code = t.getText().toString();
                showStationTimes(code);
            }
        });

        return rootView;
    }

    public void setEnablednessRefreshLayout(boolean enable){
        refreshLayout.setEnabled(enable);
    }

    private Station getStation(String code, SofiaTransportApi sofiaTransportApi) throws IOException {
        Call<Station> call = sofiaTransportApi.getStation(code);
        Response<Station> response = call.execute();
        if(!response.isSuccessful()){
            ApiError error = ParseApiError.parseError(response);
            Log.i("Error", error.getCode());
            if(error.getCode().equals(Constants.UNAUTHOROZIED_USER_ID)){
                ((MainActivity)getActivity()).removeRegistration();
                call = sofiaTransportApi.getStation(code);
                response = call.execute();
            }

        }
        return response.body();
    }

    public void showStationTimes(String code){
        if(code != null){
            refreshLayout.setEnabled(true);
            FragmentManager manager = getFragmentManager();
            StationNameFragment stationNameFragment = StationNameFragment.newInstance(code);
            manager.beginTransaction().setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right).replace(R.id.station_name_fragment, stationNameFragment).commit();
            t.setText(code);
            t.setSelection(t.getText().length());
            getTimes(code, manager);
        }else{
            ((MainActivity)getActivity()).makeSnackbar("Няма въведен код на спирка!");
        }
    }

    class SearchQuery extends AsyncTask<Void, Void, ArrayList<com.bearenterprises.sofiatraffic.restClient.second.Line>>{
        private String code;
        private FragmentManager m;
        private LoadingFragment l;
        private MainActivity a;
        private TimeResultsFragment timeResultsFragment;
        private SofiaTransportApi sofiaTransportApi;
        public SearchQuery(String code, LoadingFragment l, FragmentManager m, MainActivity a){
            this.code = code;
            this.m = m;
            this.l = l;
            this.a = a;

        }

        @Override
        protected ArrayList<com.bearenterprises.sofiatraffic.restClient.second.Line> doInBackground(Void... params) {
            sofiaTransportApi = SofiaTransportApi.retrofit.create(SofiaTransportApi.class);
            Station station= null;
            try {
                station = getStation(code, sofiaTransportApi);
                if(station == null){
                    ((MainActivity)getContext()).detachFragment(l);
//                        m.beginTransaction().detach(l).commit();
                    ((MainActivity)getActivity()).makeSnackbar("Няма информация!");
                    return null;
                }
            } catch (IOException e) {
                e.printStackTrace();
                if (station == null){
                    ((MainActivity)getContext()).detachFragment(l);
                    ((MainActivity)getActivity()).makeSnackbar("Няма информация!");
                    return null;
                }

            }


            final ArrayList<LineTimes> lineTimes = new ArrayList<>();
            for(com.bearenterprises.sofiatraffic.restClient.second.Line line : station.getLines()){
                lineTimes.add(new LineTimes(line, Integer.toString(line.getType())));
            }
            timeResultsFragment = TimeResultsFragment.newInstance(lineTimes);
            ((MainActivity)getActivity()).changeFragment(R.id.result_container, timeResultsFragment);
            return station.getLines();


        }

        @Override
        protected void onPostExecute(ArrayList<com.bearenterprises.sofiatraffic.restClient.second.Line> lines){
            if(lines != null){
                for(int i = 0; i < lines.size(); i++){
                    final com.bearenterprises.sofiatraffic.restClient.second.Line l = lines.get(i);
                    Call<List<Time>> call = this.sofiaTransportApi.getTimes(code, Integer.toString(l.getId()));
                    call.enqueue(new Callback<List<Time>>() {
                        @Override
                        public void onResponse(Call<List<Time>> call, Response<List<Time>> response) {
                            List<Time> times = response.body();
                            ((MainActivity) getActivity()).addTimes(timeResultsFragment, l, times);
                        }

                        @Override
                        public void onFailure(Call<List<Time>> call, Throwable t) {

                        }
                    });

                }
            }
        }
    }


    public void getTimes(String code, FragmentManager manager){
        LoadingFragment l = LoadingFragment.newInstance();
        manager.beginTransaction().replace(R.id.result_container, l).commit();
        SearchQuery query = new SearchQuery(code,l , manager, (MainActivity) getActivity());
        query.execute();
    }


}

