package com.example.carservice.appModule

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commit
import com.example.carservice.R
import com.example.carservice.menuModule.MenuFragment

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
            supportFragmentManager.commit {

                setReorderingAllowed(true)
                replace(R.id.fr, MenuFragment(), "menu")

            }
        }
    }
}