package com.oldster.swiftmove.app.fragment;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import com.akexorcist.googledirection.DirectionCallback;
import com.akexorcist.googledirection.GoogleDirection;
import com.akexorcist.googledirection.constant.Language;
import com.akexorcist.googledirection.constant.TransportMode;
import com.akexorcist.googledirection.constant.Unit;
import com.akexorcist.googledirection.model.Direction;
import com.akexorcist.googledirection.util.DirectionConverter;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.directions.route.Route;
import com.directions.route.RouteException;
import com.directions.route.RoutingListener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.inthecheesefactory.thecheeselibrary.manager.Contextor;
import com.oldster.swiftmove.app.Constants.Config;
import com.oldster.swiftmove.app.R;
import com.oldster.swiftmove.app.activity.DriverActivity;
import com.oldster.swiftmove.app.activity.MapFromActivity;
import com.oldster.swiftmove.app.activity.MapToActivity;
import com.oldster.swiftmove.app.databinding.FragmentMainBinding;
import com.oldster.swiftmove.app.manager.PositionMapManager;
import com.oldster.swiftmove.app.util.NotificationUtils;
import com.oldster.swiftmove.app.util.ShowDialogReviewDriver;
import com.oldster.swiftmove.app.util.ShowDialogStatusJob;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Callable;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;


public class MainFragment extends Fragment implements
        OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener,
        View.OnClickListener,
        DirectionCallback,
        RoutingListener {

    /********************
     * Variable
     ********************/

    private String TAG = MainFragment.class.getSimpleName();
    private FragmentMainBinding mBind;
    private static final int REQUEST_CODE_ASK_PERMISSIONS = 123;
    //Map
    private GoogleMap mMap;
    private GoogleApiClient googleApiClient;
    private Geocoder geocoder;
    //position from
    private double latitudeFrom = 0;
    private double longitudeFrom = 0;
    private String nameAddressFrom;

    //position to
    private double latitudeTo = 0;
    private double longitudeTo = 0;
    private String nameAddressTo;

    private LatLng origin;
    private LatLng destination;
    private Animation shake, up;
    private static int stateTypeCar = 0;

    private static final int[] COLORS = new int[]{R.color.colorPrimaryDark, R.color.colorPrimary, R.color.colorGreenBlue, R.color.colorAccent, R.color.colorStack};

    private LatLng start;
    private LatLng waypoint;
    private LatLng end;

    private List<Polyline> polylines;

    private BroadcastReceiver mRegistrationBroadcastReceiver;
    private Subscription subScription;

    /********************
     * Functions
     ********************/
    public MainFragment() {
        super();
    }

    @SuppressWarnings("unused")
    public static MainFragment newInstance() {
        MainFragment fragment = new MainFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init(savedInstanceState);

        if (savedInstanceState != null)
            onRestoreInstanceState(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mBind = DataBindingUtil.inflate(inflater, R.layout.fragment_main, container, false);
        View rootView = mBind.getRoot();
        initInstances(rootView, savedInstanceState);
        return rootView;
    }

    private void init(Bundle savedInstanceState) {
        // Init Fragment level's variable(s) here

        latitudeFrom = PositionMapManager.getInstance().getLatitudeFrom();
        longitudeFrom = PositionMapManager.getInstance().getLongitudeFrom();
        nameAddressFrom = PositionMapManager.getInstance().getNameAddressFrom();
        latitudeTo = PositionMapManager.getInstance().getLatitudeTo();
        longitudeTo = PositionMapManager.getInstance().getLongitudeTo();
        nameAddressTo = PositionMapManager.getInstance().getNameAddressTo();

        googleApiClient = new GoogleApiClient.Builder(getContext())
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        polylines = new ArrayList<>();
        geocoder = new Geocoder(getContext(), Locale.getDefault());


        origin = new LatLng(latitudeFrom, longitudeFrom);
        destination = new LatLng(latitudeTo, longitudeTo);

    }


    @SuppressWarnings("UnusedParameters")
    private void initInstances(View rootView, Bundle savedInstanceState) {

        //set animate gif active navigation tab
        shake = AnimationUtils.loadAnimation(getContext(), R.anim.anim_shack);
        up = AnimationUtils.loadAnimation(getContext(), R.anim.type_car_show);
        Glide.with(getActivity())
                .load(R.drawable.active_sel)
                .asGif()
                .crossFade()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(mBind.subRing);

        //handle click tab select type car
        mBind.frameLayoutTruck.setOnClickListener(this);
        mBind.frameLayoutEcoCar.setOnClickListener(this);
        mBind.frameLayoutPickup.setOnClickListener(this);

        //handle click button search driver
        mBind.btnSearchDriver.setOnClickListener(this);

        //handle click button get position
        mBind.btnLocationFrom.setOnClickListener(this); //position from
        mBind.btnLocationTo.setOnClickListener(this); //position to
        mBind.icPlus.setOnClickListener(this); //position to

        String type = PositionMapManager.getInstance().getTypeCar();
        if (type != null) {
            switch (type) {
                case "EcoCar":
                    setFocusEcoCar();
                    setUnFocusPickup();
                    setUnFocusTruck();
                    break;
                case "Pickup":
                    setFocusPickup();
                    setUnFocusTruck();
                    setUnFocusEcoCar();
                    break;
                case "Truck":
                    setFocusTruck();
                    setUnFocusPickup();
                    setUnFocusEcoCar();
                    break;
            }
        }
        if (nameAddressFrom != null && !nameAddressFrom.equals("")) {
            mBind.btnLocationFrom.setText(nameAddressFrom);
        }

        if (nameAddressTo != null && !nameAddressTo.equals("")) {
            mBind.icPlus.setIconResource(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_cancel, null));
            mBind.btnLocationTo.setText(nameAddressTo);

        } else {
            startAnimationPositionTo();
        }

        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            public String jid;
            public String did;
            public String status;
            public JSONObject data;
            public String payload;
            public String title;
            public String message;

            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(Config.PUSH_NOTIFICATION)) {
                    try {
                        title = intent.getStringExtra("title"); //check
                        message = intent.getStringExtra("message");//check
                        payload = intent.getStringExtra("payload");//check
                        data = new JSONObject(payload);//check
                        status = data.getString("status");//check
                        if (status.equals("end_job")) {
                            did = data.getString("did");
                            jid = data.getString("jid");
                        }
                    } catch (JSONException e) {
                        Log.e(TAG, "Json Exception: " + e.getMessage());
                    } catch (Exception e) {
                        Log.e(TAG, "Exception: " + e.getMessage());
                    }

                    //Handle Code Here!!
                    if (!status.equals("end_job")) {
                        new ShowDialogStatusJob(getContext(), title, message);
                    } else {
                        new ShowDialogReviewDriver(getContext(), did, jid);
                    }
                }

            }
        };
        //set map
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        if (nameAddressFrom != null && !nameAddressFrom.equals("")) {
            if (nameAddressTo != null && !nameAddressTo.equals("")) {
                stateTypeCar++;
            }
        }
        if (stateTypeCar > 1) {
            mBind.tabBtnTypeCar.setVisibility(View.VISIBLE);
        }else {
            mBind.tabBtnTypeCar.setVisibility(View.GONE);
        }

    }

    @Override
    public void onStart() {
        super.onStart();

        googleApiClient.connect();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (googleApiClient != null && googleApiClient.isConnected()) {
            googleApiClient.disconnect();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.PUSH_NOTIFICATION));

        // clear the notification area when the app is opened
        NotificationUtils.clearNotifications(Contextor.getInstance().getContext());
    }

    @Override
    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(mRegistrationBroadcastReceiver);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unsubscribe();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // Save Instance State here
    }

    @SuppressWarnings("UnusedParameters")
    private void onRestoreInstanceState(Bundle savedInstanceState) {
        // Restore Instance State here
    }

    private void startAnimationPositionFrom() {

        //SET Animation shake ButtonTo
        mBind.btnLocationFrom.startAnimation(shake);
        mBind.icFr.startAnimation(shake);
    }

    private void startAnimationPositionTo() {
        //SET Animation shake ButtonTo
        mBind.btnLocationTo.startAnimation(shake);
        mBind.icTo.startAnimation(shake);
        mBind.icToAdd.startAnimation(shake);
    }

    private void startAnimationTapTypeCar() {
        //SET Animation shake ButtonTo
        mBind.tabBtnTypeCar.startAnimation(shake);
    }


    public void getCurrentLocation() {
        //CHECK PERMISSION ACCESS_FINE_LOCATION
        if (ContextCompat.checkSelfPermission(getContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            //Request permission ACCESS_FINE_LOCATION
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_CODE_ASK_PERMISSIONS);
            return;
        }

        //Get current location
        LocationAvailability locationAvailability = LocationServices.FusedLocationApi.getLocationAvailability(googleApiClient);
        if (locationAvailability.isLocationAvailable()) {
            LocationRequest locationRequest = new LocationRequest()
                    .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_PERMISSIONS:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission Granted
                    getCurrentLocation();
                } else {
                    // Permission Denied
                    Toast.makeText(getContext(), "ACCESS_FINE_LOCATION Denied", Toast.LENGTH_SHORT).show();
                }
                break;
            case 5555:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                }else {
                    Toast.makeText(getContext(), "ACCESS_FINE_LOCATION Denied", Toast.LENGTH_SHORT).show();
                }
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private void refreshMap() {
        mMap.clear();
        LatLng latlng = new LatLng(latitudeFrom, longitudeFrom);
        MarkerOptions markFrom = new MarkerOptions().position(new LatLng(latitudeFrom, longitudeFrom)).title(getString(R.string.marker_title_from));
        markFrom.icon(BitmapDescriptorFactory.fromResource(R.drawable.mark_form));
        mMap.addMarker(markFrom);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom((latlng), 12));
        //set first text name position from
        setNameAddress();
    }


    private String getNameAddress() throws IOException {
        String nameAddress = "";
        List<Address> addresses = geocoder.getFromLocation(latitudeFrom, longitudeFrom, 1);
        for (int i = 0; i < addresses.get(0).getMaxAddressLineIndex(); i++) {

            if ((i + 1) < addresses.get(0).getMaxAddressLineIndex()) {
                nameAddress = nameAddress + addresses.get(0).getAddressLine(i) + ", ";
            } else {
                nameAddress = nameAddress + addresses.get(0).getAddressLine(i);
            }
        }
        PositionMapManager.getInstance().setProvinceFrom(addresses.get(0).getAdminArea());
        return nameAddress;
    }

    private void unsubscribe() {
        if (subScription != null && !subScription.isUnsubscribed()) {
            subScription.unsubscribe();
        }
    }

    private void setNameAddress() {
        Observable<String> listObservable = Observable.fromCallable(new Callable<String>() {
            @Override
            public String call() throws Exception {
                return getNameAddress();
            }
        });
        subScription = listObservable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String s) {
                        mBind.btnLocationFrom.setText(s);
                        PositionMapManager.getInstance().setNameAddressFrom(s);
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        if (!isInternetAvailable()) {
                            displayPromptForEnableNetwork();
                        }
                    }
                });

    }

    //METHOD OPEN SETTING
    private void displayPromptForEnableNetwork() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        final String action = Settings.ACTION_SETTINGS;
        final String message = getString(R.string.dialog_network_message);

        builder.setMessage(message)
                .setNegativeButton(getString(R.string.dialog_network_btn_negative), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                })
                .setPositiveButton(getString(R.string.dialog_network_btn_positive),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                startActivity(new Intent(action));
                                dialog.dismiss();
                            }
                        });
        builder.create().show();

    }

    //Checking Internet Available
    public boolean isInternetAvailable() {
        String netAddress;
        try {
            netAddress = new NetTask().execute("www.google.com").get();
            return !netAddress.equals("");
        } catch (Exception e1) {
            return false;
        }
    }

    public class NetTask extends AsyncTask<String, Integer, String> {
        @Override
        protected String doInBackground(String... params) {
            InetAddress addr;
            try {
                addr = InetAddress.getByName(params[0]);
            } catch (UnknownHostException e) {
                return "";
            }
            return addr.getHostAddress();
        }
    }

    private void animateCameraDirection() {
        LatLngBounds bounds = new LatLngBounds.Builder()
                .include(new LatLng(latitudeFrom, longitudeFrom))
                .include(new LatLng(latitudeTo, longitudeTo))
                .build();
        mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 120));

    }

    private float calculateDistances() {
        float[] result = new float[3];
        Location.distanceBetween(latitudeFrom, longitudeFrom, latitudeTo, longitudeTo, result);
        return result[0] / 1000;
    }

    /********************
     * Listener Zone
     ********************/

    @Override
    public void onClick(View v) {

        if (v == mBind.btnLocationFrom) {
            startActivity(new Intent(getContext(), MapFromActivity.class));
            getActivity().overridePendingTransition(R.anim.from_right, R.anim.to_left);
        }

        if (v == mBind.btnLocationTo) {
            startActivity(new Intent(getContext(), MapToActivity.class));
            getActivity().overridePendingTransition(R.anim.from_right, R.anim.to_left);

        }


        if (v == mBind.frameLayoutTruck) {
            setFocusTruck();
            setUnFocusPickup();
            setUnFocusEcoCar();
            PositionMapManager.getInstance().setTypeCar("Truck");
        }

        if (v == mBind.frameLayoutPickup) {
            setFocusPickup();
            setUnFocusEcoCar();
            setUnFocusTruck();
            PositionMapManager.getInstance().setTypeCar("Pickup");
        }

        if (v == mBind.frameLayoutEcoCar) {
            setFocusEcoCar();
            setUnFocusPickup();
            setUnFocusTruck();
            PositionMapManager.getInstance().setTypeCar("EcoCar");
        }

        if (v == mBind.btnSearchDriver) {
            String typeCar = PositionMapManager.getInstance().getTypeCar();
            String addressNameFrom = PositionMapManager.getInstance().getNameAddressFrom();
            String addressNameTo = PositionMapManager.getInstance().getNameAddressTo();
            PositionMapManager.getInstance().setDistance(calculateDistances());
            if (latitudeFrom != 0 && longitudeFrom != 0 && addressNameFrom != null && !addressNameFrom.equals("")) {
                if (latitudeTo != 0 && longitudeTo != 0 && addressNameTo != null && !addressNameTo.equals("")) {
                    if (typeCar != null && !typeCar.equals("")) {
                        startActivity(new Intent(getContext(), DriverActivity.class));
                        getActivity().overridePendingTransition(R.anim.from_right, R.anim.to_left);
                    } else {
                        startAnimationTapTypeCar();
                    }
                } else {
                    mBind.btnLocationTo.setFocusable(true);
                    mBind.btnLocationTo.setFocusableInTouchMode(true);
                    mBind.btnLocationTo.requestFocus();
                    mBind.btnLocationTo.setError("ระบุตำแหน่ง");
                    startAnimationPositionTo();
                }
            } else {
                startAnimationPositionFrom();
            }
        }

        if (v == mBind.icPlus) {
            if (PositionMapManager.getInstance().getNameAddressTo() == null) {
                startActivity(new Intent(getContext(), MapToActivity.class));
                getActivity().overridePendingTransition(R.anim.from_right, R.anim.to_left);
            } else {
                refreshMap();
                mBind.icPlus.setIconResource(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_center_direction, null));
                PositionMapManager.getInstance().setNameAddressTo(null);
                PositionMapManager.getInstance().setLongitudeTo(0);
                PositionMapManager.getInstance().setLatitudeTo(0);
                mBind.btnLocationTo.setText(getString(R.string.click_for_select_route_to));
            }
        }


    }

    private void setFocusTruck() {
        mBind.btnTruck.setImageResource(R.drawable.ic_truck_active);
        mBind.tvTruck.setTextColor(ContextCompat.getColor(getContext(), R.color.newColorBlueNormal));
    }

    private void setFocusPickup() {
        mBind.btnPickup.setImageResource(R.drawable.ic_pickup_active);
        mBind.tvPickup.setTextColor(ContextCompat.getColor(getContext(), R.color.newColorBlueNormal));
    }

    private void setFocusEcoCar() {
        mBind.btnEcoCar.setImageResource(R.drawable.ic_ecocar_active);
        mBind.tvEcoCar.setTextColor(ContextCompat.getColor(getContext(), R.color.newColorBlueNormal));
    }

    private void setUnFocusTruck() {
        mBind.btnTruck.setImageResource(R.drawable.ic_truck);
        mBind.tvTruck.setTextColor(ContextCompat.getColor(getContext(), R.color.colorText));
    }

    private void setUnFocusPickup() {
        mBind.btnPickup.setImageResource(R.drawable.ic_pickup);
        mBind.tvPickup.setTextColor(ContextCompat.getColor(getContext(), R.color.colorText));
    }

    private void setUnFocusEcoCar() {
        mBind.btnEcoCar.setImageResource(R.drawable.ic_ecocar);
        mBind.tvEcoCar.setTextColor(ContextCompat.getColor(getContext(), R.color.colorText));
    }

    private void showAnimateTypeCar() {
        mBind.tabBtnTypeCar.startAnimation(up);
        mBind.tabBtnTypeCar.setVisibility(View.VISIBLE);
    }

    private void requestDirection() {
        final String serverKey = "AIzaSyCPIc8hnlJy3VYV-IO8aIVatPUJVaNtw3Q";
        GoogleDirection.withServerKey(serverKey)
                .from(origin)
                .to(destination)
                .transportMode(TransportMode.DRIVING)
                .language(Language.THAI)
                .unit(Unit.METRIC)
                .execute(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.clear();
       /* if (ActivityCompat.checkSelfPermission(getContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) !=
                        PackageManager.PERMISSION_GRANTED) {

            return;
        }
        mMap.setMyLocationEnabled(true);
*/
        //Marker And Zoom
        if (latitudeFrom != 0 && longitudeFrom != 0) {
            MarkerOptions markFrom = new MarkerOptions().position(origin).title(getString(R.string.marker_title_from));
            markFrom.icon(BitmapDescriptorFactory.fromResource(R.drawable.mark_form));
            mMap.addMarker(markFrom);
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom((origin), 12));
        }
        if (latitudeFrom == 0 && longitudeFrom == 0) {
            if (latitudeTo != 0 && longitudeTo != 0) {
                MarkerOptions markTo = new MarkerOptions().position(destination).title(getString(R.string.marker_title_to));
                markTo.icon(BitmapDescriptorFactory.fromResource(R.drawable.mark_to));
                mMap.addMarker(markTo);
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom((destination), 12));
            }
        }

        //Check coordinate position for direction route
        if ((latitudeFrom != 0 && longitudeFrom != 0) && (latitudeTo != 0 && longitudeTo != 0)) {
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(origin, 12));
            requestDirection();


        }


    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (nameAddressFrom == null) {
            getCurrentLocation();
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        latitudeFrom = location.getLatitude();
        longitudeFrom = location.getLongitude();
        Log.e(TAG, "lat:" + latitudeFrom + " lng:" + longitudeFrom);
        refreshMap();
        PositionMapManager.getInstance().setLatitudeFrom(location.getLatitude());
        PositionMapManager.getInstance().setLongitudeFrom(location.getLongitude());
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onDirectionSuccess(Direction direction, String rawBody) {
        if (direction.isOK()) {
            MarkerOptions markFrom = new MarkerOptions().position(origin).title(getString(R.string.marker_title_from));
            markFrom.icon(BitmapDescriptorFactory.fromResource(R.drawable.mark_form));
            MarkerOptions markerTo = new MarkerOptions().position(destination).title(getString(R.string.marker_title_to));
            markerTo.icon(BitmapDescriptorFactory.fromResource(R.drawable.mark_to));

            mMap.addMarker(markFrom);
            mMap.addMarker(markerTo);

            ArrayList<LatLng> directionPositionList = direction.getRouteList().get(0).getLegList().get(0).getDirectionPoint();
            mMap.addPolyline(DirectionConverter.createPolyline(getContext(), directionPositionList, 2, Color.RED));

            animateCameraDirection();
            if (stateTypeCar == 1) {
                showAnimateTypeCar();
            }
        }
    }


    @Override
    public void onDirectionFailure(Throwable t) {

    }

    @Override
    public void onRoutingFailure(RouteException e) {

    }

    @Override
    public void onRoutingStart() {

    }

    @Override
    public void onRoutingSuccess(ArrayList<Route> route, int shortestRouteIndex) {

        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(start, 16));
        if (polylines.size() > 0) {
            for (Polyline poly : polylines) {
                poly.remove();
            }
        }

        polylines = new ArrayList<>();
        //add route(s) to the map.
        for (int i = 0; i < route.size(); i++) {

            //In case of more than 5 alternative routes
            int colorIndex = i % COLORS.length;

            PolylineOptions polyOptions = new PolylineOptions();
            polyOptions.color(ContextCompat.getColor(getContext(), COLORS[colorIndex]));
            polyOptions.width(10 + i * 3);
            polyOptions.addAll(route.get(i).getPoints());
            Polyline polyline = mMap.addPolyline(polyOptions);
            polylines.add(polyline);

            Toast.makeText(getContext(), "Route " + (i + 1) + ": distance - " + route.get(i).getDistanceValue() + ": duration - " + route.get(i).getDurationValue(), Toast.LENGTH_SHORT).show();
        }

        MarkerOptions markFrom = new MarkerOptions().position(start).title(getString(R.string.marker_title_from));
        markFrom.icon(BitmapDescriptorFactory.fromResource(R.drawable.mark_form));
        MarkerOptions markerWaypoint = new MarkerOptions().position(waypoint).title(getString(R.string.marker_title_to));
        markerWaypoint.icon(BitmapDescriptorFactory.fromResource(R.drawable.mark_to));
        MarkerOptions markerTo = new MarkerOptions().position(end).title(getString(R.string.marker_title_to));
        markerTo.icon(BitmapDescriptorFactory.fromResource(R.drawable.mark_to));

        mMap.addMarker(markFrom);
        mMap.addMarker(markerWaypoint);
        mMap.addMarker(markerTo);
    }

    @Override
    public void onRoutingCancelled() {

    }

    /********************
     * Inner Class
     ********************/

}
