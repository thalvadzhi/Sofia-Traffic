package com.bearenterprises.sofiatraffic.fragments;


import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bearenterprises.sofiatraffic.MainActivity;
import com.bearenterprises.sofiatraffic.R;
import com.bearenterprises.sofiatraffic.fragments.communication.StationTimeShow;
import com.bearenterprises.sofiatraffic.location.StationsLocator;
import com.bearenterprises.sofiatraffic.restClient.second.Stop;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

import io.nlopez.smartlocation.OnLocationUpdatedListener;
import io.nlopez.smartlocation.SmartLocation;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MapFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MapFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String STATIONS = "param1";
    private static final String LOCATION = "param2";


    // TODO: Rename and change types of parameters
    private ArrayList<Stop> mStations;
    private Location location;
    private GoogleMap map;
    private MapView mapView;

    public MapFragment() {
        // Required empty public constructor
    }


    // TODO: Rename and change types and number of parameters
    public static MapFragment newInstance(ArrayList<Stop> mStations, Location location) {
        MapFragment fragment = new MapFragment();
        Bundle args = new Bundle();
        args.putSerializable(STATIONS, mStations);
        args.putParcelable(LOCATION, location);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mStations = (ArrayList<Stop>) getArguments().getSerializable(STATIONS);
            location = getArguments().getParcelable(LOCATION);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_map, container, false);

        mapView = ((MapView) view.findViewById(R.id.map));
        mapView.onCreate(savedInstanceState);

        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                map = googleMap;
                setUpMap();
                mapView.onResume();
            }
        });
        //setUpMap();

        return view;
    }

    private void setUpMap() {
        //TODO implement marker info adapter
        MapsInitializer.initialize(getActivity());
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        map.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
            @Override
            public boolean onMyLocationButtonClick() {
                SmartLocation.with(getContext()).location().oneFix().start(new OnLocationUpdatedListener() {
                    @Override
                    public void onLocationUpdated(Location location) {
                        ArrayList<Stop> stations = getmStationsAround(location);
                        if (stations != null){
                            if(stations.size() != 0){
                                showOnMap(stations);

                            }

                        }

                    }
                });
                return false;
            }
        });
        map.setMyLocationEnabled(true);
        map.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                String code = marker.getSnippet();
                if(code == null){
                    return;
                }
                StationTimeShow show = (StationTimeShow) getActivity();
                show.showTimes(code);
            }
        });

        float zoomLevel = 13.0f; //This goes up to 21
        //coordinates of Serdika which seems like a Sofia-enough place
        LatLng currentLocation = new LatLng(42.697842, 23.321145);
        CameraUpdate cu = CameraUpdateFactory.newLatLngZoom(currentLocation, zoomLevel);
        map.moveCamera(cu);

        map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {

            @Override
            public void onMapClick(LatLng latLng) {
                map.clear();
                Location loc = new Location(LocationManager.PASSIVE_PROVIDER);
                loc.setLatitude(latLng.latitude);
                loc.setLongitude(latLng.longitude);
                StationsLocator locator = new StationsLocator(loc, 10, 1000, getContext());
                ArrayList<Stop> closestStations = locator.getClosestStations();
                ArrayList<Marker> markers = new ArrayList<>();
                MarkerOptions opt = new MarkerOptions();
                opt.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));
                opt.title("ОКОЛО МЕН");
                opt.position(latLng);
                Marker m = map.addMarker(opt);
                markers.add(m);
                if(closestStations != null){
                    setMarkers(closestStations, markers);
                    CameraUpdate cu = getCameraUpdate(markers);
                    map.animateCamera(cu);
                }else{
                    ((MainActivity)getActivity()).makeSnackbar("Няма спирки в близост до това място");
                }
            }
        });

    }

    private ArrayList<Stop> getmStationsAround(Location location){
        StationsLocator locator = new StationsLocator(location, 10, 1000, getContext());
        ArrayList<Stop> closestStations = locator.getClosestStations();
        return closestStations;
    }

    public void showOnMap(ArrayList<Stop> stations){
        if(map != null){
            map.clear();
            ArrayList<Marker> markers = new ArrayList<>();
            setMarkers(stations, markers);
            map.animateCamera(getCameraUpdate(markers));
        }
    }

    @NonNull
    private CameraUpdate getCameraUpdate(ArrayList<Marker> markers) {
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (Marker marker : markers){
            builder.include(marker.getPosition());
        }
        LatLngBounds bounds = builder.build();
        int padding = 0;
        return CameraUpdateFactory.newLatLngBounds(bounds, padding);
    }

    private void setMarkers(ArrayList<Stop> closestStations, ArrayList<Marker> markers) {
        for(Stop station : closestStations){
            String latitude = station.getLatitude();
            String longtitude = station.getLongtitude();

            if(!latitude.equals("") && !longtitude.equals("")){
                double lat = Float.parseFloat(latitude);
                double lon = Float.parseFloat(longtitude);
                MarkerOptions options = new MarkerOptions();
                options.position(new LatLng(lat, lon));
                options.title(station.getName());
                if(station.getCode().equals(-1)){
                    options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));
                }else{
                    options.snippet(Integer.toString(station.getCode()));
                }
                Marker marker = map.addMarker(options);
                markers.add(marker);
            }

        }
    }

    @Override
    public void onResume() {
        mapView.onResume();
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

}
