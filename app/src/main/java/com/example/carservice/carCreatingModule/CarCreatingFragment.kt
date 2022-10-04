package com.example.carservice.carCreatingModule

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.CheckBox
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.carservice.R
import com.example.carservice.appModule.AppRepository
import com.example.carservice.appModule.ServiceType
import com.example.carservice.dataBase.AppDataBase
import com.example.carservice.databinding.CarCreatingBinding
import com.example.carservice.pixabayAPI.RetrofitService
import com.google.android.material.snackbar.Snackbar


class CarCreatingFragment : Fragment(), StartCheckBoxMileageAlertDialog.OnEnterListener,
    View.OnClickListener {


    private val viewModelObj: CarCreatingViewModel by viewModels {
        CarCreatingViewModelFactory(
            AppRepository(
                AppDataBase.getDatabase(requireContext()),
                RetrofitService.invoke()
            )

        )

    }

    private var _binding: CarCreatingBinding? = null
    private val binding get() = _binding!!
    private var serviceTypeIs = ServiceType.OIL


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {


        _binding = CarCreatingBinding.inflate(inflater, container, false)


        viewModelObj.carCreatingViewModelInit()

        viewModelObj.brandNameMutableLiveData.observe(viewLifecycleOwner) {

            binding.brandAutCompTextView.setAdapter(
                ArrayAdapter(
                    requireContext(),
                    R.layout.drop_down_item,
                    it
                )
            )

        }


        viewModelObj.modelNameByBrandMutableLiveData.observe(viewLifecycleOwner) {

            binding.modelAutCompTextView.setAdapter(
                ArrayAdapter(
                    requireContext(),
                    R.layout.drop_down_item,
                    it
                )
            )

        }


        viewModelObj.checkBoxStateMutableLiveData.observe(viewLifecycleOwner) {
            when (it) {
                is CheckBoxState.IncorrectEnter -> {
                    Snackbar.make(
                        binding.root,
                        "Поля должны быть заполнены положительными значениями",
                        Snackbar.LENGTH_INDEFINITE
                    ).setAction(R.string.ok_rus_str_snack_bar) { item ->

                        item.visibility = View.GONE

                    }.show()
                    checkboxOffById(serviceTypeIs)
                }
                is CheckBoxState.IncorrectCurrentMileage -> {
                    Snackbar.make(
                        binding.root,
                        R.string.incorrect_current_mileage_str,
                        Snackbar.LENGTH_INDEFINITE
                    ).setAction(R.string.ok_rus_str_snack_bar) { item ->

                        item.visibility = View.GONE

                    }.show()
                    checkboxOffById(serviceTypeIs)


                }
                is CheckBoxState.SuccessCurrentMileageCheckBoxOff -> Log.v(
                    "Car_creating_frag",
                    "SuccessCurrentMileageCheckBoxOff"
                )
                is CheckBoxState.SuccessCurrentMileageCheckBoxOn -> Log.v(
                    "Car_creating_frag",
                    "SuccessCurrentMileageCheckBoxOn"
                )
            }


        }


        binding.modelAutCompTextView.setOnClickListener {
            if (binding.modelAutCompTextView.adapter == null) {
                binding.brandTextLayout.error = getString(R.string.brand_not_chosen)
            } else {
                binding.brandTextLayout.error = null
            }


        }


        binding.brandAutCompTextView.setOnItemClickListener { _, _, position, _ ->

            binding.modelAutCompTextView.setText("")
            binding.brandTextLayout.error = null
            viewModelObj.getModelNameByBrand(position + 1)


        }


        viewModelObj.addingStateMutableLiveData.observe(viewLifecycleOwner) {
            when (it) {

                is AddingState.AnyViewEmpty -> Snackbar.make(
                    binding.root,
                    R.string.fill_required_fields,
                    Snackbar.LENGTH_INDEFINITE
                ).setAction(R.string.ok_rus_str_snack_bar) { item ->

                    item.visibility = View.GONE

                }.show()

                is AddingState.IncorrectCurrentMileage -> Snackbar.make(
                    binding.root,
                    R.string.incorrect_current_mileage_str,
                    Snackbar.LENGTH_INDEFINITE
                ).setAction(R.string.ok_rus_str_snack_bar) { item ->

                    item.visibility = View.GONE

                }.show()

                is AddingState.Success -> {
                    Snackbar.make(binding.root, R.string.successful_insert, Snackbar.LENGTH_LONG)
                        .show()

                    val fragmentManager = parentFragmentManager
                    fragmentManager.popBackStack()

                }
                is AddingState.UnSuccess -> Snackbar.make(
                    binding.root,
                    R.string.unsuccessful_insert,
                    Snackbar.LENGTH_INDEFINITE
                ).setAction(R.string.ok_rus_str_snack_bar) { item ->

                    item.visibility = View.GONE

                }.show()


            }


        }


        binding.currentMileageEditText.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus)
                binding.currentMileageEditText.hint = ""
            else
                binding.currentMileageEditText.hint = getString(R.string.current_mileage_input_str)

        }

        binding.yearAutCompTextView.setAdapter(
            ArrayAdapter(
                requireContext(),
                R.layout.drop_down_item,
                viewModelObj.getListOfYears()
            )
        )


        binding.oilCheckBox.setOnClickListener(this)
        binding.airFiltCheckBox.setOnClickListener(this)
        binding.freezCheckBox.setOnClickListener(this)
        binding.grmCheckBox.setOnClickListener(this)


        binding.saveButton.setOnClickListener {
            addNewCarToDataBase()

        }

        binding.currentMileageEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (s.toString() != "") {
                    onCurrentMileageChanged(s)
                }
                setErrorEnterOnLayout(binding.currentMileageEditText, s)

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }
        })


        return binding.root
    }

    private fun setErrorEnterOnLayout(editText: EditText, s: Editable?) {


        if (s != null) {

            if (s.startsWith("0") || s.startsWith("-")) {

                editText.error = getString(R.string.incorrect_enter)

            } else {
                editText.error = null
            }
        }


    }

    private fun onCurrentMileageChanged(s: Editable?) {
        viewModelObj.onCurrentMileageChanged(s)
    }


    private fun addNewCarToDataBase() {
        viewModelObj.onCarAdding(
            binding.brandAutCompTextView.text.toString(),
            binding.modelAutCompTextView.text.toString(),
            binding.yearAutCompTextView.text.toString(),
            binding.currentMileageEditText.text.toString()
        )

    }


    private fun checkboxOffById(serviceType: ServiceType) {
        when (serviceType) {
            ServiceType.OIL -> {
                binding.oilCheckBox.isChecked = false
                viewModelObj.onCheckBoxOff(ServiceType.OIL)


            }
            ServiceType.AIR_FILT -> {
                binding.airFiltCheckBox.isChecked = false
                viewModelObj.onCheckBoxOff(ServiceType.AIR_FILT)

            }
            ServiceType.FREEZ -> {
                binding.freezCheckBox.isChecked = false
                viewModelObj.onCheckBoxOff(ServiceType.FREEZ)

            }
            ServiceType.GRM -> {
                binding.grmCheckBox.isChecked = false
                viewModelObj.onCheckBoxOff(ServiceType.GRM)

            }

        }


    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onPositiveButtonClicked(
        service_interval: String,
        last_service_mileage: String,
        current_mileage_checker: Boolean
    ) {

        viewModelObj.onPositiveButtonClickedCheckBox(
            service_interval,
            last_service_mileage,
            current_mileage_checker,
            binding.currentMileageEditText.text,
            serviceTypeIs
        )

    }


    override fun onNeutralButtonClicked() {

        checkboxOffById(serviceTypeIs)
        viewModelObj.onNeutralButtonClickedCheckBox(serviceTypeIs)

    }


    override fun onClick(clickedView: View?) {

        if (clickedView is CheckBox) {

            val isChecked: Boolean = clickedView.isChecked

            when (clickedView.id) {
                R.id.oil_checkBox ->
                    serviceTypeIs = ServiceType.OIL

                R.id.air_filt_checkBox ->
                    serviceTypeIs = ServiceType.AIR_FILT

                R.id.freez_checkBox ->
                    serviceTypeIs = ServiceType.FREEZ

                R.id.grm_checkBox ->
                    serviceTypeIs = ServiceType.GRM

            }

            handleCheckBox(serviceTypeIs, isChecked)

        }
    }

    private fun handleCheckBox(serviceType: ServiceType, isChecked: Boolean) {
        if (isChecked) {

            viewModelObj.startAlertDialog(parentFragmentManager, serviceType)

        } else {

            checkboxOffById(serviceType)

        }
    }


}