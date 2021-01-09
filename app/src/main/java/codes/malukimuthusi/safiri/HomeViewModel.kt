package codes.malukimuthusi.safiri

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.room.Room
import codes.malukimuthusi.safiri.models.Address
import codes.malukimuthusi.safiri.repository.AppDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeViewModel(app: Application) : AndroidViewModel(app) {

    // create room database
    val db =
        Room.databaseBuilder(app.applicationContext, AppDatabase::class.java, "malukimuthusiDB")
            .build()

    val jonathanNgeno = Address(
        "Lake Side view",
        -1.3212413399260465,
        36.79315252818723,
        "Jonathan Ng'eno Estate",
        "D100"
    )

    var allAddresses = MutableLiveData<List<Address>>()
    fun getAllAddresses(){
       viewModelScope.launch {
         allAddresses =  db.addressDao().getAll()
       }
    }


}