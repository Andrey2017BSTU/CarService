package com.example.carservice

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commit

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {

        // TODO: сделать поворот
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val viewModel: CarCreatingViewModel by viewModels()
        viewModel.init(AppDataBase.getDatabase(this))
        if (savedInstanceState == null) {
            supportFragmentManager.commit {
                setReorderingAllowed(true)

                replace(R.id.fr, CarCreatingFragment(), "car_create")
                addToBackStack(null)
            }

        }


    }

}