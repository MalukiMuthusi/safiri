package codes.malukimuthusi.safiri

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import codes.malukimuthusi.safiri.databinding.FragmentHomeBinding
import codes.malukimuthusi.safiri.models.Address
import com.google.android.material.snackbar.Snackbar
import com.mapbox.mapboxsdk.Mapbox
import com.mapbox.mapboxsdk.camera.CameraPosition
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.plugins.places.autocomplete.PlaceAutocomplete
import com.mapbox.mapboxsdk.plugins.places.autocomplete.model.PlaceOptions
import com.mapbox.mapboxsdk.plugins.places.picker.PlacePicker
import com.mapbox.mapboxsdk.plugins.places.picker.model.PlacePickerOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [HomeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class HomeFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var binding: FragmentHomeBinding
    private lateinit var navController: NavController
    private lateinit var navHostFragment: NavHostFragment
    private lateinit var favouriteListAdapter: FavoriteAdapter
    private val viewModel: HomeViewModel by viewModels {
        ViewModelProvider.AndroidViewModelFactory(
            requireActivity().application
        )
    }
    lateinit var requestPermissionLauncher: ActivityResultLauncher<String>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }

        val placePickerOptions = PlacePickerOptions.builder()
            .statingCameraPosition(
                CameraPosition.Builder()
                    .target(LatLng(-1.2921, 36.8219))
                    .zoom(16.0)
                    .build()
            )
            .build()
        val intent = PlacePicker.IntentBuilder()
            .accessToken(getString(R.string.MapboxAccessToken))
            .placeOptions(placePickerOptions)
            .build(requireActivity())
        requestPermissionLauncher =
            requireActivity().registerForActivityResult(ActivityResultContracts.RequestPermission()) {
                if (it) {
                    ActivityCompat.startActivityForResult(
                        requireActivity(),
                        intent,
                        REQUEST_CODE_AUTOCOMPLETE, null
                    )
                } else {
                    Snackbar.make(
                        binding.root,
                        "please provide required permissions",
                        Snackbar.LENGTH_LONG
                    ).show()
                }

            }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        Mapbox.getInstance(requireContext(), getString(R.string.MapboxAccessToken))


        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.topAppBar.setNavigationOnClickListener {

        }
        navHostFragment =
            requireActivity().supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController
        val drawer = requireActivity().findViewById<DrawerLayout>(R.id.drawer_layout)
        binding.collapsingToolbarLayout.setupWithNavController(
            binding.topAppBar,
            navController,
            drawer
        )

        val act = requireActivity()
        favouriteListAdapter = FavoriteAdapter(this)
        binding.recyclerviewLayoutId.adapter = favouriteListAdapter


        viewModel.allAddresses.observe(viewLifecycleOwner, {
            val emptyAddres = Address("", 0.0, 0.0, "", "")
            val changeList = mutableListOf<Address>()
            changeList.addAll(it)
            changeList.add(0, emptyAddres)
            changeList.add(0, emptyAddres)
            favouriteListAdapter.submitList(changeList)
        })

    }

    private fun addToFavourite() {
        // get the added class
        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                viewModel.db.addressDao().insertAddress(viewModel.jonathanNgeno)
            }

        }
    }


    private fun chooseLocation() {
        val placeOptions = PlaceOptions.builder()
            .country("KE")
            .hint(getString(R.string.where_do_you_want_to_go))
            .build(PlaceOptions.MODE_CARDS)
        val intent = PlaceAutocomplete.IntentBuilder()
            .accessToken(getString(R.string.MapboxAccessToken))
            .placeOptions(placeOptions)
            .build(activity)
        startActivityForResult(intent, REQUEST_CODE_AUTOCOMPLETE)
    }

    private fun pickLocation() {
        val placePickerOptions = PlacePickerOptions.builder()
            .statingCameraPosition(
                CameraPosition.Builder()
                    .target(LatLng(-1.2921, 36.8219))
                    .zoom(16.0)
                    .build()
            )
            .build()
        val intent = PlacePicker.IntentBuilder()
            .accessToken(getString(R.string.MapboxAccessToken))
            .placeOptions(placePickerOptions)
            .build(activity)
        startActivityForResult(intent, REQUEST_CODE_PICK_LOCATION)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            REQUEST_CODE_AUTOCOMPLETE -> {
                if (resultCode == Activity.RESULT_OK) {
                    val feature = PlaceAutocomplete.getPlace(data)
                }

                if (resultCode != Activity.RESULT_OK) {
                    Toast.makeText(context, "Failed to select location", Toast.LENGTH_LONG).show()
                }
            }
            REQUEST_CODE_PICK_LOCATION -> {
                if (resultCode == Activity.RESULT_OK) {
                    val feature = PlaceAutocomplete.getPlace(data)
                    val newAddress = Address(
                        feature.address() ?: "new place",
                        feature.center()?.longitude()?.toDouble() ?: 0.0,
                        feature.center()?.latitude()?.toDouble() ?: 0.0,
                        feature.placeName() ?: "default name",
                        feature.id() ?: "default shortname"
                    )

                    lifecycleScope.launch {
                        viewModel.addItem(newAddress)
                    }
                }

                if (resultCode != Activity.RESULT_OK) {
                    Toast.makeText(context, "Failed to pick location", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment HomeFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            HomeFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }

        const val REQUEST_CODE_AUTOCOMPLETE = 8267
        const val REQUEST_CODE_PICK_LOCATION = 8654
    }

}
