package com.amg.mobicue.ui.list

import android.app.Activity
import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.os.Bundle
import android.provider.OpenableColumns
import android.view.*
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.amg.mobicue.R
import com.amg.mobicue.databinding.SpeechesListFragmentBinding
import com.amg.mobicue.room.Speech
import com.google.android.gms.ads.AdRequest
import java.io.BufferedReader
import java.io.File
import java.io.FileInputStream
import java.nio.charset.StandardCharsets


class SpeechesListFragment : Fragment() {

    private lateinit var viewModel: SpeechesListViewModel
    private lateinit var binding: SpeechesListFragmentBinding
    private val REQUEST_CODE = 0
    private var search = false
    private lateinit var _activity: AppCompatActivity

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = SpeechesListFragmentBinding.inflate(inflater, container, false)
        binding.setLifecycleOwner(this)
        binding.hasData = true

        _activity = requireActivity() as AppCompatActivity
        _activity.supportActionBar?.title = "MobiCue"

        setHasOptionsMenu(true)
        viewModel = ViewModelProvider(this).get(SpeechesListViewModel::class.java)

        binding.adView.loadAd(AdRequest.Builder().build())

        val adapter = SpeechesListAdapter()
        binding.speechesList.adapter = adapter
        binding.loading.show()

        //update UI
        viewModel.speechItems.observe(viewLifecycleOwner, Observer {
            //set up recyclerview
            val isEmpty = it.isNullOrEmpty()
            binding.hasData = !isEmpty
            if (!isEmpty) {
                binding.nodataContainer.visibility = GONE
                adapter.submitList(it)
            } else {
                binding.nodataContainer.visibility = VISIBLE
            }
            binding.loading.hide()
        })

        binding.addBtn.setOnClickListener({
            findNavController().navigate(SpeechesListFragmentDirections.toAddFragment())
        })
        binding.addFab.setOnClickListener({
            findNavController().navigate(SpeechesListFragmentDirections.toAddFragment())
        })
        binding.uploadFab.setOnClickListener({
            openFile()
        })

        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.search, menu)
        val searchView = menu.findItem(R.id.action_search).actionView as SearchView
        val _activity = requireActivity()
        searchView.setSearchableInfo(
            (_activity.getSystemService(Context.SEARCH_SERVICE) as SearchManager).getSearchableInfo(
                _activity.componentName
            )
        )
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextChange(newText: String): Boolean {
                return true
            }

            override fun onQueryTextSubmit(query: String): Boolean {
                search = true
                binding.loading.show()
                viewModel.getSpeechesByNameOrContent(query)
                return true
            }
        })
        searchView.setOnCloseListener(object : SearchView.OnCloseListener {
            override fun onClose(): Boolean {
                if (search) {
                    search = false
                    binding.loading.show()
                    viewModel.getSpeechesByNameOrContent("")
                }
                return search
            }
        })
        super.onCreateOptionsMenu(menu, inflater)
    }

//    fun openFile() {
//        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
//            addCategory(Intent.CATEGORY_OPENABLE)
//            type = "text/plain"
//        }
//        startActivityForResult(Intent.createChooser(intent, "Choose File"), REQUEST_CODE)
//    }


    //    @RequiresApi(Build.VERSION_CODES.M)
    private fun openFile() {

//        val permission: Int = _activity.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
//
//        if (permission != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(_activity, PERMISSIONS_STORAGE, REQUEST_EXTERNAL_STORAGE)
//        }

        val mimeTypes = arrayOf(
//            "application/msword",
//            "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
            "text/plain"
        )
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.type = "*/*"
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivityForResult(Intent.createChooser(intent, "ChooseFile"), REQUEST_CODE)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, resultData: Intent?) {
        if (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            resultData?.data?.also { uri ->
                binding.loading.show()
                val contentResolver = requireContext().contentResolver
                val takeFlags: Int = Intent.FLAG_GRANT_READ_URI_PERMISSION
                contentResolver.takePersistableUriPermission(uri, takeFlags)

                var displayName = "File Title"
                val cursor: Cursor? = contentResolver.query(uri, null, null, null, null, null)
                cursor?.use {
                    if (it.moveToFirst()) {
                        displayName = it.getString(it.getColumnIndex(OpenableColumns.DISPLAY_NAME))
                    }
                }

                displayName = displayName.subSequence(0, displayName.length - 4).toString()

                val content = StringBuilder()
                contentResolver.openInputStream(uri)?.use { inputStream ->
                    BufferedReader(inputStream.reader(Charsets.UTF_8)).use { reader ->
                        var line: String? = reader.readLine()
                        while (line != null) {
                            content.append(line)
                            line = reader.readLine()
                        }
                    }
                }

                viewModel.insert(
                    Speech(
                        displayName,
                        String(content.toString().toByteArray(), StandardCharsets.UTF_8),
                        false,
                        32,
                        0,
                        7,
                        7,
                        0
                    )
                )
            }
        }
    }
//    private fun showSystemUI() {
//        requireActivity().window.decorView.systemUiVisibility = (
//                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
//                        or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
//                        or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
//                )
//        requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
//    }


////                new AsyncTask<Void, Void, Void>() {
////                    @Override
////                    protected Void doInBackground(Void... params) {
////                        TranslateOptions options = TranslateOptions.newBuilder()
////                                .setApiKey("AIzaSyCxaoLlTmeZodX7x5Q1ghArp53QiT-R8eY")
////                                .build();
////                        Translate translate = options.getService();
////                        final Translation translation =
////                                translate.translate("Hello World",
////                                        Translate.TranslateOption.targetLanguage("de"));
////
////                        Log.e("Translate: ", translation.getTranslatedText());
////
////                        return null;
////                    }
////                }.execute();

}
