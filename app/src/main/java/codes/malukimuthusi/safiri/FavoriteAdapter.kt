package codes.malukimuthusi.safiri

import android.content.pm.PackageManager
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.app.ActivityCompat.shouldShowRequestPermissionRationale
import androidx.core.app.ActivityCompat.startActivityForResult
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import codes.malukimuthusi.safiri.databinding.FavouriteHeaderBinding
import codes.malukimuthusi.safiri.databinding.FavouriteListLayoutBinding
import codes.malukimuthusi.safiri.databinding.LocationSelectorBinding
import codes.malukimuthusi.safiri.models.Address
import com.google.android.material.snackbar.Snackbar
import com.mapbox.mapboxsdk.camera.CameraPosition
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.plugins.places.autocomplete.PlaceAutocomplete
import com.mapbox.mapboxsdk.plugins.places.autocomplete.model.PlaceOptions
import com.mapbox.mapboxsdk.plugins.places.picker.PlacePicker
import com.mapbox.mapboxsdk.plugins.places.picker.model.PlacePickerOptions

class FavoriteAdapter(private val mainActivity: HomeFragment) :
    ListAdapter<Address, RecyclerView.ViewHolder>(FavoriteDIFF) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            1 -> LocationSelectorViewHolder.init(parent)
            2 -> FavoriteHeaderViewHolder.init(parent)
            else -> FavoriteViewHolder.init(parent)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        when (getItemViewType(position)) {
            1 -> {
                // select location layout
                holder as LocationSelectorViewHolder
                holder.bind(mainActivity)
            }
            2 -> {
                // favourite list header
                holder as FavoriteHeaderViewHolder
                holder.bind(mainActivity)
            }
            else -> {
                // favourite list
                holder as FavoriteViewHolder
                holder.bind(getItem(position))
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (position) {
            0 -> 1
            1 -> 2
            else -> super.getItemViewType(position)
        }

    }
}

object FavoriteDIFF : DiffUtil.ItemCallback<Address>() {
    override fun areItemsTheSame(oldItem: Address, newItem: Address): Boolean {
        return oldItem === newItem
    }

    override fun areContentsTheSame(oldItem: Address, newItem: Address): Boolean {
        return oldItem == newItem
    }
}

class FavoriteViewHolder(private val view: FavouriteListLayoutBinding) :
    RecyclerView.ViewHolder(view.root) {
    fun bind(favoriteItem: Address) {
        view.address.text = favoriteItem.LongName
    }

    companion object {
        fun init(parent: ViewGroup): FavoriteViewHolder {

            val binding =
                FavouriteListLayoutBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            return FavoriteViewHolder(binding)
        }
    }
}

class FavoriteHeaderViewHolder(private val view: FavouriteHeaderBinding) :
    RecyclerView.ViewHolder(view.root) {


    fun bind(mainActivity: HomeFragment) {
        val placePickerOptions = PlacePickerOptions.builder()
            .statingCameraPosition(
                CameraPosition.Builder()
                    .target(LatLng(-1.2921, 36.8219))
                    .zoom(16.0)
                    .build()
            )
            .build()
        val intent = PlacePicker.IntentBuilder()
            .accessToken(mainActivity.getString(R.string.MapboxAccessToken))
            .placeOptions(placePickerOptions)
            .build(mainActivity.requireActivity())

        view.addButton.setOnClickListener {

            when {
                ContextCompat.checkSelfPermission(
                    mainActivity.requireActivity(),
                    android.Manifest.permission.READ_PHONE_STATE
                ) == PackageManager.PERMISSION_GRANTED -> {
                    startActivityForResult(
                        mainActivity.requireActivity(),
                        intent,
                        HomeFragment.REQUEST_CODE_AUTOCOMPLETE, null
                    )
                }
                shouldShowRequestPermissionRationale(
                    mainActivity.requireActivity(),
                    android.Manifest.permission.READ_PHONE_STATE
                ) -> {
                    Snackbar.make(
                        view.root,
                        "please provide required permissions",
                        Snackbar.LENGTH_LONG
                    ).show()
                }
                else -> {
                    mainActivity.requestPermissionLauncher.launch(android.Manifest.permission.READ_PHONE_STATE)
                }
            }

        }
    }

    companion object {
        fun init(parent: ViewGroup): FavoriteHeaderViewHolder {
            val binding =
                FavouriteHeaderBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            return FavoriteHeaderViewHolder(binding)
        }
    }
}


class LocationSelectorViewHolder(private val view: LocationSelectorBinding) :
    RecyclerView.ViewHolder(view.root) {

    fun bind(mainActivity: HomeFragment) {
        view.chooseLocation.setOnClickListener {
            val placeOptions = PlaceOptions.builder()
                .country("KE")
                .hint(mainActivity.getString(R.string.where_do_you_want_to_go))
                .build(PlaceOptions.MODE_CARDS)
            val intent = PlaceAutocomplete.IntentBuilder()
                .accessToken(mainActivity.getString(R.string.MapboxAccessToken))
                .placeOptions(placeOptions)
                .build(mainActivity.requireActivity())
            mainActivity.startActivityForResult(intent, HomeFragment.REQUEST_CODE_AUTOCOMPLETE)
        }

        view.pickLocation.setOnClickListener {
            val placePickerOptions = PlacePickerOptions.builder()
                .statingCameraPosition(
                    CameraPosition.Builder()
                        .target(LatLng(-1.2921, 36.8219))
                        .zoom(16.0)
                        .build()
                )
                .build()
            val intent = PlacePicker.IntentBuilder()
                .accessToken(mainActivity.getString(R.string.MapboxAccessToken))
                .placeOptions(placePickerOptions)
                .build(mainActivity.requireActivity())
            mainActivity.startActivityForResult(intent, HomeFragment.REQUEST_CODE_PICK_LOCATION)
        }
    }

    companion object {
        fun init(parent: ViewGroup): LocationSelectorViewHolder {
            val binding =
                LocationSelectorBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            return LocationSelectorViewHolder(binding)
        }
    }
}



