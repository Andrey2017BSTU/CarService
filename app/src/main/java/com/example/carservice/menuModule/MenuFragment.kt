package com.example.carservice.menuModule

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.carservice.R
import com.example.carservice.appModule.AppRepository
import com.example.carservice.carCreatingModule.CarCreatingFragment
import com.example.carservice.dataBase.AppDataBase
import com.example.carservice.dataBase.CarsItemTable
import com.example.carservice.databinding.MenuBinding
import com.example.carservice.detailModule.DetailFragment
import com.example.carservice.pixabayAPI.RetrofitService

class MenuFragment : Fragment(), AdapterEx.OnItemClickListener {

    private var _binding: MenuBinding? = null
    private val binding get() = _binding!!

    private val viewModelObj: MenuViewModel by viewModels {
        MenuViewModelFactory(
            AppRepository(
                AppDataBase.getDatabase(requireContext()),
                RetrofitService.invoke()
            )
        )
    }


    private var recyclerAdapter = AdapterEx(this)


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        super.onCreate(savedInstanceState)

        _binding = MenuBinding.inflate(inflater, container, false)
        binding.recyclerview.layoutManager = LinearLayoutManager(context)
        viewModelObj.menuViewModelInit()
        binding.recyclerview.adapter = recyclerAdapter



        viewModelObj.recyclerListLiveData.observe(viewLifecycleOwner) {

            recyclerAdapter.setCarsListAdapter(it)
            Log.v("Fragment_obs", "Adapter changes")

        }



        binding.addCarButton.setOnClickListener {

            parentFragmentManager.beginTransaction()
            parentFragmentManager.commit {

                setReorderingAllowed(true)
                replace(R.id.fr, CarCreatingFragment(), "car_create")
                addToBackStack(null)

            }
        }





        return binding.root
    }


    override fun onDestroyView() {
        super.onDestroyView()
        Log.v("Frag", "Destroy")
        _binding = null
    }


    override fun onItemClicked(carItem: CarsItemTable) {
        val bundle = Bundle()
        bundle.putInt("EXTRA_ID", carItem.id)
        bundle.putString("EXTRA_BRAND_NAME", carItem.brand_name)
        bundle.putString("EXTRA_MODEL_NAME", carItem.model_name)
        bundle.putString("EXTRA_YEAR", carItem.year)
        bundle.putInt("EXTRA_CURRENT_MILEAGE", carItem.current_mileage)
        bundle.putString("EXTRA_URL", carItem.image_url)
        bundle.putInt("EXTRA_OIL_MILEAGE", carItem.oil_mileage)
        bundle.putInt("EXTRA_OIL_LAST_SERVICE", carItem.oil_last_service_mileage)
        bundle.putInt("EXTRA_AIR_FILT_MILEAGE", carItem.air_filt_mileage)
        bundle.putInt("EXTRA_AIR_FILT_LAST_SERVICE", carItem.air_filt_last_service_mileage)
        bundle.putInt("EXTRA_FREEZ_MILEAGE", carItem.freez_mileage)
        bundle.putInt("EXTRA_FREEZ_LAST_SERVICE", carItem.freez_last_service_mileage)
        bundle.putInt("EXTRA_GRM_MILEAGE", carItem.grm_mileage)
        bundle.putInt("EXTRA_GRM_LAST_SERVICE", carItem.grm_last_service_mileage)

        parentFragmentManager.beginTransaction()
        parentFragmentManager.commit {

            val detail = DetailFragment()
            detail.arguments = bundle
            setReorderingAllowed(true)
            replace(R.id.fr, detail, "detail")
            addToBackStack(null)

        }
        Toast.makeText(context, carItem.id.toString() + " " + carItem.image_url, Toast.LENGTH_LONG)
            .show()
    }
}