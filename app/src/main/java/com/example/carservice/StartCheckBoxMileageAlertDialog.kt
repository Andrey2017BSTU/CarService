package com.example.carservice

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.CompoundButton
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.example.carservice.databinding.CheckBoxAlertDialogCustomLayoutBinding

class StartCheckBoxMileageAlertDialog(private val serviceType: ServiceType) : DialogFragment() {


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


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {


            val binding =
                CheckBoxAlertDialogCustomLayoutBinding.inflate(LayoutInflater.from(context))
            val builder = AlertDialog.Builder(it)





            binding.currentMileageCheckbox.setOnCheckedChangeListener { _: CompoundButton?, b: Boolean ->
                if (b) {
                    binding.lastServiceMileageTextView.visibility = View.GONE
                } else {
                    binding.lastServiceMileageTextView.visibility = View.VISIBLE
                }
            }


            when (serviceType) {
                ServiceType.OIL -> builder.setTitle("Моторное масло")
                ServiceType.AIR_FILT -> builder.setTitle("Воздушный фильтр")
                ServiceType.FREEZ -> builder.setTitle("Антифриз")
                ServiceType.GRM -> builder.setTitle("ГРМ")
            }



            builder.setView(binding.root)
            builder.setPositiveButton("OK") { _, _ ->

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


                .setNeutralButton("Отмена") { dialogInterface, _ ->
                    Log.i("NeutralCB", "Click")
                    dialogInterface.dismiss()
                    mListener.onNeutralButtonClicked()


                }

            isCancelable = false
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }


}