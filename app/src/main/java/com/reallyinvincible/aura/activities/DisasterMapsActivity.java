package com.reallyinvincible.aura.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;

import android.os.Bundle;
import android.view.View;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.reallyinvincible.aura.AddDisasterBottomFragment;
import com.reallyinvincible.aura.DialogueControlInterface;
import com.reallyinvincible.aura.models.Information;
import com.reallyinvincible.aura.R;
import com.reallyinvincible.aura.utils.UtilConstants;

import java.util.Arrays;

public class DisasterMapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private FirebaseDatabase mDatabase;
    private DatabaseReference mDatabaseReference;
    private ChildEventListener mChildEventListener;
    AddDisasterBottomFragment addDisasterBottomFragment;
    private static DialogueControlInterface dialogueControlInterface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_disaster_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        mDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mDatabase.getReference().child("DisasterInformation");
        addDisasterBottomFragment = new AddDisasterBottomFragment();

        mChildEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Information disaster = dataSnapshot.getValue(Information.class);
                String arr[] = UtilConstants.arr;
                float color[] = UtilConstants.color;
                BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory
                        .defaultMarker(color[Arrays.asList(arr).indexOf(disaster.getAlertType())]);
                LatLng latLng = new LatLng(disaster.getLatitude(), disaster.getLongitude());
                mMap.addMarker(new MarkerOptions()
                .position(latLng)
                .icon(bitmapDescriptor).title(disaster.getAlertType()));
                moveCamera(latLng, 15f);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };

        dialogueControlInterface = new DialogueControlInterface() {
            @Override
            public void dismiss() {
                dismissDialogue();
            }
        };

        findViewById(R.id.fab_add_disaster).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDialogue();
            }
        });

    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mDatabaseReference.addChildEventListener(mChildEventListener);
    }

    private void moveCamera(LatLng latLng, float zoom){
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
    }

    void openDialogue(){
        addDisasterBottomFragment.show(getSupportFragmentManager(), "AddDisaster");
    }

    void dismissDialogue(){
        addDisasterBottomFragment.dismiss();
    }

    public static DialogueControlInterface getDialogueControlInterface() {
        return dialogueControlInterface;
    }

}
