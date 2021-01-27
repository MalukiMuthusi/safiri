package codes.malukimuthusi.safiri

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.ActivityResultRegistry
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.launch
import androidx.core.app.ActivityCompat.shouldShowRequestPermissionRationale
import androidx.core.content.ContextCompat
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import codes.malukimuthusi.safiri.databinding.*
import codes.malukimuthusi.safiri.models.Address
import com.google.android.material.snackbar.Snackbar
import com.mapbox.mapboxsdk.camera.CameraPosition
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.plugins.places.autocomplete.PlaceAutocomplete
import com.mapbox.mapboxsdk.plugins.places.autocomplete.model.PlaceOptions
import com.mapbox.mapboxsdk.plugins.places.picker.PlacePicker
import com.mapbox.mapboxsdk.plugins.places.picker.model.PlacePickerOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FavoriteAdapter(
    private val homeFragment: HomeFragment,
    private val viewModel: HomeViewModel
) :
    ListAdapter<Address, RecyclerView.ViewHolder>(FavoriteDIFF) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            0 -> LocationSelectorViewHolder.init(parent)
            1 -> FavoriteHeaderViewHolder.init(parent)
            2 -> HomeFavoriteViewHolder.init(parent)
            3 -> WorkAddressViewHolder.init(parent)
            else -> FavoriteViewHolder.init(parent)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        when (getItemViewType(position)) {
            0 -> {
                // select location layout
                holder as LocationSelectorViewHolder
                holder.bind(homeFragment)
            }
            1 -> {
                // favourite list header
                holder as FavoriteHeaderViewHolder
                holder.bind(homeFragment)
            }
            2 -> {
                holder as HomeFavoriteViewHolder
                holder.bind(getItem(position), homeFragment, viewModel)
            }
            3 -> {
                holder as WorkAddressViewHolder
                holder.bind(getItem(position), homeFragment, viewModel)
            }
            else -> {
                // favourite list
                holder as FavoriteViewHolder
                holder.bind(getItem(position), homeFragment, viewModel)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (position) {
            0 -> 0
            1 -> 1
            2 -> 2
            3 -> 3
            else -> 4
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

class FavoriteViewHolder(private val binding: FavouriteListSingleLayoutBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(favoriteItem: Address, homeFragment: HomeFragment, viewModel: HomeViewModel) {
        binding.address.text = favoriteItem.LongName

        binding.more.setOnClickListener { moreImageView ->
            val popupMenu = PopupMenu(moreImageView.context, moreImageView)
            popupMenu.menuInflater.inflate(R.menu.more_menu, popupMenu.menu)

            popupMenu.setOnMenuItemClickListener { menuItem ->
                when (menuItem.title) {
                    moreImageView.context.getString(R.string.remove) -> {
                        homeFragment.lifecycleScope.launch {
                            withContext(Dispatchers.Main) {
                                viewModel.removeItem(favoriteItem)
                            }
                        }
                        true
                    }
                    else -> {
                        Toast.makeText(
                            moreImageView.context,
                            "Please Edit Item",
                            Toast.LENGTH_SHORT
                        ).show()
                        true
                    }
                }

            }
            popupMenu.setOnDismissListener {
                // TODO: Handle item dismiss
            }
            popupMenu.show()
        }
    }

    companion object {
        fun init(parent: ViewGroup): FavoriteViewHolder {

            val binding =
                FavouriteListSingleLayoutBinding.inflate(
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
                    mainActivity.selectLocationLauncher.launch(intent)
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
                    mainActivity.requestPermissionLocationPickLauncher.launch(android.Manifest.permission.READ_PHONE_STATE)
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

    fun bind(homeFragment: HomeFragment) {
        view.chooseLocation.setOnClickListener {

            when {
                ContextCompat.checkSelfPermission(
                    homeFragment.requireActivity(),
                    android.Manifest.permission.READ_PHONE_STATE
                ) == PackageManager.PERMISSION_GRANTED -> {
                    val placeSelectOptions = PlaceOptions.builder()
                        .country("KE")
                        .hint(homeFragment.getString(R.string.where_do_you_want_to_go))
                        .build(PlaceOptions.MODE_CARDS)
                    val placeSelectIntent = PlaceAutocomplete.IntentBuilder()
                        .accessToken(homeFragment.getString(R.string.MapboxAccessToken))
                        .placeOptions(placeSelectOptions)
                        .build(homeFragment.requireActivity())
                    homeFragment.selectLocationLauncher.launch(placeSelectIntent)
                }

                shouldShowRequestPermissionRationale(
                    homeFragment.requireActivity(),
                    android.Manifest.permission.READ_PHONE_STATE
                ) -> {
                    // TODO: Add code to show permission rational dialog
                    Snackbar.make(
                        view.root,
                        "please provide required permissions",
                        Snackbar.LENGTH_LONG
                    ).show()
                }

                else -> {
                    homeFragment.requestPermissionLocationSelectLauncher.launch(android.Manifest.permission.READ_PHONE_STATE)
                }
            }
        }

        view.pickLocation.setOnClickListener {

            when {
                ContextCompat.checkSelfPermission(
                    homeFragment.requireActivity(),
                    android.Manifest.permission.READ_PHONE_STATE
                ) == PackageManager.PERMISSION_GRANTED -> {
                    val placePickerOptions = PlacePickerOptions.builder()
                        .statingCameraPosition(
                            CameraPosition.Builder()
                                .target(LatLng(-1.2921, 36.8219))
                                .zoom(16.0)
                                .build()
                        )
                        .build()
                    val intent = PlacePicker.IntentBuilder()
                        .accessToken(homeFragment.getString(R.string.MapboxAccessToken))
                        .placeOptions(placePickerOptions)
                        .build(homeFragment.requireActivity())
                    homeFragment.pickLocationLauncher.launch(intent)
                }

                shouldShowRequestPermissionRationale(
                    homeFragment.requireActivity(),
                    android.Manifest.permission.READ_PHONE_STATE
                ) -> {
                    Snackbar.make(
                        view.root,
                        "please provide required permissions",
                        Snackbar.LENGTH_LONG
                    ).show()
                }

                else -> {
                    homeFragment.requestPermissionLocationPickLauncher.launch(android.Manifest.permission.READ_PHONE_STATE)
                }
            }
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

// home item
class HomeFavoriteViewHolder(private val view: HomeFavouriteBinding) :
    RecyclerView.ViewHolder(view.root) {
    fun bind(favoriteItem: Address, homeFragment: HomeFragment, viewModel: HomeViewModel) {
        view.address.text = favoriteItem.LongName
        view.placeName.setOnClickListener {
            when {
                ContextCompat.checkSelfPermission(
                    homeFragment.requireActivity(),
                    android.Manifest.permission.READ_PHONE_STATE
                ) == PackageManager.PERMISSION_GRANTED -> {
                    val placePickerOptions = PlacePickerOptions.builder()
                        .statingCameraPosition(
                            CameraPosition.Builder()
                                .target(LatLng(-1.2921, 36.8219))
                                .zoom(16.0)
                                .build()
                        )
                        .build()
                    val intent = PlacePicker.IntentBuilder()
                        .accessToken(homeFragment.getString(R.string.MapboxAccessToken))
                        .placeOptions(placePickerOptions)
                        .build(homeFragment.requireActivity())
                    homeFragment.pickHomeLocationLauncher.launch(intent)
                }

                shouldShowRequestPermissionRationale(
                    homeFragment.requireActivity(),
                    android.Manifest.permission.READ_PHONE_STATE
                ) -> {
                    Snackbar.make(
                        view.root,
                        "please provide required permissions",
                        Snackbar.LENGTH_LONG
                    ).show()
                }

                else -> {
                    homeFragment.requestPermissionPickHomeLauncher.launch(android.Manifest.permission.READ_PHONE_STATE)
                }
            }
        }

        view.more.setOnClickListener { moreImageView ->
            val popMenu = PopupMenu(moreImageView.context, moreImageView)
            popMenu.menuInflater.inflate(R.menu.home_more_menu, popMenu.menu)

            popMenu.setOnMenuItemClickListener { menuItem ->
                when (menuItem.title) {
                    moreImageView.context.getString(R.string.home_reset) -> {
                        homeFragment.lifecycleScope.launch {
                            withContext(Dispatchers.Main) {
                                viewModel.removeItem(favoriteItem)
                            }
                        }
                        true
                    }
                    else -> {
                        Toast.makeText(
                            moreImageView.context,
                            "Please Edit Item",
                            Toast.LENGTH_SHORT
                        ).show()
                        true
                    }
                }
            }

            popMenu.setOnDismissListener {
                // TODO: handle menu dismiss

            }
            popMenu.show()

        }
    }

    companion object {
        fun init(parent: ViewGroup): HomeFavoriteViewHolder {
            val binding =
                HomeFavouriteBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            return HomeFavoriteViewHolder(binding)
        }
    }
}

// work viewHolder
class WorkAddressViewHolder(private val binding: WorkAddressBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(favoriteItem: Address, homeFragment: HomeFragment, viewModel: HomeViewModel) {
        binding.address.text = favoriteItem.LongName
        binding.placeName.setOnClickListener {
            when {
                ContextCompat.checkSelfPermission(
                    homeFragment.requireActivity(),
                    android.Manifest.permission.READ_PHONE_STATE
                ) == PackageManager.PERMISSION_GRANTED -> {
                    val placePickerOptions = PlacePickerOptions.builder()
                        .statingCameraPosition(
                            CameraPosition.Builder()
                                .target(LatLng(-1.2921, 36.8219))
                                .zoom(16.0)
                                .build()
                        )
                        .build()
                    val intent = PlacePicker.IntentBuilder()
                        .accessToken(homeFragment.getString(R.string.MapboxAccessToken))
                        .placeOptions(placePickerOptions)
                        .build(homeFragment.requireActivity())
                    homeFragment.pickHomeLocationLauncher.launch(intent)
                }

                shouldShowRequestPermissionRationale(
                    homeFragment.requireActivity(),
                    android.Manifest.permission.READ_PHONE_STATE
                ) -> {
                    Snackbar.make(
                        binding.root,
                        "please provide required permissions",
                        Snackbar.LENGTH_LONG
                    ).show()
                }

                else -> {
                    homeFragment.requestPermissionPickHomeLauncher.launch(android.Manifest.permission.READ_PHONE_STATE)
                }
            }
        }

        binding.more.setOnClickListener { moreImageView ->
            val popMenu = PopupMenu(moreImageView.context, moreImageView)
            popMenu.menuInflater.inflate(R.menu.home_more_menu, popMenu.menu)

            popMenu.setOnMenuItemClickListener { menuItem ->
                when (menuItem.title) {
                    moreImageView.context.getString(R.string.home_reset) -> {
                        val workAddress = Address("Work Address", 0.0, 0.0, "Tap to Add", "work")
                        binding.address.text = workAddress.LongName
                        homeFragment.saveWorkAddress(workAddress)
                        true
                    }
                    else -> {
                        editWorkAddress(homeFragment)
                        true
                    }
                }
            }

            popMenu.setOnDismissListener {
                // TODO: handle menu dismiss

            }
            popMenu.show()

        }
    }

    private fun editWorkAddress(homeFragment: HomeFragment) {
        homeFragment.editWorkAddressObserver.pickLocation()
    }

    companion object {
        fun init(parent: ViewGroup): WorkAddressViewHolder {
            val binding =
                WorkAddressBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return WorkAddressViewHolder(binding)
        }
    }

    class PickLocationResultContract(private val homeFragment: HomeFragment) :
        ActivityResultContract<Unit, Address>() {
        override fun createIntent(context: Context, input: Unit?): Intent {
            val placePickerOptions = PlacePickerOptions.builder()
                .statingCameraPosition(
                    CameraPosition.Builder()
                        .target(LatLng(-1.2921, 36.8219))
                        .zoom(16.0)
                        .build()
                )
                .build()
            val intent = PlacePicker.IntentBuilder()
                .accessToken(context.getString(R.string.MapboxAccessToken))
                .placeOptions(placePickerOptions)
                .build(homeFragment.requireActivity())

            return intent
        }

        override fun parseResult(resultCode: Int, intent: Intent?): Address {
            if (resultCode == Activity.RESULT_OK) {
                val feature = PlaceAutocomplete.getPlace(intent)
                return Address(
                    feature.address() ?: "place",
                    feature.center()?.longitude() ?: 0.0,
                    feature.center()?.latitude() ?: 0.0,
                    feature.placeName() ?: "place",
                    feature.placeName() ?: "place"
                )
            }
            return Address("default", 0.0, 0.0, "default name", "default")
        }
    }

    class EditWorkAddressObserver(
        private val registry: ActivityResultRegistry,
        private val homeFragment: HomeFragment,
    ) :
        DefaultLifecycleObserver {
        lateinit var getAddress: ActivityResultLauncher<Unit>
        override fun onCreate(owner: LifecycleOwner) {
            super.onCreate(owner)
            getAddress =
                registry.register(
                    "codes.malukimuthusi.safiri.editWorkAddress",
                    owner,
                    PickLocationResultContract(homeFragment)
                ) { address ->
                    homeFragment.saveWorkAddress(address)
                    // TODO: Update UI
                    Toast.makeText(homeFragment.context, "Edited Work address", Toast.LENGTH_LONG)
                        .show()
                }
        }

        fun pickLocation() {
            getAddress.launch()
        }
    }

}






