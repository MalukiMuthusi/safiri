package codes.malukimuthusi.safiri

import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.ext.junit.runners.AndroidJUnit4
import codes.malukimuthusi.safiri.models.Address
import com.google.common.truth.Truth.assertThat
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class HomeFragmentInstrumentedTest {

    @Test
    fun getHomeAddress() {
        val scenario = launchFragmentInContainer<HomeFragment>()
        val expectedAdress = Address(
            "longName",
            0.0,
            0.0,
            "longName",
            "longName"
        )
        scenario.onFragment { homeFragment ->
            val address = homeFragment.getHomeAddress()
            assertThat(address).isEqualTo(expectedAdress)
        }
    }

}