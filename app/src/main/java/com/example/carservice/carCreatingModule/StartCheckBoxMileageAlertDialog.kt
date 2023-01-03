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


class StartCheckBoxMileageAlertDialog : DialogFragment() {

    private var _binding: CheckBoxAlertDialogCustomLayoutBinding? = null
    private val binding get() = _binding!!

    interface OnEnterListener {
        fun onPositiveButtonClicked(
            service_interval: String,
            last_service_mileage: String,
            current_mileage_checker: Boolean
        )

        fun onNeutralButtonClicked()
    }


    private lateinit var mListener: OnEnterListener

    override fun onAttach(context: Context) {
        super.onAttach(context)

        mListener = try {
            parentFragmentManager.findFragmentByTag("car_create") as OnEnterListener
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
        super.onCreateDialog(savedInstanceState)
        _binding = CheckBoxAlertDialogCustomLayoutBinding.inflate(layoutInflater)
        val builder = AlertDialog.Builder(binding.root.context)

        val serviceType =
            requireArguments().getString("SERVICE_TYPE_EXTRA")




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
            ServiceType.OIL.toString() -> builder.setTitle("Моторное масло")
            ServiceType.AIR_FILT.toString() -> builder.setTitle("Воздушный фильтр")
            ServiceType.FREEZ.toString() -> builder.setTitle("Антифриз")
            ServiceType.GRM.toString() -> builder.setTitle("ГРМ")
        }



        builder.setView(binding.root)
        builder.setPositiveButton(R.string.ok_rus_str) { _, _ ->

            if (!binding.currentMileageCheckbox.isChecked) {
                mListener.onPositiveButtonClicked(
                    binding.serviceIntervalEditText.text.toString(),
                    binding.lastServiceMileageEditText.text.toString(),
                    false
                )

            } else {
                mListener.onPositiveButtonClicked(
                    binding.serviceIntervalEditText.text.toString(),
                    0.toString(),
                    true
                )
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