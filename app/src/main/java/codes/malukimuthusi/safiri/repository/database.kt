package codes.malukimuthusi.safiri.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.room.*
import codes.malukimuthusi.safiri.models.Address

@Dao
interface AddressDao {
    @Query("SELECT * FROM address")
  suspend  fun getAll(): MutableLiveData<List<Address>>

    @Insert()
    fun insertAddress(address: Address)
}

@Database(entities = arrayOf(Address::class), version = 100, exportSchema = false)
abstract class AppDatabase: RoomDatabase(){
    abstract fun addressDao(): AddressDao
}