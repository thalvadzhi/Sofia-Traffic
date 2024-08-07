package com.bearenterprises.sofiatraffic.fragments;


import android.Manifest;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bearenterprises.sofiatraffic.activities.MainActivity;
import com.bearenterprises.sofiatraffic.R;
import com.bearenterprises.sofiatraffic.constants.Constants;
import com.bearenterprises.sofiatraffic.location.StationsLocator;
import com.bearenterprises.sofiatraffic.restClient.Stop;
import com.bearenterprises.sofiatraffic.utilities.communication.CommunicationUtility;
import com.bearenterprises.sofiatraffic.utilities.Utility;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.PolyUtil;

import java.util.ArrayList;
import java.util.List;

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
    private Marker previousMarker;
    private int previousZIndex;
    private int colorBus, colorTram, colorTrolley;
    private int[] colors;
    private Bitmap pinBus, pinTram, pinTrolley, pinBusTram, pinTrolleyTram, pinTrolleyBus, pinBusTramTrolley;
    private BitmapDescriptor pinBusDescriptor, pinTramDescriptor, pinTrolleyDescriptor, pinBusTramDescriptor, pinTrolleyTramDescriptor, pinTrolleyBusDescriptor, pinBusTramTrolleyDescriptor;


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
        MapsInitializer.initialize(getContext().getApplicationContext());
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

        initPinBitmaps();

        mapView = (view.findViewById(R.id.map));

        if (ContextCompat.checkSelfPermission(getContext(),
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions((MainActivity) getContext(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    Constants.REQUEST_CODE_FINE_LOCATION);

        }
        mapView.onCreate(savedInstanceState);
        colorBus = ContextCompat.getColor(getContext(), R.color.colorBus);
        colorTram = ContextCompat.getColor(getContext(), R.color.colorTram);
        colorTrolley = ContextCompat.getColor(getContext(), R.color.colorTrolley);
        colors = new int[] {colorBus, colorTrolley, colorTram};
        getMap();
        //setUpMap();
        initPinBitmapDescriptors();

        return view;
    }

    private void setUpMap() {
        //TODO implement marker info adapter
        MapsInitializer.initialize(getActivity());
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        TypedValue typedValue = new TypedValue();
        getContext().getTheme().resolveAttribute(R.attr.mapTheme, typedValue, true);
        int mapTheme = typedValue.resourceId;
        MapStyleOptions style = MapStyleOptions.loadRawResourceStyle(getContext(), mapTheme);

        map.setMapStyle(style);

        map.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
            @Override
            public boolean onMyLocationButtonClick() {
                SmartLocation.with(getContext()).location().oneFix().start(new OnLocationUpdatedListener() {
                    @Override
                    public void onLocationUpdated(Location location) {
                        ArrayList<Stop> stations = getStationsAround(location);
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
        map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                if(previousMarker != null){
                    Stop stop = (Stop) previousMarker.getTag();
                    Bitmap correctImageForStop = getCorrectImageForStop(stop.getLineTypes());
                    previousMarker.setIcon(BitmapDescriptorFactory.fromBitmap(correctImageForStop));
                }
                previousMarker = marker;

                Stop stop = (Stop) marker.getTag();
                if(stop.getLineTypes() != null){

                    Bitmap correctImageForStop = getCorrectImageForStop(stop.getLineTypes());
                    @ColorInt int colorAccent = ContextCompat.getColor(getContext(), R.color.colorAccent);
                    Bitmap bitmap = Utility.replaceColor(correctImageForStop, colors, colorAccent);
                    marker.setIcon(BitmapDescriptorFactory.fromBitmap(bitmap));
                }
                if(stop != null){
                    ((MainActivity)getActivity()).showSlideUpPanelWithInfo(stop);
                }
                map.animateCamera(CameraUpdateFactory.newLatLng(marker.getPosition()), 300, null);
                return true;
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
                CommunicationUtility.showTimes(code, (MainActivity)getActivity());
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
                previousMarker = null;
                CommunicationUtility.hideSlideUpPanel((MainActivity)getActivity());
                Location loc = new Location(LocationManager.PASSIVE_PROVIDER);
                loc.setLatitude(latLng.latitude);
                loc.setLongitude(latLng.longitude);
                ArrayList<Stop> closestStations = getStationsAround(loc);
                ArrayList<Marker> markers = new ArrayList<>();
                if(closestStations != null){
                    try{
                        setMarkers(closestStations, markers, false);
                        CameraUpdate cu = getCameraUpdate(markers);
                        map.animateCamera(cu, 300, null);
                    }catch (Exception e){
                        e.printStackTrace();
                        Utility.makeSnackbar("Настъпи грешка", (MainActivity)getActivity());

                    }

                }else{
                    Utility.makeSnackbar("Няма спирки в близост до това място", (MainActivity)getActivity());
                }
            }
        });

    }

    public void getMap(){
        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                map = googleMap;
                setUpMap();
                mapView.onResume();
            }
        });
    }

    private ArrayList<Stop> getStationsAround(Location location){
        StationsLocator locator = new StationsLocator(location, 10, 1000, getContext());
        ArrayList<Stop> closestStations = locator.getClosestStationsFast();
        return closestStations;
    }

    public <T extends Stop>void showOnMap(ArrayList<T> stations){
        showOnMap(stations, false);
    }

    public <T extends Stop>void showOnMap(ArrayList<T> stations, boolean ordered){
        if(map != null && stations != null){
            previousMarker = null;
            map.clear();
            ArrayList<Marker> markers = new ArrayList<>();
            setMarkers(stations, markers, ordered);
            if(markers.size() != 0){
                map.animateCamera(getCameraUpdate(markers));
            }
        }
    }



    public void showPolyOnMap(String geo, String tr_type){
        if(map != null && geo != null && !"".equals(geo) && !geo.equals("null")){
            List<LatLng> decode = PolyUtil.decode(geo);
            int color = getColorByTrType(tr_type);
            map.addPolyline(new PolylineOptions().addAll(decode).color(color));
        }
    }

    public int getColorByTrType(String tr_type){
        switch (tr_type){
            case "0": return ContextCompat.getColor(getContext(), R.color.colorTram);
            case "1": return ContextCompat.getColor(getContext(), R.color.colorBus);
            case "2": return ContextCompat.getColor(getContext(), R.color.colorTrolley);
            default: return -1;
        }
    }

    @NonNull
    private CameraUpdate getCameraUpdate(ArrayList<Marker> markers) {
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (Marker marker : markers){
            builder.include(marker.getPosition());
        }
        LatLngBounds bounds = builder.build();
        int padding = 100;
        return CameraUpdateFactory.newLatLngBounds(bounds, padding);
    }

    private<T extends Stop> void setMarkers(ArrayList<T> closestStations, ArrayList<Marker> markers, boolean ordered) {
        int i = 0;
        for(Stop station : closestStations){
            String latitude = station.getLatitude();
            String longtitude = station.getLongitude();

            if(latitude != null && longtitude != null && !"".equals(latitude) && !"".equals(longtitude)){
                double lat = Float.parseFloat(latitude);
                double lon = Float.parseFloat(longtitude);
                MarkerOptions options = new MarkerOptions();

                options.position(new LatLng(lat, lon));

                ArrayList<Integer> lineTypesOnStop = station.getLineTypes();
                BitmapDescriptor correctImageForStop = getCorrectBitmapDescriptorForStop(lineTypesOnStop);

                Marker marker = map.addMarker(options);
                if(ordered && i == 0){
                    marker.setAlpha(0.5f);
                }

                marker.setTag(station);
                marker.setIcon(correctImageForStop);

                markers.add(marker);
                i++;
            }

        }
    }

    public Bitmap resizeMapIcons(int id,int width, int height){
        Bitmap imageBitmap = BitmapFactory.decodeResource(getContext().getResources(), id);
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(imageBitmap, width, height, false);
        return resizedBitmap;
    }


    private void initPinBitmaps(){
        Resources r = getResources();
        int height = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50, r.getDisplayMetrics());
        int width = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 36, r.getDisplayMetrics());
        pinBus = resizeMapIcons(R.drawable.pin_bus, width, height);
        pinTram = resizeMapIcons(R.drawable.pin_tram, width, height);
        pinTrolley = resizeMapIcons(R.drawable.pin_trolley, width, height);
        pinBusTram = resizeMapIcons(R.drawable.pin_bus_tram, width, height);
        pinTrolleyBus = resizeMapIcons(R.drawable.pin_bus_trolley, width, height);
        pinTrolleyTram = resizeMapIcons(R.drawable.pin_trolley_tram, width, height);
        pinBusTramTrolley = resizeMapIcons(R.drawable.pin_bus_trolley_tram, width, height);
    }

    private void initPinBitmapDescriptors(){
        try {
            pinBusDescriptor = BitmapDescriptorFactory.fromBitmap(pinBus);
            pinTramDescriptor = BitmapDescriptorFactory.fromBitmap(pinTram);
            pinTrolleyDescriptor = BitmapDescriptorFactory.fromBitmap(pinTrolley);
            pinBusTramDescriptor = BitmapDescriptorFactory.fromBitmap(pinBusTram);
            pinTrolleyBusDescriptor = BitmapDescriptorFactory.fromBitmap(pinTrolleyBus);
            pinTrolleyTramDescriptor = BitmapDescriptorFactory.fromBitmap(pinTrolleyTram);
            pinBusTramTrolleyDescriptor = BitmapDescriptorFactory.fromBitmap(pinBusTramTrolley);
        }catch(NullPointerException e){
            e.printStackTrace();
        }
    }

    private Bitmap getCorrectImageForStop(ArrayList<Integer> lineTypes){

        if(lineTypes == null){
            return null;
        }
        if(lineTypes.size() == 1){
            if(lineTypes.contains(Constants.TRAM_ID)){
                return pinTram;
            }else if (lineTypes.contains(Constants.BUS_ID)){
                return pinBus;
            }else if (lineTypes.contains(Constants.TROLLEY_ID)){
                return pinTrolley;
            }else if (lineTypes.contains(Constants.NIGHT_BUS_ID)){
                return pinBus;
            }
        }else if(lineTypes.size() == 2){
            if(lineTypes.contains(Constants.TRAM_ID) && lineTypes.contains(Constants.BUS_ID)){
                return pinBusTram;
            }else if(lineTypes.contains(Constants.TRAM_ID) && lineTypes.contains(Constants.TROLLEY_ID)){
                return pinTrolleyTram;
            }else if(lineTypes.contains(Constants.BUS_ID) && lineTypes.contains(Constants.TROLLEY_ID)){
                return pinTrolleyBus;
            }else if(lineTypes.contains(Constants.NIGHT_BUS_ID) && lineTypes.contains(Constants.BUS_ID)){
                return pinBus;
            }else if(lineTypes.contains(Constants.NIGHT_BUS_ID) && lineTypes.contains(Constants.TROLLEY_ID)) {
                return pinTrolley;
            }else if(lineTypes.contains(Constants.NIGHT_BUS_ID) && lineTypes.contains(Constants.TRAM_ID)) {
                return pinTram;
            }
        }else if(lineTypes.size() == 3){
            //if size is 3 than all types of transportation are present
            return pinBusTramTrolley;
        }else{
            return pinBusTramTrolley;
        }
        return null;
    }

    private BitmapDescriptor getCorrectBitmapDescriptorForStop(ArrayList<Integer> lineTypes){
        if(lineTypes == null){
            return null;
        }
        if(lineTypes.size() == 1){
            if(lineTypes.contains(Constants.TRAM_ID)){
                return pinTramDescriptor;
            }else if (lineTypes.contains(Constants.BUS_ID)){
                return pinBusDescriptor;
            }else if (lineTypes.contains(Constants.TROLLEY_ID)){
                return pinTrolleyDescriptor;
            }else if (lineTypes.contains(Constants.NIGHT_BUS_ID)){
                return pinBusDescriptor;
            }
        }else if(lineTypes.size() == 2){
            if(lineTypes.contains(Constants.TRAM_ID) && lineTypes.contains(Constants.BUS_ID)){
                return pinBusTramDescriptor;
            }else if(lineTypes.contains(Constants.TRAM_ID) && lineTypes.contains(Constants.TROLLEY_ID)){
                return pinTrolleyTramDescriptor;
            }else if(lineTypes.contains(Constants.BUS_ID) && lineTypes.contains(Constants.TROLLEY_ID)){
                return pinTrolleyBusDescriptor;
            }else if(lineTypes.contains(Constants.NIGHT_BUS_ID) && lineTypes.contains(Constants.BUS_ID)){
                return pinBusDescriptor;
            }else if(lineTypes.contains(Constants.NIGHT_BUS_ID) && lineTypes.contains(Constants.TROLLEY_ID)) {
                return pinTrolleyDescriptor;
            }else if(lineTypes.contains(Constants.NIGHT_BUS_ID) && lineTypes.contains(Constants.TRAM_ID)) {
                return pinTramDescriptor;
            }
        }else if(lineTypes.size() == 3){
            //if size is 3 than all types of transportation are present
            return pinBusTramTrolleyDescriptor;
        }else{
            return pinBusTramTrolleyDescriptor;
        }
        return null;
    }

    @Override
    public void onResume() {
        mapView.onResume();
        super.onResume();
        ((MainActivity)getActivity()).hideSoftKeyboad();
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
