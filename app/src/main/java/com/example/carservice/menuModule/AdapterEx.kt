package com.example.carservice.menuModule

import android.graphics.drawable.Drawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.example.carservice.R
import com.example.carservice.dataBase.CarsItemTable
import com.example.carservice.databinding.ItemBinding

class AdapterEx(private val itemClickListener: OnItemClickListener) : RecyclerView.Adapter<AdapterEx.ViewHolderEX>() {


    private var carsList = mutableListOf<CarsItemTable>()

    interface OnItemClickListener{
        fun onItemClicked(carItem: CarsItemTable)
    }




    fun setCarsListAdapter(_cars: List<CarsItemTable>) {
        this.carsList = _cars.toMutableList()
        notifyDataSetChanged()
        Log.v("Adapter_List", "Set's")

    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderEX {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemBinding.inflate(inflater, parent, false)
        Log.v("Adapt", "create")
        return ViewHolderEX(binding)
    }

    override fun onBindViewHolder(holder: ViewHolderEX, position: Int) {

        val carItem = carsList[position]

        holder.binding.TextViewCarDescriptionItem.text =
            String.format(
                "%s %s \n%s %s",
                carItem.brand_name,
                carItem.model_name,
                carItem.year,
                "г.в."
            )
        holder.binding.TextViewCurrentMileageItem.text =
            String.format("%s %s", carItem.current_mileage, "км")


        if (carItem.image_url.isNullOrEmpty()) {

            setDefaultImageIntoViewByName(holder, carItem.brand_name)

        } else {

            setImageIntoViewByUrl(holder, carItem.image_url)

        }

        holder.bind(carItem,itemClickListener)
    }


    private fun setDefaultImageIntoViewByName(holder: ViewHolderEX, brandName: String) {
        when (brandName) {
            "BMW" -> holder.binding.ImageViewCarItem.setImageResource(R.drawable.bmw_logo_2020_grey)
            "MERCEDES-BENZ" -> holder.binding.ImageViewCarItem.setImageResource(R.drawable.mercedes_logo)
            "AUDI" -> holder.binding.ImageViewCarItem.setImageResource(R.drawable.audi_logo)
            "VOLKSWAGEN" -> holder.binding.ImageViewCarItem.setImageResource(R.drawable.volkswagen_logo)
        }
    }


    private fun setImageIntoViewByUrl(holder: ViewHolderEX, url: String?) {

        holder.binding.progress.visibility = View.VISIBLE
        Glide.with(holder.itemView.context).load(url)
            .listener(object : RequestListener<Drawable> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable>?,
                    isFirstResource: Boolean
                ): Boolean {
                    e?.printStackTrace()
                    holder.binding.progress.visibility = View.VISIBLE
                    return false

                }

                override fun onResourceReady(
                    resource: Drawable?,
                    model: Any?,
                    target: Target<Drawable>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {

                    holder.binding.progress.visibility = View.GONE
                    return false

                }


            }).into(holder.binding.ImageViewCarItem)


    }


    override fun getItemCount(): Int {
        return carsList.size
    }



    class ViewHolderEX(val binding: ItemBinding) : RecyclerView.ViewHolder(binding.root) {



        fun bind(carItem: CarsItemTable, clickListener: OnItemClickListener){

            binding.root.setOnClickListener {
                clickListener.onItemClicked(carItem)
            }




        }

    }
}

