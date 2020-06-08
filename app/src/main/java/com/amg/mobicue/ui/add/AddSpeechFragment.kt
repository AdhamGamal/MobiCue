package com.amg.mobicue.ui.add

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.ui.NavigationUI
import com.amg.mobicue.R
import com.amg.mobicue.databinding.AddSpeechFragmentBinding
import com.amg.mobicue.room.Speech
import com.google.android.gms.ads.AdRequest

class AddSpeechFragment : Fragment() {

    private lateinit var viewModel: AddSpeechViewModel
    private lateinit var binding: AddSpeechFragmentBinding
    private lateinit var _activity: AppCompatActivity

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = AddSpeechFragmentBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this

        _activity = requireActivity() as AppCompatActivity
        NavigationUI.setupActionBarWithNavController(
            _activity,
            _activity.findNavController(R.id.nav_host_fragment)
        )
        _activity.supportActionBar?.title = "Add Speech"
        binding.loading.hide()
        val safeArgs: AddSpeechFragmentArgs by navArgs()
        val speechId = safeArgs.speechId

        viewModel = ViewModelProvider(
            this,
            AddSpeechViewModelFactory(speechId)
        ).get(AddSpeechViewModel::class.java)

        if (speechId != -1) {
            viewModel.speechLiveData.observe(viewLifecycleOwner, Observer {
                if (it != null) {
                    viewModel.speech = it
                    _activity.supportActionBar?.title = it.sName
                    binding.speechName.setText(it.sName)
                    binding.speechContent.setText(it.sContent)
                }
                binding.loading.hide()
            })
        }

        binding.adView.loadAd(AdRequest.Builder().build())


        binding.speechName.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) binding.speechNameLabel.isErrorEnabled = false
        }
        binding.speechContent.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) binding.speechContentLabel.isErrorEnabled = false
        }

        binding.addButton.setOnClickListener {
            val sName = binding.speechName.text.toString()
            if (sName.isEmpty()) {
                binding.speechNameLabel.error = "Missing Title"
            }
            val sContent = binding.speechContent.text.toString()
            if (sContent.isEmpty()) {
                binding.speechContentLabel.error = "Missing Content"
            }
            if (sName.isNotEmpty() and sContent.isNotEmpty()) {
                if (speechId != -1) {
                    viewModel.speech.sName = sName
                    viewModel.speech.sContent = sContent
                    viewModel.update(viewModel.speech)
                    findNavController().navigate(
                        AddSpeechFragmentDirections.toDisplayFragment(speechId)
                    )
                } else {
                    viewModel.insert(
                        Speech(
                            sName,
                            sContent,
                            true,
                            32,
                            0,
                            7,
                            7,
                            0
                        )
                    )
                    findNavController().navigate(AddSpeechFragmentDirections.toListFragment())
                }
            }
        }

        return binding.root
    }

    override fun onPause() {
        super.onPause()
        val imm = _activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(requireView().windowToken, 0)
    }
}
