package codes.malukimuthusi.safiri

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import codes.malukimuthusi.safiri.databinding.FavouriteListLayoutBinding

class FavoriteAdapter : ListAdapter<Favorite, FavoriteViewHolder>(FavoriteDIFF) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoriteViewHolder {
        return FavoriteViewHolder.init(parent)
    }

    override fun onBindViewHolder(holder: FavoriteViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}

data class Favorite(
    var address: String = ""
)

object FavoriteDIFF : DiffUtil.ItemCallback<Favorite>() {
    override fun areItemsTheSame(oldItem: Favorite, newItem: Favorite): Boolean {
        return oldItem === newItem
    }

    override fun areContentsTheSame(oldItem: Favorite, newItem: Favorite): Boolean {
        return oldItem == newItem
    }
}

class FavoriteViewHolder(private val view: FavouriteListLayoutBinding) :
    RecyclerView.ViewHolder(view.root) {
    fun bind(favoriteItem: Favorite) {
        view.address.text = favoriteItem.address
    }

    companion object {
        fun init(parent: ViewGroup): FavoriteViewHolder {

            val binding =
                FavouriteListLayoutBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            return FavoriteViewHolder(binding)
        }
    }
}