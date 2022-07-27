package com.example.carservice

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.CheckBox
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.carservice.databinding.CarCreatingBinding


class CarCreatingFragment : Fragment(), StartCheckBoxMileageAlertDialog.OnEnterListener,
    View.OnClickListener {


    private val sharedViewModel: CarCreatingViewModel by activityViewModels()

    private var _binding: CarCreatingBinding? = null
    private val binding get() = _binding!!
    private var serviceTypeIs = ServiceType.OIL


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {


        _binding = CarCreatingBinding.inflate(inflater, container, false)



        sharedViewModel.brandNameMutableLiveData.observe(viewLifecycleOwner) {

            binding.brandAutCompTxtvw.setAdapter(
                ArrayAdapter(
                    context!!,
                    R.layout.drop_down_item,
                    it
                )
            )

        }


        sharedViewModel.modelNameByBrandMutableLiveData.observe(viewLifecycleOwner) {

            binding.modelAutCompTxtvw.setAdapter(
                ArrayAdapter(
                    context!!,
                    R.layout.drop_down_item,
                    it
                )
            )

        }

        binding.brandAutCompTxtvw.setOnItemClickListener { _, _, position, _ ->

            binding.modelAutCompTxtvw.setText("")

            sharedViewModel.getModelNameByBrand(position + 1)


        }



        binding.currentMileageView.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus)
                binding.currentMileageView.hint = ""
            else
                binding.currentMileageView.hint = getString(R.string.current_mileage_input_str)

        }

        binding.yearAutCompTxtvw.setAdapter(
            ArrayAdapter(
                context!!,
                R.layout.drop_down_item,
                sharedViewModel.getListOfYears()
            )
        )


        binding.oilCheckBox.setOnClickListener(this)
        binding.airFiltCheckBox.setOnClickListener(this)
        binding.freezCheckBox.setOnClickListener(this)
        binding.grmCheckBox.setOnClickListener(this)


        binding.saveButton.setOnClickListener {
            addNewCar()
        }

        binding.currentMileageView.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (s.toString() != "") {
                    onCurrentMileageChanged(s)
                }

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }
        })


        return binding.root
    }


    private fun onCurrentMileageChanged(s: Editable?) {
        sharedViewModel.onCurrentMileageChanged(s)
    }


    // TODO: Перенести логигу во viewModel.addCar(), что бы метод возвращал события в фрагмент, а не некоретный ввод обрабатывлся здесь
    private fun addNewCar() {
        val carBrandName = binding.brandAutCompTxtvw.text.toString()
        val carModelName = binding.modelAutCompTxtvw.text.toString()
        val carYear = binding.yearAutCompTxtvw.text.toString()
        val carCurrentMileage: Int
        val isIncorrectCurrentMileage: Boolean =
            binding.currentMileageView.text.toString() == "" || binding.currentMileageView.text.toString()
                .toInt() == 0
        val isAnyViewEmpty: Boolean =
            carBrandName == "" || carModelName == "" || carYear == ""

        if (isAnyViewEmpty) {
            Toast.makeText(context, "Заполните обязательные поля (*)", Toast.LENGTH_SHORT).show()

        } else if (isIncorrectCurrentMileage) {

            Toast.makeText(context, "Неверное значение текущего пробега", Toast.LENGTH_SHORT).show()


        } else {
            carCurrentMileage = binding.currentMileageView.text.toString().toInt()

            val carEntity = CarsItemTable(
                brand_name = carBrandName,
                model_name = carModelName,
                year = carYear,
                current_mileage = carCurrentMileage,
            )


            if (sharedViewModel.addCar(carEntity) != -(1).toLong()) {
                Toast.makeText(context, R.string.successful_insert, Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(context, R.string.unsuccessful_insert, Toast.LENGTH_LONG).show()
            }


        }

    }


    private fun checkboxOffById(serviceType: ServiceType) {
        when (serviceType) {
            ServiceType.OIL -> {
                binding.oilCheckBox.isChecked = false
                sharedViewModel.onCheckBoxOff(ServiceType.OIL)


            }
            ServiceType.AIR_FILT -> {
                binding.airFiltCheckBox.isChecked = false
                sharedViewModel.onCheckBoxOff(ServiceType.AIR_FILT)

            }
            ServiceType.FREEZ -> {
                binding.freezCheckBox.isChecked = false
                sharedViewModel.onCheckBoxOff(ServiceType.FREEZ)

            }
            ServiceType.GRM -> {
                binding.grmCheckBox.isChecked = false
                sharedViewModel.onCheckBoxOff(ServiceType.GRM)

            }

        }


    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onPositiveButtonClicked(
        service_interval: Int,
        last_service_mileage: Int,
        current_mileage_checker: Boolean
    ) {
        val isIncorrectEnter: Boolean =
            service_interval == -1 || service_interval == 0 || last_service_mileage == -1 || !current_mileage_checker && last_service_mileage == 0

        val isIncorrectCurrentMileage: Boolean = binding.currentMileageView.text.toString() == "" || binding.currentMileageView.text.toString().toInt() <= 0

        if (isIncorrectEnter) {
            Toast.makeText(
                context,
                "Введите положительные значения полей",
                Toast.LENGTH_SHORT
            ).show()
            checkboxOffById(serviceTypeIs)


        } else if (current_mileage_checker && isIncorrectCurrentMileage) {
            Toast.makeText(
                context,
                "Введено неверное значение текущего пробега",
                Toast.LENGTH_SHORT
            ).show()
            checkboxOffById(serviceTypeIs)

        } else if (current_mileage_checker) {
            sharedViewModel.onPositiveButtonClickedCheckBox(
                service_interval,
                Integer.parseInt(binding.currentMileageView.text.toString()),
                current_mileage_checker,
                serviceTypeIs
            )


        } else {
            sharedViewModel.onPositiveButtonClickedCheckBox(
                service_interval,
                last_service_mileage,
                current_mileage_checker,
                serviceTypeIs
            )

        }

        Log.i("PositiveFragment", service_interval.toString())
    }

    override fun onNeutralButtonClicked() {

        checkboxOffById(serviceTypeIs)

        sharedViewModel.onNeutralButtonClickedCheckBox(serviceTypeIs)
    }


    override fun onClick(p0: View?) {
        if (p0 is CheckBox) {
            val checked: Boolean = p0.isChecked

            when (p0.id) {
                R.id.oil_checkBox -> {
                    if (checked) {
                        sharedViewModel.startAlertDialog(parentFragmentManager, ServiceType.OIL)
                        serviceTypeIs = ServiceType.OIL
                    } else {
                        // TODO: Придумать что нибудь с "хард" переменными.  upd: Исправленно, протестить
                        checkboxOffById(ServiceType.OIL)
                    }
                }
                R.id.air_filt_checkBox -> {
                    if (checked) {
                        sharedViewModel.startAlertDialog(
                            parentFragmentManager,
                            ServiceType.AIR_FILT
                        )
                        serviceTypeIs = ServiceType.AIR_FILT
                    } else {
                        checkboxOffById(ServiceType.AIR_FILT)
                    }
                }
                R.id.freez_checkBox -> {
                    if (checked) {
                        sharedViewModel.startAlertDialog(parentFragmentManager, ServiceType.FREEZ)
                        serviceTypeIs = ServiceType.FREEZ
                    } else {
                        checkboxOffById(ServiceType.FREEZ)
                    }


                }

                R.id.grm_checkBox -> {
                    if (checked) {
                        sharedViewModel.startAlertDialog(parentFragmentManager, ServiceType.GRM)
                        serviceTypeIs = ServiceType.GRM
                    } else {
                        checkboxOffById(ServiceType.GRM)
                    }


                }


            }
        }
    }


}