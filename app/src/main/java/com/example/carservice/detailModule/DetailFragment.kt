package com.example.carservice.detailModule

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.*
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.widget.Button
import android.widget.TextView
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.example.carservice.R
import com.example.carservice.appModule.AppRepository
import com.example.carservice.appModule.ServiceType
import com.example.carservice.carCreatingModule.CarCreatingFragment
import com.example.carservice.dataBase.AppDataBase
import com.example.carservice.databinding.DetailBinding
import com.example.carservice.pixabayAPI.RetrofitService
import com.google.android.material.snackbar.Snackbar


class DetailFragment : Fragment(), StartDeleteCarAlertDialog.OnDeleteListener,
    StartCurrentMileageUpdateAlertDialog.OnEnterListener,
    StartRefreshMileageToServiceAlertDialog.OnRefreshListener, View.OnClickListener {


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


    private val oilWarningAnimation = AlphaAnimation(0.0f, 1.0f)
    private val airFiltWarningAnimation = AlphaAnimation(0.0f, 1.0f)
    private val freezWarningAnimation = AlphaAnimation(0.0f, 1.0f)
    private val grmWarningAnimation = AlphaAnimation(0.0f, 1.0f)


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

        viewModelObj.carCurrentMileageMutableLiveData.observe(viewLifecycleOwner) {

            binding.currentMileageDetailView.text = String.format("%s %s", it, "км")

        }

        viewModelObj.carItemMutableLiveData.observe(viewLifecycleOwner) {
            binding.carBrandModelNameDetailView.text = String.format("%s", it.brand_name)
            binding.carBrandModelNameDetailView.append(String.format(" %s", it.model_name))
            binding.carYearDetailView.text = String.format("%s", it.year)
            binding.oilMileageDetailView.text = String.format("%s", it.oil_mileage)
            binding.airFiltMileageDetailView.text = String.format("%s", it.air_filt_mileage)
            binding.freezMileageDetailView.text = String.format("%s", it.freez_mileage)
            binding.grmMileageDetailView.text = String.format("%s", it.grm_mileage)
            binding.oilBottomsheetTextview.text = String.format(
                "%s - %s",
                getString(R.string.oil_checker_str),
                setBottomsheetMileagesToService(
                    it.current_mileage,
                    it.oil_last_service_mileage,
                    it.oil_mileage
                )
            )

            binding.airFiltBottomsheetTextview.text = String.format(
                "%s - %s",
                getString(R.string.air_filt_checker_str),
                setBottomsheetMileagesToService(
                    it.current_mileage,
                    it.air_filt_last_service_mileage,
                    it.air_filt_mileage
                )
            )

            binding.freezBottomsheetTextview.text = String.format(
                "%s - %s",
                getString(R.string.freez_checker_str),
                setBottomsheetMileagesToService(
                    it.current_mileage,
                    it.freez_last_service_mileage,
                    it.freez_mileage
                )
            )

            binding.grmBottomsheetTextview.text = String.format(
                "%s - %s",
                getString(R.string.grm_checker_str),
                setBottomsheetMileagesToService(
                    it.current_mileage,
                    it.grm_last_service_mileage,
                    it.grm_mileage
                )
            )

            if (isNotChosenBottomSheet(it.oil_last_service_mileage))
                binding.oilBottomsheetRefreshButton.visibility = View.GONE
            else binding.oilBottomsheetRefreshButton.visibility = View.VISIBLE


            if (isNotChosenBottomSheet(it.air_filt_last_service_mileage))
                binding.airFiltBottomsheetRefreshButton.visibility = View.GONE
            else binding.airFiltBottomsheetRefreshButton.visibility = View.VISIBLE


            if (isNotChosenBottomSheet(it.freez_last_service_mileage))
                binding.freezBottomsheetRefreshButton.visibility = View.GONE
            else binding.freezBottomsheetRefreshButton.visibility = View.VISIBLE


            if (isNotChosenBottomSheet(it.grm_last_service_mileage))
                binding.grmBottomsheetRefreshButton.visibility = View.GONE
            else binding.grmBottomsheetRefreshButton.visibility = View.VISIBLE

        }

        viewModelObj.carURLMutableLiveData.observe(viewLifecycleOwner) {

            setImageIntoViewByUrl(it)

        }


        viewModelObj.warningsMutableLiveData.observe(viewLifecycleOwner) {

            createBottomsheetAnimation(it)

        }

        viewModelObj.updateMileageStateMutableLiveData.observe(viewLifecycleOwner) {

            when (it) {
                UpdateMileageState.EmptyOrNullEnter ->
                    Snackbar.make(
                        binding.root,
                        R.string.empty_updated_current_mileage_snack_bar,
                        Snackbar.LENGTH_INDEFINITE,
                    ).setAction(R.string.ok_rus_str_snack_bar) { item ->

                        item.visibility = View.GONE

                    }.show()

                UpdateMileageState.IncorrectEnter ->
                    Snackbar.make(
                        binding.root,
                        R.string.incorrect_updated_current_mileage_snack_bar,
                        Snackbar.LENGTH_INDEFINITE
                    ).setAction(R.string.ok_rus_str_snack_bar) { item ->

                        item.visibility = View.GONE

                    }.show()


                is UpdateMileageState.SuccessUpdate ->
                    Snackbar.make(
                        binding.root,
                        R.string.successful_current_mileage_update_snack_bar,
                        Snackbar.LENGTH_SHORT
                    ).show()
            }


        }

        binding.oilBottomsheetTextview.setOnClickListener(this)
        binding.airFiltBottomsheetTextview.setOnClickListener(this)
        binding.freezBottomsheetTextview.setOnClickListener(this)
        binding.grmBottomsheetTextview.setOnClickListener(this)

        binding.oilBottomsheetRefreshButton.setOnClickListener(this)
        binding.airFiltBottomsheetRefreshButton.setOnClickListener(this)
        binding.freezBottomsheetRefreshButton.setOnClickListener(this)
        binding.grmBottomsheetRefreshButton.setOnClickListener(this)

        Log.v("Detail", "onCreateView")
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
                    R.id.edit_car_option -> makeEdit()

                }

                return true
            }


        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
        super.onViewCreated(view, savedInstanceState)
    }

    private fun makeEdit() {

        viewModelObj.editCarMutableLiveData.observe(viewLifecycleOwner) {
            val bundle = Bundle()
            bundle.putInt("EXTRA_ID", it.id)
            bundle.putString("EXTRA_BRAND_NAME", it.brand_name)
            bundle.putString("EXTRA_MODEL_NAME", it.model_name)
            bundle.putString("EXTRA_YEAR", it.year)
            bundle.putInt("EXTRA_CURRENT_MILEAGE", it.current_mileage)
            bundle.putString("EXTRA_URL", it.image_url)
            bundle.putInt("EXTRA_OIL_MILEAGE", it.oil_mileage)
            bundle.putInt("EXTRA_OIL_LAST_SERVICE", it.oil_last_service_mileage)
            bundle.putInt("EXTRA_AIR_FILT_MILEAGE", it.air_filt_mileage)
            bundle.putInt("EXTRA_AIR_FILT_LAST_SERVICE", it.air_filt_last_service_mileage)
            bundle.putInt("EXTRA_FREEZ_MILEAGE", it.freez_mileage)
            bundle.putInt("EXTRA_FREEZ_LAST_SERVICE", it.freez_last_service_mileage)
            bundle.putInt("EXTRA_GRM_MILEAGE", it.grm_mileage)
            bundle.putInt("EXTRA_GRM_LAST_SERVICE", it.grm_last_service_mileage)
            parentFragmentManager.beginTransaction()
            parentFragmentManager.commit {
                Log.v("Detail_fragm","commit")
                val carCreating = CarCreatingFragment()
                carCreating.arguments = bundle
                setReorderingAllowed(true)
                replace(R.id.fr, carCreating, "car_create")
                addToBackStack(null)
            }
        }

    }


    private fun createBottomsheetAnimation(warningItem: HashMap<ServiceType, Boolean>) {

        binding.oilBottomsheetTextview.animation =
            setWarningAnimationAttributes(oilWarningAnimation)
        binding.airFiltBottomsheetTextview.animation =
            setWarningAnimationAttributes(airFiltWarningAnimation)
        binding.freezBottomsheetTextview.animation =
            setWarningAnimationAttributes(freezWarningAnimation)
        binding.grmBottomsheetTextview.animation =
            setWarningAnimationAttributes(grmWarningAnimation)

        warningItem.forEach { (serviceType, flag) ->
            if (flag) {

                when (serviceType) {

                    ServiceType.OIL -> startWarningAnimation(
                        oilWarningAnimation,
                        binding.oilBottomsheetTextview
                    )

                    ServiceType.AIR_FILT -> startWarningAnimation(
                        airFiltWarningAnimation,
                        binding.airFiltBottomsheetTextview
                    )

                    ServiceType.FREEZ -> startWarningAnimation(
                        freezWarningAnimation,
                        binding.freezBottomsheetTextview
                    )

                    ServiceType.GRM -> startWarningAnimation(
                        grmWarningAnimation,
                        binding.grmBottomsheetTextview
                    )


                }

            } else {
                when (serviceType) {

                    ServiceType.OIL -> stopWarningAnimation(
                        oilWarningAnimation,
                        binding.oilBottomsheetTextview
                    )

                    ServiceType.AIR_FILT -> stopWarningAnimation(
                        airFiltWarningAnimation,
                        binding.airFiltBottomsheetTextview
                    )

                    ServiceType.FREEZ -> stopWarningAnimation(
                        freezWarningAnimation,
                        binding.freezBottomsheetTextview
                    )

                    ServiceType.GRM -> stopWarningAnimation(
                        grmWarningAnimation,
                        binding.grmBottomsheetTextview
                    )
                }
            }
        }
    }

    private fun startWarningAnimation(
        warningAnimationItem: AlphaAnimation,
        bottomsheetTextViewItem: TextView
    ) {

        warningAnimationItem.start()
        bottomsheetTextViewItem.isClickable = true

    }

    private fun stopWarningAnimation(
        warningAnimationItem: AlphaAnimation,
        bottomsheetTextViewItem: TextView
    ) {

        warningAnimationItem.cancel()
        bottomsheetTextViewItem.isClickable = false

    }

    private fun setWarningAnimationAttributes(warningAnimation: AlphaAnimation): AlphaAnimation {

        warningAnimation.duration = 500 //You can manage the blinking time with this parameter
        warningAnimation.startOffset = 20
        warningAnimation.repeatMode = Animation.REVERSE
        warningAnimation.repeatCount = Animation.INFINITE

        return warningAnimation

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

    private fun setBottomsheetMileagesToService(
        currentMileage: Int,
        lastServiceMileage: Int,
        serviceInterval: Int
    ): String = if (lastServiceMileage == 0) getString(R.string.is_not_chosen_bottomsheet_str)
    else if (serviceInterval - (currentMileage - lastServiceMileage) <= 0) "0"
    else (serviceInterval - (currentMileage - lastServiceMileage)).toString()

    private fun isNotChosenBottomSheet(lastServiceMileage: Int): Boolean {
        return lastServiceMileage == 0

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

    override fun onNegativeDeleteClicked() {

    }

    override fun onUpdatePositiveButtonClicked(mUpdated_current_mileage: String) {
        viewModelObj.onPositiveUpdateCurrentMileage(carId, mUpdated_current_mileage)
    }

    override fun onUpdateNeutralButtonClicked() {

    }

    override fun onClick(clickedView: View?) {
        if (clickedView is TextView) {
            when (clickedView.id) {

                R.id.oil_bottomsheet_textview -> {
                    stopWarningAnimation(oilWarningAnimation, binding.oilBottomsheetTextview)
                    Log.v("Detail_frag_bottom", "oil_txt")
                }
                R.id.freez_bottomsheet_textview -> {
                    stopWarningAnimation(freezWarningAnimation, binding.freezBottomsheetTextview)
                    Log.v("Detail_frag_bottom", "freez_txt")
                }
                R.id.air_filt_bottomsheet_textview -> {
                    stopWarningAnimation(
                        airFiltWarningAnimation,
                        binding.airFiltBottomsheetTextview
                    )
                    Log.v("Detail_frag_bottom", "air_filt_txt")
                }
                R.id.grm_bottomsheet_textview -> {
                    stopWarningAnimation(grmWarningAnimation, binding.grmBottomsheetTextview)
                    Log.v("Detail_frag_bottom", "grm_txt")
                }


            }

        }
        if (clickedView is Button) {

            when (clickedView.id) {

                R.id.oil_bottomsheet_refresh_button -> makeRefreshDialog(ServiceType.OIL)
                R.id.air_filt_bottomsheet_refresh_button -> makeRefreshDialog(ServiceType.AIR_FILT)
                R.id.freez_bottomsheet_refresh_button -> makeRefreshDialog(ServiceType.FREEZ)
                R.id.grm_bottomsheet_refresh_button -> makeRefreshDialog(ServiceType.GRM)

            }

        }
    }

    private fun makeRefreshDialog(serviceType: ServiceType) {

        val alertDialogObj: DialogFragment = StartRefreshMileageToServiceAlertDialog(serviceType)
        alertDialogObj.show(parentFragmentManager, "refresh")


    }

    override fun onPositiveRefreshMileageClicked(serviceType: ServiceType) {

        viewModelObj.onRefreshMileageButtonClick(
            serviceType
        )
        Log.v("Detail_frag_refresh_dialog", "$serviceType Positive_clicked")

    }

    override fun onNegativeRefreshMileageClicked() {
        Log.v("Detail_frag_refresh_dialog", "Negative_clicked")
    }


}