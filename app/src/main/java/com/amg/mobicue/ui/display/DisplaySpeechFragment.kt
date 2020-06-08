package com.amg.mobicue.ui.display

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.graphics.Typeface
import android.os.Bundle
import android.view.*
import android.view.View.*
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.ui.NavigationUI
import com.amg.mobicue.R
import com.amg.mobicue.databinding.DisplaySpeechFragmentBinding
import com.amg.mobicue.getColorResource
import com.amg.mobicue.room.Speech
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class DisplaySpeechFragment : Fragment() {

    private lateinit var viewModel: DisplaySpeechViewModel
    private lateinit var binding: DisplaySpeechFragmentBinding
    private var textSize: Int = 0
    private var textSpeed: Long = 0
    private var textStyle: Int = 0
    private var textColor: Int = 0
    private var textBackground: Int = 0
    private lateinit var anim: Animator
    private val margin = 125
    private val maxSpeed = 31
    private var isScrolling = false
    private lateinit var _activity: AppCompatActivity

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = DisplaySpeechFragmentBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this

        setHasOptionsMenu(true)

        _activity = requireActivity() as AppCompatActivity
        NavigationUI.setupActionBarWithNavController(
            _activity,
            _activity.findNavController(R.id.nav_host_fragment)
        )
        _activity.supportActionBar?.title = ""
        binding.loading.show()

        val safeArgs: DisplaySpeechFragmentArgs by navArgs()
        val speechId = safeArgs.speechId

        viewModel = ViewModelProvider(
            this,
            DisplaySpeechViewModelFactory(speechId)
        ).get(DisplaySpeechViewModel::class.java)

        viewModel.speechLiveData.observe(viewLifecycleOwner, Observer {
            if (it != null) {
                viewModel.speech = it
                _activity.supportActionBar?.title = it.sName
                resetData(it)
            }
            binding.loading.hide()
        })

        binding.textSize.addOnChangeListener { _, value, _ ->
            textSize = value.toInt()
            binding.textSizeValue.text = textSize.toString()
        }

        binding.textStyleGroup.setOnCheckedChangeListener { _, checkedId ->
            textStyle = when (checkedId) {
                R.id.text_style_normal -> Typeface.NORMAL
                R.id.text_style_bold -> Typeface.BOLD
                R.id.text_style_italic -> Typeface.ITALIC
                else -> Typeface.BOLD_ITALIC
            }
            binding.content.setTypeface(null, textStyle)
        }

        binding.textColorGroup.setOnCheckedChangeListener { _, checkedId ->
            textColor = getColorId(checkedId)
            binding.content.setTextColor(getColorResource(_activity, textColor))
        }

        binding.textBackgroundGroup.setOnCheckedChangeListener { _, checkedId ->
            textBackground = getColorId(checkedId)
            binding.contentScroll.setBackgroundColor(getColorResource(_activity, textBackground))
        }

        binding.scrollSpeed.addOnChangeListener { _, value, _ ->
            textSpeed = value.toLong()
            binding.scrollSpeedValue.text = textSpeed.toString()
        }

        binding.changeFab.setOnClickListener {
            val layout = binding.container
            it.visibility = INVISIBLE
            binding.editFab.visibility = INVISIBLE

            anim = ViewAnimationUtils.createCircularReveal(
                layout,
                layout.width - margin,
                layout.height - margin,
                0f,
                layout.width.toFloat() + layout.height.toFloat()
            )
            anim.interpolator = AccelerateDecelerateInterpolator()
            anim.duration = 400
            layout.visibility = VISIBLE
            anim.start()
        }

        binding.cancelBtn.setOnClickListener {
            if (!anim.isRunning) {
                filterFabAnimations()
                resetData(viewModel.speech)
            }
        }

        binding.editFab.setOnClickListener {
            findNavController().navigate(
                DisplaySpeechFragmentDirections.toAddFragment(speechId)
            )
        }

        binding.changeBtn.setOnClickListener {
            if (!anim.isRunning) {
                val speech = viewModel.speech
                speech.tSize = textSize
                speech.tStyle = textStyle
                speech.sColor = textColor
                speech.sBackground = textBackground
                speech.sSpeed = textSpeed
                viewModel.update(speech)
                filterFabAnimations()
            }
        }
        binding.content.setOnClickListener {
            if (!isScrolling) {
                isScrolling = true
                binding.changeFab.animate().translationX(1000f).setDuration(370).start()
                binding.editFab.animate().translationX(1000f).setDuration(400).start()
    //                hideSystemUI()
            } else {
                isScrolling = false
                if (!binding.container.isVisible) {
                    binding.editFab.visibility = VISIBLE
                    binding.changeFab.visibility = VISIBLE
                }
                binding.editFab.animate().translationX(0f).setDuration(370).start()
                binding.changeFab.animate().translationX(0f).setDuration(400).start()
    //                showSystemUI()
            }
            GlobalScope.launch {
                while (isScrolling) {
                    binding.contentScroll.scrollBy(0, 1)
                    delay(maxSpeed - textSpeed)
                }
            }
        }
        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.delete, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_delete) {
            MaterialAlertDialogBuilder(_activity)
                .setTitle("Confirm")
                .setMessage("This speech will be permanently deleted")
                .setPositiveButton("Delete"
                ) { _, _ ->
                    viewModel.delete(viewModel.speech)
                    findNavController().navigate(
                        DisplaySpeechFragmentDirections.toListFragment()
                    )
                }
                .setNegativeButton("Cancel", null)
                .show()
        }
        return super.onOptionsItemSelected(item)
    }


    private fun filterFabAnimations() {
        val layout = binding.container
        var fab = binding.changeFab

        anim = ViewAnimationUtils.createCircularReveal(
            layout,
            layout.width - margin,
            layout.height - margin,
            layout.width.toFloat(),
            0f
        )
        anim.interpolator = AccelerateDecelerateInterpolator()
        anim.start()

        anim.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator?) {
                super.onAnimationEnd(animation)
                layout.visibility = INVISIBLE
                if (!isScrolling) {
                    anim = ViewAnimationUtils.createCircularReveal(
                        fab,
                        fab.width / 2,
                        fab.height / 2,
                        0f,
                        fab.width.toFloat()
                    )
                    anim.interpolator = AccelerateDecelerateInterpolator()
                    fab.visibility = VISIBLE
                    anim.start()
                    fab = binding.editFab
                    anim = ViewAnimationUtils.createCircularReveal(
                        fab,
                        fab.width / 2,
                        fab.height / 2,
                        0f,
                        fab.width.toFloat()
                    )
                    anim.interpolator = AccelerateDecelerateInterpolator()
                    fab.visibility = VISIBLE
                    anim.start()
                }
            }
        })
    }

    private fun setTextStyle(textStyle: Int) {
        this.textStyle = textStyle
        binding.content.setTypeface(null, textStyle)
        when (textStyle) {
            0 -> binding.textStyleNormal.isChecked = true
            1 -> binding.textStyleBold.isChecked = true
            2 -> binding.textStyleItalic.isChecked = true
            else -> binding.textStyleBoldItalic.isChecked = true
        }
    }

    private fun setTextColor(Color: Int) {
        textColor = Color
        binding.content.setTextColor(getColorResource(_activity, textColor))
        when (Color) {
            0 -> binding.colorBlack.isChecked = true
            1 -> binding.colorBlue.isChecked = true
            2 -> binding.colorBrown.isChecked = true
            3 -> binding.colorGreen.isChecked = true
            4 -> binding.colorOrange.isChecked = true
            5 -> binding.colorPurple.isChecked = true
            6 -> binding.colorRed.isChecked = true
            else -> binding.colorWhite.isChecked = true
        }
    }

    private fun setTextBackground(Color: Int) {
        textBackground = Color
        binding.contentScroll.setBackgroundColor(getColorResource(_activity, Color))
        when (Color) {
            0 -> binding.backgroundBlack.isChecked = true
            1 -> binding.backgroundBlue.isChecked = true
            2 -> binding.backgroundBrown.isChecked = true
            3 -> binding.backgroundGreen.isChecked = true
            4 -> binding.backgroundOrange.isChecked = true
            5 -> binding.backgroundPurple.isChecked = true
            6 -> binding.backgroundRed.isChecked = true
            else -> binding.backgroundWhite.isChecked = true
        }
    }

    //get color id from chip id
    private fun getColorId(id: Int): Int {
        return when (id) {
            R.id.color_black, R.id.background_black -> 0
            R.id.color_blue, R.id.background_blue -> 1
            R.id.color_brown, R.id.background_brown -> 2
            R.id.color_green, R.id.background_green -> 3
            R.id.color_orange, R.id.background_orange -> 4
            R.id.color_purple, R.id.background_purple -> 5
            R.id.color_Red, R.id.background_Red -> 6
            else -> 7 //R.color.white
        }
    }

    private fun resetData(speech: Speech) {
        binding.speech = speech

        textSize = speech.tSize
        binding.textSize.value = textSize.toFloat()

        setTextStyle(speech.tStyle)

        setTextColor(speech.sColor)

        setTextBackground(speech.sBackground)

        textSpeed = speech.sSpeed
        binding.scrollSpeed.value = textSpeed.toFloat()
    }
}
