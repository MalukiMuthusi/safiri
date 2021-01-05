package codes.malukimuthusi.safiri.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "address")
data class Address(
    val name: String,
    val longitude: Double,
    val Latitude: Double,
    var LongName: String,
    var shortName: String,
    @PrimaryKey(autoGenerate = true)
    var id: Int? = null


)