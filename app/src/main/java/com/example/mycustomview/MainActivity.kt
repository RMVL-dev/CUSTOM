package com.example.mycustomview

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.mycustomview.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding:ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.myCustomView.setData(
            data = listOf(
                25,
                25,
                6,
                100,
                75,
                46,
                8,
                15,
                100
            ).reversed()
        )
    }
}