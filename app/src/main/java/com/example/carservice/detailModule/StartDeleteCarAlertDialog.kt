package com.example.carservice.detailModule

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import androidx.fragment.app.DialogFragment

class StartDeleteCarAlertDialog : DialogFragment() {


    interface OnDeleteListener{

        fun onPositiveDeleteClicked()
        fun onNegativeDeleteClicked()

    }

    private lateinit var mListener: OnDeleteListener

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mListener = try {
            parentFragmentManager.findFragmentByTag("detail") as OnDeleteListener
        } catch (e: Exception) {
            throw ClassCastException(
                parentFragmentManager.findFragmentByTag("detail")
                    .toString() + " onListener must implemented"
            )
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Вы действительно хотите удалить этот автомобиль")
        builder.setPositiveButton("Да") { _: DialogInterface?, _: Int ->

            mListener.onPositiveDeleteClicked()

        }
            .setNegativeButton(
                "Нет"
            ) { dialogInterface: DialogInterface, _: Int ->

                mListener.onNegativeDeleteClicked()
                dialogInterface.dismiss()

            }
        isCancelable = false
        return builder.create()
    }
}