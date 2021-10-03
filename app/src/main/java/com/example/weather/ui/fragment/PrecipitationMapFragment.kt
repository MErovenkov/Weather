package com.example.weather.ui.fragment

import android.content.Context
import android.graphics.*
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.weather.R
import com.example.weather.databinding.FragmentPrecipitationMapBinding
import com.example.weather.ui.map.tile.CustomTileProvider
import com.example.weather.utils.extensions.getFragmentComponent
import com.example.weather.utils.extensions.showToast
import com.example.weather.utils.resource.event.EventStatus
import com.example.weather.viewmodel.PrecipitationMapViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

class PrecipitationMapFragment: Fragment(), OnMapReadyCallback {

    companion object {
        private const val TILE_TYPE = "precipitation_new"

        private const val CITY_NAME_KEY = "cityName"
        private const val LON_KEY = "lon"
        private const val LAT_KEY = "lan"

        fun getNewBundle(cityName: String, lat: String, lon: String): Bundle {
            return Bundle().apply {
                putString(CITY_NAME_KEY, cityName)
                putString(LAT_KEY, lat)
                putString(LON_KEY, lon)
            }
        }
    }

    @Inject
    lateinit var precipitationMapViewModel: PrecipitationMapViewModel

    private lateinit var googleMap: GoogleMap
    private lateinit var binding: FragmentPrecipitationMapBinding

    private val customTileProvider: CustomTileProvider =
        object: CustomTileProvider() {
            override fun getTile(x: Int, y: Int, zoom: Int): Tile? {
                precipitationMapViewModel.getTileData(TILE_TYPE, zoom, x, y)
                return super.getTile(x, y, zoom)
            }
        }

    private var eventShown: Boolean = false

    override fun onAttach(context: Context) {
        super.onAttach(context)

        getFragmentComponent().inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPrecipitationMapBinding.inflate(layoutInflater)

        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tileDataCollector()
    }

    override fun onMapReady(googleMap: GoogleMap) {
        val cityPosition = LatLng(
            requireArguments().getString(LAT_KEY)!!.toDouble(),
            requireArguments().getString(LON_KEY)!!.toDouble()
        )

        this.googleMap = googleMap.apply {
            addMarker(MarkerOptions().position(cityPosition).title(requireArguments()
                .getString(CITY_NAME_KEY)))
            moveCamera(CameraUpdateFactory.newLatLng(cityPosition))
            animateCamera(CameraUpdateFactory.newLatLngZoom(cityPosition, 10f))
            addTileOverlay(TileOverlayOptions().tileProvider(customTileProvider))
        }
    }

    private fun tileDataCollector() {
        viewLifecycleOwner.lifecycleScope.launch {
            precipitationMapViewModel.getResource().collect { resource ->
                resource.getData()?.let { tileDataList ->
                    customTileProvider.updateTileDataList(tileDataList)
                }

                resource.getEvent()?.let { event ->
                    val eventStatus: Int? = event.getStatusIfNotHandled()

                    if (this@PrecipitationMapFragment.eventShown
                        && eventStatus == EventStatus.PRECIPITATION_TILE_ACCEPTED) {

                        this@PrecipitationMapFragment.eventShown = false
                    } else if (!this@PrecipitationMapFragment.eventShown
                        && eventStatus != EventStatus.PRECIPITATION_TILE_ACCEPTED) {

                        this@PrecipitationMapFragment.eventShown = true
                        eventStatus?.let { this@PrecipitationMapFragment.showToast(eventStatus)}
                    }
                }
            }
        }
    }
}