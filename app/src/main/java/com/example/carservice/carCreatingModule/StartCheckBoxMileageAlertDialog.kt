package com.example.carservice.carCreatingModule

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.CompoundButton
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.example.carservice.R
import com.example.carservice.appModule.ServiceType
import com.example.carservice.databinding.CheckBoxAlertDialogCustomLayoutBinding
import com.google.android.material.textfield.TextInputLayout

class StartCheckBoxMileageAlertDialog(private val serviceType: ServiceType) : DialogFragment() {

    private var _binding: CheckBoxAlertDialogCustomLayoutBinding? = null
    private val binding get() = _binding!!

    interface OnEnterListener {
        fun onPositiveButtonClicked(
            service_interval: Int,
            last_service_mileage: Int,
            current_mileage_checker: Boolean
        )

        fun onNeutralButtonClicked()
    }


    private lateinit var mListener: OnEnterListener

    override fun onAttach(context: Context) {
        super.onAttach(context)

        try {
            mListener = parentFragmentManager.findFragmentByTag("car_create") as OnEnterListener
        } catch (e: Exception) {
            throw ClassCastException(
                parentFragmentManager.findFragmentByTag("car_create")
                    .toString() + " onListener must implemented"
            )

        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        _binding = CheckBoxAlertDialogCustomLayoutBinding.inflate(layoutInflater)
        val builder = AlertDialog.Builder(binding.root.context)



        binding.serviceIntervalEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun afterTextChanged(s: Editable?) {
                setErrorEnterOnLayout(binding.serviceIntervalTextLayout, s)
            }


        })

        binding.lastServiceMileageEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun afterTextChanged(s: Editable?) {

                setErrorEnterOnLayout(binding.lastServiceMileageTextLayout, s)

            }
        })




        binding.currentMileageCheckbox.setOnCheckedChangeListener { _: CompoundButton?, b: Boolean ->
            if (b) {
                binding.lastServiceMileageTextLayout.visibility = View.GONE
            } else {
                binding.lastServiceMileageTextLayout.visibility = View.VISIBLE
            }
        }


        when (serviceType) {
            ServiceType.OIL -> builder.setTitle("Моторное масло")
            ServiceType.AIR_FILT -> builder.setTitle("Воздушный фильтр")
            ServiceType.FREEZ -> builder.setTitle("Антифриз")
            ServiceType.GRM -> builder.setTitle("ГРМ")
        }



        builder.setView(binding.root)
        builder.setPositiveButton(R.string.ok_rus_str) { _, _ ->

            val serviceInterval =
                binding.serviceIntervalEditText.text.toString()
            val lastServiceMileage =
                binding.lastServiceMileageEditText.text.toString()

            val isEmptyEnter: Boolean =
                serviceInterval == "" || !binding.currentMileageCheckbox.isChecked && lastServiceMileage == ""

            if (isEmptyEnter) {

                mListener.onPositiveButtonClicked(-1, -1, false)

            } else {
                if (!binding.currentMileageCheckbox.isChecked) {
                    mListener.onPositiveButtonClicked(
                        binding.serviceIntervalEditText.text.toString().toInt(),
                        binding.lastServiceMileageEditText.text.toString().toInt(),
                        false
                    )

                } else {
                    mListener.onPositiveButtonClicked(
                        binding.serviceIntervalEditText.text.toString().toInt(),
                        0,
                        true
                    )
                }
            }
        }


            .setNeutralButton(R.string.cancel_rus_str) { dialogInterface, _ ->
                Log.i("NeutralCB", "Click")
                dialogInterface.dismiss()
                mListener.onNeutralButtonClicked()


            }

        isCancelable = false
        return builder.create()
    }

    private fun setErrorEnterOnLayout(editText: TextInputLayout, s: Editable?) {


        if (s != null) {

            if (s.startsWith("0") || s.startsWith("-")) {

                editText.error = getString(R.string.incorrect_enter)

            } else {
                editText.error = null
            }
        }


    }


}