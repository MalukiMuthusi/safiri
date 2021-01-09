package codes.malukimuthusi.safiri

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import codes.malukimuthusi.safiri.databinding.FavouriteHeaderBinding
import codes.malukimuthusi.safiri.databinding.FavouriteListLayoutBinding
import codes.malukimuthusi.safiri.databinding.LocationSelectorBinding
import codes.malukimuthusi.safiri.models.Address

class FavoriteAdapter : ListAdapter<Address, RecyclerView.ViewHolder>(FavoriteDIFF) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            1 -> LocationSelectorViewHolder.init(parent)
            2 -> FavoriteHeaderViewHolder.init(parent)
            else -> FavoriteViewHolder.init(parent)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        when (getItemViewType(position)) {
            1 -> {
                return
            }
            2 -> {
                return
            }
            else -> {
                holder as FavoriteViewHolder
                holder.bind(getItem(position))
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (position) {
            0 -> 1
            1 -> 2
            else -> super.getItemViewType(position)
        }

    }
}

object FavoriteDIFF : DiffUtil.ItemCallback<Address>() {
    override fun areItemsTheSame(oldItem: Address, newItem: Address): Boolean {
        return oldItem === newItem
    }

    override fun areContentsTheSame(oldItem: Address, newItem: Address): Boolean {
        return oldItem == newItem
    }
}

class FavoriteViewHolder(private val view: FavouriteListLayoutBinding) :
    RecyclerView.ViewHolder(view.root) {
    fun bind(favoriteItem: Address) {
        view.address.text = favoriteItem.LongName
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

class FavoriteHeaderViewHolder(view: FavouriteHeaderBinding) :
    RecyclerView.ViewHolder(view.root) {

    companion object {
        fun init(parent: ViewGroup): FavoriteHeaderViewHolder {
            val binding =
                FavouriteHeaderBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            return FavoriteHeaderViewHolder(binding)
        }
    }
}

class LocationSelectorViewHolder(view: LocationSelectorBinding) :
    RecyclerView.ViewHolder(view.root) {

    companion object {
        fun init(parent: ViewGroup): LocationSelectorViewHolder {
            val binding =
                LocationSelectorBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            return LocationSelectorViewHolder(binding)
        }
    }
}

