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
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.carservice.R
import com.example.carservice.appModule.AddingState
import com.example.carservice.appModule.ServiceType
import com.example.carservice.dataBase.AppDataBase
import com.example.carservice.databinding.CarCreatingBinding
import com.example.carservice.pixabayAPI.RetrofitService


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


        sharedViewModel.init(AppDataBase.getDatabase(requireContext()), RetrofitService.invoke())

        sharedViewModel.brandNameMutableLiveData.observe(viewLifecycleOwner) {

            binding.brandAutCompTxtvw.setAdapter(
                ArrayAdapter(
                    requireContext(),
                    R.layout.drop_down_item,
                    it
                )
            )

        }


        sharedViewModel.modelNameByBrandMutableLiveData.observe(viewLifecycleOwner) {

            binding.modelAutCompTxtvw.setAdapter(
                ArrayAdapter(
                    requireContext(),
                    R.layout.drop_down_item,
                    it
                )
            )

        }

        sharedViewModel.stateMutableLiveData.observe(viewLifecycleOwner){
            when(it) {

                is AddingState.AnyViewEmpty -> Toast.makeText(context, "Заполните обязательные поля (*)", Toast.LENGTH_SHORT).show()
                is AddingState.IncorrectCurrentMileage -> Toast.makeText(context, "Неверное значение текущего пробега", Toast.LENGTH_SHORT).show()
                is AddingState.Success ->{
                    Toast.makeText(context, R.string.successful_insert, Toast.LENGTH_LONG).show()
                    val fragmentManager = parentFragmentManager
                    // TODO: Найти решение по-лучше
                    fragmentManager.popBackStack()
                }
                is AddingState.UnSuccess ->  Toast.makeText(context, R.string.unsuccessful_insert, Toast.LENGTH_LONG).show()


            }


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
                requireContext(),
                R.layout.drop_down_item,
                sharedViewModel.getListOfYears()
            )
        )


        binding.oilCheckBox.setOnClickListener(this)
        binding.airFiltCheckBox.setOnClickListener(this)
        binding.freezCheckBox.setOnClickListener(this)
        binding.grmCheckBox.setOnClickListener(this)


        binding.saveButton.setOnClickListener {
            addNewCarToDataBase()

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


    // TODO: Перенести логигу во viewModel.addCar(), что бы метод возвращал события в фрагмент, а не некоретный ввод обрабатывлся здесь: Сделал, протестить
    private fun addNewCarToDataBase() {

        sharedViewModel.onCarAdding(binding.brandAutCompTxtvw.text.toString(),binding.modelAutCompTxtvw.text.toString(),binding.yearAutCompTxtvw.text.toString(),binding.currentMileageView.text.toString())

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

        val isIncorrectCurrentMileage: Boolean =
            binding.currentMileageView.text.toString() == "" || binding.currentMileageView.text.toString()
                .toInt() <= 0
// TODO: Развернуть цепочку условий полиморфизмом
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
// TODO: Развернуть цепочку условий полиморфизмом
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