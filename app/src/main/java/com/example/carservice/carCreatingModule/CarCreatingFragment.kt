package com.example.carservice.carCreatingModule

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.CheckBox
import android.widget.EditText
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.example.carservice.R
import com.example.carservice.appModule.AppRepository
import com.example.carservice.appModule.ServiceType
import com.example.carservice.dataBase.AppDataBase
import com.example.carservice.databinding.CarCreatingBinding
import com.example.carservice.pixabayAPI.RetrofitService
import com.google.android.material.snackbar.Snackbar
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

// TODO: Работу с файлами перенести в репозиторий(другая ветка)
class CarCreatingFragment : Fragment(), StartCheckBoxMileageAlertDialog.OnEnterListener,
    View.OnClickListener {

    private var _binding: CarCreatingBinding? = null
    private val binding get() = _binding!!

    private val viewModelObj: CarCreatingViewModel by viewModels {
        CarCreatingViewModelFactory(
            AppRepository(
                AppDataBase.getDatabase(requireContext()),
                RetrofitService.invoke()
            )

        )

    }

    private lateinit var currentPhotoPath: String


    private val chooserResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {

                val dataFromResult: Intent? = result.data
                Log.v("Car_creating_frag", "result OK" + " " + dataFromResult.toString())

                if (dataFromResult != null) {
                    if (dataFromResult.data == null) {
                        viewModelObj.onImageAdded(currentPhotoPath)

                    } else {

                        viewModelObj.onImageAdded(dataFromResult.data.toString())

                    }
                }
            }
        }


    private var serviceTypeIs = ServiceType.OIL


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {


        _binding = CarCreatingBinding.inflate(inflater, container, false)



        if (arguments != null) {
            viewModelObj.carEditingViewModelInit(arguments)

        } else {
            viewModelObj.carCreatingViewModelInit()
        }
// TODO: Поискать другой способ получения состояния экрана (land/port)
        if (savedInstanceState != null) {
            viewModelObj.onActivityRecreatedByScreenRotation()

        } else {

            viewModelObj.onActivityRecreatedByFirstOpen()
        }


        viewModelObj.brandNameMutableLiveData.observe(viewLifecycleOwner) {

            binding.brandAutCompTextView.setAdapter(
                ArrayAdapter(
                    requireContext(),
                    R.layout.drop_down_item,
                    it
                )
            )
            Log.v("Car_creating_adapt", "brand")
        }

        viewModelObj.modelNameByBrandMutableLiveData.observe(viewLifecycleOwner) {

            binding.modelAutCompTextView.setAdapter(
                ArrayAdapter(
                    requireContext(),
                    R.layout.drop_down_item,
                    it
                )
            )
            Log.v("Car_creating_adapt", "model")
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
            if (binding.brandAutCompTextView.text.toString().isBlank()) {
                binding.brandTextLayout.error = getString(R.string.brand_not_chosen)
            } else {
                binding.brandTextLayout.error = null
            }

        }


        binding.addPhotoButton.setOnClickListener {

            chooserResultLauncher.launch(createChooserIntent())
            Log.v("Car_creating_frag", "add photo clicked")


        }



        binding.brandAutCompTextView.setOnItemClickListener { _, _, position, _ ->

            binding.modelAutCompTextView.setText("")
            binding.brandTextLayout.error = null
            viewModelObj.getModelNameByBrandId(position + 1)

        }

        binding.brandAutCompTextView.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            // TODO: Протестить разницу
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                Log.v("car_Creating_Selected_Brand", p0.toString())
            }

            override fun afterTextChanged(p0: Editable?) {
                if (p0 != null){
                    if (p0.isNotEmpty()){
                        viewModelObj.getModelNameByBrandName(p0.toString())
                    }

                }

            }
        })

        viewModelObj.addingOrEditingStateMutableLiveData.observe(viewLifecycleOwner) {
            handle(it)


        }




        binding.currentMileageEditText.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus)
                binding.currentMileageEditText.hint = ""
            else
                binding.currentMileageEditText.hint = getString(R.string.current_mileage_input_str)

        }




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
                    Log.v("TextWatcher",s.toString())
                }
                setErrorEnterOnLayout(binding.currentMileageEditText, s)

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }
        })

        viewModelObj.imageUriMutableLiveData.observe(viewLifecycleOwner) {
            Glide.with(binding.root).load(it).into(binding.imageView)

        }


        viewModelObj.updatableCurrentMileageMutableLiveDataSingle.observe(viewLifecycleOwner){

            binding.currentMileageEditText.setText(it.toString())
            Log.v("updatableCurrentMileageMutableLiveData",it.toString())

        }

        viewModelObj.updatableCarBrandNameMutableLiveDataSingle.observe(viewLifecycleOwner){
            binding.brandAutCompTextView.setText(it)

        }

        viewModelObj.updatableCarModelNameMutableLiveDataSingle.observe(viewLifecycleOwner){
            binding.modelAutCompTextView.setText(it)
        }

        viewModelObj.updatableYearMutableLiveDataSingle.observe(viewLifecycleOwner){
            binding.yearAutCompTextView.setText(it)

        }

        viewModelObj.listOfYearsMutableLiveData.observe(viewLifecycleOwner){
            binding.yearAutCompTextView.setAdapter(
                ArrayAdapter(
                    requireContext(),
                    R.layout.drop_down_item,
                    it
                )
            )

        }


        return binding.root
    }


    private fun handle(_state: AddingOrEditingState) {
        val snackbarString: String
        val snackbarLength: Int
        var snackbarActionString = ""

        when (_state) {
            AddingOrEditingState.AnyViewEmpty -> {
                snackbarString = getString(R.string.fill_required_fields)
                snackbarLength = Snackbar.LENGTH_INDEFINITE
                snackbarActionString = getString(R.string.ok_rus_str_snack_bar)
            }
            AddingOrEditingState.IncorrectCurrentMileage -> {
                snackbarString = getString(R.string.incorrect_current_mileage_str)
                snackbarLength = Snackbar.LENGTH_INDEFINITE
                snackbarActionString = getString(R.string.ok_rus_str_snack_bar)
            }
            is AddingOrEditingState.SuccessfulCarAdding -> {
                snackbarString = getString(R.string.successful_insert)
                snackbarLength = Snackbar.LENGTH_LONG
            }
            is AddingOrEditingState.SuccessfulCarEditing -> {
                snackbarString = getString(R.string.successful_update)
                snackbarLength = Snackbar.LENGTH_LONG
            }
            AddingOrEditingState.UnSuccessfulAdding -> {
                snackbarString = getString(R.string.unsuccessful_insert)
                snackbarLength = Snackbar.LENGTH_INDEFINITE
                snackbarActionString = getString(R.string.ok_rus_str_snack_bar)
            }
            AddingOrEditingState.UnSuccessfulEditing -> {
                snackbarString = getString(R.string.unsuccessful_update)
                snackbarLength = Snackbar.LENGTH_INDEFINITE
                snackbarActionString = getString(R.string.ok_rus_str_snack_bar)
            }
        }

        val snackbar = Snackbar.make(binding.root,snackbarString,snackbarLength)

        if (snackbarActionString == getString(R.string.ok_rus_str_snack_bar)) {

            snackbar.setAction(snackbarActionString){ item ->
                item.visibility = View.GONE
            }
        } else {
            makePopBackStack()
        }
        snackbar.show()


    }

    private fun makePopBackStack() {
        val fragmentManager = parentFragmentManager
        fragmentManager.popBackStack()
    }


    @Throws(IOException::class)
    private fun createImageFile(): File {
        // Create an image file name
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss",Locale.US).format(Date())
        val storageDir: File? = requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            "JPEG_${timeStamp}_", /* prefix */
            ".jpg", /* suffix */
            storageDir /* directory */
        ).apply {
            // Save a file: path for use with ACTION_VIEW intents
            currentPhotoPath = absolutePath
        }
    }

    private fun createChooserIntent(): Intent? {

        val imageFromStorageIntent =
            Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        val list = ArrayList<Intent>()
        list.add(imageFromStorageIntent)

        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->

            val photoFile: File? = try {
                createImageFile()
            } catch (ex: IOException) {
                null
            }


            photoFile?.also {
                val photoURI: Uri = FileProvider.getUriForFile(
                    requireContext(),
                    "com.example.android.fileprovider",
                    it
                )
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)

            }
            list.add(takePictureIntent)


            val chooserIntent = Intent.createChooser(imageFromStorageIntent, null)

            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, list.toTypedArray())

            //resultLauncher.launch(chooserIntent)

            return chooserIntent

        }


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
        viewModelObj.onCarAddingOrEditing(
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
// TODO: Протестить перенос в запууска диалога из viewModel сюда
           // viewModelObj.startAlertDialog(parentFragmentManager, serviceType)
            val bundle = Bundle ()
            val alertDialogObj: DialogFragment = StartCheckBoxMileageAlertDialog()
            bundle.putString("SERVICE_TYPE_EXTRA",serviceType.toString())
            alertDialogObj.arguments = bundle
            alertDialogObj.show(parentFragmentManager, "mileage")
        } else {

            checkboxOffById(serviceType)

        }
    }


}