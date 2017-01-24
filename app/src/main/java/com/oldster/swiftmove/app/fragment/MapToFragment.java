package com.oldster.swiftmove.app.fragment;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.inthecheesefactory.thecheeselibrary.manager.Contextor;
import com.oldster.swiftmove.app.Constants.Config;
import com.oldster.swiftmove.app.R;
import com.oldster.swiftmove.app.activity.MainActivity;
import com.oldster.swiftmove.app.databinding.FragmentMapToBinding;
import com.oldster.swiftmove.app.manager.PositionMapManager;
import com.oldster.swiftmove.app.util.NotificationUtils;
import com.oldster.swiftmove.app.util.ShowDialogReviewDriver;
import com.oldster.swiftmove.app.util.ShowDialogStatusJob;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Callable;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.MODE_PRIVATE;

public class MapToFragment extends Fragment implements
        OnMapReadyCallback,
        GoogleMap.OnCameraIdleListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener,
        GoogleMap.OnCameraMoveStartedListener, View.OnClickListener {


    /********************
     * Variable
     ********************/
    private FragmentMapToBinding mBind;

    private String TAG = MapToFragment.class.getSimpleName();
    private static final int CODE_GET_CURRENT = 1111;
    private static final int CODE_ENABLE_MY_LOCATION = 2222;
    private int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;

    private GoogleMap mMap;
    private GoogleApiClient googleApiClient;
    private Geocoder geocoder;
    private double latitude;
    private double longitude;
    private String nameAddress;

    private int countChange = 1;

    private boolean isAsset = true;
    private boolean isProcessGetNameAddress;
    private boolean isSearchPlace;
    private Subscription subScription;

    private BroadcastReceiver mRegistrationBroadcastReceiver;
    /********************
     * Functions
     ********************/
    public MapToFragment() {
        super();
    }

    @SuppressWarnings("unused")
    public static MapToFragment newInstance() {
        MapToFragment fragment = new MapToFragment();
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
        mBind = DataBindingUtil.inflate(inflater, R.layout.fragment_map_to, container, false);
        View rootView = mBind.getRoot();
        initInstances(rootView, savedInstanceState);
        return rootView;
    }

    private void init(Bundle savedInstanceState) {
        // Init Fragment level's variable(s) here
        googleApiClient = new GoogleApiClient.Builder(getContext())
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        geocoder = new Geocoder(getContext(), Locale.getDefault());
    }

    @SuppressWarnings("UnusedParameters")
    private void initInstances(View rootView, Bundle savedInstanceState) {
        // Init 'View' instance(s) with rootView.findViewById here
        if (isFirstTime()) {
            mBind.topLayout.setVisibility(View.INVISIBLE);
        }
        mBind.btnConfirm.setOnClickListener(this);
        mBind.btnSearchMap.setOnClickListener(this);
        mBind.imageBtnHomeUp.setOnClickListener(this);
        mBind.btnGetCurrent.setOnClickListener(this);

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
                        if (status.equals("end_job")){
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

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    public void getCurrentLocation() {
        if (ContextCompat.checkSelfPermission(getContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    CODE_GET_CURRENT);
            return;
        }

        LocationAvailability locationAvailability = LocationServices.FusedLocationApi.getLocationAvailability(googleApiClient);
        if (locationAvailability.isLocationAvailable()) {
            LocationRequest locationRequest = new LocationRequest()
                    .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
        }
    }

    private void enableMyLocation() {
        if (ContextCompat.checkSelfPermission(getContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            //ถ้ายังไม่ได้ให้สิทธิ์ ร้องขอสิทธิ์
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    CODE_ENABLE_MY_LOCATION);
        } else if (mMap != null) {
            //มีสิทธ์
            mMap.setMyLocationEnabled(true);
        }
    }

    private String getNameAddress() throws IOException {
        if (latitude != 0 && longitude != 0) {
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);

            //ถ้าค้นหาสถานที่โดยช่องค้นหาไม่ต้อง ดึงชื่อสถานที่ผ่าน geocoder
            if (!isSearchPlace) {
                nameAddress = "";
                for (int i = 0; i < addresses.get(0).getMaxAddressLineIndex(); i++) {
                    if ((i + 1) < addresses.get(0).getMaxAddressLineIndex()) {
                        nameAddress = nameAddress + addresses.get(0).getAddressLine(i) + ", ";
                    } else {
                        nameAddress = nameAddress + addresses.get(0).getAddressLine(i);
                    }
                }
            }
        }
        return nameAddress;
    }
    private void unsubscribe() {
        if (subScription != null && !subScription.isUnsubscribed()) {
            subScription.unsubscribe();
        }
    }
    private void setTextPlaceByReactiveX() {
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
                    public void call(String text) {
                        //ถ้าค้นหาสถานที่โดยช่องค้นหาไม่ต้อง ไม่ต้อง set ค่า view
                        if (!isSearchPlace) {
                            isProcessGetNameAddress = false;
                            mBind.btnSearchMap.setText(text);
                            mBind.progressBar.setVisibility(View.GONE);
                            mBind.btnConfirm.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.newColorBlueNormal));
                        } else {
                            isProcessGetNameAddress = false;
                        }

                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        if (isAsset) {
                            if (!isInternetAvailable()) {
                                displayPromptForEnableNetwork();
                            }
                            isAsset = false;
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

    //check first visible instruction
    private boolean isFirstTime() {
        SharedPreferences preferences = getActivity().getPreferences(MODE_PRIVATE);
        boolean ranBefore = preferences.getBoolean("RanBefore", false);
        if (!ranBefore) {

            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean("RanBefore", true);
            editor.apply();
            mBind.topLayout.setVisibility(View.VISIBLE);
            mBind.topLayout.setOnTouchListener(new View.OnTouchListener() {

                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    mBind.topLayout.setVisibility(View.INVISIBLE);
                    return false;
                }

            });
        }
        return ranBefore;

    }

    //Checking Internet Available
    private boolean isInternetAvailable() {
        String netAddress;
        try {
            netAddress = new NetTask().execute("www.google.com").get();
            return !netAddress.equals("");
        } catch (Exception e1) {
            return false;
        }
    }

    private class NetTask extends AsyncTask<String, Integer, String> {
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

    private void refreshMap() {
        mMap.clear();
        LatLng latlng = new LatLng(latitude, longitude);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom((latlng), 12));
    }

    @Override
    public void onStart() {
        super.onStart();
        googleApiClient.connect();
    }
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
    public void onStop() {
        super.onStop();
        if (googleApiClient != null && googleApiClient.isConnected()) {
            googleApiClient.disconnect();
        }
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case CODE_ENABLE_MY_LOCATION:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission Granted
                    enableMyLocation();
                } else {
                    // Permission Denied
                    Toast.makeText(getContext(), "ACCESS_FINE_LOCATION Denied", Toast.LENGTH_SHORT).show();
                }
                break;
            case CODE_GET_CURRENT:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getCurrentLocation();
                } else {
                    Toast.makeText(getContext(), "ACCESS_FINE_LOCATION Denied", Toast.LENGTH_SHORT).show();
                }
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = PlaceAutocomplete.getPlace(getContext(), data);
                latitude = place.getLatLng().latitude;
                longitude = place.getLatLng().longitude;
                nameAddress = String.valueOf(place.getName());
                mBind.btnSearchMap.setText(nameAddress);
                isSearchPlace = true;
                refreshMap();
            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(getContext(), data);
                Log.e("asdasdas", status.getStatusMessage());

            }
        }
    }

    /********************
     * Listener Zone
     ********************/

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnCameraIdleListener(this);
        mMap.setOnCameraMoveStartedListener(this);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        getCurrentLocation();
    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    }

    @Override
    public void onLocationChanged(Location location) {
        mMap.clear();
        double lat = PositionMapManager.getInstance().getLatitudeTo();
        double lng = PositionMapManager.getInstance().getLongitudeTo();
        if (lat != 0 && lng != 0) {
            latitude = lat;
            longitude = lng;
        } else {
            latitude = location.getLatitude();
            longitude = location.getLongitude();
        }
        if (countChange == 1) {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom((new LatLng(latitude, longitude)), 15));
            countChange++;
        } else {
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom((new LatLng(latitude, longitude)), 15));
        }
    }

    @Override
    public void onCameraIdle() {
        CameraPosition mPosition = mMap.getCameraPosition();
        latitude = mPosition.target.latitude;
        longitude = mPosition.target.longitude;
        if (latitude != 0 && longitude != 0) {
            setTextPlaceByReactiveX();
        }
    }

    @Override
    public void onCameraMoveStarted(int reason) {
        if (reason == GoogleMap.OnCameraMoveStartedListener.REASON_GESTURE) {
            mBind.btnSearchMap.setText("กำลังค้นหา...");
            mBind.progressBar.setVisibility(View.VISIBLE);
            mBind.btnConfirm.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.newColorBluePress));
            isProcessGetNameAddress = true;
            isSearchPlace = false;
        }
        if (reason == GoogleMap.OnCameraMoveStartedListener.REASON_DEVELOPER_ANIMATION) {
            if (!isSearchPlace) {
                mBind.btnSearchMap.setText("กำลังค้นหา...");
                mBind.progressBar.setVisibility(View.VISIBLE);
                mBind.btnConfirm.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.newColorBluePress));
                isProcessGetNameAddress = true;
            }
        }
    }

    @Override
    public void onClick(View v) {
        if (v == mBind.btnConfirm) {
            if (!isProcessGetNameAddress) {
                PositionMapManager.getInstance().setNameAddressTo(nameAddress);
                PositionMapManager.getInstance().setLatitudeTo(latitude);
                PositionMapManager.getInstance().setLongitudeTo(longitude);
                Intent intent = new Intent(getContext(), MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.from_left, R.anim.to_right);
            }
        }
        if (v == mBind.btnSearchMap) {
            try {
                Intent intent =
                        new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY)
                                .build(getActivity());
                startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);
            } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e) {
                Log.e("asdasd", e.getMessage());
            }
        }
        if (v == mBind.imageBtnHomeUp) {
            getActivity().finish();
            getActivity().overridePendingTransition(R.anim.from_left, R.anim.to_right);
        }
        if (v == mBind.btnGetCurrent) {
            getCurrentLocation();
        }
    }

    /********************
     * Inner Class
     ********************/

}
