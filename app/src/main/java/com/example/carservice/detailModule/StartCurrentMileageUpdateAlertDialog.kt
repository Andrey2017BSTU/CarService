package com.example.carservice.detailModule

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import com.example.carservice.R
import com.example.carservice.appModule.AppRepository
import com.example.carservice.dataBase.AppDataBase
import com.example.carservice.databinding.CurrentMileageUpdateAllerDialogLayoutBinding
import com.example.carservice.pixabayAPI.RetrofitService
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputLayout

class StartCurrentMileageUpdateAlertDialog : DialogFragment() {
    private var _binding: CurrentMileageUpdateAllerDialogLayoutBinding? = null
    private val binding get() = _binding!!
    private val viewModelObj: DetailViewModel by viewModels {
        DetailViewModelFactory(
            AppRepository(
                AppDataBase.getDatabase(requireContext()),
                RetrofitService.invoke()
            )

        )


    }

    interface OnEnterListener {

        fun onUpdatePositiveButtonClicked(updated_current_mileage: Int)
        fun onUpdateNeutralButtonClicked()

    }

    private lateinit var mListener: OnEnterListener

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mListener = try {
            parentFragmentManager.findFragmentByTag("detail") as OnEnterListener
        } catch (e: Exception) {
            throw ClassCastException(
                parentFragmentManager.findFragmentByTag("detail")
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

        _binding = CurrentMileageUpdateAllerDialogLayoutBinding.inflate(layoutInflater)
        viewModelObj.detailViewModelInit(arguments)
        val builder = AlertDialog.Builder(binding.root.context)

        binding.updatedCurrentMileageInputText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun afterTextChanged(s: Editable?) {

                setErrorEnterOnLayout(binding.updatedCurrentMileageInputLayout, s)

            }


        })



        viewModelObj.carCurrentMileageMutableLiveData.observe(this) {

            binding.oldCurrentMileage.text = String.format(
                "%s: %s",
                getString(R.string.current_mileage_check_box_dialog_str),
                it
            )

        }

        builder.setTitle(getString(R.string.current_mileage_update_input_str))
        builder.setView(binding.root)


        builder.setPositiveButton(R.string.ok_rus_str) { dialogInterface: DialogInterface, _: Int ->


            val mUpdatedCurrentMileage =
                binding.updatedCurrentMileageInputText.text.toString().toInt()
            if (mUpdatedCurrentMileage <= 0) {

                Snackbar.make(
                    binding.root,
                    R.string.incorrect_updated_current_mileage_snack_bar,
                    Snackbar.LENGTH_INDEFINITE
                ).show()
            } else {

                mListener.onUpdatePositiveButtonClicked(mUpdatedCurrentMileage)
                dialogInterface.dismiss()

            }
        }
            .setNeutralButton(
                R.string.cancel_rus_str
            ) { dialogInterface: DialogInterface, _: Int ->

                mListener.onUpdateNeutralButtonClicked()
                dialogInterface.dismiss()


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