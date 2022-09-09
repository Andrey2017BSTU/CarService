package com.example.carservice.detailModule

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.example.carservice.R
import com.example.carservice.appModule.AppRepository
import com.example.carservice.dataBase.AppDataBase
import com.example.carservice.databinding.DetailBinding
import com.example.carservice.pixabayAPI.RetrofitService


class DetailFragment : Fragment(), StartDeleteCarAlertDialog.OnDeleteListener,
    StartCurrentMileageUpdateAlertDialog.OnEnterListener {


    private var _binding: DetailBinding? = null
    private val binding get() = _binding!!
    private var carId: Int = 0
    private var carMileage: Int = 0
    private var bundleForAlertDialog: Bundle? = null
    private val viewModelObj: DetailViewModel by viewModels {
        DetailViewModelFactory(
            AppRepository(
                AppDataBase.getDatabase(requireContext()),
                RetrofitService.invoke()
            )
        )
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DetailBinding.inflate(inflater, container, false)
        viewModelObj.detailViewModelInit(arguments)
        bundleForAlertDialog = arguments
        carId = requireArguments().getInt("EXTRA_ID")

        carMileage = requireArguments().getInt("EXTRA_CURRENT_MILEAGE")

        viewModelObj.carItemMutableLiveData.observe(viewLifecycleOwner) {
            binding.carBrandModelNameDetailView.text = String.format("%s", it.brand_name)
            binding.carBrandModelNameDetailView.append(String.format(" %s", it.model_name))
            binding.carYearDetailView.text = String.format("%s", it.year)
            binding.currentMileageDetailView.text = String.format("%s %s", it.current_mileage, "км")
            binding.oilMileageDetailView.text = String.format("%s", it.oil_mileage)
            binding.airFiltMileageDetailView.text = String.format("%s", it.air_filt_mileage)
            binding.freezMileageDetailView.text = String.format("%s", it.freez_mileage)
            binding.grmMileageDetailView.text = String.format("%s", it.grm_mileage)
            binding.oilBottomsheetTextview.text = String.format(
                "%s: %s",
                getString(R.string.oil_checker_str),
                setBottomsheet(
                    it.current_mileage,
                    it.oil_last_service_mileage,
                    it.oil_mileage
                )
            )

            binding.airFiltBottomsheetTextview.text = String.format(
                "%s: %s",
                getString(R.string.air_filt_checker_str),
                setBottomsheet(
                    it.current_mileage,
                    it.air_filt_last_service_mileage,
                    it.air_filt_mileage
                )
            )

            binding.freezBottomsheetTextview.text = String.format(
                "%s: %s",
                getString(R.string.freez_checker_str),
                setBottomsheet(
                    it.current_mileage,
                    it.freez_last_service_mileage,
                    it.freez_mileage
                )
            )

            binding.grmBottomsheetTextview.text = String.format(
                "%s: %s",
                getString(R.string.grm_checker_str),
                setBottomsheet(
                    it.current_mileage,
                    it.freez_last_service_mileage,
                    it.freez_mileage
                )
            )

        }
        viewModelObj.carURLMutableLiveData.observe(viewLifecycleOwner) {

            setImageIntoViewByUrl(it)

        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val menuHost: MenuHost = requireActivity()


        menuHost.addMenuProvider(object : MenuProvider {

            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.over_flow_detail_menu, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                Log.v("overflow_menu", "Selected" + " " + menuItem.title)
                when (menuItem.itemId) {

                    R.id.update_current_mileage_option -> makeUpdate()
                    R.id.delete_car_option -> makeDelete()


                }

                return true
            }


        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
        super.onViewCreated(view, savedInstanceState)
    }

    private fun makeDelete() {
        val alertDialogObj: DialogFragment = StartDeleteCarAlertDialog()
        alertDialogObj.show(parentFragmentManager, "delete")

    }

    private fun makeUpdate() {
        val alertDialogObj: DialogFragment = StartCurrentMileageUpdateAlertDialog()
        alertDialogObj.arguments = bundleForAlertDialog
        alertDialogObj.show(parentFragmentManager, "update")
    }

    private fun setBottomsheet(
        currentMileage: Int,
        lastServiceMileage: Int,
        mileageToService: Int
    ): Int {

        return if (mileageToService - (currentMileage - lastServiceMileage) <= 0) 0
        else mileageToService - (currentMileage - lastServiceMileage)

    }


    private fun setImageIntoViewByUrl(url: String?) {


        binding.progress.visibility = View.VISIBLE
        Glide.with(requireContext()).load(url)
            .listener(object : RequestListener<Drawable> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable>?,
                    isFirstResource: Boolean
                ): Boolean {

                    e?.printStackTrace()
                    binding.progress.visibility = View.VISIBLE
                    setDefaultImageIntoViewByName()
                    binding.progress.visibility = View.GONE
                    Log.v("Detail", "LoadFailed")
                    return true

                }

                override fun onResourceReady(
                    resource: Drawable?,
                    model: Any?,
                    target: Target<Drawable>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {

                    Log.v("Detail", "ResourceReady")
                    binding.progress.visibility = View.GONE
                    return false

                }


            }).into(binding.carLogoImgView)


    }


    private fun setDefaultImageIntoViewByName() {
        viewModelObj.brandNameMutableLiveData.observe(viewLifecycleOwner) {


            when (it) {
                "BMW" -> binding.carLogoImgView.setImageResource(R.drawable.bmw_logo_2020_grey)
                "MERCEDES-BENZ" -> binding.carLogoImgView.setImageResource(R.drawable.mercedes_logo)
                "AUDI" -> binding.carLogoImgView.setImageResource(R.drawable.audi_logo)
                "VOLKSWAGEN" -> binding.carLogoImgView.setImageResource(R.drawable.volkswagen_logo)

            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onPositiveDeleteClicked() {
        Log.v("Detail_frag", "Positive_clicked")
        parentFragmentManager.popBackStack()
        viewModelObj.onPositiveDelete(carId)
    }

    override fun onUpdatePositiveButtonClicked(updated_current_mileage: Int) {
        viewModelObj.onPositiveUpdateCurrentMileage(carId, updated_current_mileage)
    }

    override fun onUpdateNeutralButtonClicked() {

    }


}