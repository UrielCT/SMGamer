package com.example.socialmediagamer.view.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.socialmediagamer.view.activities.FiltersActivity
import com.example.socialmediagamer.databinding.FragmentFiltersBinding


class FiltersFragment : Fragment() {

    private var _binding: FragmentFiltersBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentFiltersBinding.inflate(inflater, container,false)



        binding.itemPs4.setOnClickListener { goToFilterActivity("PS4") }
        binding.itemXbox.setOnClickListener { goToFilterActivity("XBOX") }
        binding.itemNintendo.setOnClickListener { goToFilterActivity("NINTENDO") }
        binding.itemPc.setOnClickListener { goToFilterActivity("PC") }


        return binding.root
    }

    private fun goToFilterActivity(category:String){
        val intent: Intent = Intent(this.context,FiltersActivity::class.java)
        intent.putExtra("category",category)
        startActivity(intent)
    }

}