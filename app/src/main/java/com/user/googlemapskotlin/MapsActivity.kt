package com.user.googlemapskotlin

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.user.googlemapskotlin.databinding.ActivityMapsBinding
import java.lang.Exception
import java.util.*

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private lateinit var locationManager:LocationManager
    private lateinit var locationListener:LocationListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.setOnMapClickListener(dinleyici)


        locationManager=getSystemService(Context.LOCATION_SERVICE) as LocationManager

        locationListener=object :LocationListener{  //Arayüz
            override fun onLocationChanged(location: Location) {
                //lokasyon yada konum değişince yapılacak işlemler.
                mMap.clear()
                val guncelKonum=LatLng(location.latitude,location.altitude)
                mMap.addMarker(MarkerOptions().position(guncelKonum).title(""))
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(guncelKonum,13f))

                //Güncel enlem ve boylamdan güncel adres

                val geocoer=Geocoder(this@MapsActivity, Locale.getDefault())
                try {
                    val adreslistesi=geocoer.getFromLocation(location.latitude,location.altitude,1)
                    if(adreslistesi.size>0){

                    }

                }catch (e:Exception){
                    e.printStackTrace()
                }
            }

        }

        if(ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION) !=PackageManager.PERMISSION_GRANTED){
            //İzin verilmemiş
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),1)

        }else{
            //İzin verilmiş
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,2,2f,locationListener)
            val sonKonum=locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
            if(sonKonum !=null){
                val sonKonumLatLng=LatLng(sonKonum.latitude,sonKonum.altitude)
                mMap.addMarker(MarkerOptions().position(sonKonumLatLng).title("Son Konum"))
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sonKonumLatLng,13f))
            }
        }

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if(requestCode==1){
            if(grantResults.size>0){
                if(ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED){
                    //izin verildi
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,1,1f,locationListener)
                }
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }
    val dinleyici=object: GoogleMap.OnMapClickListener {
        override fun onMapClick(p0: LatLng?) {
            mMap.clear()
            val geocoder=Geocoder(this@MapsActivity,Locale.getDefault())
            if(p0 !=null){
                var adres=""
                try {
                    val adresListesi=geocoder.getFromLocation(p0.latitude,p0.longitude,1)
                    if(adresListesi.size>0){
                        if(adresListesi.get(0).thoroughfare!=null){
                            adres += adresListesi.get(0).thoroughfare

                            if(adresListesi.get(0).subThoroughfare!=null){
                                adres +=adresListesi.get(0).subThoroughfare
                            }
                        }


                    }

                }catch (e:Exception){
                    e.printStackTrace()
                }

                mMap.addMarker(MarkerOptions().position(p0).title(adres))
            }
        }

    }
}