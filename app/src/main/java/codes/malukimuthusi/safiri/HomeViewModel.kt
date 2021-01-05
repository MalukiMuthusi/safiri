package codes.malukimuthusi.safiri

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.room.Room
import codes.malukimuthusi.safiri.models.Address
import codes.malukimuthusi.safiri.repository.AppDatabase

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


}