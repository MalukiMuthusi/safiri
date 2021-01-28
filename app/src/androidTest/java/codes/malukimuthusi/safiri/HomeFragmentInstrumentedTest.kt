package codes.malukimuthusi.safiri

import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.navigation.Navigation
import androidx.navigation.testing.TestNavHostController
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.internal.runner.junit4.statement.UiThreadStatement.runOnUiThread
import com.google.common.truth.Truth
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class HomeFragmentInstrumentedTest {


    @Test
    fun getHomeAddress() {

        val testNavController =
            TestNavHostController(ApplicationProvider.getApplicationContext())
        runOnUiThread {
            testNavController.setGraph(R.navigation.nav_host)
        }

        val scenario =
            launchFragmentInContainer(themeResId = R.style.Theme_Safiri) {
                HomeFragment().also { homeFragment ->
                    homeFragment.viewLifecycleOwnerLiveData.observeForever { viewLifecycleOwner ->
                        if (viewLifecycleOwner != null) {
                            Navigation.setViewNavController(
                                homeFragment.requireView(),
                                testNavController
                            )
                        }
                    }
                }
            }

        scenario.onFragment { home ->
            val address = home.getHomeAddress()
            Truth.assertThat(address)
                .isEqualTo(
                    codes.malukimuthusi.safiri.models.Address(
                        "home",
                        0.0,
                        0.0,
                        "home",
                        "home"
                    )
                )
        }
    }

}