package codes.malukimuthusi.safiri

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.room.Room
import codes.malukimuthusi.safiri.models.Address
import codes.malukimuthusi.safiri.repository.AppDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class HomeViewModel(app: Application) : AndroidViewModel(app) {

    // create room database
    val db =
        Room.databaseBuilder(app.applicationContext, AppDatabase::class.java, "malukimuthusiDB")
            .build()

    suspend fun addItem(newPlace: Address) {

        withContext(Dispatchers.IO) {
            db.addressDao().insertAddress(newPlace)
        }

    }

    val allAddresses: LiveData<List<Address>>
        get() {
            return db.addressDao().getAllAddresses()
        }

    suspend fun removeItem(place: Address) {
        withContext(Dispatchers.IO) {
            db.addressDao().deleteAddress(place)
        }
    }

    /* Check work Address change */
    private var workCounter = 0
    private val _workAddress = MutableLiveData<Int>()
    val workAddress: LiveData<Int>
        get() = _workAddress

    fun changeWorkAddress() {
        workCounter += 1
        _workAddress.value = workCounter
    }

    /* check home address change */
    private var homeCounter = 0
    private val _homeAddress = MutableLiveData<Int>()
    fun changeHomeAddress() {
        homeCounter += homeCounter
        _homeAddress.value = _homeAddress.value
    }

    val homeAddress: LiveData<Int>
        get() = _homeAddress
}