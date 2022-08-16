package com.example.carservice.menuModule

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.carservice.R
import com.example.carservice.appModule.AppRepository
import com.example.carservice.appModule.MainViewModelFactory
import com.example.carservice.carCreatingModule.CarCreatingFragment
import com.example.carservice.dataBase.AppDataBase
import com.example.carservice.databinding.MenuBinding
import com.example.carservice.pixabayAPI.RetrofitService

class MenuFragment : Fragment() {

    private var _binding: MenuBinding? = null
    private val binding get() = _binding!!

    private val sharedViewModel: MenuViewModel by viewModels {
        MainViewModelFactory(
            AppRepository(
                AppDataBase.getDatabase(requireContext()),
                RetrofitService.invoke()
            )
        )
    }

    private var recyclerAdapter = AdapterEx()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        super.onCreate(savedInstanceState)

        _binding = MenuBinding.inflate(inflater, container, false)
        binding.recyclerview.layoutManager = LinearLayoutManager(context)
        sharedViewModel.menuViewModelInit()
        binding.recyclerview.adapter = recyclerAdapter


        sharedViewModel.recyclerListLiveData.observe(viewLifecycleOwner) {

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
}