package com.example.carservice.detailModule

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.example.carservice.appModule.ServiceType

class StartRefreshMileageToServiceAlertDialog(private val serviceType: ServiceType) :
    DialogFragment() {

    interface OnRefreshListener {
        fun onPositiveRefreshMileageClicked(serviceType: ServiceType)
        fun onNegativeRefreshMileageClicked()
    }

    private lateinit var mListener: OnRefreshListener

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mListener = try {
            parentFragmentManager.findFragmentByTag("detail") as OnRefreshListener
        } catch (e: Exception) {
            throw ClassCastException(
                parentFragmentManager.findFragmentByTag("detail")
                    .toString() + " onListener must implemented"
            )
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val builder = AlertDialog.Builder(context)

        when (serviceType) {
            ServiceType.OIL -> builder.setTitle("Подтвердить замену масла?")
            ServiceType.AIR_FILT -> builder.setTitle("Подтвердить замену воздушного фильтра?")
            ServiceType.FREEZ -> builder.setTitle("Подтвердить замену антифриза?")
            ServiceType.GRM -> builder.setTitle("Подтвердить замену ГРМ?")
        }

        builder.setPositiveButton(
            "Да"
        ) { dialogInterface: DialogInterface?, i: Int ->
            mListener.onPositiveRefreshMileageClicked(serviceType)
        }
            .setNegativeButton(
                "Нет"
            ) { dialogInterface: DialogInterface, i: Int ->
                mListener.onNegativeRefreshMileageClicked()
                dialogInterface.dismiss()
            }
        isCancelable = false
        return builder.create()
    }


}