package codes.malukimuthusi.safiri

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
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
    lateinit var requestPermisLctPickLauncher: ActivityResultLauncher<String>

    lateinit var requestPermisLctSelectLauncher: ActivityResultLauncher<String>

    val selectLocationLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val carmen = PlacePicker.getPlace(result.data)
                // save to the database
                val newAddress = Address(
                    carmen?.address() ?: "new place",
                    carmen?.center()?.longitude()?.toDouble() ?: 0.0,
                    carmen?.center()?.latitude()?.toDouble() ?: 0.0,
                    carmen?.placeName() ?: "default name",
                    carmen?.placeName() ?: "default shortname"
                )

                lifecycleScope.launch(Dispatchers.IO) {
                    viewModel.addItem(newAddress)
                }
            }

            if (result.resultCode == Activity.RESULT_CANCELED) {
                Toast.makeText(context, "Canceled Selecting Location", Toast.LENGTH_LONG).show()
            }
        }
    val pickLocationLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val carmen = PlacePicker.getPlace(result.data)
                /*save to the database*/
                val newAddress = Address(
                    carmen?.address() ?: "new place",
                    carmen?.center()?.longitude() ?: 0.0,
                    carmen?.center()?.latitude() ?: 0.0,
                    carmen?.placeName() ?: "default name",
                    carmen?.placeName() ?: "default shortname"
                )

                lifecycleScope.launch(Dispatchers.IO) {
                    viewModel.addItem(newAddress)
                }
            }

            if (result.resultCode == Activity.RESULT_CANCELED) {
                // handle nil result
                Toast.makeText(context, "Canceled Picking Location", Toast.LENGTH_LONG).show()
            }
        }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }

        requestPermisLctPickLauncher =
            requireActivity().registerForActivityResult(ActivityResultContracts.RequestPermission()) {
                if (it) {
                    val placePickerOptions = PlacePickerOptions.builder()
                        .statingCameraPosition(
                            CameraPosition.Builder()
                                .target(LatLng(-1.2921, 36.8219))
                                .zoom(16.0)
                                .build()
                        )
                        .build()
                    val pickPlaceintent = PlacePicker.IntentBuilder()
                        .accessToken(getString(R.string.MapboxAccessToken))
                        .placeOptions(placePickerOptions)
                        .build(requireActivity())
                    pickLocationLauncher.launch(pickPlaceintent)
                } else {
                    Snackbar.make(
                        binding.root,
                        "please provide required permissions",
                        Snackbar.LENGTH_LONG
                    ).show()
                }

            }

        requestPermisLctSelectLauncher =
            requireActivity().registerForActivityResult(ActivityResultContracts.RequestPermission()) {
                if (it) {
                    val placeSelectOptions = PlaceOptions.builder()
                        .country("KE")
                        .hint(getString(R.string.where_do_you_want_to_go))
                        .build(PlaceOptions.MODE_CARDS)
                    val placeSelectIntent = PlaceAutocomplete.IntentBuilder()
                        .accessToken(getString(R.string.MapboxAccessToken))
                        .placeOptions(placeSelectOptions)
                        .build(activity)
                    pickLocationLauncher.launch(placeSelectIntent)
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
